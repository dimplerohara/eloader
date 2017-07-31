

package com.hcl.neo.dctm.microservices.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import com.hcl.neo.dctm.microservices.excel.schema_objecttype.ObjectTypeSet;
import com.hcl.neo.dctm.microservices.excel.schema_objecttype.PropertyInformation;
import com.hcl.neo.dctm.microservices.excel.schema_objecttype.TypeInformation;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)

public class ObjectTypeHelper {
	
    @Autowired
    private Jaxb2Marshaller jaxb2Marshaller2;
    
    private ObjectTypeSet objectTypeSet = null;
    
    @PostConstruct
    public void init(){
        objectTypeSet = (ObjectTypeSet) jaxb2Marshaller2.unmarshal(new StreamSource(getClass().getResourceAsStream("/xml/objectType.xml")));
    }

    public ObjectTypeSet getObjectTypeSet() {
        return objectTypeSet;
    }
    
    public List<String> allPropertiesName(String objectType){
        List<String> propertyNameList = new ArrayList<>();
        for(TypeInformation type:getObjectTypeSet().getTypeInformation()){
            if(type.getName().equalsIgnoreCase(objectType)){
                for(PropertyInformation property:type.getPropertyInformation()){
                    propertyNameList.add(property.getName());
                }
                break;
            }
        }        
        return propertyNameList;
    }
    public String allCommaSeperatedPropNames(String objectType){
        List<String> propertyNameList = allPropertiesName(objectType);
        return StringUtils.join(propertyNameList, ',');
    }
    public List<PropertyInformation> getProperties(String objectType){
        List<PropertyInformation> propertyList = new ArrayList<>();
        for(TypeInformation type:getObjectTypeSet().getTypeInformation()){
            if(type.getName().equalsIgnoreCase(objectType)){
                for(PropertyInformation property:type.getPropertyInformation()){
                    propertyList.add(property);
                }
                break;
            }
        }
        return propertyList;
    }
    
    public Map<String,String> nameToTypeMap(Map<String,Integer> indexToNameMap){
        Map <String,String> nameToTypeMap = new HashMap<>();
        for(String key :indexToNameMap.keySet()){
            nameToTypeMap.put(key, getType(key));
        }
        return nameToTypeMap;
    }
    public String getType(String propertyName){
        for(TypeInformation typeInformation:getObjectTypeSet().getTypeInformation()){
            for(PropertyInformation propertyInformation:typeInformation.getPropertyInformation()){
                if(propertyInformation.getName()!=null && propertyInformation.getName().equalsIgnoreCase(propertyName)){
                    return propertyInformation.getDatatype().value();
                }
            }
        }
        return null;
    }
}