package com.hcl.cms.data.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.filenet.api.collection.PropertyDescriptionList;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescription;
import com.filenet.api.property.Properties;
import com.hcl.cms.data.constants.Constants;
import com.hcl.cms.data.params.ObjectIdentity;

/**
 * get metadata implementation Class
 * @author sakshi_ja
 *
 */
class GetPropertiesImpl extends CmsImplBase {

	/**
	 * @param con
	 */
	public GetPropertiesImpl(Connection con) {
		super(con);
	}
	
	
	
	/**
	 * Method to fetch all properties of a object in the map 
	 * @param identity
	 * @param objectStoreName
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getPropertiesByIdentity(ObjectIdentity identity,String objectStoreName) throws Exception{
		try{
			Map<String, String> properties = new HashMap<String, String>();
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IndependentObject object = getObjectFromIdentity.getObject(identity,objectStoreName);
			if(null == object) return properties;

			List<String> attrList=getPropertiesByType(identity.getObjectType(),objectStoreName);
			Properties props=null;
			if(object instanceof Document){
				Document doc=(Document)object;
				props=doc.getProperties();
				
			}else if(object instanceof Folder){
				Folder folder=(Folder)object;
				props=folder.getProperties();
			}
			for(int count=0;count<attrList.size();count++){
				String propName=attrList.get(count);
				Object propValue=props.getObjectValue(attrList.get(count));
				String strPropValue="";
				if(propValue!=null){
					strPropValue=propValue.toString();
				}
				if(props.isPropertyPresent(propName)){
					properties.put(propName,strPropValue);
				}
			}		
			return properties;
		}
		catch(Throwable e){
			throw new Exception(e);
		}
	}
	
	/**
	 * Method to fetch properties according to its type
	 * @param objectType
	 * @param objectStoreName
	 * @return
	 * @throws Exception
	 */
	public List<String> getPropertiesByType(String objectType,String objectStoreName) throws Exception{
		try{
		
			Domain domain = Factory.Domain.fetchInstance(getSession(), null, null);	
			ObjectStore store=Factory.ObjectStore.fetchInstance(domain,objectStoreName, null);
			ClassDescription cs=Factory.ClassDescription.fetchInstance(store ,objectType,null);

			PropertyDescriptionList propDetails=cs.get_PropertyDescriptions();

			List<String> attrList = new ArrayList<>();
			for(int count=0;count<propDetails.size();count++){

				PropertyDescription p= (PropertyDescription) propDetails.get(count);
				if(!p.get_DataType().toString().equalsIgnoreCase(Constants.ATTR_DATA_TYPE_OBJECT))
				attrList.add(p.get_SymbolicName());
				
			}

			return attrList;
		}catch(Throwable e){
			throw new Exception(e);
		}

	}
	
	
}