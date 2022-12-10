package org.ganeshatech05102021.ecommerce.model;

public class adminSerchModel {
    private String name,id;

    public adminSerchModel() {
    }

    public adminSerchModel(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
