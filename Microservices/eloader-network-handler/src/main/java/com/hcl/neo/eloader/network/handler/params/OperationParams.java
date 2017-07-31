package com.hcl.neo.eloader.network.handler.params;

import com.hcl.neo.eloader.network.handler.operation.ContentTransferMonitor;


public class OperationParams extends Params {

	private String localPath;
	private String remotePath;
	private int transferStreams;
	private ContentTransferMonitor contentTransferMonitor;
	private int retryCount;
	
	public OperationParams(){
		this.transferStreams = 1;
		this.retryCount = 5;
	}
	
	public OperationParams(String localPath, String remotePath){
		this.localPath = localPath;
		this.remotePath  = remotePath;
		this.transferStreams = 1;
	}
	
	public OperationParams(String localPath, String remotePath, int transferStreams){
		this.localPath = localPath;
		this.remotePath  = remotePath;
		this.transferStreams = transferStreams;
	}
	
	public OperationParams(String localPath, String remotePath, int transferStreams, ContentTransferMonitor monitor){
		this.localPath = localPath;
		this.remotePath  = remotePath;
		this.transferStreams = transferStreams;
		this.contentTransferMonitor = monitor;
	}
	
	
	@Override
	public boolean validate() {
		getErrorList().clear();
		if(null == localPath || "".equals(localPath)){
			addValidationErrorMessage("Invalid local path: "+localPath);
		}
		if(null == remotePath || "".equals(remotePath)){
			addValidationErrorMessage("Invalid remote path: "+remotePath);
		}
		return getErrorList().isEmpty();
	}
	
	public String getLocalPath() {
		return localPath;
	}
	
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	
	public String getRemotePath() {
		return remotePath;
	}
	
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public int getTransferStreams() {
		return transferStreams;
	}

	/**
	 * Content transfer streams. minimum value is 1 and maximum value is 5. 
	 * Content transfer won't resume upload/download in case more than 1 stream is used to transfer content.
     * @param transferStreams
	 */
	public void setTransferStreams(int transferStreams) {
		this.transferStreams = transferStreams < 0 || transferStreams > 5 ? 1 : transferStreams;
	}

	public ContentTransferMonitor getContentTransferMonitor() {
		return contentTransferMonitor;
	}

	public void setContentTransferMonitor(ContentTransferMonitor contentTransferMonitor) {
		this.contentTransferMonitor = contentTransferMonitor;
	}
	
	@Override
	public String toString() {
		return "OperationParams [localPath=" + localPath + ", remotePath="
				+ remotePath + ", transferStreams=" + transferStreams
				+ ", contentTransferMonitor=" + contentTransferMonitor
				+ ", retryCount=" + retryCount + "]";
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
}