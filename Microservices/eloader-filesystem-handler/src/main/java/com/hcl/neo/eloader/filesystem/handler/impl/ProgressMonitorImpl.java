package com.hcl.neo.eloader.filesystem.handler.impl;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.filesystem.handler.ProgressMonitor;

/**
 * Example implementation for {@link ProgressMonitor}
 * @author jasneets
 *
 */
public class ProgressMonitorImpl implements ProgressMonitor {

	private long bytesProcessed;
	private long totalBytes;

	public ProgressMonitorImpl(){
		this.bytesProcessed = 0;
		this.totalBytes = 0;
	}
	
	synchronized final public void bytesProcessed(long byteProcessed) {
		this.bytesProcessed += byteProcessed;
		Logger.info(getClass(), bytesProcessed+"/"+totalBytes);
	}
	
	public void setTotalByteSize(long totalCount) {
		this.totalBytes = totalCount;
	}

	public long getTotalByteSize() {
		return this.totalBytes;
	}
}