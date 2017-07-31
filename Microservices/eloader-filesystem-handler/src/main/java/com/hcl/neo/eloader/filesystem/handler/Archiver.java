package com.hcl.neo.eloader.filesystem.handler;

import com.hcl.neo.eloader.filesystem.handler.exceptions.ArchiverException;
import com.hcl.neo.eloader.filesystem.handler.params.ArchiverParams;

/**
 * create and extract tgz, zip archive. 
 * Generate or validate md5 checksum for file
 * @author jasneets
 *
 */
public interface Archiver {
	
    /**
     * Create archive
     *
     * @param archiverParams
     * @return
     * @throws ArchiverException
     */
    public abstract void createArchive(ArchiverParams archiverParams) throws ArchiverException;

    /**
     * Extract archive
     *
     * @param archiverParams
     * @return
     * @throws ArchiverException
     */
    public abstract void extractArchive(ArchiverParams archiverParams) throws ArchiverException;

    /**
     * Get md5 checksum of file
     *
     * @param filePath
     * @return
     * @throws com.hcl.neo.eloader.filesystem.handler.exceptions.ArchiverException
     */
    public String getMd5HexChecksum(String filePath) throws ArchiverException;

    /**
     * Validate md5 checksum of file
     *
     * @param filePath
     * @param checksum
     * @return
     * @throws com.hcl.neo.eloader.filesystem.handler.exceptions.ArchiverException
     */
    public boolean validateMd5HexChecksum(String filePath, String checksum) throws ArchiverException;
}
