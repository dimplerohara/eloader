package com.hcl.neo.eloader.network.handler.operation;

import java.io.File;
import java.io.IOException;

import com.hcl.neo.eloader.network.handler.client.Client;
import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.params.DownloadParams;
import com.hcl.neo.eloader.network.handler.params.OperationParams;
import com.hcl.neo.eloader.network.handler.params.UploadParams;

public class TransportOperation {

	private Client client;
	
	protected TransportOperation(Client client){
		this.client = client;
	}
	
	protected Client getClient(){
		return this.client;
	}
	
	protected boolean validate(OperationParams operationParams) throws TransporterException{
		if(!operationParams.validate()) throw new TransporterException(operationParams.getErrors());
		return true;
	}
	
	protected boolean validate(DownloadParams downloadParams) throws TransporterException{
		if(!downloadParams.validate()) throw new TransporterException(downloadParams.getErrors());
		getClient().goToHomeDir();
		
		// remote file exists?
		boolean remotePathExists = getClient().exists(downloadParams.getRemotePath());
		if(!remotePathExists){
			throw new TransporterException("Remote path not found: "+downloadParams.getRemotePath());
		}
		return true;
	}
	
	protected boolean validate(UploadParams uploadParams) throws TransporterException{
		if(!uploadParams.validate()) throw new TransporterException(uploadParams.getErrors());
		getClient().goToHomeDir();
		
		// local file exists?
		File localPath = new File(uploadParams.getLocalPath());
		if(!localPath.exists()){
			throw new TransporterException("Local path not found: "+uploadParams.getLocalPath());
		}
		return true;
	}
	
	protected String appendToPath(String path, String append){
		return (path + "/" +append).replaceAll("//", "/");
	}
	
	protected long getLocalSize(String path) throws IOException{
		File localPath = new File(path);
		long size = localPath.length();
		if(localPath.isDirectory()){
			File[] files = localPath.listFiles();
			for(File file : files){
				if(file.isDirectory()){
					size = size + getLocalSize(file.getCanonicalPath());
				}
				size = size + file.length();
			}
		}
		return size;
	}
}
