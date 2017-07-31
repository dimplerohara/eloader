package com.hcl.cms.data.impl;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.filenet.api.collection.BinaryList;
import com.filenet.api.collection.BooleanList;
import com.filenet.api.collection.DateTimeList;
import com.filenet.api.collection.StringList;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.collection.Float64List;
import com.filenet.api.collection.IdList;
import com.filenet.api.collection.Integer32List;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.property.Properties;
import com.filenet.apiimpl.collection.BinaryListImpl;
import com.filenet.apiimpl.collection.BooleanListImpl;
import com.filenet.apiimpl.collection.DateTimeListImpl;
import com.filenet.apiimpl.collection.Float64ListImpl;
import com.filenet.apiimpl.collection.IdListImpl;
import com.filenet.apiimpl.collection.Integer32ListImpl;
import com.filenet.apiimpl.collection.StringListImpl;
import com.hcl.cms.data.constants.Constants;
import com.hcl.cms.data.exceptions.CmsException;
import com.hcl.cms.data.impl.ObjectTypeDetails.AttrDetail;
import com.hcl.cms.data.impl.ObjectTypeDetails.AttrDetailList;
import com.hcl.cms.data.impl.ObjectTypeDetails;

/**
 * Set object Properties implementation Class
 * @author sakshi_ja
 *
 */
class SetObjectProperties extends CmsImplBase{

	/**
	 * @param con
	 */
	public SetObjectProperties(Connection con) {
		super(con);

		this.objectTypeDetail = new ObjectTypeDetails(con);
	}

	/**
	 * Inital Method to set attribute values
	 * @param object
	 * @param properties
	 * @param objectStoreName
	 * @return
	 * @throws CmsException
	 */
	public boolean setProperties(IndependentObject object, Map<String, Object> properties, String objectStoreName) throws CmsException{
		try{
			boolean status=false;
			if(object instanceof Document){
				status = setProperties(object,Constants.DEFAULT_DOCUMENT_TYPE, properties,objectStoreName);
				return status;
			}else{
				status = setProperties(object, Constants.DEFAULT_FOLDER_TYPE, properties,objectStoreName);
				return status;	
			}
		}
		catch(Throwable e){
			throw new CmsException(e);
		}
	}

	/**
	 * Method to set  attribute values
	 * @param object
	 * @param objectType
	 * @param properties
	 * @param objectStoreName
	 * @return
	 * @throws CmsException
	 */
	public boolean setProperties(IndependentObject object, String objectType, Map<String, Object> properties, String objectStoreName) throws CmsException{
		try{
			boolean status = false;

			AttrDetailList attrDetailList = getObjectTypeDetail().getAttrDetail(objectType,objectStoreName);
			Set<String> attrSet = properties.keySet();
			for(String attrName : attrSet){
				AttrDetail attrDetail = attrDetailList.getAttrDetail(attrName);
				if( null == attrDetail || attrDetail.isReadOnly() ) continue;

				Object attrValue = properties.get(attrName);
				String attrType = attrDetail.getAttrType();
				attrName=attrDetail.getAttrActualName();
				if(attrDetail.isRepeating()){

					if(attrValue!=null){
						String attrValue1=(String)attrValue;
						String[] attrArray=attrValue1.split(",", attrValue1.length()); 
						if(attrArray.length>0){
							List<String> lstAttrVal = new ArrayList<>();
							for(int count=0;count<attrArray.length;count++){
								lstAttrVal.add(attrArray[count]);
							}

							attrValue=lstAttrVal;
						}                    
					}

					List<Object> attrValueList = null; 
					Properties props=object.getProperties();
					StringList list=null;
					if(attrValue instanceof List){
						list = props.getStringListValue(attrName);
						list.clear();
						attrValueList = (List<Object>) attrValue;
					}
					else{
						setSingleAttrValue(object, attrName, attrValue, attrType);
					}

					if(attrValueList.size() > 0){
						for(int index=0; index<attrValueList.size(); index++){
							Object value = attrValueList.get(index);
							if(isNotNull(value) && isNotNull(value.toString()))
								list.add(attrValueList.get(index));
						}
					}	

					setRepeatingAttrValue(object, attrName, list, attrType);
				}
				else{

					setSingleAttrValue(object, attrName, attrValue, attrType);
				}
				status = true;
			}

			return status;
		}
		catch(Throwable e){
			throw new CmsException(e);
		}
	}

	/**
	 * Method to set single attribute values
	 * @param object
	 * @param attrName
	 * @param attrValue
	 * @param attrType
	 */
	private void setSingleAttrValue(IndependentObject object, String attrName, Object attrValue, String attrType){
		try{
			if(null == attrValue) return;
			if(object instanceof Document){
				object=(Document)object;
			}else{
				object=(Folder)object;
			}
			Properties props=object.getProperties();

			if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_STRING)){
				props.putValue(attrName, attrValue.toString());
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_BOOLEAN)){
				props.putValue(attrName, Boolean.valueOf(attrValue.toString()));
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_DOUBLE)){
				props.putValue(attrName, Double.valueOf(attrValue.toString()));
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_BINARY)){
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_GUID)){
				props.putValue(attrName, attrValue.toString());
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_LONG)){
				props.putValue(attrName, Integer.valueOf(attrValue.toString()));
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_DATE)){

				DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
				Date date = (Date)formatter.parse(attrValue.toString());
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				String formatedDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" +         cal.get(Calendar.YEAR);	
				props.putValue(attrName, new Date(formatedDate));
			}

			else{ 
				props.putValue(attrName, attrValue.toString());
			}
		}catch(Exception e){

		}
	}

	/**
	 * Method to set repeating attribute values
	 * @param object
	 * @param attrName
	 * @param attrValue
	 * @param attrType
	 * @throws Throwable
	 */
	private void setRepeatingAttrValue(IndependentObject object, String attrName, Object attrValue, String attrType) throws Throwable{
		try{
			if(null == attrValue) return;
			if(object instanceof Document){
				object=(Document)object;
			}else{
				object=(Folder)object;
			}

			List<Object> attrValueList = null; 
			if(attrValue instanceof List){
				attrValueList = (List<Object>) attrValue;
			}
			Properties props=object.getProperties();
			if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_STRING)){

				StringList list=new StringListImpl(attrValueList);
				props.putValue(attrName, list);
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_BOOLEAN)){

				BooleanList  list=new BooleanListImpl(attrValueList);
				props.putValue(attrName, list);
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_DOUBLE)){

				Float64List list=new Float64ListImpl(attrValueList);
				props.putValue(attrName, list);
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_BINARY)){
				BinaryList list=new BinaryListImpl(attrValueList);
				props.putValue(attrName, list);
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_GUID)){

				IdList list=new IdListImpl(attrValueList);
				props.putValue(attrName, list);
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_LONG)){
				Integer32List list=new Integer32ListImpl(attrValueList);
				props.putValue(attrName, list);
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_DATE)){
				DateTimeList list =new DateTimeListImpl(attrValueList);
				for(int count=0;count<attrValueList.size();count++){
					DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
					Date date = (Date)formatter.parse(attrValueList.get(count).toString());
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					String formatedDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" +         cal.get(Calendar.YEAR);	
					list.add(new Date(formatedDate));
				}
				props.putValue(attrName, list);
			}
			else{ 
				props.putValue(attrName, attrValue.toString());
			}
		}catch(Exception e){

		}
	}

	public ObjectTypeDetails getObjectTypeDetail() {
		return objectTypeDetail;
	}

	private ObjectTypeDetails objectTypeDetail;
}
