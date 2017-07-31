package com.hcl.neo.eloader.microservices.params;

public enum BulkJobStatus {

	CREATED,
	QUEUED,
	QUEUED_TRANSPORTER,
	QUEUED_ARCHIVER,
	QUEUED_REPO,
	IN_PROGRESS_TRANSPORTER,
	IN_PROGRESS_ARCHIVER,
	IN_PROGRESS_REPO,
	COMPLETED,
	FAILED,
	PARTIAL_SUCCESS
}