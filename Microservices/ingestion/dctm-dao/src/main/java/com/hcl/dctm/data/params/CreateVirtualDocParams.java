package com.hcl.dctm.data.params;

public class CreateVirtualDocParams extends DctmCommonParam {

	@Override
	public boolean isValid() {
		return null != destIdentity && destIdentity.isValid()&& null != node && node.getIdentity().isValid();
	}
	public ObjectIdentity getDestIdentity() {
		return destIdentity;
	}
	public void setDestIdentity(ObjectIdentity destIdentity) {
		this.destIdentity = destIdentity;
	}
	public VirtualDocumentNode getNode() {
		return node;
	}
	public void setNode(VirtualDocumentNode node) {
		this.node = node;
	}
	public static CreateVirtualDocParams newObject(){
		return new CreateVirtualDocParams();
	}
	public static VirtualDocumentNode newVirtualDocumentNode(){
		return new VirtualDocumentNode();
	}
	
	private ObjectIdentity destIdentity;
	private VirtualDocumentNode node;
	@Override
	public String toString() {
		return "CreateVirtualDocParams [destIdentity=" + destIdentity
				+ ", node=" + node + "]";
	}
}