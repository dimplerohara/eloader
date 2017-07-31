package com.hcl.neo.cms.microservices.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hcl.neo.cms.microservices.logger.ServiceLogger;
import com.hcl.neo.cms.microservices.model.DctmAttrMapperObject;
import com.hcl.neo.eloader.common.JsonApi;

@Service
public class DctmObjectMapper {
	
	@Value("${search.attrMappingLocation}")
    private String attrMappingLocation;
	
	public Map<String, Object> toDctmAttributes(Map<String, Object> atributeMap){
		if(null == this.attrMapperObj){ this.attrMapperObj = getDctmAttrMap(); }
		
		Iterator<String> iterator = atributeMap.keySet().iterator();
		Map<String, Object> dctmAttrMap = new HashMap<String, Object>();
		String key = null;
		String dctmAttr = null;
		while(iterator.hasNext()){
			key = iterator.next();
			dctmAttr = toDctmAttribute(key);
			if(null != dctmAttr){
				dctmAttrMap.put(dctmAttr, atributeMap.get(key));
			}else{
				ServiceLogger.info(getClass(), key+ " attribute is not mapped to dctm attribute. Hence ignoring the same.");
			}
		}
		return dctmAttrMap;
	}

	public String toDctmAttribute(String attributeName) {
		if(null == this.attrMapperObj){ this.attrMapperObj = getDctmAttrMap();
		}
		return (null == attrMapperObj.getAttributeMap()) ? null : attrMapperObj.getAttributeMap().get(attributeName);
	}
	
	public String toCustomAttribute(String dctmAttribute){
		if(null == this.attrMapperObj){ this.attrMapperObj = getDctmAttrMap(); }
		Map<String, String> map = attrMapperObj.getAttributeMap();
		return (null == map) ? null : getKeyByValue(map, dctmAttribute);
	}

	@SuppressWarnings("deprecation")
	private DctmAttrMapperObject getDctmAttrMap(){
		File file = new File(attrMappingLocation);
		InputStream inputStream = null;
		try{
			if(file.exists()){
				inputStream = new FileInputStream(file);
				String fileContent = IOUtils.toString(inputStream);
				return JsonApi.fromJson(fileContent, DctmAttrMapperObject.class);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(null != inputStream){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new DctmAttrMapperObject();
	}
	
	private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
	
	private DctmAttrMapperObject attrMapperObj;

}
