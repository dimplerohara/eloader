package com.hcl.neo.eloader.microservices.params;

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
