package com.hcl.neo.eloader.network.handler.operation;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FilenameUtils;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.network.handler.client.Client;
import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.params.UploadParams;

class UploadThread extends ContentTransferThread {

	private UploadParams uploadParams;
	private TransferOffset offset;
	private Client client;	
	
	public UploadThread(CountDownLatch finish, Client client, UploadParams uploadParams, TransferOffset offset) {
		super(finish);
		this.uploadParams = uploadParams;
		this.offset = offset;
		this.client = client;
	}
	
	@Override
	public void run() {
		try{
			Logger.info(getClass(), "UploadThread.run - begin - "+getUploadParams() +" - "+getOffset());
			
			// login to server
			getClient().login();
			
			// verify local path
			File localPath = new File(getUploadParams().getLocalPath());
			boolean localFileExist = localPath.exists();
			if(!localFileExist){
				throw new TransporterException("local path not found - "+getUploadParams().getLocalPath());
			}
			
			// is local path file or dir
			boolean isLocalPathDir = localPath.isDirectory();
			
			if(isLocalPathDir){
				uploadDir();
			}
			else{
				uploadFile();
			}
		}
		catch(Throwable e){
			throw new RuntimeException(e);
		}
		finally{
			getClient().disconnect();
			getFinishSignal().countDown();
			Logger.info(getClass(), "UploadThread.run - end - "+getUploadParams() +" - "+getOffset());
		}
	}
	
	private void uploadDir() throws Throwable{
		// Do Nothing
	}
	
	@SuppressWarnings("unused")
	private void uploadFile() throws Throwable{
		String remoteDirPath = "";
		String remotePath = getUploadParams().getRemotePath();
		String localPath = getUploadParams().getLocalPath();
		String filename = FilenameUtils.getName(localPath);
		//In case of Export remove the file name to get the directory name
		//while in case of import we should not remove anything
		Logger.debug(getClass(), "filename : "+filename);
		Logger.debug(getClass(), "remotePath : " + remotePath);
		remoteDirPath = remotePath.endsWith(filename) ? FilenameUtils.getFullPathNoEndSeparator(remotePath):remotePath;
		Logger.debug(getClass(), "remoteDirPath : " + remoteDirPath);
//		String remoteDirPath = remotePath.replaceAll(filename+"+$", "");
		getClient().mkdirs(remoteDirPath);
		remotePath = remotePath.endsWith(filename) ? remotePath : remotePath + Client.PATH_SEPARATOR + filename;
		
		long beginOffset = getOffset().getBeginOffset();
		long endOffSet = getOffset().getEndOffset();
		long streamSize = getOffset().getByteCount();
		long transferSize = getOffset().getByteCount();
		long bytesCopied = 0;
		int retryCount = 0;
		while(beginOffset < endOffSet && retryCount <= getUploadParams().getRetryCount()){
			retryCount++;
			FileInputStream localStream = null;
			OutputStream remoteStream = null;
			try{
				// get remote stream
				remoteStream = getClient().put(filename, beginOffset);
				// build local stream
				localStream = new FileInputStream(localPath);
				FileChannel channel = localStream.getChannel();
				channel.position(beginOffset);
				
				// set up monitor
				ContentTransferMonitor monitor = getUploadParams().getContentTransferMonitor();
				boolean monitorProgress = null != monitor;
				
			 	byte[] buffer = new byte[Client.CHUNK_SIZE];
				int bytesRead = 0;
				while(-1 != (bytesRead = localStream.read(buffer)) && bytesCopied <= transferSize ){
					remoteStream.write(buffer, 0, bytesRead);
					bytesCopied += bytesRead;
					if(monitorProgress){
						monitor.bytesTransferred(bytesRead);
					}
				} 
				beginOffset += bytesCopied;
				streamSize -= bytesCopied;
			}
			catch(Throwable e){
				if(!getClient().isValidSession()){
					Logger.error(getClass(), "session timed out?");
					getClient().login();
				}
				bytesCopied = 0;
				Logger.error(getClass(), "upload interrupted: "+e.getMessage());
				
				if(retryCount > uploadParams.getRetryCount()){
					throw new TransporterException("upload interrupted: "+e.getMessage(), e);
				}
			}
			finally{
				beginOffset += bytesCopied;
				streamSize -= bytesCopied;
				
				if(null != localStream){
					localStream.close();
					localStream = null;
				}
				if(null != remoteStream){
					//remoteStream.flush();
					remoteStream.close();
					remoteStream = null;
				}
				getClient().completeCommand();
				Logger.info(getClass(), "Server Message: "+getClient().getResponseString());			
			}
		}
	}
	
	private UploadParams getUploadParams(){
		return this.uploadParams;
	}
	
	private TransferOffset getOffset() {
		return offset;
	}

	private Client getClient(){
		return this.client;
	}
}