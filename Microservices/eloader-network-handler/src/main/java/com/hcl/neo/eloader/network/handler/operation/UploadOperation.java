package com.hcl.neo.eloader.network.handler.operation;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FilenameUtils;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.network.handler.client.Client;
import com.hcl.neo.eloader.network.handler.common.TransportType;
import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.params.UploadParams;

public class UploadOperation extends TransportOperation {

	public UploadOperation(Client client) {
		super(client);
	}

	public boolean upload(UploadParams uploadParams) throws TransporterException{
		boolean status = false;
		try{
			getClient().login();
			Logger.debug(getClass(), "UploadOperation.upload - "+ uploadParams);
			// validate parameters
			validate(uploadParams);
			if(null != uploadParams.getContentTransferMonitor()){
				long localSize = getLocalSize(uploadParams.getLocalPath());
				uploadParams.getContentTransferMonitor().setTotalBytes(localSize);
			}
			
			// is local path file or dir?
			File localPath = new File(uploadParams.getLocalPath());
//			String remotePath = uploadParams.getRemotePath().replaceAll("[\\+\\^\\$]+", "");
//			uploadParams.setRemotePath(remotePath);
			boolean isDir = localPath.isDirectory();
			if(isDir){
				status = uploadDir(uploadParams);
			}
			else{
				status = uploadFile(uploadParams);
			}
		}
		catch(TransporterException e){
			throw e;
		}
		catch(Throwable e){
			throw new TransporterException(e);
		}
		finally{
			getClient().disconnect();
		}
		return status;
	}
	
	private boolean uploadDir(UploadParams uploadParams) throws Throwable{
		boolean status = false;
		File localPath = new File(uploadParams.getLocalPath());
		getClient().goToHomeDir();
		String remotePath = uploadParams.getRemotePath();
		remotePath = appendToPath(remotePath, localPath.getName());
		status = getClient().mkdirs(remotePath);
		File[] childFiles = localPath.listFiles();
		String workingDir = getClient().getWorkingDirectory();
		for(int index=0; index<childFiles.length; index++){
			if(!getClient().isValidSession()){
				Logger.warn(getClass(), "UploadOperation.uploadDir: session invalidated");
				getClient().login();
				getClient().gotoDir(workingDir);
			}
			workingDir = getClient().getWorkingDirectory();
			if(childFiles[index].isDirectory()){
				UploadParams uploadParams1 = new UploadParams(childFiles[index].getCanonicalPath(), remotePath, uploadParams.getTransferStreams(), uploadParams.getContentTransferMonitor());
				status = uploadDir(uploadParams1);
			}
			else{
				UploadParams uploadParams1 = new UploadParams(childFiles[index].getCanonicalPath(), remotePath+Client.PATH_SEPARATOR+childFiles[index].getName(), uploadParams.getTransferStreams(), uploadParams.getContentTransferMonitor());
				status = uploadFile(uploadParams1);
			}
		}
		return status;
	}
	
	private boolean uploadFile(UploadParams uploadParams) throws Throwable{

		if(getClient().getTransportType().equals(TransportType.FTP) || getClient().getTransportType().equals(TransportType.SFTP)){
			uploadParams.setTransferStreams(1);
		}
		String remotePath = uploadParams.getRemotePath();
		Logger.debug(getClass(), "UploadOperation.uploadFile - remote path: "+ remotePath);
		String filename = FilenameUtils.getName(uploadParams.getLocalPath());
//		filename = filename.replaceAll("[\\+\\^\\$]+", "");
		if(!remotePath.endsWith(filename)){
			remotePath = appendToPath(remotePath, filename);
		}
		long skip = 0;
		if(uploadParams.getTransferStreams() == 1){
			skip = getClient().getSize(remotePath);
		}
		getClient().goToHomeDir();
		File localPath = new File(uploadParams.getLocalPath());
		long localFileSize = localPath.length();
		TransferOffset[] transferOffsets = TransferOffset.calcTransferOffsets(localFileSize, uploadParams.getTransferStreams(), skip);
		CountDownLatch finishSignal = new CountDownLatch(uploadParams.getTransferStreams());
		for(int index=0; index<transferOffsets.length; index++){
			Client newClient = getClient().cloneClient();
			UploadThread thread = new UploadThread(finishSignal, newClient, uploadParams, transferOffsets[index]);
			thread.start();
		}
		finishSignal.await();
		
		return true;
	}
}
