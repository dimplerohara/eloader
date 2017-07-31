package com.hcl.dctm.data.impl;

import java.util.HashMap;
import java.util.Map;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.hcl.dctm.data.exceptions.DctmException;

class ObjectTypeDetails extends DctmImplBase{

	public ObjectTypeDetails(IDfSession session){
		super(session);
		this.typeDetail = new HashMap<String, AttrDetailList>();
	}

	public AttrDetailList getAttrDetail(String typeName) throws DctmException{
		try{
			if(!getTypeDetail().containsKey(typeName)){
				fetchDataForType(typeName);
			}
			return getTypeDetail().get(typeName);
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
	
	private void fetchDataForType(String typeName) throws DfException{
		IDfCollection col = null;
		try{
			col = execQuery(getDql(typeName));
			AttrDetailList attrList = new AttrDetailList();
			while(col.next()){
				AttrDetail attr = new AttrDetail();
				attr.setAttrName(col.getString("attr_name"));
				attr.setAttrType(col.getInt("attr_type"));
				attr.setRepeating(col.getBoolean("attr_repeating"));
				attr.setReadOnly(col.getBoolean("read_only"));
				attrList.addAttrDetail(attr);
			}
			getTypeDetail().put(typeName, attrList);
		}
		finally{
			closeCollection(col);
		}
	}
	
	private String getDql(String typeName){
		return "select distinct t.attr_name, t.attr_type, t.attr_repeating, d.read_only from dm_type t, dmi_dd_attr_info d where d.type_name=t.name and t.attr_name=d.attr_name and t.name='"+typeName+"' enable(row_based)";
		//return "SELECT attr_name, attr_type, attr_repeating FROM dm_type WHERE name='"+typeName+"' ENABLE(ROW_BASED)";
	}
	
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
		private int attrType;
		private boolean isRepeating;
		private boolean readOnly;
		public String getAttrName() {
			return attrName;
		}
		public void setAttrName(String attrName) {
			this.attrName = attrName;
		}
		public int getAttrType() {
			return attrType;
		}
		public void setAttrType(int attrType) {
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
			return "AttrDetail [attrName=" + attrName + ", attrType="
					+ attrType + ", isRepeating=" + isRepeating + ", readOnly="
					+ readOnly + "]";
		}
	}
}