package com.hcl.neo.eloader.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "job_master")
public class JobMaster {

	@Id
    public String id;
	public Long jobId;
	public String name;
	public String type;
	public String status;
	public String userId;
	public String userName;
	public String userEmail;
	public Date creationDate;
	public Date completionDate;
	public String packageCheckSum;
	public long packageSize;
	public long contentSize;
	public long packageFolderCount;
	public long packageFileCount;
	public long repositoryId;
	public long transportServerId;
	public String transportServerPath;
	public String clientOS;
	public String folderTypes;
	public String businessGroup;
	public String kmGroup;
	public List<String> repositoryPath;
	public long successCount;
	

	public long failureCount;
	public long landZoneId;
	
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param name the name to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}
	/**
	 * @param userEmail the userEmail to set
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}
	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	/**
	 * @return the completionDate
	 */
	public Date getCompletionDate() {
		return completionDate;
	}
	/**
	 * @param completionDate the completionDate to set
	 */
	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}
	/**
	 * @return the packageCheckSum
	 */
	public String getPackageCheckSum() {
		return packageCheckSum;
	}
	/**
	 * @param packageCheckSum the packageCheckSum to set
	 */
	public void setPackageCheckSum(String packageCheckSum) {
		this.packageCheckSum = packageCheckSum;
	}
	/**
	 * @return the packageSize
	 */
	public long getPackageSize() {
		return packageSize;
	}
	/**
	 * @param packageSize the packageSize to set
	 */
	public void setPackageSize(long packageSize) {
		this.packageSize = packageSize;
	}
	/**
	 * @return the contentSize
	 */
	public long getContentSize() {
		return contentSize;
	}
	/**
	 * @param contentSize the contentSize to set
	 */
	public void setContentSize(long contentSize) {
		this.contentSize = contentSize;
	}
	/**
	 * @return the packageFolderCount
	 */
	public long getPackageFolderCount() {
		return packageFolderCount;
	}
	/**
	 * @param packageFolderCount the packageFolderCount to set
	 */
	public void setPackageFolderCount(long packageFolderCount) {
		this.packageFolderCount = packageFolderCount;
	}
	/**
	 * @return the packageFileCount
	 */
	public long getPackageFileCount() {
		return packageFileCount;
	}
	/**
	 * @param packageFileCount the packageFileCount to set
	 */
	public void setPackageFileCount(long packageFileCount) {
		this.packageFileCount = packageFileCount;
	}
	/**
	 * @return the repositoryId
	 */
	public long getRepositoryId() {
		return repositoryId;
	}
	/**
	 * @param repositoryId the repositoryId to set
	 */
	public void setRepositoryId(long repositoryId) {
		this.repositoryId = repositoryId;
	}
	/**
	 * @return the transportServerId
	 */
	public long getTransportServerId() {
		return transportServerId;
	}
	/**
	 * @param transportServerId the transportServerId to set
	 */
	public void setTransportServerId(long transportServerId) {
		this.transportServerId = transportServerId;
	}
	/**
	 * @return the transportServerPath
	 */
	public String getTransportServerPath() {
		return transportServerPath;
	}
	/**
	 * @param transportServerPath the transportServerPath to set
	 */
	public void setTransportServerPath(String transportServerPath) {
		this.transportServerPath = transportServerPath;
	}
	/**
	 * @return the clientOS
	 */
	public String getClientOS() {
		return clientOS;
	}
	/**
	 * @param clientOS the clientOS to set
	 */
	public void setClientOS(String clientOS) {
		this.clientOS = clientOS;
	}
	/**
	 * @return the folderTypes
	 */
	public String getFolderTypes() {
		return folderTypes;
	}
	/**
	 * @param folderTypes the folderTypes to set
	 */
	public void setFolderTypes(String folderTypes) {
		this.folderTypes = folderTypes;
	}
	/**
	 * @return the businessGroup
	 */
	public String getBusinessGroup() {
		return businessGroup;
	}
	/**
	 * @param businessGroup the businessGroup to set
	 */
	public void setBusinessGroup(String businessGroup) {
		this.businessGroup = businessGroup;
	}
	/**
	 * @return the kmGroup
	 */
	public String getKmGroup() {
		return kmGroup;
	}
	/**
	 * @param kmGroup the kmGroup to set
	 */
	public void setKmGroup(String kmGroup) {
		this.kmGroup = kmGroup;
	}
	/**
	 * @return the jobId
	 */
	public Long getJobId() {
		return jobId;
	}
	/**
	 * @param jobId the jobId to set
	 */
	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}
	
	/**
	 * @return the repositoryPath
	 */
	public List<String> getRepositoryPath() {
		return repositoryPath;
	}
	/**
	 * @param repositoryPath the repositoryPath to set
	 */
	public void setRepositoryPath(List<String> repositoryPath) {
		this.repositoryPath = repositoryPath;
	}
	/**
	 * @return the landZoneId
	 */
	public Long getLandZoneId() {
		return landZoneId;
	}
	/**
	 * @param landZoneId the landZoneId to set
	 */
	public void setLandZoneId(Long landZoneId) {
		this.landZoneId = landZoneId;
	}
	
	/**
	 * @return the failureCount
	 */
	public long getFailureCount() {
		return failureCount;
	}
	/**
	 * @param failureCount the failureCount to set
	 */
	public void setFailureCount(long failureCount) {
		this.failureCount = failureCount;
	}
	/**
	 * @return the successCount
	 */
	public long getSuccessCount() {
		return successCount;
	}
	/**
	 * @param successCount the successCount to set
	 */
	public void setSuccessCount(long successCount) {
		this.successCount = successCount;
	}
	
	@Override
	public String toString() {
		return "JobMaster [id=" + id + ", jobId=" + jobId + ", name=" + name + ", type=" + type + ", status=" + status
				+ ", userId=" + userId + ", userName=" + userName + ", userEmail=" + userEmail + ", creationDate="
				+ creationDate + ", completionDate=" + completionDate + ", packageCheckSum=" + packageCheckSum
				+ ", packageSize=" + packageSize + ", contentSize=" + contentSize + ", packageFolderCount="
				+ packageFolderCount + ", packageFileCount=" + packageFileCount + ", repositoryId=" + repositoryId
				+ ", transportServerId=" + transportServerId + ", transportServerPath=" + transportServerPath
				+ ", clientOS=" + clientOS + ", folderTypes=" + folderTypes + ", businessGroup=" + businessGroup
				+ ", kmGroup=" + kmGroup + ", repositoryPath=" + repositoryPath + ", successCount=" + successCount
				+ ", failureCount=" + failureCount + ", landZoneId=" + landZoneId + "]";
	}
}
