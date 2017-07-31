package com.hcl.cms.data.exceptions;

public class CmsException extends Exception{
	private static final long serialVersionUID = 1L;
	private String errorMessage;
	
	public CmsException(String message) {
		super(message);
	}
	
	public CmsException(Throwable e) {
		super(e);
		setErrorMessage(e.getMessage());
		setStackTrace(e.getStackTrace());
	}
	
	public CmsException(String message, Throwable e) {
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
