package be.neteo.leshan.extension;

import org.junit.Test;

import static org.junit.Assert.*;

public class NeteoClientTest {

    @Test
    public void testAddDevice() throws Exception {
        NeteoClient client = new NeteoClient("http://private-f3d9b-neteo.apiary-mock.com");

        NeteoClient.Device device = new NeteoClient.Device();
        device.setDeviceClass("iot");
        device.setManufacturer("Skylane");
        device.setSerialNumber("pierre123456");
        boolean response = client.addDevice(device);

        assertTrue(response);
    }

    @Test
    public void testAddSensor() {
        NeteoClient client = new NeteoClient("http://private-f3d9b-neteo.apiary-mock.com");

        NeteoClient.Sensor sensor = new NeteoClient.Sensor();
        sensor.setSensorClass("temperature");
        sensor.setId("/3303/0");
        sensor.setUnit("°C");
        sensor.setValue("21");
        boolean response = client.addSensor("dummy", sensor);

        assertTrue(response);


    }
}