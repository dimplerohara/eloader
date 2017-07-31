package com.hcl.dctm.data.params;

public class GetObjectTreeParam extends DctmCommonParam {

	private String rootFolderType;
	private String objectId;
	private int depth;
	private boolean returnOnlyFolders;
	
	public GetObjectTreeParam() {
		setDepth(0);
		setReturnOnlyFolders(false);
		setRootFolderType("dm_cabinet");
	}

	@Override
	public boolean isValid() {
		return true;
	}
	
	public static GetObjectTreeParam newObject(){
		return new GetObjectTreeParam();
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public boolean isReturnOnlyFolders() {
		return returnOnlyFolders;
	}

	public void setReturnOnlyFolders(boolean returnOnlyFolders) {
		this.returnOnlyFolders = returnOnlyFolders;
	}

	public String getRootFolderType() {
		return rootFolderType;
	}

	/**
	 * Works only when no object id is provided
	 * @param rootFolderType
	 */
	public void setRootFolderType(String rootFolderType) {
		this.rootFolderType = rootFolderType;
	}

	@Override
	public String toString() {
		return "GetObjectTreeParam [rootFolderType=" + rootFolderType
				+ ", objectId=" + objectId + ", depth=" + depth
				+ ", returnOnlyFolders=" + returnOnlyFolders + "]";
	}
}
