package com.hcl.neo.eloader.network.handler.params;

import java.util.ArrayList;
import java.util.List;

public abstract class Params {

	private List<String> errorList;
	
	public Params(){
		this.errorList = new ArrayList<String>();
	}
	
	public List<String> getErrorList(){
		return this.errorList;
	}
	
	public String getErrors(){
		String longMessage = "";
		for(String msg : this.errorList){
			longMessage = longMessage.concat(msg).concat("\n");
		}
		return longMessage;
	}
	
	public void addValidationErrorMessage(String message){
		errorList.add(message);
	}
	
	public abstract boolean validate();
	
	@Override
	public String toString() {
		return "Params [errorList=" + errorList + "]";
	}
}
