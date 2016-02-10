package be.neteo.leshan.extension;

import org.eclipse.leshan.LinkObject;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.server.client.Client;
import org.eclipse.leshan.server.client.ClientRegistryListener;
import org.eclipse.leshan.server.observation.Observation;
import org.eclipse.leshan.server.observation.ObservationRegistryListener;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by pierre on 27/01/16.
 */
class NeteoAPIv1 implements ClientRegistryListener, ObservationRegistryListener {
    private Neteo neteo;

    public NeteoAPIv1(Neteo neteo) {
        this.neteo = neteo;
    }

    @Override
    public void registered(Client client) {

        Neteo.LOG.info("Client {} registered.", client.getEndpoint());

        LinkObject[] objectLinks = client.getSortedObjectLinks();


        // Retrieve Device information
        // ---
        NeteoClient.Device device = new NeteoClient.Device();
        device.setDeviceClass("iot");
        device.setAddress(client.getAddress().getHostAddress());
        device.setId(client.getEndpoint());

        try {
            device.setManufacturer(neteo.readResource(client, 3, 0, 0).getValue().value.toString());
            device.setName(neteo.readResource(client, 3, 0, 1).getValue().value.toString()); // Model Number
            device.setSerialNumber(neteo.readResource(client, 3, 0, 2).getValue().value.toString());
            device.setFirmwareVersion(neteo.readResource(client, 3, 0, 3).getValue().value.toString());
        } catch (Exception e) {
            Neteo.LOG.error("Unable to read resource", e);
        }

        //neteo.neteoClient.addDevice(device);


        // Retrieve Sensors information
        // ---
        Map<Integer, SmartObject> supportedSensors = SensorUtils.getSupportedSensorMap();

        // Cache of read instance (format: <objectId>/<instanceId>)
        Set<String> instances = new HashSet<>();

        for (LinkObject linkObject : objectLinks) {
            if (Neteo.LOG.isDebugEnabled()) {
                Neteo.LOG.debug("LinkObject[objectId:{}; objectInstanceId:{}]", linkObject.getObjectId(), linkObject.getObjectInstanceId());
            }

            Integer objectId = linkObject.getObjectId();
            Integer objectInstanceId = linkObject.getObjectInstanceId();

            if (objectInstanceId != null && supportedSensors.containsKey(objectId) && !instances.contains(objectId + "/" + objectInstanceId)) {

                SmartObject smartObject = supportedSensors.get(objectId);

                NeteoClient.Sensor sensor = new NeteoClient.Sensor();
                sensor.setId(objectId + "-" + objectInstanceId);
                sensor.setSensorClass(smartObject.getName());

                for (Integer id : smartObject.getResourceIds()) {

                    LwM2mResource resource = neteo.readResource(client, objectId, objectInstanceId, id);
                    if (resource != null) {
                        SmartObjectResource smartObjectResource = smartObject.getResources().get(id);
                        SensorUtils.set(sensor, smartObjectResource.getName(), resource.getValue().value.toString());
                        if (smartObjectResource.isObserve()) {
                            neteo.observeResource(client, objectId, objectInstanceId, id);
                        }
                    }
                }

                //neteo.neteoClient.addSensor(device.getId(), sensor);
                device.addSensor(sensor);

                instances.add(sensor.getId());
            }
        }

        neteo.neteoClient.addDevice(device);

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
