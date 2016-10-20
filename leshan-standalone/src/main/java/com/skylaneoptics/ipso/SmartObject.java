package com.skylaneoptics.ipso;

import org.eclipse.leshan.core.model.ObjectModel;

import java.io.Closeable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represent a Smart Object instance.
 *
 * @author Pierre Saelens
 */
public class SmartObject {


    private ObjectModel model;

    private int instanceId;

    public SmartObject(ObjectModel model, int instanceId) {
        this.model = model;
        this.instanceId = instanceId;
    }

    public ObjectModel getModel() {
        return model;
    }

    public int getInstanceId() {
        return instanceId;
    }
}
