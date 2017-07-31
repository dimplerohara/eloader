package com.hcl.neo.eloader.filesystem.handler.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import com.hcl.neo.eloader.filesystem.handler.ArchiveType;
import com.hcl.neo.eloader.filesystem.handler.exceptions.ArchiverException;

/**
 * Base class for create and extract archive implementations
 * @author jasneets
 *
 */
class ArchiveBase {

	protected static final int BUFFER_LENGTH = 65536;
	
	protected ArchiveInputStream createArchiveInputStream(ArchiveType archiveType, File sourceFile) throws FileNotFoundException, IOException, ArchiverException{
		ArchiveInputStream ais = null;
		if(ArchiveType.GZ.equals(archiveType)){
			ais = ArchiveFactory.createTgzArchiveInputStream(sourceFile);
		}
		else if(ArchiveType.TAR.equals(archiveType)){
			ais = ArchiveFactory.createTarArchiveInputStream(sourceFile);
		}
		else if(ArchiveType.ZIP.equals(archiveType)){
			ais = ArchiveFactory.createZipArchiveInputStream(sourceFile);
		}
		else{
			throw new ArchiverException("ArchiveType not supported: "+archiveType);
		}
		return ais;
	}
	
	protected ArchiveOutputStream createArchiveOutputStream(ArchiveType archiveType, File sourceFile) throws FileNotFoundException, IOException, ArchiverException{
		ArchiveOutputStream aos = null;
		if(ArchiveType.GZ.equals(archiveType)){
			TarArchiveOutputStream tos = ArchiveFactory.createTgzArchiveOutputStream(sourceFile);
			tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
			tos.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
			aos = tos;
		}
		else if(ArchiveType.TAR.equals(archiveType)){
			TarArchiveOutputStream tos = ArchiveFactory.createTarArchiveOutputStream(sourceFile);
			tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
			tos.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
			aos = tos;
		}
		else if(ArchiveType.ZIP.equals(archiveType)){
			aos = ArchiveFactory.createZipArchiveOutputStream(sourceFile);
		}
		else{
			throw new ArchiverException("ArchiveType not supported: "+archiveType);
		}
		return aos;
	}
	
	protected ArchiveEntry createArchiveEntry(ArchiveType archiveType, String entryName) throws ArchiverException{
		ArchiveEntry entry = null;
		
		if(ArchiveType.GZ.equals(archiveType)){
			entry = ArchiveFactory.createTarArchiveEntry(entryName);
		}
		else if(ArchiveType.TAR.equals(archiveType)){
			entry = ArchiveFactory.createTarArchiveEntry(entryName);
		}
		else if(ArchiveType.ZIP.equals(archiveType)){
			entry = ArchiveFactory.createZipArchiveEntry(entryName);
		}
		else{
			throw new ArchiverException("ArchiveType not supported: "+archiveType);
		}
		return entry;
	}
}
