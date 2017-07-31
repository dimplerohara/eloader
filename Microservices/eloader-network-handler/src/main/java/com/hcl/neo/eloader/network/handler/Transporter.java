package com.hcl.neo.eloader.network.handler;

import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.params.DownloadParams;
import com.hcl.neo.eloader.network.handler.params.SessionParams;
import com.hcl.neo.eloader.network.handler.params.UploadParams;

public interface Transporter {

	public void setSessionParams(SessionParams sessionParams) throws TransporterException;
	public boolean upload(UploadParams uploadParams) throws TransporterException;
	public boolean download(DownloadParams downloadParams) throws TransporterException;
	public boolean testConnection() throws TransporterException;
}