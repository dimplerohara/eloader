/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hcl.neo.eloader.microservices.model;

import java.util.List;

/**
 *
 * @author Pankaj.Srivastava
 */
public class DownloadJobMessage {

    private String jobType;
    private String userId;
    private String userName;
    private String userEmail;
    private long repositoryId;
    private List<String> repositoryPath;
    private long folderCount;
    private long fileCount;
    private long totalContentSize;
    private String jobName;
    private String businessGroup;
    private String kmGroup;
    private String clientOs;
    private String networkLocation;
    private String transportServerType;
    private String transportServerName;
    private int transportPort;
    private String transportUserId;
    private String transportPassword;
    private String transportPath;
    private long transportId;

    public DownloadJobMessage() {
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public List<String> getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(List<String> repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public long getFolderCount() {
        return folderCount;
    }

    public void setFolderCount(long folderCount) {
        this.folderCount = folderCount;
    }

    public long getFileCount() {
        return fileCount;
    }

    public void setFileCount(long fileCount) {
        this.fileCount = fileCount;
    }


    public long getTotalContentSize() {
        return totalContentSize;
    }

    public void setTotalContentSize(long totalContentSize) {
        this.totalContentSize = totalContentSize;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
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

    public String getClientOs() {
        return clientOs;
    }

    public void setClientOs(String clientOs) {
        this.clientOs = clientOs;
    }

    public String getNetworkLocation() {
        return networkLocation;
    }

    public void setNetworkLocation(String networkLocation) {
        this.networkLocation = networkLocation;
    }

    public String getTransportServerType() {
        return transportServerType;
    }

    public void setTransportServerType(String transportServerType) {
        this.transportServerType = transportServerType;
    }

    public String getTransportServerName() {
        return transportServerName;
    }

    public void setTransportServerName(String transportServerName) {
        this.transportServerName = transportServerName;
    }

    public int getTransportPort() {
        return transportPort;
    }

    public void setTransportPort(int transportPort) {
        this.transportPort = transportPort;
    }

    public String getTransportUserId() {
        return transportUserId;
    }

    public void setTransportUserId(String transportUserId) {
        this.transportUserId = transportUserId;
    }

    public String getTransportPassword() {
        return transportPassword;
    }

    public void setTransportPassword(String transportPassword) {
        this.transportPassword = transportPassword;
    }

    public String getTransportPath() {
        return transportPath;
    }

    public void setTransportPath(String transportPath) {
        this.transportPath = transportPath;
    }

    public long getTransportId() {
        return transportId;
    }

    public void setTransportId(long transportId) {
        this.transportId = transportId;
    }

    @Override
    public String toString() {
        return "DownloadJobMessage{" + "jobType=" + jobType + ", userId=" + userId + ", userName=" + userName + ", userEmail=" + userEmail + ", repositoryId=" + repositoryId + ", repositoryPath=" + repositoryPath + ", folderCount=" + folderCount + ", fileCount=" + fileCount + ", totalContentSize=" + totalContentSize + ", jobName=" + jobName + ", businessGroup=" + businessGroup + ", kmGroup=" + kmGroup + ", clientOs=" + clientOs + ", networkLocation=" + networkLocation + ", transportServerType=" + transportServerType + ", transportServerName=" + transportServerName + ", transportPort=" + transportPort + ", transportUserId=" + transportUserId + ", transportPath=" + transportPath + ", transportId=" + transportId + '}';
    }
}
