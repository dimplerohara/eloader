package com.hcl.dctm.data.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfTime;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.impl.ObjectTypeDetails.AttrDetail;
import com.hcl.dctm.data.impl.ObjectTypeDetails.AttrDetailList;

class SetObjectProperties extends DctmImplBase{

	public SetObjectProperties(IDfSession session) {
		super(session);
		this.objectTypeDetail = new ObjectTypeDetails(session);
	}

	protected void addRepeating(IDfSysObject object, String attrName, List<String> values) throws Throwable{
		for(String value: values){
			object.appendString(attrName, value);
		}
	}
	
	protected void setRepeating(IDfSysObject object, String attrName, List<String> values) throws DfException{
		for(int index=0; index<values.size(); index++){
			object.setRepeatingString(attrName, index, values.get(index));
		}
	}
	
	public boolean setProperties(IDfSysObject object, Map<String, Object> properties) throws DctmException{
		try{
			boolean status = setProperties(object, object.getTypeName(), properties);
			return status;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
	
	public boolean setProperties(IDfPersistentObject object, String objectType, Map<String, Object> properties) throws DctmException{
		try{
			boolean status = false;
			AttrDetailList attrDetailList = getObjectTypeDetail().getAttrDetail(objectType);
			Set<String> attrSet = properties.keySet();
			for(String attrName : attrSet){
				AttrDetail attrDetail = attrDetailList.getAttrDetail(attrName);

				if( null == attrDetail || attrDetail.isReadOnly() ) continue;
				
				Object attrValue = properties.get(attrName);
				int attrType = attrDetail.getAttrType();
				if(attrDetail.isRepeating()){
					setRepeatingAttrValue(object, attrName, attrValue, attrType);
				}
				else{
					setSingleAttrValue(object, attrName, attrValue, attrType);
				}
				status = true;
			}
			
			return status;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
	
	private void setSingleAttrValue(IDfPersistentObject object, String attrName, Object attrValue, int attrType) throws Throwable{
		
		if(null == attrValue) return;
		
		if(attrType == IDfType.DF_STRING){
			object.setString(attrName, attrValue.toString());
		}
		else if(attrType == IDfType.DF_BOOLEAN){
			object.setBoolean(attrName, Boolean.valueOf(attrValue.toString()));
		}
		else if(attrType == IDfType.DF_DOUBLE){
			object.setDouble(attrName, Double.valueOf(attrValue.toString()));
		}
		else if(attrType == IDfType.DF_ID){
			object.setId(attrName, new DfId(attrValue.toString()));
		}
		else if(attrType == IDfType.DF_INTEGER){
			object.setInt(attrName, Integer.valueOf(attrValue.toString()));
		}
		else if(attrType == IDfType.DF_TIME){
			object.setTime(attrName, new DfTime(attrValue.toString(), DfTime.DF_TIME_PATTERN44));
		}
		else{ //(attrType == IDfType.DF_STRING || attrType == IDfType.DF_UNDEFINED)
			object.setString(attrName, attrValue.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setRepeatingAttrValue(IDfPersistentObject object, String attrName, Object attrValue, int attrType) throws Throwable{
		
		List<Object> attrValueList = null; 
		if(attrValue instanceof List){
			attrValueList = (List<Object>) attrValue;
		}
		else{
			setSingleAttrValue(object, attrName, attrValue, attrType);
			return;
		}
		
		if(attrValueList.size() > 0){
			object.removeAll(attrName);
		}
		
		Object value;
		if(attrType == IDfType.DF_STRING){
			for(int index=0; index<attrValueList.size(); index++){
				value = attrValueList.get(index);
				if(isNotNull(value) && isNotNull(value.toString())) object.setRepeatingString(attrName, index, value.toString());
			}
		}
		else if(attrType == IDfType.DF_BOOLEAN){
			for(int index=0; index<attrValueList.size(); index++){
				value = attrValueList.get(index);
				if(isNotNull(value) && isNotNull(value.toString())) object.setRepeatingBoolean(attrName, index, Boolean.valueOf(value.toString()));
			}
		}
		else if(attrType == IDfType.DF_DOUBLE){
			for(int index=0; index<attrValueList.size(); index++){
				value = attrValueList.get(index);
				if(isNotNull(value) && isNotNull(value.toString())) object.setRepeatingDouble(attrName, index, Double.valueOf(value.toString()));
			}
		}
		else if(attrType == IDfType.DF_ID){
			for(int index=0; index<attrValueList.size(); index++){
				value = attrValueList.get(index);
				if(isNotNull(value) && isNotNull(value.toString())) object.setRepeatingId(attrName, index,  new DfId(value.toString()));
			}
		}
		else if(attrType == IDfType.DF_INTEGER){
			for(int index=0; index<attrValueList.size(); index++){
				value = attrValueList.get(index);
				if(isNotNull(value) && isNotNull(value.toString())) object.setRepeatingInt(attrName, index, Integer.valueOf(value.toString()));
			}
		}
		else if(attrType == IDfType.DF_TIME){
			for(int index=0; index<attrValueList.size(); index++){
				value = attrValueList.get(index);
				if(isNotNull(value) && isNotNull(value.toString())) object.setRepeatingTime(attrName, index, new DfTime(value.toString(), DfTime.DF_TIME_PATTERN44));
			}
		}
		else{ //(attrType == IDfType.DF_STRING || attrType == IDfType.DF_UNDEFINED)
			for(int index=0; index<attrValueList.size(); index++){
				value = attrValueList.get(index);
				if(isNotNull(value) && isNotNull(value.toString())) object.setRepeatingString(attrName, index, value.toString());
			}
		}
	}
	
	public ObjectTypeDetails getObjectTypeDetail() {
		return objectTypeDetail;
	}

	private ObjectTypeDetails objectTypeDetail;
}
