/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hcl.neo.cms.microservices.helpers;

import java.util.ArrayList;
import java.util.List;

import com.hcl.neo.cms.microservices.excel.schema_metadata.Attribute;
import com.hcl.neo.cms.microservices.excel.schema_metadata.Object;

/**
 *
 * @author Pankaj.Srivastava
 */
public class ProcessObject {

    private final Object object;
    private String id;
    private String objectName;
    private String objectType;
    private String path;
    public ProcessObject(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public String getId() {
        if(id == null){
            id = (String)getPropertyValue("object_id");
        }
        return id;        
    }
    
    public String getObjectName() {
        if(objectName == null){
            objectName = (String)getPropertyValue("title");
        }
        return objectName;               
    }

    public String getType() {
        if(objectType == null){
            objectType = (String)getPropertyValue("object_type");
        }
        return objectType;
    }    

    public List<String> getPropertyValueAsList(String propertyName) {
        List<Attribute> attrList = object.getAttribute();
        List<String> valueList = new ArrayList<>();
        for (Attribute attribute : attrList) {
            if (attribute.getName() != null && attribute.getName().equalsIgnoreCase(propertyName)) {
                valueList.add((String)attribute.getValue());
            }
        }
        return valueList;
    }

    public String getPropertyValue(String propertyName) {
        List<Attribute> attrList = object.getAttribute();
        for (Attribute attribute : attrList) {
            if (attribute.getName() != null && attribute.getName().equalsIgnoreCase(propertyName)) {
                if (attribute.getValue() != null) {
                    return attribute.getValue();
                }
            }
        }
        return null;
    }

    public String getFormat() {        
        if (getType() != null && getType().equalsIgnoreCase("Document")) {
            return "Document";
        } else if (getType() != null && getType().equalsIgnoreCase("Folder")) {
            return "Folder";
        } else {
            return "Custom Object";
        }
    }
    public String getPath() {        
        if(path == null){
            path = (String)getPropertyValue("object_path");
        }
        return path;
    }
}