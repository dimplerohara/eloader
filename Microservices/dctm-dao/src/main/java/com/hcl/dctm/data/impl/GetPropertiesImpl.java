package com.hcl.dctm.data.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.documentum.fc.client.IDfEnumeration;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.impl.objectpath.ObjectPath;
import com.documentum.fc.common.IDfAttr;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.ObjectIdentity;

class GetPropertiesImpl extends DctmImplBase {

	public GetPropertiesImpl(IDfSession session) throws DctmException{
		super(session);
	}
	
	public Map<String, Object> getPropertiesByQualification(String qualification) throws DctmException{
		try{
			Map<String, Object> properties = new HashMap<String, Object>();
			IDfPersistentObject object = getSession().getObjectByQualification(qualification);
			if(null == object) return properties;
			int attrCount = object.getAttrCount();
			for(int index=0; index<attrCount; index++){
				IDfAttr attr = object.getAttr(index);
				if(attr.isRepeating()){
					ArrayList<String> repeatingValue = new ArrayList<String>();
					int valCount = object.getValueCount(attr.getName());
					for(int valIndex=0; valIndex<valCount; valIndex++){
						repeatingValue.add(object.getRepeatingString(attr.getName(), valIndex));
					}
					properties.put(attr.getName(), repeatingValue);
				}
				else{
					properties.put(attr.getName(), object.getString(attr.getName()));
				}
			}
			return properties;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
	
	public Map<String, String> getPropertiesByIdentity(ObjectIdentity identity) throws DctmException{
		try{
			Map<String, String> properties = new HashMap<String, String>();
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IDfPersistentObject object = getObjectFromIdentity.getObject(identity);
			if(null == object) return properties;
			int attrCount = object.getAttrCount();
			for(int index=0; index<attrCount; index++){
				IDfAttr attr = object.getAttr(index);
				//logger.info(attr.getName());
				if(attr.isRepeating()){
					/*ArrayList<String> repeatingValue = new ArrayList<String>();
					int valCount = object.getValueCount(attr.getName());
					for(int valIndex=0; valIndex<valCount; valIndex++){
						repeatingValue.add(object.getRepeatingString(attr.getName(), valIndex));
					}*/
					properties.put(attr.getName(), object.getAllRepeatingStrings(attr.getName(), "|"));
				}
				else{
					properties.put(attr.getName(), object.getString(attr.getName()));
				}
			}
			properties.put("r_object_id", object.getObjectId().getId());
			properties.put("r_folder_path", object.getAllRepeatingStrings("r_folder_path", "|"));
			return properties;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
	
	public String getObjectPaths(ObjectIdentity identity)throws DctmException{
		String path = "";
		try{
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IDfPersistentObject object = getObjectFromIdentity.getObject(identity);
			if(null == object) return "/";
			IDfEnumeration objectPaths = getSession().getObjectPaths(object.getObjectId());
            while(objectPaths.hasMoreElements()){
                ObjectPath tempPath = (ObjectPath)objectPaths.nextElement();
                String tempPathStr = tempPath.getFullPath();
                if(tempPathStr != null && !tempPathStr.isEmpty()){
                    tempPathStr =tempPathStr+"/"+object.getValue("object_name");
                    if(path.isEmpty()){
                        path = tempPathStr;
                    } else{
                        path = path +","+ tempPath;
                    }
                }
            }
		}catch(Throwable e){
			throw new DctmException(e);
		}
		return path;
	}
}