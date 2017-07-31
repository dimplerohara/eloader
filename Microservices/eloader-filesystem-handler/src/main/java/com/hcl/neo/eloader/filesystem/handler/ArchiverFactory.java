package com.hcl.neo.eloader.filesystem.handler;

import com.hcl.neo.eloader.filesystem.handler.impl.TgzArchiver;
import com.hcl.neo.eloader.filesystem.handler.impl.ZipArchiver;

/**
 * Factory class to create tgz or zip archiver
 * @author jasneets
 *
 */
public abstract class ArchiverFactory {

	/**
	 * Create {@link TgzArchiver} object
	 * @return
	 */
	public static TgzArchiver createTgzArchiver(){
		return new TgzArchiver();
	}
	
	/**
	 * Create {@link ZipArchiver} object
	 * @return
	 */
	public static ZipArchiver createZipArchiver(){
		return new ZipArchiver();
	}
	
	/**
	 * Create {@link Archiver} instance based on ArchiveType parameter 
	 * @param type
	 * @return
	 */
	public static Archiver createArchiver(ArchiveType type){
		Archiver archiver = null;
		if(ArchiveType.GZ.equals(type)){
			archiver = new TgzArchiver();
		}
		else if(ArchiveType.ZIP.equals(type)){
			archiver = new ZipArchiver();
		}
		return archiver;
	}
}
