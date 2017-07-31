package com.hcl.neo.eloader.microservices.inbound;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.model.TransportServerMaster;
import com.hcl.neo.eloader.model.TransportServerType;
import com.hcl.neo.eloader.model.TransportType;
import com.hcl.neo.eloader.network.handler.TransportFactory;
import com.hcl.neo.eloader.network.handler.Transporter;
import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.ftp.FtpTransportFactory;
import com.hcl.neo.eloader.network.handler.params.DownloadParams;
import com.hcl.neo.eloader.network.handler.params.SessionParams;
import com.hcl.neo.eloader.network.handler.sftp.SftpTransportFactory;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DownloadManager {
	
	@Value("${bulk.centralServerPath}")
	private String centralServerPath;
	
	@Value("${bulk.ftpConnectRetryCount}")
	private int retryCount;

	public boolean download(SessionParams sessionParams, TransportServerMaster xportServerMaster, DownloadParams downloadParams) throws TransporterException{
		boolean status = false;
		try{
			boolean validParams = downloadParams.validate();
			if(!validParams){
				// return
			}
			TransportServerType serverType = xportServerMaster.getTransportServerType();
			if(TransportServerType.CENTRAL.equals(serverType)){   
				System.out.println("Transport Server Type i sCentral");
				String targetPath = downloadParams.getLocalPath();
				String sourcePath = centralServerPath+"/"+downloadParams.getRemotePath();
                                Logger.info(getClass(), "This is Central Transport Server. So copying archive "+sourcePath+" to location "+targetPath);
				File srcFile = new File(sourcePath);
				File destDir = new File(targetPath);
				FileUtils.copyFileToDirectory(srcFile, destDir);
			}
			else{

				System.out.println("Transport Server Type is not Central");
				TransportFactory xportFactory = getTransportFactory(xportServerMaster.getTransportType());
				Transporter xporter = xportFactory.createTransporter();
				xporter.setSessionParams(sessionParams);
				boolean isConnected = false;
				for(int i=0;i<retryCount; i++){
					 Logger.info(getClass(), "Connection attempt... " + i);
					isConnected = xporter.testConnection();
					if(isConnected)
						break;
				}
				if(isConnected){
					System.out.println("Local Path while calling download"+downloadParams.getLocalPath());
					status = xporter.download(downloadParams);
					System.out.println(status);
				}
			}
		}
		catch(Throwable e){
			throw new TransporterException(e);
		}
		return status;
	}
	
	private TransportFactory getTransportFactory(TransportType xportType){
		TransportFactory xportFactory = null;
		
		if(TransportType.FTP.equals(xportType)){
			xportFactory = FtpTransportFactory.getInstance();
		}
		else if(TransportType.SFTP.equals(xportType)){
			xportFactory = SftpTransportFactory.getInstance();
		}
		
		return xportFactory;
	}
}
