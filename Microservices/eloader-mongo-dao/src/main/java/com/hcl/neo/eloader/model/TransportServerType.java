package com.hcl.neo.eloader.model;

public enum TransportServerType {

	INTERNAL("I"), 
	EXTERNAL("E"), 
	CENTRAL("C");
	
	private final String value;
	
	private TransportServerType(String value) {
		this.value = value;
	}

	public static TransportServerType valueFrom(String value){
		TransportServerType type = null;
		switch(value){
		case "I":
			type = TransportServerType.INTERNAL;
			break;
		case "E":
			type = TransportServerType.EXTERNAL;
			break;
		case "C":
			type = TransportServerType.CENTRAL;
			break;
		case "i":
			type = TransportServerType.INTERNAL;
			break;
		case "e":
			type = TransportServerType.EXTERNAL;
			break;
		case "c":
			type = TransportServerType.CENTRAL;
			break;
		}
		return type;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
