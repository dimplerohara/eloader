package com.hcl.neo.cms.microservices.model;

public class JobResponse {

	private String jobId;
	private String status;
	/**
	 * @return the jobId
	 */
	public String getJobId() {
		return jobId;
	}
	/**
	 * @param jobId the jobId to set
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		if(null == status){
			status = "Error";
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
