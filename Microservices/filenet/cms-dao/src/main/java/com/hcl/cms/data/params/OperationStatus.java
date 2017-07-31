package com.hcl.cms.data.params;

import java.util.List;

public class OperationStatus {

	private boolean status;
	
	private List<OperationObjectDetail> operationObjectDetails;
	
	private String jobId;

	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * @return the operationObjectDetails
	 */
	public List<OperationObjectDetail> getOperationObjectDetails() {
		return operationObjectDetails;
	}

	/**
	 * @param operationObjectDetails the operationObjectDetails to set
	 */
	public void setOperationObjectDetails(List<OperationObjectDetail> operationObjectDetails) {
		this.operationObjectDetails = operationObjectDetails;
	}

	
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OperationStatus [status=" + status + ", operationObjectDetails=" + operationObjectDetails + ", jobId="
				+ jobId + "]";
	}
}
