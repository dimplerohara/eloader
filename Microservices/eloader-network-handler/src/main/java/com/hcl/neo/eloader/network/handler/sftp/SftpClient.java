package com.hcl.neo.eloader.network.handler.sftp;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.network.handler.client.Client;
import com.hcl.neo.eloader.network.handler.common.TransportType;
import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.params.SessionParams;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;
import org.apache.commons.io.FilenameUtils;

class SftpClient implements Client{
	
	private SessionParams sessionParams;
	private String homeDir;
	private ChannelSftp sftpChannel;
	
	public SftpClient(SessionParams sessionParams){
		this.sessionParams = sessionParams;
		this.homeDir = PATH_SEPARATOR;
	}
	
	public SessionParams getSessionParams() {
		return sessionParams;
	}

	public void login() throws TransporterException {
		Logger.info(getClass(), "SftpClient.login - begin - "+getSessionParams());
		try{
			JSch client = new JSch();
			Session session = client.getSession(getSessionParams().getUser(), getSessionParams().getHost(), getSessionParams().getPort());
			session.setPassword(getSessionParams().getPassword());
			// disable host key check
			session.setConfig("StrictHostKeyChecking", "no");
			// disable client side private key generation(Kerberos)
            session.setConfig("PreferredAuthentications", "keyboard-interactive,password,publickey");                        
			session.connect();
			
			sftpChannel = (ChannelSftp) session.openChannel(CHANNEL_TYPE_SFTP);
			sftpChannel.connect();
			homeDir = sftpChannel.getHome();
		}
		catch(Throwable e){
			throw new TransporterException(e);
		}
		Logger.info(getClass(), "SftpClient.login - end - "+getSessionParams());
	}

	public void disconnect() {
		if(sftpChannel.isConnected()) sftpChannel.disconnect();		
	}

	public boolean isValidSession() {
		boolean status = false;
		try {
			sftpChannel.getServerVersion();
			status = true;
		} 
		catch (SftpException e) {
			Logger.warn(getClass(), e.getMessage());
		}
		return status;
	}

	public boolean mkdirs(String path) throws TransporterException {
		try{
			if(isAbsolutePath(path)) goToHomeDir();
			return mkdirs1(path);
		}
		catch(Throwable e){
			Logger.warn(getClass(), e.getMessage());
			throw new TransporterException(e);	
		}
	}
	
	@SuppressWarnings("rawtypes")
	private boolean mkdirs1(String path) throws Throwable{
		boolean status = false;
		if(null == path || path.length() == 0 || path.equals(PATH_SEPARATOR)) return status;
		String token = "";
		if( path.startsWith(PATH_SEPARATOR) && path.length() > 1) path = path.substring(1);
		if(path.indexOf(PATH_SEPARATOR) != -1) token = path.substring(0, path.indexOf(PATH_SEPARATOR));
		else token = path;
		Vector files = sftpChannel.ls(".");
		LsEntry sftpFile = null;
		for(Object file : files){
			LsEntry entry = (LsEntry) file;
			if(token.equals(entry.getFilename())){
				sftpFile = entry;
				status = true;
				break;
			}
		}
		if(null == sftpFile) {
			try{
				sftpChannel.mkdir(token);
			}
			catch(SftpException e){
				Logger.warn(getClass(), e.getMessage()+" "+ sftpChannel.pwd()+PATH_SEPARATOR+token);
			}
			sftpChannel.cd(token);
		}
		else if(sftpFile.getAttrs().isDir()) {
			sftpChannel.cd(token);
		}
		else{
			return false;
		}
		
		if(path.length() > 0 && path.indexOf(PATH_SEPARATOR) > 0) {
			path = path.substring(path.indexOf(PATH_SEPARATOR), path.length());
			if(path.length() > 0 && !path.equals(PATH_SEPARATOR)) {
				status = mkdirs1(path);
			}
		}
		return status;
	}

	@SuppressWarnings("rawtypes")
	public boolean rmdirs(String path) throws TransporterException {
		boolean status = false;
		try{
			path = path.replaceAll("/+$", "");
			sftpChannel.cd(path);
			Vector files = sftpChannel.ls(".");
			for(Object file : files){
				LsEntry entry = (LsEntry) file;
				if(entry.getAttrs().isDir()){
					status = rmdirs(entry.getLongname());
				}
				else{
					sftpChannel.rm(entry.toString());
				}
			}
			
			sftpChannel.cd("..");
			String token = "";
			if( !path.contains(PATH_SEPARATOR) ){
				token = path;
			}
			else if(path.lastIndexOf(PATH_SEPARATOR) < path.length()-1){
				token = path.substring(path.lastIndexOf(PATH_SEPARATOR)+1, path.length());
			}
			sftpChannel.rmdir(token);
			status = true;
		}
		catch(Throwable e){
			Logger.error(getClass(), e.getMessage());
			throw new TransporterException(e);
		}
		return status;
	}

	@SuppressWarnings({ "rawtypes" })
	public boolean exists(String path) throws TransporterException {
		boolean exists = false;
		try{
			Vector list = sftpChannel.ls(path);
			exists = list.size() > 0;
		}
		catch(Throwable e){
			Logger.warn(getClass(), e.getMessage() +" "+ path);
			exists = false;
		}
		return exists;
	}

	@SuppressWarnings("unchecked")
	public long getSize(String path) throws TransporterException {
		long size = 0;
		try {
			Vector<LsEntry> files = sftpChannel.ls(path);
			for(LsEntry file : files) {
				if( (".".equals(file.getFilename()) || "..".equals(file.getFilename())) ) continue;
				size += file.getAttrs().getSize();
				if(file.getAttrs().isDir()){
					path = path + "/" +file.getFilename();
					path = path.replaceAll("//", "/");
					size = size + getSize(path);
				}
			}	
		} 
		catch (SftpException e) {
			Logger.warn(getClass(), e.getMessage()+" "+path);
		}	
		return size;
	}

	public boolean goToHomeDir() throws TransporterException {
		boolean status = false;
		try {
			sftpChannel.cd(getHomeDirectory());
			status = true;
		} 
		catch (SftpException e) {
			throw new TransporterException(e);
		}
		return status;
	}

	public String getName(String path) throws TransporterException {
		try{
			path = path.replaceAll(PATH_SEPARATOR+"$", "");
			return FilenameUtils.getName(path);
		}
		catch(Throwable e){
			Logger.warn(getClass(), e.getMessage());
			throw new TransporterException(e);
		}
	}

	public boolean isAbsolutePath(String path) throws TransporterException{
		try{
			return path.startsWith(PATH_SEPARATOR);
		}
		catch(Throwable e){
			Logger.warn(getClass(), e.getMessage());
			throw new TransporterException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean isDirectory(String path) throws TransporterException {
		boolean status = false;
		try {
			Vector list = sftpChannel.ls(path);
			for(Object f : list){
				LsEntry entry = (LsEntry) f;
				status = entry.getAttrs().isDir();
			}
		} 
		catch (SftpException e) {
			status = false;
			throw new TransporterException(e);
		}
		return status;
	}

	@SuppressWarnings("rawtypes")
	public String[] listNamesOnly(String path) throws TransporterException {
		String[] list = null;
		try {
			Vector files = sftpChannel.ls(path);
			ArrayList<String> tempList = new ArrayList<String>();
			for(int index=0; index<files.size(); index++){
				LsEntry entry = (LsEntry) files.get(index);
				if( !(".".equals(entry.getFilename()) || "..".equals(entry.getFilename())) ){
					tempList.add(entry.getFilename());
				}
			}
			tempList.trimToSize();
			list = new String[tempList.size()];
			list = tempList.toArray(list);
		} 
		catch (SftpException e) {
			throw new TransporterException(e.getMessage()+" - "+path, e);
		}
		return list;
	}

	public boolean gotoDir(String path) throws TransporterException {
		boolean status = false;
		try {
			String[] pathList = path.split(PATH_SEPARATOR);
			for(String dir : pathList){
				sftpChannel.cd(dir);
			}
			status = true;
		} 
		catch (SftpException e) {
			throw new TransporterException(e);
		}
		return status;
	}
	
	public ChannelSftp getSftpChannel() {
		return sftpChannel;
	}

	public String getWorkingDirectory() throws TransporterException {
		try {
			return sftpChannel.pwd();
		} 
		catch (SftpException e) {
			throw new TransporterException(e); 
		}
	}

	public String getHomeDirectory() throws TransporterException {
		return this.homeDir;
	}

	@Override
	public TransportType getTransportType() {
		return TransportType.SFTP;
	}

	@Override
	public Client cloneClient() {
		return new SftpClient(this.sessionParams);
	}

	@Override
	public OutputStream put(String path) throws TransporterException {
		try{
			return sftpChannel.put(path);
		}
		catch(Throwable e){
			throw new TransporterException(e);
		}
	}

	@Override
	public OutputStream put(String path, long offset) throws TransporterException {
		try{
			return sftpChannel.put(path, null, ChannelSftp.OVERWRITE, offset);
		}
		catch(Throwable e){
			throw new TransporterException(e);
		}
	}

	@Override
	public InputStream get(String path) throws TransporterException {
		try{
			return sftpChannel.get(path);
		}
		catch(Throwable e){
			throw new TransporterException(e);
		}
	}

	@Override
	public InputStream get(String path, long offset) throws TransporterException {
		try{
			return sftpChannel.get(path, null, offset);
		}
		catch(Throwable e){
			throw new TransporterException(e);
		}	
	}

	/**
	 * Not implemented
	 */
	@Override
	public String getResponseString() {
		return "";
	}
	
	/**
	 * Not implemented
	 */
	@Override
	public boolean completeCommand() throws TransporterException {
		return false;
	}
	
	public static final String CHANNEL_TYPE_SESSION = "session";
	public static final String CHANNEL_TYPE_SHELL = "shell";
	public static final String CHANNEL_TYPE_EXEC = "exec";
	public static final String CHANNEL_TYPE_X11 = "x11";
	public static final String CHANNEL_TYPE_AUTH_AGENT_OPENSSH = "auth-agent@openssh.com";
	public static final String CHANNEL_TYPE_DIRECT_TCP_IP = "direct-tcpip";
	public static final String CHANNEL_TYPE_FORWARDED_TCP_IP = "forwarded-tcpip";
	public static final String CHANNEL_TYPE_SFTP = "sftp";
	public static final String CHANNEL_TYPE_SUBSYSTEM = "subsystem";

}