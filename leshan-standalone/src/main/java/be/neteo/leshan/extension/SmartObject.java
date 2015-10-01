package be.neteo.leshan.extension;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by pierre on 01/10/15.
 */
public class SmartObject {
    private int id;
    private String name;
    private Map<Integer, SmartObjectResource> resources = new HashMap();

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

    public Map<Integer, SmartObjectResource> getResources() {
        return resources;
    }

    public Set<Integer> getResourceIds() {
        return this.resources.keySet();
    }

    public void addResource(SmartObjectResource resource) {
        resources.put(resource.getId(), resource);
    }
}
