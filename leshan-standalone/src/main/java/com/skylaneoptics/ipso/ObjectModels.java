package com.skylaneoptics.ipso;


import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;

import java.util.*;

/**
 * @author Pierre Saelens
 */
public class ObjectModels {

    private final List<ObjectModel> MODELS;

    private static ObjectModels instance;

    private ObjectModels() {
        MODELS = ObjectLoader.loadDefault();
    }

    private static ObjectModels getInstance() {
        if (instance == null)
            instance = new ObjectModels();
        return instance;
    }

    public static List<ObjectModel> all() {
        return getInstance().MODELS;
    }

    public static ObjectModel get(int id) {
        for (ObjectModel object : all()) {
            if (object.id == id)
                return object;
        }
        return null;
    }
}
