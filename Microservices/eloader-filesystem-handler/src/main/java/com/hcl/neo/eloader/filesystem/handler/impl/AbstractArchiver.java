package com.hcl.neo.eloader.filesystem.handler.impl;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.filesystem.handler.Archiver;
import com.hcl.neo.eloader.filesystem.handler.exceptions.ArchiverException;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Abstract implementation class for archivers
 * @author jasneets
 *
 */
abstract class AbstractArchiver implements Archiver {

        @Override
	public String getMd5HexChecksum(String filePath) throws ArchiverException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePath);
			return DigestUtils.md5Hex(fis);
		} 
		catch (Throwable e) {
			throw new ArchiverException(e);
		}
		finally{
			try {
				if(null != fis){
					fis.close();
					fis = null;
				} 
			}
			catch (IOException e) {
				throw new ArchiverException(e);
			}
		}
	}
	
        @Override
	public boolean validateMd5HexChecksum(String filePath, String checksum) throws ArchiverException {
		String cs = getMd5HexChecksum(filePath);
                Logger.info(getClass(), "Checksum of Source File: "+checksum);
                Logger.info(getClass(), "Checksum of target file "+filePath+": "+cs);
		return cs.equalsIgnoreCase(checksum);
	}
}