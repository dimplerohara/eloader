package com.hcl.dctm.data.common;

import java.io.File;
import java.io.FilenameFilter;

public class MetadataFilenameFilter implements FilenameFilter {

	public static final String[] metadataFileNames = {"dctmMetadata.xml", "dctmMetadata.xls", "dctmMetadata.xlsx"};
	
	@Override
	public boolean accept(File dir, String name) {
		boolean status = false;
		for(String metadataFileName : metadataFileNames){
			status = metadataFileName.equalsIgnoreCase(name);
			if(status) break;
		}
		return status;
	}
}
