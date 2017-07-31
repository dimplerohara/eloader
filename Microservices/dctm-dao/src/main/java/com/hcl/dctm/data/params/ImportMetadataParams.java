package com.hcl.dctm.data.params;

public class ImportMetadataParams extends ImportParams {

	private String metadataFilePath;

	public String getMetadataFilePath() {
		return metadataFilePath;
	}

	public void setMetadataFilePath(String metadataFilePath) {
		this.metadataFilePath = metadataFilePath;
	}

	@Override
	public String toString() {
		return "ImportMetadataParams [metadataFilePath=" + metadataFilePath
				+ "]";
	}
}
