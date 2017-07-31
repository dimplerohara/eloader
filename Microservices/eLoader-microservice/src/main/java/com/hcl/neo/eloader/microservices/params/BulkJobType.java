package com.hcl.neo.eloader.microservices.params;

public enum BulkJobType {

	IMPORT(true),
	EXPORT(false),
	CHECKIN(true),
	CHECKOUT(false),
	IMPORT_PLUS(true),
	EXPORT_PLUS(false),
	CANCEL_CHECKOUT(true),
	CHECKOUT_PLUS(false),
	IMPORT_METADATA(true),
	EXPORT_METADATA(false);
	
	private final boolean inbound;
	private BulkJobType(boolean inbound){
		this.inbound = inbound;
	}
	public boolean isInbound(){
		return this.inbound;
	}
}
