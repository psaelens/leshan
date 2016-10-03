package com.skylaneoptics.ipso;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.util.*;

/**
 * @author Pierre Saelens
 */
public class SmartObjects {

    private final List<SmartObject> SMART_OBJECTS;
    private final List<SmartObject> LWM2M_OBJECTS;

    private static SmartObjects instance;

    private SmartObjects() {
        Gson gson = new GsonBuilder().create();
        InputStreamReader reader = new InputStreamReader(SmartObjects.class.getResourceAsStream("/ipso-objects-spec.json"));
        SMART_OBJECTS = gson.fromJson(reader, new TypeToken<List<SmartObject>>(){}.getType());
        reader = new InputStreamReader(SmartObjects.class.getResourceAsStream("/oma-objects-spec.json"));
        LWM2M_OBJECTS = gson.fromJson(reader, new TypeToken<List<SmartObject>>(){}.getType());
    }

    private static SmartObjects getInstance() {
        if (instance == null)
            instance = new SmartObjects();
        return instance;
    }

    public static List<SmartObject> all() {
        List<SmartObject> all = new ArrayList<>();
        all.addAll(getInstance().SMART_OBJECTS);
        all.addAll(getInstance().LWM2M_OBJECTS);
        return all;
    }

    public static SmartObject get(int id) {
        for (SmartObject object : all()) {
            if (object.getId() == id)
                return object;
        }
        return null;
    }
}
