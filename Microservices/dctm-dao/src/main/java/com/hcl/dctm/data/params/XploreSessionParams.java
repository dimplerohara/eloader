package com.hcl.dctm.data.params;

public class XploreSessionParams extends DctmCommonParam{

	private String host;
	private int port;
	private String domain;
	
	public XploreSessionParams() {
		this.port=55000;
	}

	@Override
	public boolean isValid() {
		return null != host && null != domain;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public static XploreSessionParams newObject(){
		return new XploreSessionParams(); 
	}
	
	@Override
	public String toString() {
		return "XploreSessionParams [host=" + host + ", port=" + port
				+ ", domain=" + domain + "]";
	}
}
