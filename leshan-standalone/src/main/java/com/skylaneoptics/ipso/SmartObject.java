package com.skylaneoptics.ipso;

import java.io.Closeable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represent a Smart Object.
 *
 * @author Pierre Saelens
 */
public class SmartObject implements Cloneable {


    private int id;
    private String name;
    private String instancetype;
    private boolean mandatory;
    private List<Resource> resourcedefs;

    private transient int instanceId;

    public SmartObject(int id, String value) {
        this.id = id;
        this.name = value;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInstancetype() {
        return instancetype;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public List<Resource> getResourcedefs() {
        return resourcedefs;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public SmartObject instance(int id) {
        try {
            SmartObject clone = (SmartObject) this.clone();
            clone.setInstanceId(id);
            return clone;
        } catch (CloneNotSupportedException e) {
            return new SmartObject(this.id, this.name);
        }
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
