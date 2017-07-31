package com.hcl.neo.eloader.microservices.model;

import com.google.gson.Gson;

public class BusinessGroup {

    private String name;
    private String displayName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

 

    @Override
    public String toString() {
        return "BusinessGroup{" + "name=" + name + ", displayName=" + displayName +'}';
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
