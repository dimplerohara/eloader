package com.hcl.neo.eloader.microservices.outbound;

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
import com.hcl.neo.eloader.dao.JobMasterRepository;
import com.hcl.neo.eloader.dao.TransportServerRepository;
import com.hcl.neo.eloader.exceptions.BulkException;
import com.hcl.neo.eloader.filesystem.handler.Archiver;
import com.hcl.neo.eloader.filesystem.handler.ArchiverFactory;
import com.hcl.neo.eloader.filesystem.handler.exceptions.ArchiverException;
import com.hcl.neo.eloader.filesystem.handler.params.ArchiverParams;
import com.hcl.neo.eloader.microservices.exceptions.EloaderDbException;
import com.hcl.neo.eloader.microservices.exceptions.RepositoryException;
import com.hcl.neo.eloader.microservices.exceptions.SessionException;
import com.hcl.neo.eloader.microservices.params.ArchiveManager;
import com.hcl.neo.eloader.microservices.params.BulkJobParams;
import com.hcl.neo.eloader.microservices.params.BulkJobStatus;
import com.hcl.neo.eloader.microservices.params.BulkJobType;
import com.hcl.neo.eloader.microservices.params.CheckoutPlusParams;
import com.hcl.neo.eloader.microservices.params.ExportMetadataParams;
import com.hcl.neo.eloader.microservices.params.ExportPlusParams;
import com.hcl.neo.eloader.microservices.params.OperationObjectDetail;
import com.hcl.neo.eloader.microservices.params.Response;
import com.hcl.neo.eloader.microservices.services.JobProcessService;
import com.hcl.neo.eloader.microservices.services.JobProcessor;
import com.hcl.neo.eloader.model.JobMaster;
import com.hcl.neo.eloader.model.TransportServerMaster;
import com.hcl.neo.eloader.network.handler.common.TransportServerType;
import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.params.SessionParams;
import com.hcl.neo.eloader.network.handler.params.UploadParams;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class OutboundJobProcessor extends JobProcessor {

	@Autowired
	private JobProcessService jobProcessService;

	@Autowired
	private ArchiveManager archiveManager;

	@Autowired
	private TransportServerRepository transportServerRepository;

	String downloadUrl;

	@Autowired
	private JobMasterRepository jobMasterRepostory;	

	@Override
	public void process(BulkJobParams params) throws BulkException {
		Response response = null;
		try {

			Logger.info(getClass(), "start - create");
			updateJobStatus(params.getId(), BulkJobStatus.CREATED, downloadUrl);
			Logger.info(getClass(), "end - create");

			// extract content from repository
			Logger.info(getClass(), "start - Retreive from repository");
			updateJobStatus(params.getId(), BulkJobStatus.IN_PROGRESS_REPO, downloadUrl);
			response = retreiveFromRepository(params);
			Logger.info(getClass(), "end - Retreive from repository");

		}catch (RepositoryException | EloaderDbException | SessionException e) {
			doPostExceptionTask(params.getId(), "Error has occured while retreiving content from repository. " + e.getMessage());
			throw new BulkException(e);
		} catch (Throwable e) {
			doPostExceptionTask(params.getId(), "Unknown error has occured while processing. " + e.getMessage());
			throw new BulkException(e);
		}
	}

	private void archive(BulkJobParams params) throws ArchiverException {
		ArchiverParams archiverParams = archiveManager.toExportArchiverParams(params);
		Archiver archiver = ArchiverFactory.createArchiver(archiverParams.getArchiveType());
		archiver.createArchive(archiverParams);
	}

	private boolean upload(BulkJobParams params) throws TransporterException {
		boolean status = false;
		String xtransportServerPath = null;
		try {
			long xportServerId = params.getTransportServerId();
			TransportServerMaster transportServerMaster = transportServerRepository.findByServerId(xportServerId);
			String typeBefore = transportServerMaster.getType();
			TransportServerType serverType = JsonApi.fromJson(JsonApi.toJson(transportServerMaster.getTransportServerType()), TransportServerType.class);
			xtransportServerPath = archiveManager.buildUploadPath(params, serverType);
			params.setTransportServerPath(xtransportServerPath);
			UploadParams uploadParams = archiveManager.toUploadParams(params);
			SessionParams sessionParams = archiveManager.toSessionParam(transportServerMaster);
			transportServerMaster = uploadManager.upload(sessionParams, transportServerMaster, uploadParams);
			if(!typeBefore.equalsIgnoreCase(transportServerMaster.getType())){
				transportServerMaster = transportServerRepository.findByType("C");
				Logger.debug(getClass(),"Transport Server Details -\n Id : " + transportServerMaster.getServerId()
				+"\n Host : "+transportServerMaster.getHost()
				+"\n Transport Server type : "+transportServerMaster.getTransportServerType());
				params.setTransportServerId(transportServerMaster.getServerId());
				params.setTransportServerPath(uploadParams.getRemotePath());
			}
		} catch (Throwable e) {
			status = false;
			throw new TransporterException(e);
		}
		return status;
	}

	private Response retreiveFromRepository(BulkJobParams params) throws EloaderDbException, RepositoryException, SessionException {
		Response operationResponse = null;
		try {
			Logger.info(getClass(), "start - " + params.getType());

			if (BulkJobType.EXPORT.equals(params.getType()) || BulkJobType.EXPORT_PLUS.equals(params.getType())) {
				ExportPlusParams operationParams = archiveManager.toExportPlusParams(params);
				Logger.info(getClass(), "operationParams - " + operationParams + "repository name --- " + operationParams.getRepository());
				//operationResponse = jobProcessService.execExportPlus(operationParams);
				//downloadUrl = operationParams.getRepository() + "&locateId=" + operationParams.getFolderId();
			} else if (BulkJobType.CHECKOUT.equals(params.getType()) || BulkJobType.CHECKOUT_PLUS.equals(params.getType())) {
				CheckoutPlusParams operationParams = archiveManager.toCheckoutPlusParams(params);
				Logger.info(getClass(), "operationParams - " + operationParams + "repository name --- " + operationParams.getRepository());
				//operationResponse = jobProcessService.execCheckoutPlus(operationParams);
				//downloadUrl = operationParams.getRepository() + "&locateId=" + operationParams.getFolderId();
			} else if (BulkJobType.EXPORT_METADATA.equals(params.getType())) {
				ExportMetadataParams operationParams = archiveManager.toExportMetadataParams(params);
				Logger.info(getClass(), "operationParams - " + operationParams + "repository name --- " + operationParams.getRepository());
				operationResponse = jobProcessService.execExportMetadata(operationParams);
				downloadUrl = operationParams.getRepository() + "&locateId=" + operationParams.getFolderId();
				/*if(operationParams.getLocalPath() !=null){
					File folder = new File(operationParams.getLocalPath());
					if(folder.exists()){
						long contentSize = FileUtils.sizeOfDirectory(folder);
						Logger.debug(getClass(), "JobId: "+params.getId()+", Total Size: "+contentSize);
						if(contentSize !=0){
							updateJobContentSize(params.getId(), contentSize);
						}
					}
				}*/
			}
			queueJobforWrapper(params);
		} finally {
			Logger.info(getClass(), "end - " + params.getType());
		}
		return operationResponse;
	}

	public void updateJobStatus(Long jobId, JsonElement operationObjectDetails) throws BulkException{
		BulkJobParams params = null;
		try {
			Type listType = new TypeToken<List<OperationObjectDetail>>() {}.getType();
			List<OperationObjectDetail> objectDetailList = new Gson().fromJson(operationObjectDetails, listType);

			JobMaster job = jobMasterRepostory.findByJobId(jobId);

			params = toBulkJobParams(job);

			// persist response in database
			persistOperationResponse(jobId, objectDetailList);

			// create archive
			Logger.info(getClass(), "start - Create archive");
			updateJobStatus(jobId, BulkJobStatus.IN_PROGRESS_ARCHIVER, downloadUrl);
			archive(params);
			Logger.info(getClass(), "end - Create archive");

			// upload to remote server
			Logger.info(getClass(), "start - Upload");
			updateJobStatus(jobId, BulkJobStatus.IN_PROGRESS_TRANSPORTER, downloadUrl);
			upload(params);
			Logger.info(getClass(), "end - Upload");

			// update outbound data for job
			updateOutboundData(params);

			// update completed status
			updateJobStatus(jobId, getJobStatusFromResponse(objectDetailList), downloadUrl);

			try {
				updateJobStatus(jobId, getJobStatusFromResponse(objectDetailList), downloadUrl);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} catch (TransporterException e) {
			doPostExceptionTask(params.getId(), "Error has occured while uploading package. " + e.getMessage());
			throw new BulkException(e);
		} catch (ArchiverException e) {
			doPostExceptionTask(params.getId(), "Error has occured while creating content package. " + e.getMessage());
			throw new BulkException(e);
		} catch (RepositoryException | EloaderDbException | SessionException e) {
			doPostExceptionTask(params.getId(), "Error has occured while retreiving content from repository. " + e.getMessage());
			throw new BulkException(e);
		} catch (Throwable e) {
			doPostExceptionTask(params.getId(), "Unknown error has occured while processing. " + e.getMessage());
			throw new BulkException(e);
		} finally {
			if (doCleanupWorkspace()) {
				String path = archiveManager.buildWorkspacePath(params);
				Logger.debug(getClass(), "Deleting " + path);
				FileUtils.deleteQuietly(new File(path));
			}
		}
	}

	private BulkJobParams toBulkJobParams(JobMaster job){
		BulkJobParams bulkJobParams = new BulkJobParams();
		bulkJobParams.setId(job.getJobId());
		return bulkJobParams;
	}
}