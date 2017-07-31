package com.hcl.neo.eloader.microservices.inbound;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.hcl.neo.eloader.common.JsonApi;
import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.dao.TransportServerRepository;
import com.hcl.neo.eloader.exceptions.BulkException;
import com.hcl.neo.eloader.filesystem.handler.Archiver;
import com.hcl.neo.eloader.filesystem.handler.ArchiverFactory;
import com.hcl.neo.eloader.filesystem.handler.exceptions.ArchiverException;
import com.hcl.neo.eloader.filesystem.handler.impl.TgzArchiver;
import com.hcl.neo.eloader.filesystem.handler.params.ArchiverParams;
import com.hcl.neo.eloader.microservices.exceptions.EloaderDbException;
import com.hcl.neo.eloader.microservices.exceptions.RepositoryException;
import com.hcl.neo.eloader.microservices.params.ArchiveManager;
import com.hcl.neo.eloader.microservices.params.BulkJobParams;
import com.hcl.neo.eloader.microservices.params.BulkJobStatus;
import com.hcl.neo.eloader.microservices.params.BulkJobType;
import com.hcl.neo.eloader.microservices.params.CancelCheckoutParams;
import com.hcl.neo.eloader.microservices.params.CheckinParams;
import com.hcl.neo.eloader.microservices.params.ImportMetadataParams;
import com.hcl.neo.eloader.microservices.params.ImportParams;
import com.hcl.neo.eloader.microservices.params.OperationObjectDetail;
import com.hcl.neo.eloader.microservices.params.Response;
import com.hcl.neo.eloader.microservices.services.JobProcessor;
import com.hcl.neo.eloader.model.TransportServerMaster;
import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.params.DownloadParams;
import com.hcl.neo.eloader.network.handler.params.SessionParams;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class InboundJobProcessor extends JobProcessor {

	String downloadUrl;

	@Autowired
	private ArchiveManager archiveManager;

	@Autowired
	private TransportServerRepository transportServerRepository;


	@Override
	public void process(BulkJobParams params) throws BulkException {
		try {
			// download archive

			Logger.info(getClass(), "start - create");
			updateJobStatus(params.getId(), BulkJobStatus.CREATED, downloadUrl);
			Logger.info(getClass(), "end - create");

			Logger.info(getClass(), "start - Download");
			updateJobStatus(params.getId(), BulkJobStatus.IN_PROGRESS_TRANSPORTER, downloadUrl);
			download(params);
			Logger.info(getClass(), "end - Download");

			// validate archive
			Logger.info(getClass(), "start - Validate archive");
			updateJobStatus(params.getId(), BulkJobStatus.IN_PROGRESS_ARCHIVER, downloadUrl);
			String archiveFilePath = archiveManager.buildImportArchiveFilePath(params);
			boolean validated = validateArchive(archiveFilePath, params.getPackageChecksum());
			Logger.info(getClass(), "end - validate archive: " + validated);
			if (validated) {

				// extract archive
				Logger.info(getClass(), "start - Extract archive");
				extract(params);
				updateJobStatus(params.getId(), BulkJobStatus.IN_PROGRESS_REPO, downloadUrl);
				Logger.info(getClass(), "end - Extract archive");
				// import content
				Logger.info(getClass(), "start - Store in repository");
				Response response = storeInRepository(params);
				/*persistOperationResponse(params.getId().longValue(), response.getOperationObjectDetails());
				  updateJobStatus(params.getId(), getJobStatusFromResponse(response), downloadUrl);*/
				Logger.info(getClass(), "end - Store in repository");
			} else {
				updateJobStatus(params.getId(), BulkJobStatus.FAILED, downloadUrl);
				persistOperationResponse(params.getId(), "checksum validation failed for archive file.");
			}
		} catch (TransporterException e) {
			doPostExceptionTask(params.getId(), "Error has occured while downloading package. " + e.getMessage());
			throw new BulkException(e);
		} catch (ArchiverException e) {
			doPostExceptionTask(params.getId(), "Error has occured while extracting content from package. " + e.getMessage());
			throw new BulkException(e);
		} catch (RepositoryException | EloaderDbException e) {
			doPostExceptionTask(params.getId(), "Error has occured while storing content in repository. " + e.getMessage());
			throw new BulkException(e);
		} catch (Throwable e) {
			doPostExceptionTask(params.getId(), "Unknown error has occured while processing. " + e.getMessage());
			throw new BulkException(e);
		} finally {
			if (doCleanupWorkspace()) {
				String path = archiveManager.buildWorkspacePath(params);
				Logger.info(getClass(), "Deleting " + path);
				FileUtils.deleteQuietly(new File(path));
			}
		}
	}
	
	public void updateJobStatus(Long jobId, JsonElement operationObjectDetails){
		Type listType = new TypeToken<List<OperationObjectDetail>>() {}.getType();
		List<OperationObjectDetail> objectDetailList = new Gson().fromJson(operationObjectDetails, listType);
		persistOperationResponse(jobId, objectDetailList);
		try {
			updateJobStatus(jobId, getJobStatusFromResponse(objectDetailList), downloadUrl);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private boolean download(BulkJobParams params) throws EloaderDbException, TransporterException {
		boolean status = false;
		long xportServerId = params.getTransportServerId();
		TransportServerMaster record = transportServerRepository.findByServerId(xportServerId);
		Logger.info(getClass(), "record " + record);
		if (record != null) {
			DownloadParams downloadParams = archiveManager.toDownloadParams(params);
			SessionParams sessionParams = archiveManager.toSessionParam(record);
			status = downloadManager.download(sessionParams, record, downloadParams);
		} else {
			//return
		}
		return status;
	}

	private boolean validateArchive(String archiveFilePath, String checksum) throws ArchiverException {
		Archiver archiver = new TgzArchiver();
		return archiver.validateMd5HexChecksum(archiveFilePath, checksum);
	}

	private void extract(BulkJobParams params) throws ArchiverException {
		ArchiverParams archiverParams = archiveManager.toImportArchiverParams(params);
		Archiver archiver = ArchiverFactory.createArchiver(archiverParams.getArchiveType());
		archiver.extractArchive(archiverParams);
	}

	private Response storeInRepository(BulkJobParams params) throws RepositoryException {
		Response operationResponse = new Response();
		try {
			Logger.info(getClass(), "start - " + params.getType());

			if (BulkJobType.IMPORT.equals(params.getType())) {
				ImportParams operationParams = archiveManager.toImportParams(params);
				Logger.info(getClass(), JsonApi.toJson(operationParams));
				Logger.info(getClass(), "operationParams - " + operationParams + "repo name -- " +operationParams.getRepository());
				//operationResponse = jobProcessService.callImportService(operationParams);
				downloadUrl = operationParams.getRepository() + "&locateId=" + operationParams.getFolderId();
			} else if (BulkJobType.IMPORT_METADATA.equals(params.getType())) {
                ImportMetadataParams operationParams = archiveManager.toImportMetadataParams(params);
                Logger.info(getClass(), "operationParams - " + operationParams + "repo name -- " +operationParams.getRepository());
              //  operationResponse = jobProcessService.callImportMetadataService(operationParams);
            } /*else if (BulkJobType.IMPORT_PLUS.equals(params.getType())) {
            	ImportPlusParams operationParams = paramMapper.toImportPlusParams(params);
                Logger.info(getClass(), "operationParams - " + operationParams + "repo name -- " +operationParams.getRepository());
                operationResponse = dctmRepositoryManager.execImportPlus(operationParams);
                downloadUrl = operationParams.getRepository() + "&locateId=" + operationParams.getFolderId();
            }*/ else if (BulkJobType.CHECKIN.equals(params.getType())) {
                CheckinParams operationParams = archiveManager.toCheckinParams(params);
                Logger.info(getClass(), "operationParams - " + operationParams + "repo name -- " +operationParams.getRepository());
               // operationResponse = jobProcessService.execCheckin(operationParams);
                downloadUrl = operationParams.getRepository() + "&locateId=" + operationParams.getFolderId();
            } else if (BulkJobType.CANCEL_CHECKOUT.equals(params.getType())) {
                CancelCheckoutParams operationParams = archiveManager.toCancelCheckoutParams(params);
                Logger.info(getClass(), "operationParams - " + operationParams + "repo name -- " +operationParams.getRepository());
               // operationResponse = jobProcessService.execCancelCheckout(operationParams);
            }
			queueJobforWrapper(params);
		} catch (Throwable e) {
			throw new RepositoryException(e);
		} finally {
			Logger.info(getClass(), "end - " + params.getType());
		}
		return operationResponse;
	}
}