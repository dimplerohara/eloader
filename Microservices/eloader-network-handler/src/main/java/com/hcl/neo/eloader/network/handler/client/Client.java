package com.hcl.neo.eloader.network.handler.client;

import java.io.InputStream;
import java.io.OutputStream;

import com.hcl.neo.eloader.network.handler.common.TransportType;
import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.params.SessionParams;

public interface Client {
	
	public static final int ONE_MB = 1048576;
	public static final int CHUNK_SIZE = 8192;

	/**
	 * 
	 * @return
	 */
	public static final String PATH_SEPARATOR = "/";
	
	/**
	 * 
	 * @return
	 */
	public TransportType getTransportType();

	/**
	 * 
	 * @return
	 */
	public Client cloneClient();
	
	/**
	 * 
	 * @return
	 */
	public void login() throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public void disconnect();

	/**
	 * 
	 * @return
	 */
	public boolean isValidSession();

	/**
	 * 
	 * @return
	 */
	public boolean mkdirs(String path) throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public boolean rmdirs(String path) throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public boolean exists(String path) throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public long getSize(String path) throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public boolean goToHomeDir() throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public String getName(String path) throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public boolean isAbsolutePath(String path) throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public boolean isDirectory(String path) throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public String[] listNamesOnly(String path) throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public boolean gotoDir(String path) throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public String getWorkingDirectory() throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public String getHomeDirectory() throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public OutputStream put(String path) throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public OutputStream put(String path, long offset) throws TransporterException;
	
	/**
	 * 
	 * @return
	 */
	public InputStream get(String path) throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public InputStream get(String path, long offset) throws TransporterException;

	/**
	 * 
	 * @return
	 */
	public boolean completeCommand() throws TransporterException;
	
	/**
	 * 
	 * @return
	 */
	public SessionParams getSessionParams();
	
	/**
	 * 
	 * @return
	 */
	public String getResponseString(); 
}