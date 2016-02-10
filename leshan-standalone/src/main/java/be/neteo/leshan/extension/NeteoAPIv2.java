package be.neteo.leshan.extension;

import org.eclipse.leshan.LinkObject;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.node.LwM2mObjectInstance;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.server.client.Client;
import org.eclipse.leshan.server.client.ClientRegistryListener;
import org.eclipse.leshan.server.observation.Observation;
import org.eclipse.leshan.server.observation.ObservationRegistryListener;

import java.util.*;

/**
 *
 */
class NeteoAPIv2 implements ClientRegistryListener, ObservationRegistryListener {
    private Neteo neteo;

    public NeteoAPIv2(Neteo neteo) {
        this.neteo = neteo;
    }

    private Map<String, String> toMap(Client client) {
        Map<String, String> map = new TreeMap<>();


        map.put("registrationDate", String.format("%s", client.getRegistrationDate()));
        map.put("address", String.format("%s", client.getAddress()));
        map.put("port", String.format("%s", client.getPort()));
        map.put("registrationEndpoint", String.format("%s", client.getRegistrationEndpointAddress()));
        map.put("lifeTimeInSec", String.format("%s", client.getLifeTimeInSec()));
        map.put("smsNumber", String.format("%s", client.getSmsNumber()));
        map.put("lwM2mVersion", String.format("%s", client.getLwM2mVersion()));
        map.put("bindingMode", String.format("%s", client.getBindingMode()));
        map.put("endpoint", String.format("%s", client.getEndpoint()));
        map.put("registrationId", String.format("%s", client.getRegistrationId()));
        map.put("lastUpdate", String.format("%s", client.getLastUpdate()));

        for (LinkObject linkObject : client.getObjectLinks()) {
            map.put(linkObject.getUrl(), "");
        }

        return map;
    }
    @Override
    public void registered(Client client) {

        Neteo.LOG.info("Client {} registered.", client.getEndpoint());

        // Retrieve Device information
        // ---
        NeteoClient.Thing thing = new NeteoClient.Thing();
        thing.setDescription("LWM2M Thing");
        thing.setProperties(toMap(client));


        try {
            LwM2mObjectInstance deviceInstance = neteo.read(client, "/3/0");

            for(Map.Entry<Integer, LwM2mResource> resource : deviceInstance.getResources().entrySet()) {
                Map<String, String> properties = thing.getProperties();
                properties.put("/3/0/" + resource.getKey(), resource.getValue().getValue().value.toString());
            }
        } catch (Exception e) {
            Neteo.LOG.error("Unable to read resource", e);
        }

        neteo.neteoClient.addThing(thing);

    }

    @Override
    public void updated(Client clientUpdated) {
        Neteo.LOG.debug("Client updated: " + clientUpdated.getEndpoint());

    }

    @Override
    public void unregistered(Client client) {
        Neteo.LOG.debug("Client unregistered: " + client.getEndpoint());
    }

    @Override
    public void cancelled(Observation observation) {
        Neteo.LOG.debug("Observation cancelled");

    }

    @Override
    public void newValue(Observation observation, LwM2mNode node) {
        if (Neteo.LOG.isDebugEnabled()) {
            Neteo.LOG.debug("Received notification from [{}] containing value [{}]", observation.getPath(),
                    node.toString());
        }

        Map<Integer, SmartObject> supportedSensors = SensorUtils.getSupportedSensorMap();

        String endpoint = observation.getClient().getEndpoint();
        if (node instanceof LwM2mResource) {
            LwM2mResource resource = (LwM2mResource) node;
            NeteoClient.Sensor sensor = new NeteoClient.Sensor();
            sensor.setId(observation.getPath().getObjectId() + "-" + observation.getPath().getObjectInstanceId());

            SmartObject smartObject = supportedSensors.get(observation.getPath().getObjectId());

            SmartObjectResource smartObjectResource = smartObject.getResources().get(node.getId());

            if (smartObjectResource != null) {
                SensorUtils.set(sensor, smartObjectResource.getName(), resource.getValue().value.toString());
                neteo.neteoClient.updateSensor(endpoint, sensor);
            }
        }

    }

    @Override
    public void newObservation(Observation observation) {
        Neteo.LOG.debug("Observation created");
    }
}
