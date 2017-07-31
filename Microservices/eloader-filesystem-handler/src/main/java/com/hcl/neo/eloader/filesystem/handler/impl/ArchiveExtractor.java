package com.hcl.neo.eloader.filesystem.handler.impl;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.filesystem.handler.ArchiveType;
import com.hcl.neo.eloader.filesystem.handler.ProgressMonitor;
import com.hcl.neo.eloader.filesystem.handler.exceptions.ArchiverException;
import com.hcl.neo.eloader.filesystem.handler.params.ArchiverParams;
import com.hcl.neo.eloader.filesystem.handler.util.ArchiverUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;

public class ArchiveExtractor extends ArchiveBase {

    protected void extractArchive(ArchiverParams params) throws ArchiverException {
        ArchiveInputStream inputStream = null;
        try {
            ArchiveType archiveType = null == params.getArchiveType() ? ArchiverUtil.identifyArchiveType(params.getArchivePath()) : params.getArchiveType();
            ProgressMonitor progressMonitor = params.getProgressMonitor();
            boolean updateProgress = null != progressMonitor;
            File sourceFile = new File(params.getArchivePath());
            File destFile = new File(params.getContentPath().get(0));
            inputStream = createArchiveInputStream(archiveType, sourceFile);
            ArchiveEntry archiveEntry;
            while ((archiveEntry = inputStream.getNextEntry()) != null) {
            	Logger.debug(getClass(), "Archive entry: "+archiveEntry.getName());
                File extractedFile = new File(destFile, archiveEntry.getName());
                if (archiveEntry.isDirectory()) {
                    if(!extractedFile.exists()){
                        extractedFile.mkdirs();
                    }
                } 
                else {
                    if(!extractedFile.getParentFile().exists()){
                        extractedFile.getParentFile().mkdirs();
                    }
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(extractedFile));
                    byte[] buffer = new byte[BUFFER_LENGTH];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                        if (updateProgress) {
                            progressMonitor.bytesProcessed(bytesRead);
                        }
                    }
                    bos.close();
                }
            }
        } 
        catch (ArchiverException | IOException e) {
            throw new ArchiverException(e);
        }
        finally {
        	try {
                if (null != inputStream) {
                    inputStream.close();
                }
            } 
            catch (IOException e) {
                Logger.debug(getClass(), e.getMessage());
            }
        }
    }
}
