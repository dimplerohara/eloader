package com.hcl.neo.eloader.microservices.model;

import com.hcl.neo.eloader.microservices.properties.Constants;

public class JobResponse {

	private Long jobId;
	private String status;
	/**
	 * @return the jobId
	 */
	public Long getJobId() {
		return jobId;
	}
	/**
	 * @param jobId the jobId to set
	 */
	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		if(null == status){
			status = Constants.STATUS_ERROR;
		}
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JobResponse [jobId=" + jobId + ", status=" + status + "]";
	}
}
