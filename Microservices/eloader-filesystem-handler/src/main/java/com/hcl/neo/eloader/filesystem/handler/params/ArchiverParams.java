package com.hcl.neo.eloader.filesystem.handler.params;

import java.util.ArrayList;
import java.util.List;

import com.hcl.neo.eloader.filesystem.handler.ArchiveType;
import com.hcl.neo.eloader.filesystem.handler.ProgressMonitor;

/**
 * Archiver parameters
 * @author jasneets
 *
 */
public class ArchiverParams {

	private String archivePath;
	private List<String> contentPath;
	private ArchiveType archiveType;
	private ProgressMonitor progressMonitor;
	
	public ArchiverParams(){
		this.contentPath = new ArrayList<String>();
	}
	
	/**
	 * When creating archive, it's target path of archive file.
	 * When extracting archive, it's source path of archive file.
	 * @return
	 */
	public String getArchivePath() {
		return archivePath;
	}
	
	/**
	 * When creating archive, it's target path of archive file.
	 * When extracting archive, it's source path of archive file.
	 * @return
	 */
	public void setArchivePath(String archivePath) {
		this.archivePath = archivePath;
	}
	
	/**
	 * When creating archive, it's source list of file/folder 
	 * when extracting archive, it's destination location where to extract content
	 * @return
	 */
	public List<String> getContentPath() {
		return contentPath;
	}
	
	/**
	 * When creating archive, it's source list of file/folder 
	 * when extracting archive, it's destination location where to extract content
	 * @return
	 */
	public void setContentPath(List<String> contentPath) {
		this.contentPath = contentPath;
	}
	
	/**
	 * When creating archive, it's source list of file/folder 
	 * when extracting archive, it's destination location where to extract content
	 * @return
	 */
	public void addContentPath(String path){
		this.contentPath.add(path);
	}
	
	public ProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	/**
	 * {@link ArchiveType} parameter, required only for creating archive.
	 * @return
	 */
	public ArchiveType getArchiveType() {
		return archiveType;
	}

	/**
	 * {@link ArchiveType} parameter, required only for creating archive.
	 * @return
	 */
	public void setArchiveType(ArchiveType archiveType) {
		this.archiveType = archiveType;
	}
	
	@Override
	public String toString() {
		return "ArchiverParams [archivePath=" + archivePath + ", contentPath="
				+ contentPath + ", archiveType=" + archiveType
				+ ", progressMonitor=" + progressMonitor + "]";
	}
}
