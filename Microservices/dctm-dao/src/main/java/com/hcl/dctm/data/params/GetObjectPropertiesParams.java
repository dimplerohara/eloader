package com.hcl.dctm.data.params;

import java.util.Arrays;

public class GetObjectPropertiesParams  extends DctmCommonParam{

	private String[] fields;
	private ObjectIdentity identity;
	
	public static GetObjectPropertiesParams newObject(){
		return new GetObjectPropertiesParams();
	}

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public ObjectIdentity getIdentity() {
		return identity;
	}

	public void setIdentity(ObjectIdentity identity) {
		this.identity = identity;
	}

	public String toString() {
		return "GetObjectPropertiesParams [fields=" + Arrays.toString(fields)
				+ ", identity=" + identity + "]";
	}

	public boolean isValid() {
		return null != identity && identity.isValid();
	}
}
