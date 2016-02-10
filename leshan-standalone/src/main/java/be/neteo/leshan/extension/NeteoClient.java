package be.neteo.leshan.extension;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.standalone.servlet.json.ClientSerializer;
import org.eclipse.leshan.standalone.servlet.json.LwM2mNodeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NeteoClient {

    private static final Logger LOG = LoggerFactory.getLogger(NeteoClient.class);

    public static class Device {

        private String id;

        private String name;

        @SerializedName("class")
        private String deviceClass;
        private String manufacturer;
        @SerializedName("serial_number")
        private String serialNumber;
        @SerializedName("firmware_version")
        private String firmwareVersion;

        private String address;

        private List<Sensor> sensors = new ArrayList<>();


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDeviceClass() {
            return deviceClass;
        }

        public void setDeviceClass(String deviceClass) {
            this.deviceClass = deviceClass;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public String getFirmwareVersion() {
            return firmwareVersion;
        }

        public void setFirmwareVersion(String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
        }

        public void addSensor(Sensor sensor) {
            this.sensors.add(sensor);
        }
    }

    public static class Sensor {

        private String id;

        @SerializedName("class")
        private String sensorClass;

        private String value;

        private String unit;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSensorClass() {
            return sensorClass;
        }

        public void setSensorClass(String sensorClass) {
            this.sensorClass = sensorClass;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }

    public static class Event {
        private String endpoint;
        private String path;
        private String value;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Thing {
        private String id;
        private String description;
        private Map<String, String> properties;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Map<String, String> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }
    }

    private final String host;
    private final Client client;
    private final Gson gson;

    public NeteoClient(String host) {
        this.host = host;
        this.client = ClientBuilder.newClient();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(org.eclipse.leshan.server.client.Client.class, new ClientSerializer());
        gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeSerializer());
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        this.gson = gsonBuilder.create();
    }

    public boolean addThing(Thing thing) {
        String target = this.host + "/api/iot/things";
        Entity<String> json = Entity.json(this.gson.toJson(thing));
        return post(target, json);
    }

    public boolean addDevice(Device device) {
        String target = this.host + "/api/devices";
        return post(target, Entity.json(this.gson.toJson(device)));
    }

    public boolean addSensor(String deviceId, Sensor sensor) {
        String target = this.host + "/api/devices/" + deviceId + "/sensors";
        return post(target, Entity.json(this.gson.toJson(sensor)));
    }

    public boolean updateSensor(String deviceId, Sensor sensor) {
        String target = this.host + "/api/devices/" + deviceId + "/sensors/" + sensor.getId();
        Response response = client.target(target)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(this.gson.toJson(sensor)));

        LOG.info("API Endpoint: {}, Status: {}", target, response.getStatus());
        if (LOG.isDebugEnabled()) {
            LOG.debug("headers: " + response.getHeaders());
            LOG.debug("body: {}", response.readEntity(String.class));
        }

        return response.getStatus() >= 200 && response.getStatus() <= 299;
    }

    public boolean publishEvent(Event event) {
        String target = this.host + "/api/events";
        return post(target, Entity.json(this.gson.toJson(event)));
    }

    private boolean post(String target, Entity<String> json) {
        Response response = client.target(target)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(json);

        LOG.info("API Endpoint: {}, Status: {}", target, response.getStatus());
        if (LOG.isDebugEnabled()) {
            LOG.debug("headers: " + response.getHeaders());
            LOG.debug("body: {}", response.readEntity(String.class));
        }

        return response.getStatus() >= 200 && response.getStatus() <= 299;
    }

}
