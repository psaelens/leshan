package be.neteo.leshan.extension;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.request.ReadRequest;
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class MongoDBStorage {

    private static final long TIMEOUT = 20000; // ms

    private static final Logger LOG = LoggerFactory.getLogger(MongoDBStorage.class);

    private final MongoCollection<Document> collection;
    private final Gson gson;
    private final LwM2mServer server;

    public final class ObjectResource {
        private final int objectId;
        private final int instanceId;
        private final int resourceId;

        private String value;

        public ObjectResource(int objectId, int instanceId, int resourceId) {
            this.objectId = objectId;
            this.instanceId = instanceId;
            this.resourceId = resourceId;
        }
    }

    private final ClientRegistryListener clientRegistryListener = new ClientRegistryListener() {
        @Override
        public void registered(Client client) {

            Document document = Document.parse(gson.toJson(client));

            // read some value from the device
            // /3/0/0
            ReadRequest request = new ReadRequest("/3/0/0");
            ValueResponse cResponse = server.send(client, request, TIMEOUT);

            LwM2mNode node = cResponse.getContent();
            String response = gson.toJson(node);

            LOG.debug("Read /3/0/0: " + response);

            List<ObjectResource> objectResources = new ArrayList<>();

            document.append("objectResources", objectResources);


            collection.insertOne(document);

        }

        @Override
        public void updated(Client clientUpdated) {

            collection.updateOne(
                    Filters.eq("endpoint", clientUpdated.getEndpoint()),
                    new Document("$set", Document.parse(gson.toJson(clientUpdated))));

        }

        @Override
        public void unregistered(Client client) {

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

    public MongoDBStorage(LeshanServer server) {
        LOG.info("extension MongoDBStorage used");

        Properties properties = new Properties();
        try {
            InputStream input = new FileInputStream("/Users/pierre/Projects/leshan/leshan-standalone/MongoDB.properties");
            properties.load(input);
            LOG.info("Loading standard properties from file MongoDB.properties");
        } catch (IOException e) {
            LOG.warn("Unable to load standard properties from file MongoDB.properties", e);
        }

        this.server = server;

        MongoClient mongoClient = new MongoClient(properties.getProperty("db.mongo.host", "localhost"));
        MongoDatabase database = mongoClient.getDatabase(properties.getProperty("db.mongo.database", "lwm2m"));
        this.collection = database.getCollection(properties.getProperty("db.mongo.collection", "lwm2m_devices"));

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(Client.class, new ClientSerializer());
        gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeSerializer());
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        this.gson = gsonBuilder.create();


        server.getClientRegistry().addListener(this.clientRegistryListener);
        server.getObservationRegistry().addListener(this.observationRegistryListener);

    }

}
