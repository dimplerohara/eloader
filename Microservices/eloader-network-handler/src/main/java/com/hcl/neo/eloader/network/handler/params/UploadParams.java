package com.hcl.neo.eloader.network.handler.params;

import com.hcl.neo.eloader.network.handler.operation.ContentTransferMonitor;

public class UploadParams extends OperationParams{

	public UploadParams(){
		
	}
	
	public UploadParams (String localPath, String remotePath) {
		super(localPath, remotePath);
	}
	
	public UploadParams (String localPath, String remotePath, int transferStreams) {
		super(localPath, remotePath, transferStreams);
	}
	
	public UploadParams (String localPath, String remotePath, int transferStreams, ContentTransferMonitor monitor) {
		super(localPath, remotePath, transferStreams, monitor);
	}
}
