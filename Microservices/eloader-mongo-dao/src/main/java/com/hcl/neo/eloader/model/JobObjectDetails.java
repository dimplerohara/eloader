package com.hcl.neo.eloader.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "job_object_details")
public class JobObjectDetails {

	@Id
    public String id;
	public Long jobId;
	public String objectId;
	public String objectName;
	public String sourcePath;
	public String targetPath;
	public boolean isFile;
	public String message;
	public boolean isError;
	public Date creationDate;
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
	 * @return the objectId
	 */
	public String getObjectId() {
		return objectId;
	}
	/**
	 * @param objectId the objectId to set
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	/**
	 * @return the objectName
	 */
	public String getObjectName() {
		return objectName;
	}
	/**
	 * @param objectName the objectName to set
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	/**
	 * @return the sourcePath
	 */
	public String getSourcePath() {
		return sourcePath;
	}
	/**
	 * @param sourcePath the sourcePath to set
	 */
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	/**
	 * @return the targetPath
	 */
	public String getTargetPath() {
		return targetPath;
	}
	/**
	 * @param targetPath the targetPath to set
	 */
	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}
	/**
	 * @return the isFile
	 */
	public boolean isFile() {
		return isFile;
	}
	/**
	 * @param isFile the isFile to set
	 */
	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the isError
	 */
	public boolean isError() {
		return isError;
	}
	/**
	 * @param isError the isError to set
	 */
	public void setError(boolean isError) {
		this.isError = isError;
	}
	public String getIsError() {
		return String.valueOf(isError);
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
	
	
	@Override
	public String toString() {
		return "JobObjectDetails [id=" + id + ", jobId=" + jobId + ", objectId=" + objectId + ", objectName="
				+ objectName + ", sourcePath=" + sourcePath + ", targetPath=" + targetPath + ", isFile=" + isFile
				+ ", message=" + message + ", isError=" + isError + ", creationDate=" + creationDate + "]";
	}
	
}
