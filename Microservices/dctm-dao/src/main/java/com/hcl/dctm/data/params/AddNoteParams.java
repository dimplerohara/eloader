package com.hcl.dctm.data.params;

public class AddNoteParams extends DctmCommonParam {

	@Override
	public String toString() {
		return "AddNoteParams [identity=" + identity + ", note=" + note
				+ ", charset=" + charset + ", destIdentity=" + destIdentity
				+ "]";
	}
	@Override
	public boolean isValid() {
		return false;
	}

	private ObjectIdentity identity;
	private String note;
	private String charset;
	private ObjectIdentity destIdentity;
	public ObjectIdentity getIdentity() {
		return identity;
	}
	public void setIdentity(ObjectIdentity identity) {
		this.identity = identity;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public static AddNoteParams newObject(){
		return new AddNoteParams();
	}
	public ObjectIdentity getDestIdentity() {
		return destIdentity;
	}
	public void setDestIdentity(ObjectIdentity destIdentity) {
		this.destIdentity = destIdentity;
	}
}
