package com.hcl.neo.eloader.filesystem.handler.util;

import com.hcl.neo.eloader.common.BulkUtils;
import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.filesystem.handler.ArchiveType;
import com.hcl.neo.eloader.filesystem.handler.exceptions.ArchiverException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

/**
 * Archiver utility class
 * @author jasneets
 *
 */
public class ArchiverUtil {

	/**
	 * Guess format of archive file, tar, gz and zip are recognized formats.
	 * @param filePath
	 * @return
	 * @throws ArchiverException
	 */
	public static ArchiveType identifyArchiveType(String filePath) throws ArchiverException{
		ArchiveType archiveType = null;
                BufferedInputStream fis = null;
		try{
			fis = new BufferedInputStream(new FileInputStream(filePath));
			
			try{
				ArchiveStreamFactory asfactory = new ArchiveStreamFactory();
				ArchiveInputStream is = asfactory.createArchiveInputStream(fis);
				if(is instanceof TarArchiveInputStream){
					archiveType = ArchiveType.TAR;
				}
				else if(is instanceof ZipArchiveInputStream){
					archiveType = ArchiveType.ZIP;
				}
                                if(is !=null){
                                    is.close();
                                }
			}
			catch(ArchiveException | IOException e){
				Logger.debug(ArchiverUtil.class, e.getMessage());
			}
			try{
				CompressorStreamFactory csFactory = new CompressorStreamFactory();
				CompressorInputStream is = csFactory.createCompressorInputStream(fis);
				if(is instanceof GzipCompressorInputStream){
					archiveType = ArchiveType.GZ;
				}
                                if(is !=null){
                                    is.close();
                                }
			}
			catch(CompressorException | IOException e){
				Logger.debug(ArchiverUtil.class, e.getMessage());
			}
		}
		catch(Throwable e){
			throw new ArchiverException(e);
		}finally{
                    try {
                        if(fis !=null){
                            fis.close();
                        }
                    } catch (IOException ex) {
                        Logger.debug(ArchiverUtil.class, ex.getMessage());
                    }
                }
		return archiveType;
	}
	
	public static String runNativeCommand(String command) throws ArchiverException{
		String status = "";
	    try {
	    	ProcessBuilder processBuilder;
	    	if(BulkUtils.isMac() || BulkUtils.isLinux()){
	    		processBuilder = new ProcessBuilder("/bin/sh", "-c", command);
	    	}
	    	else if(BulkUtils.isWindows()){
	    		processBuilder = new ProcessBuilder("cmd", "/c", command);
	    	}
	    	else{
	    		throw new ArchiverException(BulkUtils.getOsName() + " is not supported");
	    	}
	    	Process process = processBuilder.start();
	    	process.getErrorStream();
	    	StreamReader error = new StreamReader(process.getErrorStream());
	    	StreamReader output = new StreamReader(process.getInputStream());
	    	error.start();
	    	output.start();
	    	error.join(5000);
	    	output.join(5000);
	        int exitValue = process.waitFor();
	
	        status = "exit value: "+exitValue+"\noutput: "+output.getMessage()+"\nerror: "+error.getMessage();
	        if(null != error.getMessage() && error.getMessage().length() > 0){
	        	throw new ArchiverException(error.getMessage());
	        }
	    }
	    catch(Throwable e){
	    	throw new ArchiverException(e);
	    }
	    return status;
	}
	
	
}