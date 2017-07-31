package com.hcl.neo.eloader.microservices.model;

import com.google.gson.Gson;

public class ErrorResponse {

	private String errorCode;
	private String errorMessage;
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String toJsonString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	@Override
	public String toString() {
		return "ErrorResponse [errorCode=" + errorCode + ", errorMessage="
				+ errorMessage + "]";
	}
}
