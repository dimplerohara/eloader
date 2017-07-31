package com.hcl.neo.eloader.microservices.params;

import java.util.ArrayList;
import java.util.List;

public class Response {
	private List<OperationObjectDetail> operationObjectDetails;

	public Response(){
		operationObjectDetails = new ArrayList<OperationObjectDetail>();
	}
	
	public List<OperationObjectDetail> getOperationObjectDetails() {
		return operationObjectDetails;
	}

	public void setOperationObjectDetails(List<OperationObjectDetail> operationObjectDetails) {
		this.operationObjectDetails = operationObjectDetails;
	}
	
	public void addOperationObjectDetails(OperationObjectDetail operationObjectDetail){
		this.operationObjectDetails.add(operationObjectDetail);
	}
	
	public void addOperationObjectDetails(List<OperationObjectDetail> operationObjectDetails){
		this.operationObjectDetails.addAll(operationObjectDetails);
	}
}