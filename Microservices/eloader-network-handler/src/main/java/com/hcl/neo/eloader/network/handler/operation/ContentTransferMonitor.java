package com.hcl.neo.eloader.network.handler.operation;

public interface ContentTransferMonitor {

	public void bytesTransferred(long totalBytesTransferred);
	public void setTotalBytes(long totalBytes);
	public long getTotalBytes();
}
