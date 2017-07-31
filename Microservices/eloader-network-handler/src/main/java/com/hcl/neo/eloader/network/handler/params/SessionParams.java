package com.hcl.neo.eloader.network.handler.params;

public class SessionParams extends Params{

	public static final int DEFAULT_FTP_PORT=21;
	public static final int DEFAULT_SFTP_PORT=22;
	private String host;
	private int port;
	private String user;
	private String password;
	
	public SessionParams(){
		setPort(DEFAULT_FTP_PORT);
	}
	
	@Override
	public boolean validate() {
		getErrorList().clear();
		if( null == getHost() || "".equals(getHost()) ){
			addValidationErrorMessage("Invalid host: "+getHost());
		}
		if( null == getUser() || "".equals(getUser()) ){
			addValidationErrorMessage("Invalid user: "+getUser());
		}
		if( null == getPassword() ){
			addValidationErrorMessage("Password can't be null");
		}
		return getErrorList().size() == 0;
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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "SessionParams [host=" + host + ", port=" + port + ", user="	+ user + ", password=*gotcha*]";
	}
}
