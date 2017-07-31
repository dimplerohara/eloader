package com.hcl.neo.eloader.network.handler.sftp;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.network.handler.Transporter;
import com.hcl.neo.eloader.network.handler.client.Client;
import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.operation.DownloadOperation;
import com.hcl.neo.eloader.network.handler.operation.UploadOperation;
import com.hcl.neo.eloader.network.handler.params.DownloadParams;
import com.hcl.neo.eloader.network.handler.params.SessionParams;
import com.hcl.neo.eloader.network.handler.params.UploadParams;

class SftpTransporter implements Transporter{

private Client transportClient;
	
	public void setSessionParams(SessionParams sessionParams) throws TransporterException {
		this.transportClient = new SftpClient(sessionParams);
	}
	
	public boolean upload(UploadParams uploadParams) throws TransporterException {
		UploadOperation uploader = new UploadOperation(this.transportClient);
		return uploader.upload(uploadParams);
	}

	public boolean download(DownloadParams downloadParams) throws TransporterException {
		DownloadOperation downloader = new DownloadOperation(this.transportClient);
		return downloader.download(downloadParams);
	}

	public boolean testConnection() throws TransporterException {
		Logger.info(getClass(), "SftpTransporter.testConnection - begin");
		boolean status = false;
		try {
			this.transportClient.login();
			Logger.info(getClass(), "working directory: "+this.transportClient.getHomeDirectory());
			this.transportClient.disconnect();
			status = true;
		}
		finally{
			Logger.info(getClass(), "SftpTransporter.testConnection - end");
		}
		return status;
	}
}
