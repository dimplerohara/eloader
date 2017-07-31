package com.hcl.neo.dctm.microservices.utils;


public class OperationCodes {

	/*
	 * Code starting with 
	 * 1xxxx: successful response
	 * 2xxxx: warning response
	 * 3xxxx: failure response
	 * 
	 * Error codes and generic messages could be kept in properties/xml file for more flexibility. 
	 */
	
	public static final OperationCode SUCCESS = new OperationCode(10001, "Operation completed successfully.");
	public static final OperationCode SUCCESS_ACCEPTED = new OperationCode(10002, "Request is accepted for processing.");
	// define more success codes here
	
	public static final OperationCode WARNING = new OperationCode(20001, "Operation completed with warning(s).");
	public static final OperationCode WARNING_NAME_CORRECTED = new OperationCode(20002, "Restricted characters like /\\:*?|<> were removed from object name.");
	// define more warning codes here
	
	public static final OperationCode FAILED = new OperationCode(30001, "Operation failed with error(s).");
	public static final OperationCode FAILED_NO_WRITE_PERMIT = new OperationCode(30002, "User does not have permission to set content/properties on object.");
	public static final OperationCode FAILED_NOT_FOUND = new OperationCode(30003, "Request object is not found in repository.");
	// define more failure codes here
}
