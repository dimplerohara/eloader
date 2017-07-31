package com.hcl.neo.eloader.filesystem.handler;

/**
 * Monitor progress of create and extract process
 * @author jasneets
 *
 */
public interface ProgressMonitor {

	/**
	 * Invoked on each read/write command
	 * @param bytesProcessed
	 */
	public void bytesProcessed(long bytesProcessed);
	
	/**
	 * Set size in bytes
	 * @param totalByteSize
	 */
	public void setTotalByteSize(long totalByteSize);
	
	/**
	 * Get size in bytes
	 * @return
	 */
	public long getTotalByteSize();
}
