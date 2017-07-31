package com.hcl.neo.eloader.network.handler.scp;

import java.io.InputStream;
import java.io.OutputStream;

import com.hcl.neo.eloader.network.handler.client.Client;
import com.hcl.neo.eloader.network.handler.common.TransportType;
import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.params.SessionParams;

public class ScpClient implements Client {

	@Override
	public TransportType getTransportType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Client cloneClient() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void login() throws TransporterException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isValidSession() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mkdirs(String path) throws TransporterException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rmdirs(String path) throws TransporterException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exists(String path) throws TransporterException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getSize(String path) throws TransporterException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean goToHomeDir() throws TransporterException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName(String path) throws TransporterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAbsolutePath(String path) throws TransporterException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDirectory(String path) throws TransporterException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] listNamesOnly(String path) throws TransporterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean gotoDir(String path) throws TransporterException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getWorkingDirectory() throws TransporterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHomeDirectory() throws TransporterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream put(String path) throws TransporterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream put(String path, long offset)
			throws TransporterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream get(String path) throws TransporterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream get(String path, long offset)
			throws TransporterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean completeCommand() throws TransporterException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SessionParams getSessionParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResponseString() {
		// TODO Auto-generated method stub
		return null;
	}

}
