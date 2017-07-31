package com.hcl.neo.eloader.microservices.outbound;

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
import com.hcl.neo.eloader.network.handler.params.SessionParams;
import com.hcl.neo.eloader.network.handler.params.UploadParams;
import com.hcl.neo.eloader.network.handler.sftp.SftpTransportFactory;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UploadManager {

	@Value("${bulk.centralServerPath}")
	private String centralServerPath;

	public TransportServerMaster upload(SessionParams sessionParams, TransportServerMaster xportServerMaster, UploadParams uploadParams) throws TransporterException{
		boolean status = false;
		try{
			status = uploadParams.validate();
			if(!status){
				throw new TransporterException("Invalid upload parameters: "+uploadParams);
			}

			TransportServerType serverType = xportServerMaster.getTransportServerType();
			if(TransportServerType.CENTRAL.equals(serverType)){
				String sourcePath = uploadParams.getLocalPath();
				String targetPath = centralServerPath+"/"+uploadParams.getRemotePath();
				Logger.info(getClass(), "This is Central Transport Server. So copying archive "+sourcePath+" to location "+targetPath);
				File srcFile = new File(sourcePath);
				File destFile = new File(targetPath);
				FileUtils.copyFile(srcFile, destFile);
			} else if(TransportServerType.EXTERNAL.equals(serverType)){
				TransportFactory xportFactory = getTransportFactory(xportServerMaster.getTransportType());
				Transporter xporter = xportFactory.createTransporter();
				xporter.setSessionParams(sessionParams);
				Logger.debug(getClass(), "EXTERNAL SERVER PATH "+uploadParams.getRemotePath());
				boolean isConnected = false;
				for(int i=0; i<3; i++){
					Logger.info(getClass(), "Connection attempt... "+i);
					isConnected = xporter.testConnection();
					if(isConnected)
						break;
				}
				if(isConnected){
					status = xporter.upload(uploadParams);
				}
				else{
					Logger.info(getClass(), "Not able to connect to External Server.");
				}
			}
			else{
				TransportFactory xportFactory = getTransportFactory(xportServerMaster.getTransportType());
				Transporter xporter = xportFactory.createTransporter();
				xporter.setSessionParams(sessionParams);
				boolean isConnected = false;
				for(int i=0;i<3;i++){
					Logger.info(getClass(), "Connection attempt... "+(i+1));
					try{
						isConnected = xporter.testConnection();
						if(isConnected)
							break;
					}
					catch(TransporterException e){
						Logger.info(getClass(), "Unable to connect to BCH Server");
					}
				}
				if(isConnected){
					status = xporter.upload(uploadParams);
				}
				else{
					xportServerMaster.setType("C");
					Logger.info(getClass(), "Connection to BCH Server failed. Connecting to Central Server ");
					String sourcePath = uploadParams.getLocalPath();
					String targetPath = centralServerPath+"/"+uploadParams.getRemotePath();
					File srcFile = new File(sourcePath);
					File destFile = new File(targetPath);
					FileUtils.copyFile(srcFile, destFile);
				}
			}
			return xportServerMaster;
		}
		catch(Throwable e){
			throw new TransporterException(e);
		}
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