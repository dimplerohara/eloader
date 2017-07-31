package com.hcl.neo.eloader.network.handler.ftp;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.network.handler.Transporter;
import com.hcl.neo.eloader.network.handler.client.Client;
import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.operation.DownloadOperation;
import com.hcl.neo.eloader.network.handler.operation.UploadOperation;
import com.hcl.neo.eloader.network.handler.params.DownloadParams;
import com.hcl.neo.eloader.network.handler.params.SessionParams;
import com.hcl.neo.eloader.network.handler.params.UploadParams;

class FtpTransporter implements Transporter{

	private Client transportClient;
	
        @Override
	public void setSessionParams(SessionParams sessionParams) throws TransporterException {
		this.transportClient = new FtpClient(sessionParams);
	}
	
        @Override
	public boolean upload(UploadParams uploadParams) throws TransporterException {
		UploadOperation uploader = new UploadOperation(this.transportClient);
		return uploader.upload(uploadParams);
	}

        @Override
	public boolean download(DownloadParams downloadParams) throws TransporterException {
		DownloadOperation downloader = new DownloadOperation(this.transportClient);
		return downloader.download(downloadParams);
	}

        @Override
	public boolean testConnection() throws TransporterException {
		Logger.info(getClass(), "FtpTransporter.testConnection - begin");
		boolean status = false;
		try {
			this.transportClient.login();
			Logger.info(getClass(), "working directory: "+this.transportClient.getHomeDirectory());
			this.transportClient.disconnect();
			status = true;
		}
		finally{
			Logger.info(getClass(), "FtpTransporter.testConnection - end");
		}
		return status;
	}
}