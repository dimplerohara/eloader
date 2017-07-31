package com.hcl.neo.eloader.filesystem.handler.impl;

import com.hcl.neo.eloader.filesystem.handler.exceptions.ArchiverException;
import com.hcl.neo.eloader.filesystem.handler.params.ArchiverParams;

/**
 * Implementation for zip archiver
 * @author jasneets
 *
 */
public class ZipArchiver extends AbstractArchiver {

        @Override
	public void createArchive(ArchiverParams archiverParams) throws ArchiverException {
		ArchiveCreator creator = new ArchiveCreator();
		creator.createArchive(archiverParams);
	}

        @Override
	public void extractArchive(ArchiverParams archiverParams) throws ArchiverException {
		ArchiveExtractor extractor = new ArchiveExtractor();
		extractor.extractArchive(archiverParams);
	}
}
