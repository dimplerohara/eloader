package com.hcl.neo.eloader.network.handler.operation;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FilenameUtils;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.network.handler.client.Client;
import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.params.DownloadParams;

public class DownloadOperation extends TransportOperation {

	public DownloadOperation(Client client) {
		super(client);
	}

	public boolean download(DownloadParams downloadParams) throws TransporterException{
		boolean status = false;
		try{
			getClient().login();
			
			// validate parameters
			validate(downloadParams);

			if(null != downloadParams.getContentTransferMonitor()){
				long remoteSize = getClient().getSize(downloadParams.getRemotePath());
				downloadParams.getContentTransferMonitor().setTotalBytes(remoteSize);
			}
			
			// is remote path file or dir?
			boolean isDir = getClient().isDirectory(downloadParams.getRemotePath());
			if(isDir){
				status = downloadDir(downloadParams);
			}
			else{
				status = downloadFile(downloadParams);
			}
		}
		catch(TransporterException e){
			throw e;
		}
		catch(Throwable e){
			throw new TransporterException(e);
		}
		return status;
	}
	
	private boolean downloadDir(DownloadParams downloadParams) throws Throwable{
		Logger.info(getClass(), "DownloadOperation.downloadDir: "+downloadParams);
		String remotePath = downloadParams.getRemotePath();
		boolean status = false;
		File localDir = new File(downloadParams.getLocalPath()+Client.PATH_SEPARATOR+getClient().getName(downloadParams.getRemotePath()));
		if(!localDir.exists()) status = localDir.mkdirs();
		getClient().goToHomeDir();
		String workingDir = getClient().getWorkingDirectory();
		String[] childObjects = getClient().listNamesOnly(remotePath);
		for(int index=0; index<childObjects.length; index++){
			if(!getClient().isValidSession()){
				Logger.warn(getClass(), "DownloadOperation.downloadDir: session invalidated");
				getClient().login();
			}
			String remoteChildPath = remotePath + Client.PATH_SEPARATOR + childObjects[index];
			Logger.info(getClass(), "remotePath="+workingDir+"::"+remoteChildPath);
			if(getClient().isDirectory(remoteChildPath)){
				status = downloadDir(new DownloadParams(localDir.getAbsolutePath(), remoteChildPath, downloadParams.getTransferStreams(), downloadParams.getContentTransferMonitor()));
			}
			else{
				status = downloadFile(new DownloadParams(localDir.getAbsolutePath(), remoteChildPath, downloadParams.getTransferStreams(), downloadParams.getContentTransferMonitor()));
			}
		}
		return status;
	}

	private boolean downloadFile(DownloadParams downloadParams) throws Throwable{
		int streams = downloadParams.getTransferStreams();
		long skip = (streams == 1) ? calcSkipBytes(downloadParams) : 0;
		long remoteFileSize = getClient().getSize(downloadParams.getRemotePath());
		TransferOffset[] transferOffsets = TransferOffset.calcTransferOffsets(remoteFileSize, streams, skip);
		CountDownLatch finishSignal = new CountDownLatch(streams);
		for(int index=0; index<transferOffsets.length; index++){
			Client newClient = getClient().cloneClient();
			DownloadThread thread = new DownloadThread(finishSignal, newClient, downloadParams, transferOffsets[index]);
			thread.start();
		}
		finishSignal.await();
		return true;
	}
	
	private long calcSkipBytes(DownloadParams downloadParams){
		long skip = 0;
		File localPath = new File(downloadParams.getLocalPath());
		if(null != localPath && localPath.exists()){
			if(localPath.isDirectory()){
				File[] files = localPath.listFiles();
				String remoteFilename = FilenameUtils.getName(downloadParams.getRemotePath());
				for(File file : files){
					if(file.getName().equals(remoteFilename)){
						skip = file.length();
						break;
					}
				}
			}
			else{
				skip = localPath.length();
			}
		}
		return skip;
	}
}
