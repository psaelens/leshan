package be.neteo.leshan.extension;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by pierre on 01/10/15.
 */
public class SensorUtilsTest {

    @Test
    public void read_sensors_json_file() {

        SensorUtils.getSupportedSensorMap();
    }
}