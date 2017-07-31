package com.hcl.neo.eloader.network.handler.exceptions;

public class TransporterException extends Exception{

	private static final long serialVersionUID = 1L;
	private String errorMessage;
	
	public TransporterException(String message) {
		super(message);
	}
	
	public TransporterException(Throwable e) {
		super(e);
		setErrorMessage(e.getMessage());
		setStackTrace(e.getStackTrace());
	}
	
	public TransporterException(String message, Throwable e) {
		super(message, e);
		setErrorMessage(e.getMessage());
		setStackTrace(e.getStackTrace());
	}

	public String getErrorMessage() {
		return null == this.errorMessage ? "Internal server exception occured." : this.errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
