package org.ganeshatech05102021.ecommerce.model;

public class CategoryModel {
   private String name,url;

    public CategoryModel() {
    }

    public CategoryModel(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
