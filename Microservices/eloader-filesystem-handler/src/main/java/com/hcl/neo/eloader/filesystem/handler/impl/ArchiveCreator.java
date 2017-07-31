package com.hcl.neo.eloader.filesystem.handler.impl;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.filesystem.handler.ArchiveType;
import com.hcl.neo.eloader.filesystem.handler.ProgressMonitor;
import com.hcl.neo.eloader.filesystem.handler.exceptions.ArchiverException;
import com.hcl.neo.eloader.filesystem.handler.params.ArchiverParams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

public class ArchiveCreator extends ArchiveBase {

    protected void createArchive(ArchiverParams params) throws ArchiverException {
        ArchiveOutputStream outputStream = null;
        File targetFile = null;
        try {
            targetFile = new File(params.getArchivePath());
            outputStream = createArchiveOutputStream(params.getArchiveType(), targetFile);
            List<String> pathList = params.getContentPath();
            if (params.getProgressMonitor() != null) {
                Logger.debug(getClass(), "getting All Content Size: ");
                long totalSize = 0;
                for (String path : pathList) {
                    File file = new File(path);
                    if (file.exists()) {
                        if (file.isDirectory()) {
                            totalSize = totalSize + getDirSize(file);
                        } else {
                            totalSize = totalSize + file.length();
                        }
                    }
                }
                Logger.debug(getClass(), "Total content Size " + totalSize);
                params.getProgressMonitor().setTotalByteSize(totalSize);
            }
            for (String path : pathList) {
                Logger.debug(getClass(), "Adding path: " + path);
                File file = new File(path);
                String basePath = file.getParentFile().getCanonicalPath();
                addEntry(params.getArchiveType(), basePath, file, outputStream, params.getProgressMonitor(), 0);
            }
        } catch (Throwable e) {
            if (targetFile != null && targetFile.exists()) {
                targetFile.delete();
            }
            throw new ArchiverException(e);
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Logger.debug(getClass(), e.getMessage());
            }
        }
    }

    private void addEntry(ArchiveType archiveType, String basePath, File file, ArchiveOutputStream outputStream, ProgressMonitor progressMonitor, long processed) throws Throwable {
        String path = file.getCanonicalPath();
        String entryName = path.substring(basePath.length(), path.length());
        Logger.debug(getClass(), "Adding entry: " + entryName);
        ArchiveEntry archiveEntry;
        if (file.isDirectory()) {
            archiveEntry = createArchiveEntry(archiveType, entryName + "/");
            outputStream.putArchiveEntry(archiveEntry);
            outputStream.closeArchiveEntry();

            File[] childFiles = file.listFiles();
            for (File childFile : childFiles) {
                addEntry(archiveType, basePath, childFile, outputStream, progressMonitor, processed);
            }
        } else {
            archiveEntry = new TarArchiveEntry(entryName);
            if (ArchiveType.GZ.equals(archiveType) || ArchiveType.TAR.equals(archiveType)) {
                ((TarArchiveEntry) archiveEntry).setSize(file.length());
            } else if (ArchiveType.ZIP.equals(archiveType)) {
                ((ZipArchiveEntry) archiveEntry).setSize(file.length());
            }
            outputStream.putArchiveEntry(archiveEntry);
            byte[] buffer = new byte[BUFFER_LENGTH];
            int bytesRead;
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                while ((bytesRead = bis.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    if (progressMonitor != null) {
                        processed = processed + bytesRead;
                        progressMonitor.bytesProcessed(processed);
                    }
                }
                outputStream.closeArchiveEntry();
            }
        }

    }

    private long getDirSize(File dir) {
        long size = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                size = getDirSize(file);
            } else {
                size = size + file.length();
            }
        }
        return size;
    }
}
