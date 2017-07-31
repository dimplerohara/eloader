package com.hcl.neo.eloader.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "job_source_drop_location")
public class JobSourceDropLocationDetails {

	@Id
    public String id;
	public String ip;
	public String port;

	public String userName;
	public String password;
	public String srcLocation;
	public String targetLocation;
	public String targetCMS;
	public String locationType;
	public String businessGroup;

	
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

	@Override
	public String toString() {
		return "JobSourceDropLocationDetails [id=" + id + ", ip=" + ip + ", port=" + port + ", userName=" + userName
				+ ", password=" + password + ", srcLocation=" + srcLocation + ", targetLocation=" + targetLocation
				+ ", targetCMS=" + targetCMS + ", locationType=" + locationType + ", businessGroup=" + businessGroup
				+ "]";
	}	
}
