package com.hcl.neo.eloader.network.handler.operation;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FilenameUtils;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.network.handler.client.Client;
import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.params.DownloadParams;

class DownloadThread extends ContentTransferThread {
	
	private DownloadParams downloadParams;
	private TransferOffset offset;
	private Client client;
	
	public DownloadThread(CountDownLatch finish, Client client, DownloadParams downloadParams, TransferOffset offset) throws Throwable{
		super(finish);
		this.downloadParams = downloadParams;
		this.offset = offset;
		this.client = client;
	}
	
	@Override
	public void run() {
		try{
			Logger.info(getClass(), "start - DownloadThread.run - "+getDownloadParams() +" - "+getOffset());

			// login to server
			getClient().login();
			
			// verify remote path
			boolean remoteFileExist = getClient().exists(getDownloadParams().getRemotePath());
			if(!remoteFileExist){
				throw new TransporterException("remote path not found - "+getDownloadParams().getRemotePath());
			}
			
			// is remote path file or dir
			boolean isRemotePathDir = getClient().isDirectory(getDownloadParams().getRemotePath());
			
			if(isRemotePathDir){
				downloadDir();
			}
			else{
				downloadFile();
			}
		}
		catch(Throwable e){
			throw new RuntimeException(e);
		}
		finally{
			getClient().disconnect();
			getFinishSignal().countDown();
			Logger.info(getClass(), "end - DownloadThread.run - "+getDownloadParams() +" - "+getOffset());
		}
	}
	
	private void downloadDir() throws Throwable{
		// Do nothing
	}
	
	@SuppressWarnings("unused")
	private void downloadFile() throws Throwable{
	
		// create or verify local path
		File localPath = new File(getDownloadParams().getLocalPath());
		localPath.mkdirs();
		if(!localPath.exists()){
			throw new TransporterException("Unable to create path: "+localPath.getAbsolutePath());
		}

		// get remote file name
		String remoteFilename = FilenameUtils.getName(getDownloadParams().getRemotePath());
		File file = null;
		
		// is local path file or dir
		if(localPath.isDirectory()){
			file = new File(localPath, remoteFilename);
		}
		else{
			file = localPath;
		}
		
		// start download
		long endOffset = getOffset().getEndOffset();
		long beginOffset = getOffset().getBeginOffset();
		long transferSize = endOffset - beginOffset;
		long bytesCopied = 0;
		int retryCount = 0;
		while(beginOffset < endOffset && retryCount <= getDownloadParams().getRetryCount()){
			retryCount++;
			InputStream remoteStream = null;
			RandomAccessFile localStream = null;
			try{
				long streamSize = endOffset - beginOffset;
			
				// build remote stream
				remoteStream = getClient().get(getDownloadParams().getRemotePath(), beginOffset);
				
				// build local stream
				localStream = new RandomAccessFile(file, "rw");
				localStream.seek(beginOffset);
				
				ContentTransferMonitor monitor = getDownloadParams().getContentTransferMonitor();
				boolean monitorProgress = null != monitor;
				
			 	byte[] buffer = new byte[Client.CHUNK_SIZE];
				int bytesRead = 0;
				while(-1 != (bytesRead = remoteStream.read(buffer)) ){
					localStream.write(buffer, 0, bytesRead);
					bytesCopied += bytesRead;
					if(monitorProgress){
						monitor.bytesTransferred(bytesRead);
					}
					if(bytesCopied >= transferSize) break;
				} 
							
				beginOffset += bytesCopied;
				streamSize -= bytesCopied;
				
				getClient().completeCommand();
			}
			catch(Throwable e){
				if(!getClient().isValidSession()){
					Logger.error(getClass(), "session timed out?");
					getClient().login();
				}
				beginOffset += bytesCopied;
				bytesCopied = 0;
				Logger.error(getClass(), "download interrupted: "+e.getMessage());
				if(retryCount > getDownloadParams().getRetryCount()){
					throw new TransporterException("download interrupted: "+e.getMessage(), e);
				}
			}
			finally{
				if(null != remoteStream){
					remoteStream.close();
					remoteStream = null;
				}
				if(null != localStream){
					localStream.close();
					localStream = null;
				}
			}
		}
	}
	
	private DownloadParams getDownloadParams() {
		return downloadParams;
	}

	private TransferOffset getOffset() {
		return offset;
	}

	private Client getClient(){
		return this.client;
	}
}
