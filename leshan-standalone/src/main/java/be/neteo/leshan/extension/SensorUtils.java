package be.neteo.leshan.extension;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class SensorUtils {

    public static Map<Integer, SmartObject> getSupportedSensorMap() {
        JsonElement jsonElement = new JsonParser().parse(new InputStreamReader(SensorUtils.class.getResourceAsStream("/sensors.json")));

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Map<Integer, SmartObject> smartObjectMap = new HashMap<>(jsonArray.size());

        for (JsonElement smartObjectElement : jsonArray) {
            JsonObject smartObjectElementAsJsonObject = smartObjectElement.getAsJsonObject();
            int id = smartObjectElementAsJsonObject.get("id").getAsInt();
            String value = smartObjectElementAsJsonObject.get("name").getAsString();
            SmartObject smartObject = new SmartObject(id, value);
            JsonArray resourcesJsonArray = smartObjectElementAsJsonObject.get("resources").getAsJsonArray();
            for (JsonElement resourceElement : resourcesJsonArray) {
                JsonObject resourceElementAsJsonObject = resourceElement.getAsJsonObject();
                int resourceId = resourceElementAsJsonObject.get("id").getAsInt();
                String resourceValue = resourceElementAsJsonObject.get("name").getAsString();
                boolean observe = resourceElementAsJsonObject.get("observe").getAsBoolean();
                smartObject.addResource(new SmartObjectResource(resourceId, resourceValue, observe));
            }
            smartObjectMap.put(id, smartObject);
        }

        return smartObjectMap;
    }

    public static boolean set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return true;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return false;
    }
}
