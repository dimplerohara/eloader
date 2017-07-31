package com.hcl.neo.eloader.network.handler.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.network.handler.client.Client;
import com.hcl.neo.eloader.network.handler.common.TransportType;
import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.params.SessionParams;

class FtpClient extends FTPClient implements Client{

	private SessionParams sessionParams;
	private String homeDir;
	
	public FtpClient(SessionParams params){
		setSessionParams(params);
		this.homeDir = PATH_SEPARATOR;
	}
	
        @Override
	public void login() throws TransporterException {
		Logger.info(getClass(), "FtpClient.login - begin - "+getSessionParams());
		try {
			if(null == getSessionParams()){
				throw new TransporterException("Invalid SessionParams provided: "+getSessionParams());
			}
			if(!getSessionParams().validate()){
				throw new TransporterException("Invalid login parameters provided: "+getSessionParams().getErrors());
			}
			connect(getSessionParams().getHost(), getSessionParams().getPort());
			setOptions();
			if( FTPReply.isPositiveCompletion(getReplyCode()) ){
				boolean loginStatus = login(getSessionParams().getUser(), getSessionParams().getPassword());
				if(!loginStatus) {
					throw new TransporterException("Invalid login credetials provided. "+getReplyString());
				}
				enterLocalPassiveMode();
				setFileType(FtpClient.BINARY_FILE_TYPE);
				this.homeDir = printWorkingDirectory();
			}
			else{
				disconnect();
				throw new TransporterException("Invalid connection details provided. "+getReplyString());
			}
		}
		catch (Throwable e) {
			Logger.warn(getClass(), e.getMessage());
			throw new TransporterException(e);
		} 
		Logger.info(getClass(), "FtpClient.login - end - "+getSessionParams());
	}
	
	private void setOptions() throws Throwable{
		setKeepAlive(true);
		setControlKeepAliveTimeout(10); //sec
		setControlKeepAliveReplyTimeout(10000); //ms
		setDataTimeout(60000); //ms
		setTcpNoDelay(true);
		setBufferSize(ONE_MB);
		setReceieveDataSocketBufferSize(ONE_MB);
		setReceiveBufferSize(ONE_MB);
		setSendDataSocketBufferSize(ONE_MB);
		setSendBufferSize(ONE_MB);
	}

        @Override
	public void disconnect(){
		Logger.info(getClass(), "FtpClient.disconnect - begin - "+getSessionParams());
		try {	
			if(isConnected()) super.disconnect();
		} 
		catch (Throwable e) {
			Logger.warn(getClass(), e.getMessage()+" "+getReplyString());
		}
		Logger.info(getClass(), "FtpClient.disconnect - end - "+getSessionParams());
	}

        @Override
	public boolean isValidSession(){
		boolean isValid = true;
		try {
			printWorkingDirectory();
			isValid = true;
		} 
		catch (Throwable e) {
			isValid = false;
			Logger.warn(getClass(), e.getMessage()+" "+getReplyString());
		}
		return isValid;
	}
	
        @Override
	public boolean mkdirs(String path) throws TransporterException{
		try{
			if(isAbsolutePath(path)) goToHomeDir();
			return mkdirs1(path);
		}
		catch(Throwable e){
			Logger.warn(getClass(), e.getMessage()+" "+getReplyString());
			throw new TransporterException(e);	
		}
	}

	private boolean mkdirs1(String path) throws Throwable{
		
		boolean status = false;
		if(null == path || path.length() == 0 || path.equals(PATH_SEPARATOR)) return status;
		String token = "";
		if( path.startsWith(PATH_SEPARATOR) && path.length() > 1) path = path.substring(1);
		if(path.indexOf(PATH_SEPARATOR) != -1) token = path.substring(0, path.indexOf(PATH_SEPARATOR));
		else token = path;
		FTPFile[] ftpFiles = listFiles();
		FTPFile ftpFile = null;
		for(int index=0; index<ftpFiles.length; index++){
			if(token.equals(ftpFiles[index].getName())){
				ftpFile = ftpFiles[index];
				status = true;
				break;
			}
		}
		if(null == ftpFile) {
			status = makeDirectory(token);
			changeWorkingDirectory(token);
		}
		else if(ftpFile.isDirectory()) changeWorkingDirectory(token);
		else return false;
		if(path.length() > 0 && path.indexOf(PATH_SEPARATOR) > 0) {
			path = path.substring(path.indexOf(PATH_SEPARATOR), path.length());
			if(path.length() > 0 && !path.equals(PATH_SEPARATOR)) status = mkdirs1(path);
		}
		return status;
	}
	
        @Override
	public boolean rmdirs(String path) throws TransporterException{
		boolean status = false;
		try{
			path = path.replaceAll("/+$", "");
			if( !changeWorkingDirectory(path) ) return status;
			FTPFile[] ftpFiles = listFiles();
			for(int index=0; index<ftpFiles.length; index++){
				if(ftpFiles[index].isFile()) deleteFile(ftpFiles[index].getName());
				else{
					status = rmdirs(ftpFiles[index].getName());
				}
			}
			if(changeToParentDirectory()){
				String token = "";
				if( !path.contains(PATH_SEPARATOR) ) 
					token = path;
				else if(path.lastIndexOf(PATH_SEPARATOR) < path.length()-1)
					token = path.substring(path.lastIndexOf(PATH_SEPARATOR)+1, path.length());			
				status = removeDirectory(token);
			}
		}
		catch(Throwable e){
			Logger.warn(getClass(), e.getMessage()+" "+getReplyString());
			throw new TransporterException(e);
		}
		return status;
	}

        @Override
	public boolean exists(String path) throws TransporterException{
		try{
			FTPFile[] ftpFile = listFiles(path);
			return ftpFile.length > 0;
		}
		catch(Throwable e){
			Logger.warn(getClass(), e.getMessage()+" "+getReplyString());
			throw new TransporterException(e);
		}
	}

	public long getFileSize(String path) throws TransporterException {
		long size = 0;
		try{
			FTPFile[] ftpFile = listFiles(path);
			if(ftpFile.length > 0) size = ftpFile[0].getSize();
		}
		catch(Throwable e){
			Logger.warn(getClass(), e.getMessage()+" "+getReplyString());
			throw new TransporterException(e);
		}
		return size;
	}
	
        @Override
	public long getSize(String path) throws TransporterException {
		long size = 0;
		try{
			FTPFile[] ftpFiles = listFiles(path);
			for(FTPFile ftpFile : ftpFiles){
				size += ftpFile.getSize();
				if(ftpFile.isDirectory()){
					path = path + "/" +ftpFile.getName();
					path = path.replaceAll("//", "/");
					size = size + getSize(path);
				}
			}
		}
		catch(Throwable e){
			Logger.warn(getClass(), e.getMessage()+" "+getReplyString());
			throw new TransporterException(e);
		}
		return size;
	}

        @Override
	public boolean goToHomeDir() throws TransporterException {
		try{
			return changeWorkingDirectory(getHomeDirectory());
		}
		catch(Throwable e){
			Logger.warn(getClass(), e.getMessage()+" "+getReplyString());
			throw new TransporterException(e);
		}
	}
	
        @Override
	public String getName(String path) throws TransporterException{
		try{
			path = path.replaceAll(PATH_SEPARATOR+"$", "");
			return FilenameUtils.getName(path);
		}
		catch(Throwable e){
			Logger.warn(getClass(), e.getMessage()+" "+getReplyString());
			throw new TransporterException(e);
		}
	}

        @Override
	public boolean isAbsolutePath(String path) throws TransporterException{
		try{
			return path.startsWith(PATH_SEPARATOR);
		}
		catch(Throwable e){
			Logger.warn(getClass(), e.getMessage()+" "+getReplyString());
			throw new TransporterException(e);
		}
	}
	
        @Override
	public boolean isDirectory(String path) throws TransporterException{
		try{
			return changeWorkingDirectory(path);
		}
		catch(Throwable e){
			Logger.warn(getClass(), e.getMessage()+" "+getReplyString());
			throw new TransporterException(e);
		}
	}
	
        @Override
	public String[] listNamesOnly(String path) throws TransporterException{
		String[] list = new String[0];
		try {
			FTPFile[] files = listFiles(path);
			list = new String[files.length]; 
			for(int index=0; index<files.length; index++){
				list[index] = files[index].getName();
			}
		}
		catch (IOException e) {
			throw new TransporterException(e.getMessage()+" "+getReplyString(), e);
		}
		return list;
	}
	
        @Override
	public boolean gotoDir(String path) throws TransporterException {
		try {
			return changeWorkingDirectory(path);
		} 
		catch (IOException e) {
			throw new TransporterException(e.getMessage()+" "+getReplyString(), e);
		}
	}
	
        @Override
	public SessionParams getSessionParams() {
		return sessionParams;
	}

	public void setSessionParams(SessionParams sessionParams) {
		this.sessionParams = sessionParams;
	}

        @Override
	public String getWorkingDirectory() throws TransporterException {
		try {
			return printWorkingDirectory();
		}
		catch (IOException e) {
			throw new TransporterException(e.getMessage()+" "+getReplyString(), e);
		}
	}

        @Override
	public String getHomeDirectory() throws TransporterException {
		return this.homeDir;
	}

	@Override
	public OutputStream put(String path) throws TransporterException {
		return put(path, 0);
	}

	@Override
	public OutputStream put(String path, long offset) throws TransporterException {
		OutputStream os = null;
		try {
			setRestartOffset(offset);
			if(exists(path)){
				os = appendFileStream(path);
			}
			else{
				os = storeFileStream(path);
			}
			if(null == os){
				throw new TransporterException("Unable to open output stream. "+getReplyString()+"; working dir: "+ printWorkingDirectory()+"; path: "+path+"; offset: "+offset);
			}
			return os;
		} 
		catch (IOException e) {
			throw new TransporterException(e.getMessage()+" "+getReplyString(), e);
		}
	}
	
	@Override
	public InputStream get(String path) throws TransporterException {
		try {
			return retrieveFileStream(path);
		} 
		catch (IOException e) {
			throw new TransporterException(e.getMessage()+" "+getReplyString(), e);
		}
	}

	@Override
	public InputStream get(String path, long offset) throws TransporterException {
		try {
			setRestartOffset(offset);
			return retrieveFileStream(path);
		} 
		catch (IOException e) {
			throw new TransporterException(e.getMessage()+" "+getReplyString(), e);
		}
	}

	@Override
	public boolean completeCommand() throws TransporterException {
		try {
			return completePendingCommand();
		} 
		catch (IOException e) {
			throw new TransporterException(e.getMessage()+" "+getReplyString(), e);
		}
	}

	@Override
	public TransportType getTransportType() {
		return TransportType.FTP;
	}

	@Override
	public Client cloneClient() {
		return new FtpClient(this.sessionParams);
	}

	@Override
	public String getResponseString() {
		return getReplyString();
	}
}