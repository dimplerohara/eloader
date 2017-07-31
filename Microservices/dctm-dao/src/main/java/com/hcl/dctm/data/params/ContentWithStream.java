package com.hcl.dctm.data.params;

import java.io.ByteArrayInputStream;

public class ContentWithStream {
	private ByteArrayInputStream body;
	private String contentType;
	public ByteArrayInputStream getBody() {
		return body;
	}
	public String getContentType() {
		return contentType;
	}public void setBody(ByteArrayInputStream body) {
		this.body = body;
	}public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
