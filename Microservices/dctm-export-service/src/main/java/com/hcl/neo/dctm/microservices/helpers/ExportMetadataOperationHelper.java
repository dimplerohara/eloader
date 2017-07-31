package com.hcl.neo.dctm.microservices.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.hcl.dctm.data.DctmDao;
import com.hcl.dctm.data.params.ObjectIdentity;
import com.hcl.dctm.data.params.OperationObjectDetail;
import com.hcl.neo.dctm.microservices.excel.schema_metadata.Attribute;
import com.hcl.neo.dctm.microservices.excel.schema_objecttype.DataType;
import com.hcl.neo.dctm.microservices.excel.schema_objecttype.PropertyInformation;
import com.hcl.neo.eloader.common.Logger;

@Service
public class ExportMetadataOperationHelper {

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Object[] getObjectsData(List<ObjectIdentity> repositoryPaths, ObjectTypeHelper objectTypeHelper, DctmDao dctmDao) throws Exception {
        Object[] objectsData = new Object[2];
        List<com.hcl.neo.dctm.microservices.excel.schema_metadata.Object> objectList = new ArrayList<>();
        List<OperationObjectDetail> objectDetailList = new ArrayList<>();        
        //Removing Duplicate paths        
        HashSet hashSet = new HashSet();
        hashSet.addAll(repositoryPaths);
        repositoryPaths.clear();
        repositoryPaths.addAll(hashSet);
        
        for (ObjectIdentity objectPath : repositoryPaths) {
            if (objectPath != null) {
                getObjectProperties(objectPath, objectTypeHelper, objectList, objectDetailList, dctmDao);
            }
        }
        objectsData [0] = objectList;
        objectsData[1] = objectDetailList;
        return objectsData;
    }

    public void getObjectProperties(ObjectIdentity objectIdentity, ObjectTypeHelper objectTypeHelper, 
    			List<com.hcl.neo.dctm.microservices.excel.schema_metadata.Object> objectList, List<OperationObjectDetail> objectDetailList, DctmDao dctmDao) {
        OperationObjectDetail objectDetail;
        String objectId = "";
        String objectPath = "";
        Map<String,String> properties = null;
        if (objectIdentity.getObjectPath() != null && objectIdentity.getObjectPath().startsWith("/")) {
        	objectPath = objectIdentity.getObjectPath();
            String rootFolderPath = objectPath.substring(0, objectPath.lastIndexOf("/"));
            try {
            	properties= dctmDao.getPropertiesByIdentity(objectIdentity);
            	if (properties != null) {
                    objectId = (String) properties.get("r_object_id");
                    getObject(objectTypeHelper, rootFolderPath, properties, objectList, objectDetailList, dctmDao);
                }
            } catch (Exception ex) {
                objectDetail = new OperationObjectDetail();
                objectDetail.setSourcePath(objectPath);
                objectDetail.setError(true);
                objectDetail.setMessage(ex.getMessage());
                objectDetailList.add(objectDetail);
            }
            if (properties != null && (objectId.startsWith("0b") || objectId.startsWith("0c"))) {
                String dql = "select r_object_id, i_vstamp, r_object_type, r_aspect_name, i_is_replica, i_is_reference, object_name, i_chronicle_id from dm_sysobject where folder(id('" + objectId + "'),descend)";
            	try {
                	List<Map<String,String>> searchResults = dctmDao.execSelect(dql);
                	for (Map<String, String> objectDetails : searchResults) {
                        getObject(objectTypeHelper, rootFolderPath, objectDetails, objectList, objectDetailList, dctmDao);
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
    		List<com.hcl.neo.dctm.microservices.excel.schema_metadata.Object> objectList, List<OperationObjectDetail> objectDetailList, DctmDao dctmDao) {
        com.hcl.neo.dctm.microservices.excel.schema_metadata.Object object = new com.hcl.neo.dctm.microservices.excel.schema_metadata.Object();
        StringBuilder errorMessage = new StringBuilder();
        OperationObjectDetail objectDetail = new OperationObjectDetail();
        try {
            objectDetail.setObjectId((String) objectProperties.get("r_object_id"));
            objectDetail.setObjectName(objectProperties.get("object_name").toString());
            Attribute attr;
            attr = new Attribute();
            attr.setName("r_object_id");
            attr.setType("STRING");
            attr.setValue((String) objectProperties.get("r_object_id"));
            object.getAttribute().add(attr);
            attr = new Attribute();
            attr.setName("i_chronicle_id");
            attr.setType("STRING");
            attr.setValue(objectProperties.get("i_chronicle_id").toString());
            object.getAttribute().add(attr);
            attr = new Attribute();
            attr.setName("r_object_type");
            attr.setType("STRING");
            attr.setValue(objectProperties.get("r_object_type").toString());
            object.getAttribute().add(attr);
            attr = new Attribute();
            attr.setName("object_path");
            attr.setType("STRING");
            attr.setValue((String) objectProperties.get("r_object_id"));
            String folderPath = objectProperties.get("r_folder_path");
            if(null == folderPath){
            	ObjectIdentity objectIdentity = new ObjectIdentity();
            	objectIdentity.setObjectId((String) objectProperties.get("r_object_id"));
            	String path = dctmDao.getObjectPaths(objectIdentity);
            	if (path != null && path.length() > 0) {
                    path = path.replaceFirst(rootFolderPath, "");
                    objectDetail.setSourcePath(path);
                    attr.setValue(path);
                } else {
                    attr.setValue("/");
                }
            	objectProperties = dctmDao.getPropertiesByIdentity(objectIdentity);
            }else{
            	List<String> objectPaths = Arrays.asList(objectProperties.get("r_folder_path").split("\\|"));
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
                System.out.println(path + " -- "+rootFolderPath);
                if (path != null && path.length() > 0) {
                    path = path.replaceFirst(rootFolderPath, "");
                    objectDetail.setSourcePath(path);
                    attr.setValue(path);
                } else {
                    attr.setValue("/"+objectDetail.getObjectName());
                }
            }
            object.getAttribute().add(attr);
            String type = objectProperties.get("r_object_type").toString();
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
    public File
            mergeXML(List<String> xmlFileList, File tempDir) throws Exception {
        Logger.info(ExportMetadataOperationHelper.class, "Merging started.");
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

        domFactory.setIgnoringComments(
                true);
        DocumentBuilder builder;
        File finalXmlFile = new File(tempDir, "dctmMetadata.xml");

        try {
            builder = domFactory.newDocumentBuilder();
            Document finalDoc = null;
            Element toRoot = null;
            if (xmlFileList.size() > 0) {
                File firstFile = new File(xmlFileList.get(0));
                if (firstFile.exists()) {
                    if (!finalXmlFile.exists()) {
                        finalXmlFile.createNewFile();
                    }
                    FileUtils.copyFile(firstFile, finalXmlFile);
                }
                finalDoc = builder.parse(finalXmlFile);
                toRoot = finalDoc.getDocumentElement();
                Logger.debug(ExportMetadataOperationHelper.class, "Root File Name: " + finalXmlFile.getName());
            }
            for (int i = 1; i < xmlFileList.size(); i++) {
                Logger.debug(ExportMetadataOperationHelper.class, "Merging File : " + xmlFileList.get(i));
                Document document = builder.parse(xmlFileList.get(i));
                Element root = document.getDocumentElement();
                Node child;
                while ((child = root.getFirstChild()) != null) {
                    if (finalDoc != null) {
                        finalDoc.adoptNode(child);
                    }
                    if (toRoot != null) {
                        toRoot.appendChild(child);
                    }
                }
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(finalDoc);
            transformer.transform(source, result);
            Writer output;
            output = new BufferedWriter(new FileWriter(finalXmlFile));
            String xmlOutput = result.getWriter().toString();
            output.write(xmlOutput);
            output.close();
            Logger.info(ExportMetadataOperationHelper.class, "Merging completed. Merge File:" + finalXmlFile);
        } catch (ParserConfigurationException | SAXException | IOException | TransformerFactoryConfigurationError | TransformerException e) {
            Logger.error(ExportMetadataOperationHelper.class, "Error wile deleting temp location.", e);
            throw new Exception(e);
        }
        return finalXmlFile;
    }
}
