package com.hcl.neo.eloader.filesystem.handler.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

abstract class ArchiveFactory {

	protected static TarArchiveInputStream createTgzArchiveInputStream(File sourceFile) throws FileNotFoundException, IOException{
		return new TarArchiveInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(sourceFile))), "UTF-8");
	}
	
	protected static TarArchiveInputStream createTarArchiveInputStream(File sourceFile) throws FileNotFoundException, IOException{
		return new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(sourceFile)), "UTF-8");
	}
	
	protected static ZipArchiveInputStream createZipArchiveInputStream(File sourceFile) throws FileNotFoundException, IOException{
		return new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(sourceFile)));
	}
	
	protected static GZIPInputStream createGzipArchiveInputStream(File sourceFile) throws FileNotFoundException, IOException{
		return new GZIPInputStream(new BufferedInputStream(new FileInputStream(sourceFile)));
	}
	
	protected static TarArchiveOutputStream createTgzArchiveOutputStream(File sourceFile) throws FileNotFoundException, IOException{
		return new TarArchiveOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(sourceFile))), "UTF-8");
	}
	
	protected static TarArchiveOutputStream createTarArchiveOutputStream(File sourceFile) throws FileNotFoundException, IOException{
		return new TarArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(sourceFile)), "UTF-8");
	}
	
	protected static ZipArchiveOutputStream createZipArchiveOutputStream(File sourceFile) throws FileNotFoundException, IOException{
		return new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(sourceFile)));
	}
	
	protected static GZIPOutputStream createGzipArchiveOutputStream(File sourceFile) throws FileNotFoundException, IOException{
		return new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(sourceFile)));
	}
	
	protected static TarArchiveEntry createTarArchiveEntry(String entryName){
		return new TarArchiveEntry(entryName);
	}
	
	protected static ZipArchiveEntry createZipArchiveEntry(String entryName){
		return new ZipArchiveEntry(entryName);
	}
}