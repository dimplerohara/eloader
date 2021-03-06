/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hcl.neo.eloader.microservices.model;

import java.util.List;

/**
 *
 * @author Sakshi Jain
 */
public class UploadJobMessage {
    
    private String name;
    private String type;    
    private String userId;
    private String userName;
    private String userEmail;
    private String packageChecksum;
    private long contentSize;
    private long packageSize;
    private long packageFolderCount;
    private long packageFileCount;
    private long repositoryId;    
    private long transportServerId;
    private String transportServerPath;
    private String clientos;
    private String folderTypes;
    private String businessGroup;
    private String kmGroup;
    private List<String> repositoryPaths;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPackageChecksum() {
        return packageChecksum;
    }

    public void setPackageChecksum(String packageChecksum) {
        this.packageChecksum = packageChecksum;
    }

    public long getContentSize() {
        return contentSize;
    }

    public void setContentSize(long contentSize) {
        this.contentSize = contentSize;
    }

    public long getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(long packageSize) {
        this.packageSize = packageSize;
    }

    public long getPackageFolderCount() {
        return packageFolderCount;
    }

    public void setPackageFolderCount(long packageFolderCount) {
        this.packageFolderCount = packageFolderCount;
    }

    public long getPackageFileCount() {
        return packageFileCount;
    }

    public void setPackageFileCount(long packageFileCount) {
        this.packageFileCount = packageFileCount;
    }

    public long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public long getTransportServerId() {
        return transportServerId;
    }

    public void setTransportServerId(long transportServerId) {
        this.transportServerId = transportServerId;
    }

    public String getTransportServerPath() {
        return transportServerPath;
    }

    public void setTransportServerPath(String transportServerPath) {
        this.transportServerPath = transportServerPath;
    }

    public String getClientos() {
        return clientos;
    }

    public void setClientos(String clientos) {
        this.clientos = clientos;
    }

    public String getFolderTypes() {
        return folderTypes;
    }

    public void setFolderTypes(String folderTypes) {
        this.folderTypes = folderTypes;
    }

    public String getBusinessGroup() {
        return businessGroup;
    }

    public void setBusinessGroup(String businessGroup) {
        this.businessGroup = businessGroup;
    }

    public String getKmGroup() {
        return kmGroup;
    }

    public void setKmGroup(String kmGroup) {
        this.kmGroup = kmGroup;
    }

    public List<String> getRepositoryPaths() {
        return repositoryPaths;
    }

    public void setRepositoryPaths(List<String> repositoryPaths) {
        this.repositoryPaths = repositoryPaths;
    }

    @Override
    public String toString() {
        return "UploadJobMessage{" + "name=" + name + ", type=" + type + ", userId=" + userId + ", userName=" + userName + ", userEmail=" + userEmail + ", packageChecksum=" + packageChecksum + ", contentSize=" + contentSize + ", packageSize=" + packageSize + ", packageFolderCount=" + packageFolderCount + ", packageFileCount=" + packageFileCount + ", repositoryId=" + repositoryId + ", transportServerId=" + transportServerId + ", transportServerPath=" + transportServerPath + ", clientos=" + clientos + ", folderTypes=" + folderTypes + ", businessGroup=" + businessGroup + ", kmGroup=" + kmGroup + ", repositoryPaths=" + repositoryPaths + '}';
    }         
}
