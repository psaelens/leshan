package be.neteo.leshan.extension;

/**
 * Created by pierre on 01/10/15.
 */
public class SmartObjectResource {
    private int id;
    private String name;
    private boolean observe = false;

    public SmartObjectResource(int id, String value, boolean observe) {
        this.id = id;
        this.name = value;
        this.observe = observe;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isObserve() {
        return observe;
    }
}
