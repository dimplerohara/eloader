package com.hcl.neo.eloader.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "transport_server_master")
public class TransportServerMaster {

	@Id
    public String id;
	public Long serverId;
	public String protocol;
	public String host;	
	public int port;	
	public String userName;	
	public String password;	
	public String type;
	public String dispNetworkLoc;
	
	/**
	 * @return the serverId
	 */
	public long getServerId() {
		return serverId;
	}
	/**
	 * @param serverId the serverId to set
	 */
	public void setServerId(long serverId) {
		this.serverId = serverId;
	}
	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}
	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	
	/**
	 * @return the dispNetworkLoc
	 */
	public String getDispNetworkLoc() {
		return dispNetworkLoc;
	}
	/**
	 * @param dispNetworkLoc the dispNetworkLoc to set
	 */
	public void setDispNetworkLoc(String dispNetworkLoc) {
		this.dispNetworkLoc = dispNetworkLoc;
	}
	
	public TransportServerType getTransportServerType(){
		TransportServerType serverType = null;
		if(null != this.type){
			serverType = TransportServerType.valueFrom(this.type);
		}
		return serverType;
	}
	
	public TransportType getTransportType(){
		TransportType xportType = null;
		if(null != this.protocol){
			xportType = TransportType.valueOf(protocol);
		}
		return xportType;
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
	 * @param serverId the serverId to set
	 */
	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}
	
	@Override
	public String toString() {
		return "TransportServerMaster [id=" + id + ", serverId=" + serverId + ", protocol=" + protocol + ", host="
				+ host + ", port=" + port + ", userName=" + userName + ", password=" + password + ", type=" + type
				+ ", dispNetworkLoc=" + dispNetworkLoc + "]";
	}
}
