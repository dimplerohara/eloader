package com.hcl.neo.eloader.microservices.params;

import java.util.List;

public class BulkJobParams {

    public BulkJobParams() {
        objectTypes = new ObjectTypes();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BulkJobType getType() {
        return type;
    }

    public void setType(BulkJobType type) {
        this.type = type;
    }

    public BulkJobStatus getStatus() {
        return status;
    }

    public void setStatus(BulkJobStatus status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
    }

    public String getPackageChecksum() {
        return packageChecksum;
    }

    public void setPackageChecksum(String packageChecksum) {
        this.packageChecksum = packageChecksum;
    }

    public long getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(long packageSize) {
        this.packageSize = packageSize;
    }

    public long getPackageFolderCount() {
        return packageFolderCount;
    }

    public void setPackageFolderCount(long packageFolderCount) {
        this.packageFolderCount = packageFolderCount;
    }

    public long getPackageFileCount() {
        return packageFileCount;
    }

    public void setPackageFileCount(long packageFileCount) {
        this.packageFileCount = packageFileCount;
    }

    public long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public long getTransportServerId() {
        return transportServerId;
    }

    public void setTransportServerId(long transportServerId) {
        this.transportServerId = transportServerId;
    }

    public String getTransportServerPath() {
        return transportServerPath;
    }

    public void setTransportServerPath(String transportServerPath) {
        this.transportServerPath = transportServerPath;
    }

    public String getClientOs() {
        return clientOs;
    }

    public void setClientOs(String clientOs) {
        this.clientOs = clientOs;
    }

    public String getBusinessGroup() {
        return businessGroup;
    }

    public void setBusinessGroup(String businessGroup) {
        this.businessGroup = businessGroup;
    }

    public String getFolderTypes() {
        return folderTypes;
    }

    public void setFolderTypes(String folderTypes) {
        this.folderTypes = folderTypes;
    }    

    public ObjectTypes getObjectTypes() {
        return objectTypes;
    }

    public void setObjectTypes(ObjectTypes objectTypes) {
        this.objectTypes = objectTypes;
    }    

    public void addObjectType(String path, String type) {
        objectTypes.addObjectType(path, type);
    }

    /**
	 * @return the repositoryPaths
	 */
	public List<String> getRepositoryPaths() {
		return repositoryPaths;
	}

	/**
	 * @param repositoryPaths the repositoryPaths to set
	 */
	public void setRepositoryPaths(List<String> repositoryPaths) {
		this.repositoryPaths = repositoryPaths;
	}

	/**
	 * @return the landZoneId
	 */
	public Long getLandZoneId() {
		return landZoneId;
	}

	/**
	 * @param landZoneId the landZoneId to set
	 */
	public void setLandZoneId(Long landZoneId) {
		this.landZoneId = landZoneId;
	}
	
	@Override
	public String toString() {
		return "BulkJobParams [id=" + id + ", name=" + name + ", type=" + type + ", status=" + status + ", userId="
				+ userId + ", userName=" + userName + ", userEmail=" + userEmail + ", createDate=" + createDate
				+ ", completeDate=" + completeDate + ", packageChecksum=" + packageChecksum + ", packageSize="
				+ packageSize + ", packageFolderCount=" + packageFolderCount + ", packageFileCount=" + packageFileCount
				+ ", repositoryId=" + repositoryId + ", transportServerId=" + transportServerId
				+ ", transportServerPath=" + transportServerPath + ", clientOs=" + clientOs + ", businessGroup="
				+ businessGroup + ", folderTypes=" + folderTypes + ", objectTypes=" + objectTypes + ", repositoryPaths="
				+ repositoryPaths + ", landZoneId=" + landZoneId + "]";
	}
    private Long id;
    private String name;
    private BulkJobType type;
    private BulkJobStatus status;
    private String userId;
    private String userName;
    private String userEmail;
    private String createDate;
    private String completeDate;
    private String packageChecksum;
    private long packageSize;
    private long packageFolderCount;
    private long packageFileCount;
    private long repositoryId;
    private long transportServerId;
    private String transportServerPath;
    private String clientOs;
    private String businessGroup;
    private String folderTypes;
    private ObjectTypes objectTypes;
    private List<String> repositoryPaths;

    private Long landZoneId;


    public static final String CLIENT_OS_MAC = "MACINTOSH";
    public static final String CLIENT_OS_WIN = "WINDOWS";
}
