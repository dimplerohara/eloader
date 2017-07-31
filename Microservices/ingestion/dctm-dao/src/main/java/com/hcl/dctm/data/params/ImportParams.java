package com.hcl.dctm.data.params;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportParams extends Params {

    private String repositoryPath;
    private List<String> localPath;
    private String ownerName;
    private Map<String, String> objectTypes;
    private String defaultFolderType;
    private String folderId;

    public ImportParams() {
        this.localPath = new ArrayList<>();
        this.objectTypes = new HashMap<>();
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public List<String> getLocalPath() {
        return localPath;
    }

    public void setLocalPath(List<String> localPath) {
        this.localPath = localPath;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Map<String, String> getObjectTypes() {
        return objectTypes;
    }

    public void setObjectTypes(Map<String, String> objectTypes) {
        this.objectTypes = objectTypes;
    }

    public void addLocalPath(String path) {
        this.localPath.add(path);
    }

    public void addObjectType(String localPath, String objectType) {
        this.objectTypes.put(localPath, objectType);
    }

    public String getDefaultFolderType() {
        return defaultFolderType;
    }

    public void setDefaultFolderType(String defaultFolderType) {
        this.defaultFolderType = defaultFolderType;
    }

    public void setFolderId(String folderId){
    	this.folderId = folderId;
    }
    
    public String getFolderId(){
    	return folderId;
    }
    
    @Override
    public String toString() {
        return "ImportParams{" + "repositoryPath=" + repositoryPath + ", localPath=" + localPath + ", ownerName=" + ownerName + ", objectTypes=" + objectTypes + ", defaultFolderType=" + defaultFolderType + '}';
    }

}
