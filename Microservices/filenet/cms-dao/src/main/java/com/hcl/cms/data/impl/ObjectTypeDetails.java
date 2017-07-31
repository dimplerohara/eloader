package com.hcl.cms.data.impl;

import java.util.HashMap;
import java.util.Map;

import com.filenet.api.collection.PropertyDescriptionList;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescription;
import com.hcl.cms.data.constants.Constants;


/**
 * Get attributes implementation Class
 * @author sakshi_ja
 *
 */
public class ObjectTypeDetails extends CmsImplBase{

	/**
	 * @param con
	 */
	public ObjectTypeDetails(Connection con){
		super(con);
		this.typeDetail = new HashMap<String, AttrDetailList>();
	}

	/**
	 * Method to return attribute detail for particular filenet object type
	 * @param typeName
	 * @param objectStoreName
	 * @return
	 * @throws Exception
	 */
	public AttrDetailList getAttrDetail(String typeName,String objectStoreName) throws Exception{
		try{
			if(!getTypeDetail().containsKey(typeName)){
				fetchDataForType(typeName,objectStoreName);
			}
			return getTypeDetail().get(typeName);
		}
		catch(Throwable e){
			throw new Exception(e);
		}
	}

	/**
	 * Method to get attribute detail for particular filenet object type
	 * @param typeName
	 * @param objectStoreName
	 * @throws Exception
	 */
	private void fetchDataForType(String typeName,String objectStoreName) throws Exception{
		try{
			Domain domain = Factory.Domain.fetchInstance(getSession(), null, null);	
			ObjectStore store=Factory.ObjectStore.fetchInstance(domain,objectStoreName, null);
			ClassDescription cs=Factory.ClassDescription.fetchInstance(store ,typeName,null);

			PropertyDescriptionList propDetails=cs.get_PropertyDescriptions();

			AttrDetailList attrList = new AttrDetailList();
			for(int count=0;count<propDetails.size();count++){
				AttrDetail attr = new AttrDetail();
				PropertyDescription p= (PropertyDescription) propDetails.get(count);
				attr.setAttrName(p.get_Name());
				attr.setAttrActualName(p.get_SymbolicName());
				attr.setAttrType(p.get_DataType().toString());
				String value=p.get_Cardinality().toString();
				if(value.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_SINGLE)){
					attr.setRepeating(false);
				}
				if(value.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_LIST) || value.equalsIgnoreCase(Constants.ATTR_DATA_TYPE_ENUM)){
					attr.setRepeating(true);
				}

				attr.setReadOnly(p.get_IsReadOnly());
				attrList.addAttrDetail(attr);
				attr.setAttrName(p.get_SymbolicName());
				attrList.addAttrDetail(attr);
			}
			getTypeDetail().put(typeName, attrList);
		}catch(Exception e){

		}
	}

	/*private String getDql(String typeName){
		return "select distinct t.attr_name, t.attr_type, t.attr_repeating, d.read_only from dm_type t, dmi_dd_attr_info d where d.type_name=t.name and t.attr_name=d.attr_name and t.name='"+typeName+"' enable(row_based)";
		//return "SELECT attr_name, attr_type, attr_repeating FROM dm_type WHERE name='"+typeName+"' ENABLE(ROW_BASED)";
	}*/

	private Map<String, AttrDetailList> getTypeDetail() {
		return typeDetail;
	}

	private Map<String, AttrDetailList> typeDetail;

	public class AttrDetailList{
		private Map<String, AttrDetail> attrDetail;
		public AttrDetailList(){
			this.attrDetail = new HashMap<String, AttrDetail>(); 
		}
		public void addAttrDetail(AttrDetail attrDetail){
			this.attrDetail.put(attrDetail.getAttrName(), attrDetail);
		}
		public AttrDetail getAttrDetail(String attrName) {
			return this.attrDetail.get(attrName);
		}
	}

	public class AttrDetail{
		private String attrName;
		private String attrActualName;
		public String getAttrActualName() {
			return attrActualName;
		}
		public void setAttrActualName(String attrActualName) {
			this.attrActualName = attrActualName;
		}
		private String attrType;
		private boolean isRepeating;
		private boolean readOnly;
		public String getAttrName() {
			return attrName;
		}
		public void setAttrName(String attrName) {
			this.attrName = attrName;
		}
		public String getAttrType() {
			return attrType;
		}
		public void setAttrType(String attrType) {
			this.attrType = attrType;
		}
		public boolean isRepeating() {
			return isRepeating;
		}
		public void setRepeating(boolean isRepeating) {
			this.isRepeating = isRepeating;
		}
		public boolean isReadOnly() {
			return readOnly;
		}
		public void setReadOnly(boolean readOnly) {
			this.readOnly = readOnly;
		}
		@Override
		public String toString() {
			return "AttrDetail [attrName=" + attrName + ", attrActualName=" + attrActualName + ", attrType=" + attrType
					+ ", isRepeating=" + isRepeating + ", readOnly=" + readOnly + "]";
		}
	}
}