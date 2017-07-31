package com.hcl.neo.eloader.microservices.exceptions;

import com.hcl.neo.eloader.exceptions.BulkException;

public class EloaderDbException extends BulkException{

	private static final long serialVersionUID = 1L;
	
	public EloaderDbException(String message) {
		super(message);
	}
	
	public EloaderDbException(Throwable e) {
		super(e);
		setErrorMessage(e.getMessage());
		setStackTrace(e.getStackTrace());
	}
	
	public EloaderDbException(String message, Throwable e) {
		super(message, e);
		setErrorMessage(e.getMessage());
		setStackTrace(e.getStackTrace());
	}
}
