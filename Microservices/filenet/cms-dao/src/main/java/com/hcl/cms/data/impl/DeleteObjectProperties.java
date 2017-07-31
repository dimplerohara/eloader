package com.hcl.cms.data.impl;


import java.util.Date;
import java.util.List;

import com.filenet.api.collection.BinaryList;
import com.filenet.api.collection.BooleanList;
import com.filenet.api.collection.DateTimeList;
import com.filenet.api.collection.Float64List;
import com.filenet.api.collection.IdList;
import com.filenet.api.collection.Integer32List;
import com.filenet.api.collection.StringList;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
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
import com.hcl.cms.data.impl.ObjectTypeDetails.AttrDetail;
import com.hcl.cms.data.impl.ObjectTypeDetails.AttrDetailList;

/**
 * Delete object metadata implementation Class
 * @author sakshi_ja
 *
 */
class DeleteObjectProperties extends CmsImplBase{

	/**
	 * @param con
	 */
	public DeleteObjectProperties(Connection con) {
		super(con);
		this.objectTypeDetail = new ObjectTypeDetails(con);
	}

	/**
	 * Method to delete property values of the documents or folder
	 * @param object
	 * @param objectType
	 * @param properties
	 * @param objectStoreName
	 * @return
	 * @throws Exception
	 */
	public boolean deleteProperties(IndependentObject object,  String objectType,List<String> properties, String objectStoreName) throws Exception{
		try{
			boolean status = false;

			AttrDetailList attrDetailList = getObjectTypeDetail().getAttrDetail(objectType,objectStoreName);
			for(int count=0;count<properties.size();count++){
				AttrDetail attrDetail = attrDetailList.getAttrDetail(properties.get(count));
				if( null == attrDetail || attrDetail.isReadOnly() ) {
					continue;
				}else{
					String attrName = properties.get(count);
					String attrType = attrDetail.getAttrType();
					attrName=attrDetail.getAttrActualName();
					if(attrDetail.isRepeating()){
						removeRepeatingAttr(object, attrName,attrType);
					}else{
						removeSingleAttr(object, attrName,attrType);
					}
				}

				status = true;
			}			
			return status;
		}
		catch(Throwable e){
			throw new Exception(e);
		}
	}	

	/**
	 * Method to remove single attribute values
	 * @param object
	 * @param attrName
	 * @param attrType
	 * @throws Throwable
	 */
	private void removeSingleAttr(IndependentObject object, String attrName,String attrType) throws Throwable{
		try{
			if(null == attrName) return ;
			if(object instanceof Document){
				object=(Document)object;
			}else{
				object=(Folder)object;
			}
			Properties props=object.getProperties();

			if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_STRING)){
				props.putValue(attrName, "");
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_BOOLEAN)){
				props.putValue(attrName, false);
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_DOUBLE)){
				props.putValue(attrName, Double.valueOf(0));
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_BINARY)){
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_LONG)){
				props.putValue(attrName, Integer.valueOf(0));
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_DATE)){		
				Date d=null;
				props.putValue(attrName, d);
			}
			else{ 
				props.putValue(attrName, "");
			}
		}catch(Exception e){
		}
	}

	/**
	 * Method to remove repeating attribute values
	 * @param object
	 * @param attrName
	 * @param attrType
	 * @throws Throwable
	 */
	protected void removeRepeatingAttr(IndependentObject object, String attrName, String attrType) throws Throwable{
		try{
			if(null == attrName) return ;
			if(object instanceof Document){
				object=(Document)object;
			}else{
				object=(Folder)object;
			}

			Properties props=object.getProperties();
			if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_STRING)){

				StringList list=new StringListImpl(object.getProperties().getStringListValue(attrName));
				list.clear();
				props.putValue(attrName, list.toString());
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_BOOLEAN)){

				BooleanList  list=new BooleanListImpl(object.getProperties().getBooleanListValue(attrName));
				list.clear();
				props.putValue(attrName, list);
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_DOUBLE)){

				Float64List list=new Float64ListImpl(object.getProperties().getFloat64ListValue(attrName));
				list.clear();
				props.putValue(attrName, list);
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_BINARY)){
				BinaryList list=new BinaryListImpl(object.getProperties().getBinaryListValue(attrName));
				list.clear();
				props.putValue(attrName, list);
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_GUID)){

				IdList list=new IdListImpl(object.getProperties().getIdListValue(attrName));
				list.clear();
				props.putValue(attrName, list);
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_LONG)){
				Integer32List list=new Integer32ListImpl(object.getProperties().getInteger32ListValue(attrName));
				list.clear();
				props.putValue(attrName, list.toString());
			}
			else if(attrType.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_DATE)){
				DateTimeList list =new DateTimeListImpl(object.getProperties().getDateTimeListValue(attrName));
				list.clear();
				props.putValue(attrName, list);
			}
		}catch(Exception e){
		}
	}
	public ObjectTypeDetails getObjectTypeDetail() {
		return objectTypeDetail;
	}

	private ObjectTypeDetails objectTypeDetail;
}
