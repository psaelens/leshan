package be.neteo.leshan.extension;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.eclipse.leshan.LinkObject;
import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.ValueResponse;
import org.eclipse.leshan.server.LwM2mServer;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.client.Client;
import org.eclipse.leshan.server.client.ClientRegistryListener;
import org.eclipse.leshan.server.observation.Observation;
import org.eclipse.leshan.server.observation.ObservationRegistryListener;
import org.eclipse.leshan.standalone.servlet.json.ClientSerializer;
import org.eclipse.leshan.standalone.servlet.json.LwM2mNodeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class Neteo {

    private static final long TIMEOUT = 20000; // ms

    private static final Logger LOG = LoggerFactory.getLogger(Neteo.class);

    private final Gson gson;
    private final LwM2mServer server;
    private final NeteoClient neteoClient;

    private final ClientRegistryListener clientRegistryListener = new ClientRegistryListener() {
        @Override
        public void registered(Client client) {

            LinkObject[] objectLinks = client.getSortedObjectLinks();


            // Retrieve Device information
            // ---
            NeteoClient.Device device = new NeteoClient.Device();
            device.setDeviceClass("iot");
            device.setAddress(client.getAddress().getHostAddress());
            device.setId(client.getEndpoint());

            neteoClient.addDevice(device);


            // Retrieve Sensors information
            // ---
            // objectId => set of resource ids
            Map<Integer, Set<Integer>> supportedObjectResourceIds = new HashMap<>();
            // 3303 : temperature
            supportedObjectResourceIds.put(3303, new HashSet<>(Arrays.asList(5700, 5701)));
            // 3304 : humidity
            supportedObjectResourceIds.put(3304, new HashSet<>(Arrays.asList(5700, 5701)));

            // Cache of read instance (format: <objectId>/<instanceId>)
            Set<String> instances = new HashSet<>();

            for (LinkObject linkObject : objectLinks) {
                Integer objectId = linkObject.getObjectId();
                Integer objectInstanceId = linkObject.getObjectInstanceId();

                if (objectInstanceId != null && supportedObjectResourceIds.containsKey(objectId) && !instances.contains(objectId + "/" + objectInstanceId)) {

                    Set<Integer> supportedResourceIds = supportedObjectResourceIds.get(objectId);

                    NeteoClient.Sensor sensor = new NeteoClient.Sensor();
                    switch (objectId) {
                        case 3303:
                            sensor.setSensorClass("temperature");
                            break;
                        case 3304:
                            sensor.setSensorClass("humidity");
                            break;
                    }

                    for (Integer id : supportedResourceIds) {

                        LwM2mResource resource = readResource(client, objectId, objectInstanceId, id);
                        if (resource != null) {
                            switch(id) {
                                case 5700:
                                    sensor.setValue(resource.getValue().toString());
                                    break;
                                case 5701:
                                    sensor.setUnit(resource.getValue().toString());
                                    break;
                            }
                            observeResource(client, objectId, objectInstanceId, id);
                        }
                    }

                    neteoClient.addSensor(device.getId(), sensor);

                    instances.add(objectId + "/" + objectInstanceId);
                }
            }

        }

        @Override
        public void updated(Client clientUpdated) {
            LOG.debug("Client updated: " + clientUpdated.getEndpoint());

        }

        @Override
        public void unregistered(Client client) {
            LOG.debug("Client unregistered: " + client.getEndpoint());
        }
    };

    private final ObservationRegistryListener observationRegistryListener = new ObservationRegistryListener() {
        @Override
        public void cancelled(Observation observation) {
            LOG.debug("Observation cancelled");

        }

        @Override
        public void newValue(Observation observation, LwM2mNode value) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Received notification from [{}] containing value [{}]", observation.getPath(),
                        value.toString());
            }
            String data = new StringBuffer("{\"ep\":\"").append(observation.getClient().getEndpoint())
                    .append("\",\"res\":\"").append(observation.getPath().toString()).append("\",\"val\":")
                    .append(gson.toJson(value)).append("}").toString();

        }

        @Override
        public void newObservation(Observation observation) {
            LOG.debug("Observation created");
        }
    };

    public Neteo(LeshanServer server) {
        LOG.info("extension Neteo used");


        String configFile = System.getenv("neteo.config.file");
        if (configFile == null)
            configFile = "./leshan-standalone/Neteo.properties";

        Properties properties = new Properties();
        try {

            InputStream input = new FileInputStream(configFile);
            properties.load(input);
            LOG.info("Loading standard properties from file [" + configFile + "]");
        } catch (IOException e) {
            LOG.warn("Unable to load standard properties from file [" + configFile + "]", e);
        }

        this.server = server;

        this.neteoClient = new NeteoClient(properties.getProperty("api.host", "localhost"));

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(Client.class, new ClientSerializer());
        gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeSerializer());
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        this.gson = gsonBuilder.create();


        server.getClientRegistry().addListener(this.clientRegistryListener);
        server.getObservationRegistry().addListener(this.observationRegistryListener);

    }

    protected LwM2mResource readResource(Client client, Integer objectId, Integer objectInstanceId, Integer resourceId) {
        String target = "/" + objectId + "/" + objectInstanceId + "/" + resourceId;
        ReadRequest request = new ReadRequest(target);
        ValueResponse cResponse = this.server.send(client, request, TIMEOUT);

        LwM2mResource node = (LwM2mResource) cResponse.getContent();

        return node;
    }

    protected boolean observeResource(Client client, Integer objectId, Integer objectInstanceId, Integer resourceId) {
        String target = "/" + objectId + "/" + objectInstanceId + "/" + resourceId;
        ObserveRequest request = new ObserveRequest(target);
        LwM2mResponse cResponse = server.send(client, request, TIMEOUT);

        ResponseCode code = cResponse.getCode();
        return code.equals(ResponseCode.CONTENT);
    }

}
