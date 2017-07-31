package com.hcl.neo.eloader.model;

import java.io.Serializable;
import java.util.Map;

public class ServiceInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3318802621600466482L;
	private String host;
	private int port;
	private String uri;
	private String serviceId;
	private Map<String, String> metadata;
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
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}
	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	/**
	 * @return the serviceId
	 */
	public String getServiceId() {
		return serviceId;
	}
	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	/**
	 * @return the metadata
	 */
	public Map<String, String> getMetadata() {
		return metadata;
	}
	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}
	
	@Override
	public String toString() {
		return "ServiceInfo [host=" + host + ", port=" + port + ", uri=" + uri + ", serviceId=" + serviceId
				+ ", metadata=" + metadata + "]";
	}
}
