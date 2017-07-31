package com.hcl.neo.cms.microservices.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hcl.cms.data.CmsDao;
import com.hcl.cms.data.params.ObjectIdentity;
import com.hcl.cms.data.params.OperationObjectDetail;
import com.hcl.neo.cms.microservices.excel.schema_metadata.Attribute;
import com.hcl.neo.cms.microservices.excel.schema_objecttype.DataType;
import com.hcl.neo.cms.microservices.excel.schema_objecttype.PropertyInformation;

@Service
public class ExportMetadataOperationHelper {

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Object[] getObjectsData(List<ObjectIdentity> repositoryPaths, ObjectTypeHelper objectTypeHelper, CmsDao cmsDao, String repository) throws Exception {
        Object[] objectsData = new Object[2];
        List<com.hcl.neo.cms.microservices.excel.schema_metadata.Object> objectList = new ArrayList<>();
        List<OperationObjectDetail> objectDetailList = new ArrayList<>();        
        //Removing Duplicate paths        
        HashSet hashSet = new HashSet();
        hashSet.addAll(repositoryPaths);
        repositoryPaths.clear();
        repositoryPaths.addAll(hashSet);
        
        for (ObjectIdentity objectPath : repositoryPaths) {
            if (objectPath != null) {
                getObjectProperties(objectPath, objectTypeHelper, objectList, objectDetailList, cmsDao, repository);
            }
        }
        objectsData [0] = objectList;
        objectsData[1] = objectDetailList;
        return objectsData;
    }

    public void getObjectProperties(ObjectIdentity objectIdentity, ObjectTypeHelper objectTypeHelper, 
    			List<com.hcl.neo.cms.microservices.excel.schema_metadata.Object> objectList, List<OperationObjectDetail> objectDetailList, CmsDao cmsDao, String repository) {
        OperationObjectDetail objectDetail;
        //String objectId = "";
        String objectType = "";
        String objectPath = "";
        Map<String,String> properties = null;
        if (objectIdentity.getObjectPath() != null && objectIdentity.getObjectPath().startsWith("/")) {
        	objectPath = objectIdentity.getObjectPath();
            String rootFolderPath = objectPath.substring(0, objectPath.lastIndexOf("/"));
            try {
            	properties= cmsDao.getPropertiesByIdentityExport(objectIdentity, repository);
            	if (properties != null) {
                    //objectId = (String) properties.get("object_id");
            		objectType = properties.get("object_type");
                    getObject(objectTypeHelper, rootFolderPath, properties, objectList, objectDetailList, cmsDao, repository);
                }
            } catch (Exception ex) {
                objectDetail = new OperationObjectDetail();
                objectDetail.setSourcePath(objectPath);
                objectDetail.setError(true);
                objectDetail.setMessage(ex.getMessage());
                objectDetailList.add(objectDetail);
            }
            
            if(properties != null && objectType.equalsIgnoreCase("Folder")){
            	try {
					List<Map<String,String>> totalResults = cmsDao.getAllProperties(objectIdentity, repository);
					for (Map<String, String> objectDetails : totalResults) {
						getObject(objectTypeHelper, rootFolderPath, objectDetails, objectList, objectDetailList, cmsDao, repository);
					}
				} catch (Exception ex) {
                    objectDetail = new OperationObjectDetail();
                    objectDetail.setSourcePath(objectPath);
                    objectDetail.setError(true);
                    objectDetail.setMessage(ex.getMessage());
                    objectDetailList.add(objectDetail);
                }
            }
            
        }
    }

    public void getObject(ObjectTypeHelper objectTypeHelper, String rootFolderPath, Map<String,String> objectProperties, 
    		List<com.hcl.neo.cms.microservices.excel.schema_metadata.Object> objectList, List<OperationObjectDetail> objectDetailList, CmsDao cmsDao, String repository) {
        com.hcl.neo.cms.microservices.excel.schema_metadata.Object object = new com.hcl.neo.cms.microservices.excel.schema_metadata.Object();
        StringBuilder errorMessage = new StringBuilder();
        OperationObjectDetail objectDetail = new OperationObjectDetail();
        try {
            objectDetail.setObjectId((String) objectProperties.get("object_id"));
            objectDetail.setObjectName(objectProperties.get("title").toString());
            Attribute attr;
            attr = new Attribute();
            attr.setName("object_id");
            attr.setType("STRING");
            attr.setValue((String) objectProperties.get("object_id"));
            object.getAttribute().add(attr);
            /*attr = new Attribute();
            attr.setName("i_chronicle_id");
            attr.setType("STRING");
            attr.setValue(objectProperties.get("i_chronicle_id").toString());
            object.getAttribute().add(attr);*/
            attr = new Attribute();
            attr.setName("object_type");
            attr.setType("STRING");
            attr.setValue(objectProperties.get("object_type").toString());
            object.getAttribute().add(attr);
            attr = new Attribute();
            attr.setName("object_path");
            attr.setType("STRING");
            attr.setValue((String) objectProperties.get("object_id"));
            String folderPath = objectProperties.get("object_path");
            if(null == folderPath){
            	ObjectIdentity objectIdentity = new ObjectIdentity();
            	//objectIdentity.setObjectId((String) objectProperties.get("r_object_id"));
            	/*String path = dctmDao.getObjectPaths(objectIdentity);
            	if (path != null && path.length() > 0) {
                    path = path.replaceFirst(rootFolderPath, "");
                    objectDetail.setSourcePath(path);
                    attr.setValue(path);
                } else {
                    attr.setValue("/");
                }*/
            	objectProperties = cmsDao.getPropertiesByIdentityExport(objectIdentity, repository);
            }else{
            	List<String> objectPaths = Arrays.asList(objectProperties.get("object_path").split("\\|"));
                String path = "";
                for(String objectPath : objectPaths){
                    if(objectPath != null && !objectPath.isEmpty()){
                    	//objectPath = objectPath+"/"+objectDetail.getObjectName();
                        if(path.isEmpty()){
                            path = objectPath;
                        } else{
                            path = path +","+ objectPath;
                        }
                    }
                }
                
                if (path != null && path.length() > 0) {
                    //path = path.replaceFirst(rootFolderPath, "");
                	if(objectProperties.get("object_type").equalsIgnoreCase("Document")){
                		String filePath = path.substring(0, path.lastIndexOf("/")) + "/";
                		objectDetail.setSourcePath(filePath);
                        attr.setValue(filePath);
                	} else {
                		objectDetail.setSourcePath(path);
                		attr.setValue(path);
                	}
                } else {
                    attr.setValue("/"+objectDetail.getObjectName());
                }
            }
            object.getAttribute().add(attr);
            //String type = objectProperties.get("object_type").toString();
            String type = "Document";
            List<PropertyInformation> properties = objectTypeHelper.getProperties(type);
            for (PropertyInformation p : properties) {
                try {
                    if (objectProperties.containsKey(p.getName())) {
                        attr = new Attribute();
                        attr.setName(p.getName());
                        attr.setType(p.getDatatype().value());
                        if (p.isIsArray()) {
                            attr.setValue(objectProperties.get(p.getName()));
                        } else if (p.getDatatype().equals(DataType.STRING)) {
                            String value = objectProperties.get(p.getName()).toString();
                            attr.setValue(value);
                        } else if (p.getDatatype().equals(DataType.OBJECT_ID)) {
                            String value = objectProperties.get(p.getName()).toString();
                            if (value != null) {
                                attr.setValue(value);
                            } else {
                                attr.setValue("");
                            }
                        } else if (p.getDatatype().equals(DataType.BOOLEAN)) {
                            /*Boolean value = dctmObj.getBoolean(p.getName());
                            attr.setValue(Boolean.toString(value));*/
                        	String value = objectProperties.get(p.getName()).toString();
                        	attr.setValue(value);
                        }else{
                        	String value = objectProperties.get(p.getName()).toString();
                        	attr.setValue(value);
                        }
                        
                        /*else if (p.getDatatype().equals(DataType.DATE)) {
                            attr.setValue(dctmObj.getTime(p.getName()).asString(IDfTime.DF_TIME_PATTERN1));
                        } else if (p.getDatatype().equals(DataType.DOUBLE)) {
                            Double value = dctmObj.getDouble(p.getName());
                            attr.setValue(Double.toString(value));
                        } else if (p.getDatatype().equals(DataType.INTEGER)) {
                            Integer value = dctmObj.getInt(p.getName());
                            attr.setValue(Integer.toString(value));
                        } else if (p.getDatatype().equals(DataType.LONG)) {
                            Long value = dctmObj.getLong(p.getName());
                            attr.setValue(Long.toString(value));
                        } else if (p.getDatatype().equals(DataType.SHORT)) {
                            Integer value = dctmObj.getInt(p.getName());
                            attr.setValue(Integer.toString(value));
                        }*/
                        object.getAttribute().add(attr);
                    }
                } catch (Exception e) {
                    errorMessage.append("\n").append(e.getMessage());
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
            objectDetail.setError(true);
            objectDetail.setMessage(e.getMessage());
        } finally {
            if (errorMessage.length() > 0) {
                objectDetail.setError(true);
                errorMessage.append("\n").append(objectDetail.getMessage());
                objectDetail.setMessage(errorMessage.toString());
            }
            objectDetailList.add(objectDetail);
            objectList.add(object);
        }        
    }
    
}
