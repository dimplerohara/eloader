package com.hcl.neo.eloader.network.handler.operation;

import com.hcl.neo.eloader.common.Logger;

public class ContentTransferMonitorImpl implements ContentTransferMonitor {

	private long totalBytesTransferred;
	private long totalBytes;

	public ContentTransferMonitorImpl(){
		this.totalBytesTransferred = 0;
		this.totalBytes = 0;
	}
	
        @Override
	synchronized final public void bytesTransferred(long bytesTransferred) {
		this.totalBytesTransferred += bytesTransferred;
		Logger.info(getClass(), totalBytesTransferred+"/"+totalBytes);
	}
	
        @Override
	public long getTotalBytes() {
		return this.totalBytes;
	}

        @Override
	public void setTotalBytes(long totalBytes) {
		this.totalBytes = totalBytes;
	}
}
