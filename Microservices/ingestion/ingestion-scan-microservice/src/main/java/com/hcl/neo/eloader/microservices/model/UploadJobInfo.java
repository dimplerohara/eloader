/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hcl.neo.eloader.microservices.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Sakshi Jain
 */
public class UploadJobInfo implements Serializable{
    @Override
	public String toString() {
		return "UploadJobInfo [parentFile=" + parentFile + ", defaultFolderType=" + defaultFolderType + ", folderTypes="
				+ folderTypes + ", repositoryPath=" + repositoryPath + ", fileCount=" + fileCount + ", folderCount="
				+ folderCount + ", contentSize=" + contentSize + ", packageSize=" + packageSize + ", illegalObjectList="
				+ illegalObjectList + ", selectedFilePaths=" + selectedFilePaths + ", businessGroup=" + businessGroup
				+ ", jobName=" + jobName + ", checksum=" + checksum + ", transportServerPath=" + transportServerPath
				+ "]";
	}

	private static final long serialVersionUID = 4L;
    private File parentFile;
    private String defaultFolderType;
    private String folderTypes;
    private String repositoryPath;
    private long fileCount;
	private long folderCount;
    private long contentSize;
    private long packageSize;
    public long getPackageSize() {
		return packageSize;
	}

	public void setPackageSize(long packageSize) {
		this.packageSize = packageSize;
	}

	private final ArrayList<String> illegalObjectList = new ArrayList<>();
    private ArrayList<String> selectedFilePaths = new ArrayList<>();
    private String businessGroup;
    private String jobName;
    private String checksum;
    private String transportServerPath;
    public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getTransportServerPath() {
		return transportServerPath;
	}

	public void setTransportServerPath(String transportServerPath) {
		this.transportServerPath = transportServerPath;
	}

	public File getParentFile() {
        return parentFile;
    }

    public void setParentFile(File parentFile) {
        this.parentFile = parentFile;
    }
    
    public String getDefaultFolderType() {
        return defaultFolderType;
    }

    public void setDefaultFolderType(String defaultFolderType) {
        this.defaultFolderType = defaultFolderType;
    }

    public String getFolderTypes() {
        return folderTypes;
    }

    public void setFolderTypes(String folderTypes) {
        this.folderTypes = folderTypes;
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public long getFileCount() {
        return fileCount;
    }

    public void setFileCount(long fileCount) {
        this.fileCount = fileCount;
    }

    public long getFolderCount() {
        return folderCount;
    }

    public void setFolderCount(long folderCount) {
        this.folderCount = folderCount;
    }

    public long getContentSize() {
        return contentSize;
    }

    public void setContentSize(long contentSize) {
        this.contentSize = contentSize;
    }

      

    public ArrayList<String> getIllegalObjectList() {
        return illegalObjectList;
    }

    public String getBusinessGroup() {
        return businessGroup;
    }

    public void setBusinessGroup(String businessGroup) {
        this.businessGroup = businessGroup;
    }

   

    public ArrayList<String> getSelectedFilePaths() {
        return selectedFilePaths;
    }    
    
    public void setSelectedFilePaths(ArrayList<String> selectedFilePaths) {
 		this.selectedFilePaths = selectedFilePaths;
 	}
    
}
