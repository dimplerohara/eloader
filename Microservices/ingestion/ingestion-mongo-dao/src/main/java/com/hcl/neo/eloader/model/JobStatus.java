package com.hcl.neo.eloader.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "job_status")
public class JobStatus {

	@Id
    public String id;
	
	public Long jobId;
	public String status;
	public Date statusDate;
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
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the statusDate
	 */
	public Date getStatusDate() {
		return statusDate;
	}
	/**
	 * @param statusDate the statusDate to set
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}
	
	@Override
	public String toString() {
		return "JobStatus [id=" + id + ", jobId=" + jobId + ", status=" + status + ", statusDate=" + statusDate + "]";
	}
}
