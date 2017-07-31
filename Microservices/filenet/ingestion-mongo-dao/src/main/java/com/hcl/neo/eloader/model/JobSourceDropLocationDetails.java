package com.hcl.neo.eloader.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "job_source_drop_location")
public class JobSourceDropLocationDetails {

	@Id
    public String id;
	public Long dropLocId;
	public String ip;
	public String port;
	public String userName;
	public String password;
	public String srcLocation;
	public String targetLocation;
	public String targetCMS;
	public String locationType;
	public String businessGroup;
	public String geoLocation;
	public String errorLocation;
	public String backupLocation;
	public String name;

	
	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getSrcLocation() {
		return srcLocation;
	}


	public void setSrcLocation(String srcLocation) {
		this.srcLocation = srcLocation;
	}


	public String getTargetLocation() {
		return targetLocation;
	}


	public void setTargetLocation(String targetLocation) {
		this.targetLocation = targetLocation;
	}


	public String getTargetCMS() {
		return targetCMS;
	}


	public void setTargetCMS(String targetCMS) {
		this.targetCMS = targetCMS;
	}


	public String getLocationType() {
		return locationType;
	}


	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}


	public String getBusinessGroup() {
		return businessGroup;
	}


	public void setBusinessGroup(String businessGroup) {
		this.businessGroup = businessGroup;
	}


	public String getPort() {
		return port;
	}


	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return the geoLocation
	 */
	public String getGeoLocation() {
		return geoLocation;
	}


	/**
	 * @param geoLocation the geoLocation to set
	 */
	public void setGeoLocation(String geoLocation) {
		this.geoLocation = geoLocation;
	}


	/**
	 * @return the errorLocation
	 */
	public String getErrorLocation() {
		return errorLocation;
	}


	/**
	 * @param errorLocation the errorLocation to set
	 */
	public void setErrorLocation(String errorLocation) {
		this.errorLocation = errorLocation;
	}


	/**
	 * @return the backupLocation
	 */
	public String getBackupLocation() {
		return backupLocation;
	}


	/**
	 * @param backupLocation the backupLocation to set
	 */
	public void setBackupLocation(String backupLocation) {
		this.backupLocation = backupLocation;
	}


	/**
	 * @return the dropLocId
	 */
	public Long getDropLocId() {
		return dropLocId;
	}


	/**
	 * @param dropLocId the dropLocId to set
	 */
	public void setDropLocId(Long dropLocId) {
		this.dropLocId = dropLocId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JobSourceDropLocationDetails [id=" + id + ", dropLocId=" + dropLocId + ", ip=" + ip + ", port=" + port
				+ ", userName=" + userName + ", password=" + password + ", srcLocation=" + srcLocation
				+ ", targetLocation=" + targetLocation + ", targetCMS=" + targetCMS + ", locationType=" + locationType
				+ ", businessGroup=" + businessGroup + ", geoLocation=" + geoLocation + ", errorLocation="
				+ errorLocation + ", backupLocation=" + backupLocation + ", name=" + name + "]";
	}	
}
