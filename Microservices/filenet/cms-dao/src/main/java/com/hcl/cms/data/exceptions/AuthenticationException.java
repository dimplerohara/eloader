package com.hcl.cms.data.exceptions;

public class AuthenticationException extends CmsException {

	private static final long serialVersionUID = 1L;

	public AuthenticationException(String message) {
		super(message);
	}
	
	public AuthenticationException(Throwable e) {
		super(e);
		setErrorMessage(e.getMessage());
		setStackTrace(e.getStackTrace());
	}
	
	public AuthenticationException(String message, Throwable e) {
		super(message, e);
		setErrorMessage(e.getMessage());
		setStackTrace(e.getStackTrace());
	}
}