package com.hcl.dctm.data.params;

import java.util.ArrayList;
import java.util.List;

public class VirtualDocumentNode{
	private ObjectIdentity identity;
	private List <VirtualDocumentNode> childNodes;
	
	public VirtualDocumentNode(){
		childNodes = new ArrayList<VirtualDocumentNode>();
	}
	
	public List<VirtualDocumentNode> getChildNodes() {
		return childNodes;
	}
	public void setChildNodes(List<VirtualDocumentNode> childNodes) {
		this.childNodes = childNodes;
	}
	public void addChildNode(VirtualDocumentNode node){
		childNodes.add(node);
	}
	public static VirtualDocumentNode newObject(){
		return new VirtualDocumentNode();
	}
	public ObjectIdentity getIdentity() {
		return identity;
	}
	public void setIdentity(ObjectIdentity identity) {
		this.identity = identity;
	}

	@Override
	public String toString() {
		return "VirtualDocumentNode [identity=" + identity + ", childNodes="
				+ childNodes + "]";
	}
}