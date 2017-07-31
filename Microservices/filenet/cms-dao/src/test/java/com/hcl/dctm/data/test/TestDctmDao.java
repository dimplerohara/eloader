package com.hcl.dctm.data.test;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.collection.ClassDescriptionSet;
import com.filenet.api.collection.DependentObjectList;
import com.filenet.api.collection.PropertyDescriptionList;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.collection.StringList;
import com.filenet.api.constants.AccessRight;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Factory.AccessPermission;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescription;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import com.filenet.apiimpl.collection.StringListImpl;
import com.filenet.apiimpl.core.AccessPermissionImpl;
import com.filenet.apiimpl.core.ClassDescriptionImpl;
import com.filenet.wcm.api.BaseObject;
import com.hcl.cms.data.CmsDao;
import com.hcl.cms.data.impl.CmsDaoFactory;
import com.hcl.cms.data.params.CmsSessionObjectParams;
import com.hcl.cms.data.params.CmsSessionParams;
import com.hcl.cms.data.params.ImportContentParams;
import com.hcl.cms.data.params.ObjectIdentity;
import com.hcl.cms.data.params.OperationStatus;
import com.hcl.cms.data.params.UpdateObjectParam;
import com.hcl.cms.data.session.CEConnectionManager;

public class TestDctmDao {

	public static void main(String[] args) {
		try{
			doSomething();
		}
		catch(Throwable e){
			e.printStackTrace();
		}
	}
	
	private static final int ACCESS_REQUIRED =  AccessRight.READ_AS_INT + AccessRight.WRITE_AS_INT + AccessRight.VIEW_CONTENT_AS_INT + AccessRight.LINK_AS_INT + AccessRight.CREATE_INSTANCE_AS_INT + AccessRight.CHANGE_STATE_AS_INT + AccessRight.READ_ACL_AS_INT + AccessRight.UNLINK_AS_INT;

	private static PropertyFilter PF=null;
	public static void doSomething() throws Throwable{
		System.out.println("Insi1444de do simething");

		
		/*Import Code	
		CmsDao filenetDao = null;
		List<String> srcPathList = new ArrayList<String>();
		srcPathList.add("H:\\opt\\customer\\dctm\\apps\\ftpdropbox\\work\\INGESTION\\20170509\\289\\import\\1");
		//srcPathList.add("H:\\opt\\customer\\dctm\\apps\\ftpdropbox\\work\\INGESTION\\20170509\\289\\import\\Again Uplaod");
		
		ImportContentParams params=new ImportContentParams();
		ObjectIdentity ob=new ObjectIdentity();
		ob.setObjectPath("/Test");
		CmsSessionParams sessionParams = toCmsSessionParams();
		filenetDao = CmsDaoFactory.createCmsDao();
		filenetDao.setSessionParams(sessionParams);
		params.setSrcPathList(srcPathList);
		params.setDestFolder(ob);
		params.setRepository("ECM");
		OperationStatus status = filenetDao.importOperation(params);*/
		
		
		
	
		Document doc=null;
		Connection con = Factory.Connection.getConnection("http://10.137.186.123:9080/wsi/FNCEWS40MTOM");
        System.out.println("Connection Created"+con);
        Subject subject = UserContext.createSubject(con, "fnadmin", "Hello123", "FileNetP8WSI");
		UserContext.get().pushSubject(subject);
		Domain domain = Factory.Domain.fetchInstance(con, null, null);	
		System.out.println("Domain Fetched");
		ObjectStore store=Factory.ObjectStore.fetchInstance(domain, "SAKOS", null);
		System.out.println("Trying to read folder");
		/*String mySQLString = "SELECT s.DocumentTitle,s.Id,s.DateCreated,s.Creator,s.Versions FROM Document As s "
		        + "INNER JOIN ContentSearch cs ON d.This = cs.QueriedObject "
		        + "WHERE CONTAINS(Content, 'FileNet')"; 
		*/
		//String mySQLString="SELECT s.DocumentTitle,s.Id,s.DateCreated,s.Creator,s.Versions FROM Document AS s WITH EXCLUDESUBCLASSES INNER JOIN ContentSearch ON s.This = ContentSearch.QueriedObject WHERE DocumentTitle='5june' AND CONTAINS(Content, '5june') ORDER BY s.DateCreated";

		String string = "10/26/2012";
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM/DD/YYYY");
		Date now=sdf1.parse(string);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T000000Z'");
		String content=sdf.format(now);
		String mySQLString = "SELECT DocumentTitle Id FROM Document d "
		        + "Where DateLastModified > "+content;
		 SearchSQL sqlObject = new SearchSQL(mySQLString);
	     /*// sqlObject.setDistinct();
	     String selectList = "s.Id, s.Name, s.DateCreated";
	     sqlObject.setSelectList(selectList);
	     String symbolicClassName = "Document";
	     String aliasName = "s";
	     boolean includeSubclasses = false;
	     sqlObject.setFromClauseInitialValue(symbolicClassName, aliasName, includeSubclasses);
	     String whereClause = "DocumentTitle LIKE '%" + "a" + "%'";
	     if (symbolicClassName == "Folder") {
	            whereClause = "FolderName LIKE '%" + "a" + "%'";
	     }
	     sqlObject.setWhereClause(whereClause);
	     String orderByClause = "s.DateCreated";
	     sqlObject.setOrderByClause(orderByClause);
	     */
	     List<Map<String, String>> result = new ArrayList<Map<String, String>>();
	        SearchScope searchScope = new SearchScope(store);
	        RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
	        Iterator<?> search = rowSet.iterator();
	        while (search.hasNext()) {
	               RepositoryRow rr = (RepositoryRow) search.next();
	               Properties properties = rr.getProperties();
	               Iterator<?> iterProps = properties.iterator();
	               // System.out.print("\n");
	               Map<String, String> item = new HashMap<String, String>();
	               while (iterProps.hasNext()) {
	                     Property prop = (Property) iterProps.next();
	                     // System.out.print(prop.getPropertyName());
	                     if (prop.getObjectValue() != null)
	                            item.put(prop.getPropertyName(), prop.getObjectValue().toString());

	                     // System.out.print(" | " + prop.getObjectValue() + ",\n");
	               }
	               result.add(item);
	        }
	        System.out.println("result2 : " + result);
	}

	


		//Folder a=Factory.Folder.fetchInstance(store,"/Test/23", null);
		//System.out.println("Folder is-->"+a.getProperties().getObjectValue("Id").toString());
		
		/*ClassDescription cs=Factory.ClassDescription.fetchInstance(store , "Document",null);

		 PropertyDescriptionList l=cs.get_PropertyDescriptions();
		 for(int i=0;i<l.size();i++){
			  PropertyDescription p= (PropertyDescription) l.get(i);
			  //if(!p.get_IsReadOnly()){
				  System.out.println(p.get_Name()+" " +p.get_DataType()+" "+p.get_Cardinality()+" "+p.get_IsReadOnly()+" "+p.get_SymbolicName()	+" "+p.get_IsSystemGenerated()+" "+p.get_IsSystemOwned());
			  //}
			  
		 }*/
		 /*
		doc=Factory.Document.fetchInstance(store, "{05554638-387C-41A3-9F0A-FCF516C39E7A}", null);
		//System.out.println(doc.get_Name());
		Object attrValue="Hello";
		List<Object> attrValueList = new ArrayList<Object>();
		attrValueList.add(attrValue);
		Properties props=doc.getProperties();
		attrValue="Fri Oct 21 00:00:00 IST 2016";
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        String dateInString = "7-Jun-2013";
      
        Date date = new Date(Long.parseLong(attrValue+""));
            //Date date = formatter.parse(attrValue.toString());
            System.out.println(date);
            System.out.println(formatter.format(date));

       
		//StringList list=new StringListImpl(attrValueList);
		try{
			attrValue="Fri Oct 28 00:00:00 IST 2011";
			DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
			Date date = (Date)formatter.parse(attrValue.toString());
			System.out.println(date);        

			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			String formatedDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" +         cal.get(Calendar.YEAR);
			System.out.println("formatedDate : " + formatedDate);  
		props.putValue("CmRetentionDate", new Date(formatedDate));
		 
		props.putValue("DocumentTitle", "hello");
		//props.putValue("MimeType", "txt");
		//props.putValue("CompoundDocumentState", Integer.valueOf("52"));
		//System.out.println(new Date(attrValue.toString()));
		doc.save(RefreshMode.REFRESH);
		}catch(Exception e){
			e.printStackTrace();
		}*/
	
		/*ClassDescription cs=Factory.ClassDescription.fetchInstance(store , "Folder",null);

		 PropertyDescriptionList l=cs.get_PropertyDescriptions();
		 for(int i=0;i<l.size();i++){
			  PropertyDescription p= (PropertyDescription) l.get(i);
			  System.out.println(p.get_Name()+" " +p.get_DataType()+" "+p.get_Cardinality()+" "+p.get_IsReadOnly());
			  
		  }
		}
		
		*/
		/*ObjectIdentity ob=new ObjectIdentity();
		ObjectIdentity ob1=new ObjectIdentity();
		ob.setObjectId("{F6D9D723-1492-4249-9B34-5B1D76B1D190}");
		ob1.setObjectId("{FAFF4544-DD52-4B53-A15A-ED93A96F8719}");
		ob.setObjectType("Document");
		ob1.setObjectType("Folder");
		//ob.setObjectPath("/sample");
		CmsSessionParams sessionParams = toCmsSessionParams();
		filenetDao = CmsDaoFactory.createCmsDao();
		filenetDao.setSessionParams(sessionParams);
				Document docObject=null;
				Folder folderObject=null;
				boolean isRequiredPermission=false;
				IndependentObject idObject=filenetDao.getObjectByIdentity(ob1,"SAKOS");
				if(idObject instanceof Document){	
					docObject=(Document)idObject;
				}else{
					folderObject=(Folder)idObject;
				}		
				
				if (docObject != null) {

					int accessMask = docObject.getAccessAllowed();
					Document resObject=(Document)docObject.get_Reservation();
					if ( (accessMask & ACCESS_REQUIRED) == ACCESS_REQUIRED){
						isRequiredPermission=true;
					}
					
					
					if (!docObject.get_IsFrozenVersion() && isRequiredPermission) {
						//isObjectUpdated=filenetDao.updateObjectProps(getUpdateObjectParam(object,filenetDao,docObject,identity),objectStoreName);
						System.out.println("Frozen version"+docObject.get_IsFrozenVersion());
						System.out.println("Reserved ibhect"+resObject.get_LastModifier());
						System.out.println("Required permission"+isRequiredPermission);
						System.out.println("Reserved version"+docObject.get_IsReserved());
						if (true) {
							System.out.println("Document with id " + docObject.get_Id() + " updated.");
						}else{
							System.out.println("Unable to update properties for non-existent object identity ");  
						}
					} else {
						System.out.println("User does not have permission to modify the object with id: ");
					}
				} else {
					if (folderObject != null) {

					int accessMask = folderObject.getAccessAllowed();
					if ( (accessMask & ACCESS_REQUIRED) == ACCESS_REQUIRED){
						isRequiredPermission=true;
					}
					
					
					if (isRequiredPermission) {
						//isObjectUpdated=filenetDao.updateObjectProps(getUpdateObjectParam(object,filenetDao,docObject,identity),objectStoreName);
						System.out.println("Required permission"+isRequiredPermission);
						if (true) {
							System.out.println("Document with id " + folderObject.get_Id() + " updated.");
						}else{
							System.out.println("Unable to update properties for non-existent object identity ");  
						}
					} else {
						System.out.println("User does not have permission to modify the object with id: ");
					}
				} else {
					System.out.println("Object with id does not exists in the repository."); 
				}
				}
				
		if (docObject != null) {
			
			Properties props=docObject.getProperties();
			UpdateObjectParam params1=new UpdateObjectParam();
			Map<String, Object> attrMap=new HashMap<String, Object>();

			Object objAttrValue="";
			objAttrValue="Main to Hun Pagal";
			attrMap.put("Title", objAttrValue);	
			params1.setObjectIdentity(ob);
			params1.setAttrMap(attrMap);
			String Title = props.getStringValue("DocumentTitle");
			System.out.println(Title);
			props.putValue("DocumentTitle", objAttrValue.toString());

			// Save and update property cache.
			docObject.save(RefreshMode.REFRESH);	

			System.out.println(Title);
		}*/
			/*DependentObjectList acl=props.getDependentObjectListValue(PropertyNames.PERMISSIONS);
			AccessPermissionList acl=(AccessPermissionList) props.getDependentObjectListValue(PropertyNames.PERMISSIONS);
			System.out.println(acl);
			
					for (Iterator it = acl.iterator(); it.hasNext();)
					{
						//AccessPermission p = (AccessPermission)it.next();
						//System.out.println(p.toString());
						AccessPermissionImpl p= (AccessPermissionImpl) it.next();
						System.out.println(p.get_GranteeName());
						System.out.println(p.get_AccessType());
						System.out.println(p.get_AccessMask());
						System.out.println(AccessRight.WRITE_AS_INT);
						System.out.println(AccessRight.DELETE_AS_INT);
						System.out.println(AccessRight.READ_AS_INT);
						System.out.println(AccessRight.MODIFY_OBJECTS_AS_INT);
						System.out.println(AccessRight.LINK_AS_INT);
						System.out.println(AccessRight.CONNECT_AS_INT);
						System.out.println(AccessRight.CHANGE_STATE_AS_INT);
						System.out.println(AccessRight.NONE_AS_INT);System.out.println(AccessRight.WRITE_OWNER_AS_INT);
						System.out.println(p);
					}
			Iterator it = null;
			
			it = permissions.iterator();
			for(int i=0;it.hasNext();i++){
				
				*/
				
	
	
	
	
	public static CmsSessionParams toCmsSessionParams() throws Exception{

		CmsSessionParams params = new CmsSessionParams();
		params.setUri("http://10.137.186.123:9080/wsi/FNCEWS40MTOM");
    	params.setStanza("FileNetP8WSI");
		params.setUser("Administrator");
		params.setPassword("filenet");
		return params;
	}
}

