package com.hcl.neo.eloader.filesystem.handler.impl;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.filesystem.handler.exceptions.ArchiverException;
import com.hcl.neo.eloader.filesystem.handler.params.ArchiverParams;
import com.hcl.neo.eloader.filesystem.handler.util.ArchiverUtil;

import java.io.File;
import java.io.IOException;

/**
 * Implementation for tar+gz archiver
 * @author jasneets
 *
 */
public class TgzArchiver extends AbstractArchiver {

        @Override
	public void createArchive(ArchiverParams archiverParams) throws ArchiverException {
		ArchiveCreator creator = new ArchiveCreator();
		creator.createArchive(archiverParams);
		//if(BulkUtils.isWindows()){
		//	creator.createArchive(archiverParams);
		//}
		//else if (BulkUtils.isLinux() || BulkUtils.isMac()) {
		//	createArchiveNative(archiverParams);
		//}
		//else{
		//	throw new ArchiverException(BulkUtils.getOsName() +" is not supported");
		//}
	}

        @Override
	public void extractArchive(ArchiverParams archiverParams) throws ArchiverException {
		ArchiveExtractor  extractor = new ArchiveExtractor();
                extractor.extractArchive(archiverParams);
//		if(BulkUtils.isWindows()){
//			extractor.extractArchive(archiverParams);
//		}
//		else if (BulkUtils.isLinux() || BulkUtils.isMac()) {
//			extractArchiveNative(archiverParams);
//		}
//		else{
//			throw new ArchiverException(BulkUtils.getOsName() +" is not supported");
//		}
	}
	
	private String extractArchiveNative(ArchiverParams params) throws ArchiverException{
		StringBuilder command = new StringBuilder();
		command.append("tar -xzf '").append(params.getArchivePath()).append("' -C ");
		for(String path : params.getContentPath()){
			command.append("'").append(path).append("' ");
			File file = new File(path);
			file.mkdirs();
			break;
		}
		Logger.info(ArchiverUtil.class, "Start - command: "+command.toString());
		String status = ArchiverUtil.runNativeCommand(command.toString());
		Logger.info(ArchiverUtil.class, "End - command: "+status);
		return status;
	}
	
	private String createArchiveNative(ArchiverParams params) throws ArchiverException{
		try{
			if(params.getContentPath().isEmpty()) throw new ArchiverException("Nothing to archive: "+params);
			StringBuilder command = new StringBuilder();
			File contentPath = new File(params.getContentPath().get(0));
			File parentFile = contentPath.getParentFile();
			String basePath = parentFile.getCanonicalPath();
			command.append("tar -czf '").append(params.getArchivePath()).append("' -C ").append(basePath).append(" ");
			for(File file : parentFile.listFiles()){
				command.append("'").append(file.getName()).append("' ");
			}
			Logger.info(ArchiverUtil.class, "Start - command: "+command.toString());
			String status = ArchiverUtil.runNativeCommand(command.toString());
			Logger.info(ArchiverUtil.class, "End - command: "+status);
			return status;
		}
		catch(ArchiverException | IOException e){
			throw new ArchiverException(e);
		}
	}
}