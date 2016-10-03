package com.skylaneoptics.ipso;

/**
 * Represent a Smart Object Resource
 *
 * @author Pierre Saelens
 */
public class Resource {
    private int id;
    private String name;
    private String operations;
    private String instancetype;
    private boolean mandatory = false;
    private String type;
    private String range;
    private String units;
    private String description;

    public Resource() {
    }

    public Resource(int id, String value) {
        this();
        this.id = id;
        this.name = value;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperations() {
        return operations;
    }

    public void setOperations(String operations) {
        this.operations = operations;
    }

    public String getInstancetype() {
        return instancetype;
    }

    public void setInstancetype(String instancetype) {
        this.instancetype = instancetype;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
