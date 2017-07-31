package com.hcl.neo.eloader.microservices.model;

public class IngestionJob {

	private Long landZoneId;
	private String businessLine;
	private String jobType;
	
	/**
	 * @return the landZoneId
	 */
	public Long getLandZoneId() {
		return landZoneId;
	}
	/**
	 * @param landZoneId the landZoneId to set
	 */
	public void setLandZoneId(Long landZoneId) {
		this.landZoneId = landZoneId;
	}
	/**
	 * @return the businessLine
	 */
	public String getBusinessLine() {
		return businessLine;
	}
	/**
	 * @param businessLine the businessLine to set
	 */
	public void setBusinessLine(String businessLine) {
		this.businessLine = businessLine;
	}
	
	/**
	 * @return the jobType
	 */
	public String getJobType() {
		return jobType;
	}
	/**
	 * @param jobType the jobType to set
	 */
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	
	@Override
	public String toString() {
		return "IngestionJob [landZoneId=" + landZoneId + ", businessLine=" + businessLine + ", jobType=" + jobType
				+ "]";
	}
	
}
