package com.hcl.neo.eloader.network.handler.params;

import com.hcl.neo.eloader.network.handler.operation.ContentTransferMonitor;

public class DownloadParams extends OperationParams{

	public DownloadParams(){
		
	}
	
	public DownloadParams(String localPath, String remotePath) {
		super(localPath, remotePath);
	}
	
	public DownloadParams(String localPath, String remotePath, int transferStreams) {
		super(localPath, remotePath, transferStreams);
	}
	
	public DownloadParams(String localPath, String remotePath, int transferStreams, ContentTransferMonitor monitor) {
		super(localPath, remotePath, transferStreams, monitor);
	}
}
