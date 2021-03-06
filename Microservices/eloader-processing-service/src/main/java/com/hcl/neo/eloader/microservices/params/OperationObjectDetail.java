package com.hcl.neo.eloader.microservices.params;

public class OperationObjectDetail {
	
	private String objectId;
	private String objectName;
	private String sourcePath;
	private String targetPath;
	private boolean isFile;
	private String message;
	private boolean isError;
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	public String getTargetPath() {
		return targetPath;
	}
	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}
	public boolean isFile() {
		return isFile;
	}
	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isError() {
		return isError;
	}
	public void setError(boolean isError) {
		this.isError = isError;
	}
	@Override
	public String toString() {
		return "OperationResponse [objectId=" + objectId + ", objectName="
				+ objectName + ", sourcePath=" + sourcePath + ", targetPath="
				+ targetPath + ", isFile=" + isFile + ", message=" + message
				+ ", isError=" + isError + "]";
	}
}