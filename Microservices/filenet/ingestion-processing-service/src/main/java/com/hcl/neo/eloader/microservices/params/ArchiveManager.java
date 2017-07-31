package com.hcl.neo.eloader.microservices.params;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.dao.BusinessGrpMasterRepository;
import com.hcl.neo.eloader.dao.JobMasterRepository;
import com.hcl.neo.eloader.dao.JobObjectDetailsRepository;
import com.hcl.neo.eloader.dao.RepoMasterRepository;
import com.hcl.neo.eloader.filesystem.handler.ArchiveType;
import com.hcl.neo.eloader.filesystem.handler.exceptions.ArchiverException;
import com.hcl.neo.eloader.filesystem.handler.params.ArchiverParams;
import com.hcl.neo.eloader.filesystem.handler.util.ArchiverUtil;
import com.hcl.neo.eloader.microservices.exceptions.EloaderDbException;
import com.hcl.neo.eloader.model.BusinessGroupMaster;
import com.hcl.neo.eloader.model.JobMaster;
import com.hcl.neo.eloader.model.JobObjectDetails;
import com.hcl.neo.eloader.model.RepositoryMaster;
import com.hcl.neo.eloader.model.TransportServerMaster;
import com.hcl.neo.eloader.network.handler.common.TransportServerType;
import com.hcl.neo.eloader.network.handler.params.DownloadParams;
import com.hcl.neo.eloader.network.handler.params.SessionParams;
import com.hcl.neo.eloader.network.handler.params.UploadParams;

@Service
public class ArchiveManager {

	@Autowired
	private RepoMasterRepository repoMasterRepository;

	@Autowired
	private JobMasterRepository jobMasterRepository;
	
	@Autowired
	private JobObjectDetailsRepository jobObjectDetailsRepository;

	@Autowired
	private BusinessGrpMasterRepository businessGrpMasterRepository;

	private String workspacePath;

	@Value("${bulk.workspacePath}")
	protected String baseWorkspacePath;

	@Value("${bulk.downloadStream}")
	protected String downloadStream;

	@Value("${bulk.retryCount}")
	protected String retryCount;
	
	@Value("${bulk.uploadStream}")
    protected String uploadStream;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	public ArchiverParams toImportArchiverParams(BulkJobParams params) throws ArchiverException {
		ArchiverParams archiverParams = new ArchiverParams();
		String archiveFilePath = buildImportArchiveFilePath(params);
		System.out.println("archive file path is--"+archiveFilePath);
		archiverParams.setArchivePath(archiveFilePath);
		archiverParams.addContentPath(buildImportPath(params));
		System.out.println("content path is--"+buildImportPath(params));
		archiverParams.setArchiveType(ArchiverUtil.identifyArchiveType(archiveFilePath));
		return archiverParams;
	}

	public String buildImportArchiveFilePath(BulkJobParams params) {
		return buildDownloadArchivePath(params) + "/" + FilenameUtils.getName(params.getTransportServerPath());
	}

	public String buildDownloadArchivePath(BulkJobParams params) {
		return buildWorkspacePath(params);
	}

	public String buildImportPath(BulkJobParams params) {
		return buildWorkspacePath(params) + "/import";
	}

	public String buildWorkspacePath(BulkJobParams params) {
		//if (null == this.workspacePath) {
			String date = dateFormat.format(new Date());
			workspacePath = baseWorkspacePath + "/" + params.getType() + "/" + date + "/" + params.getId();
			System.out.println("Inside BuildWorkspace Path"+params.getId());
		//}
		return workspacePath;
	}

	public ImportParams toImportParams(BulkJobParams bulkJobParams) throws IOException, EloaderDbException {
		ImportParams params = new ImportParams();
		
		// set user login id
		params.setUserLoginId(bulkJobParams.getUserId());

		// set repository
		String repository = "";
		RepositoryMaster repo = repoMasterRepository.findByRepoId(bulkJobParams.getRepositoryId());
		repository = repo.getName();
		params.setRepository(repository);
		params.setRepoType(repo.getRepositoryType());
		// set is mac client
		params.setMacClient(BulkJobParams.CLIENT_OS_MAC.equalsIgnoreCase(bulkJobParams.getClientOs()));

		// set source data paths (local)
		buildWorkspacePath(bulkJobParams);
		String extractionPath = buildImportPath(bulkJobParams);
		File extractionFolder = new File(extractionPath);
		File[] files = extractionFolder.listFiles();
		for (File file : files) {
			params.addLocalPath(file.getCanonicalPath());
		}

		// set destination path in repository
		JobMaster jobMaster = jobMasterRepository.findByJobId(bulkJobParams.getId());
		Logger.info(getClass(), jobMaster);
		if (null != jobMaster) {
			String repositoryPath = jobMaster.getRepositoryPath().get(0);
			params.setRepositoryPath(repositoryPath);
		}

		// set object type for folders
		ObjectTypes folderTypes = bulkJobParams.getObjectTypes();
		if (null != folderTypes) {
			params.setDefaultFolderType(folderTypes.getDefaultFolderType());
			TreeMap<String, String> objectTypes = new TreeMap<>();
			List<ObjectType> objectTypeList = folderTypes.getObjectType();
			for (ObjectType objectType : objectTypeList) {
				objectTypes.put(objectType.getPath(), objectType.getType());
			}
			params.setObjectTypes(objectTypes);
		}

		// set content owner (km group)
		String businessGroup = bulkJobParams.getBusinessGroup();
		if (null != businessGroup) {
			BusinessGroupMaster businessGroupMaster = businessGrpMasterRepository.findByName(businessGroup);
			if (businessGroupMaster != null) {
				params.setOwnerName(businessGroupMaster.getKmGroup());
			}
		}
		params.setId(bulkJobParams.getId());
		params.setJobType(bulkJobParams.getType().name());
		return params;
	}

	public JobObjectDetails toJobObjectDetails(long jobId, OperationObjectDetail object) {
		JobObjectDetails record = new JobObjectDetails();
		record.setError(object.isError());
		record.setFile(object.isFile());
		record.setJobId(jobId);
		record.setMessage(object.getMessage());
		record.setObjectId(object.getObjectId());
		record.setObjectName(object.getObjectName());
		record.setSourcePath(object.getSourcePath());
		record.setTargetPath(object.getTargetPath());
		record.setCreationDate(object.getCreationDate());
		return record;
	}

	public DownloadParams toDownloadParams(BulkJobParams params) {
		DownloadParams downloadParams = new DownloadParams();
		System.out.println("Inside to downlaodParams"+buildDownloadArchivePath(params));
		System.out.println(params.getId());
		downloadParams.setLocalPath(buildDownloadArchivePath(params));
		downloadParams.setRemotePath(params.getTransportServerPath());
		if(downloadStream !=null){
			int downloadStreamInt = Integer.parseInt(downloadStream);
			downloadParams.setTransferStreams(downloadStreamInt);
		} else{
			downloadParams.setTransferStreams(1);
		}
		if( retryCount!=null){
			int retryCountInt = Integer.parseInt(retryCount);
			downloadParams.setRetryCount(retryCountInt);
		} else{
			downloadParams.setRetryCount(5);
		}

		return downloadParams;
	}

	public ExportPlusParams toExportPlusParams(BulkJobParams bulkJobParams) throws EloaderDbException {
		ExportPlusParams params = new ExportPlusParams();
		ExportParams tempParams = toExportParams(bulkJobParams);

		params.setLocalPath(tempParams.getLocalPath());
		params.setRepository(tempParams.getRepository());
		params.setRepositoryPath(tempParams.getRepositoryPath());
		params.setUserLoginId(bulkJobParams.getUserId());

		return params;
	}

	public ExportMetadataParams toExportMetadataParams(BulkJobParams bulkJobParams) throws EloaderDbException {
		ExportMetadataParams params = new ExportMetadataParams();
		ExportParams tempParams = toExportParams(bulkJobParams);

		params.setLocalPath(tempParams.getLocalPath());
		params.setRepository(tempParams.getRepository());
		params.setRepositoryPath(tempParams.getRepositoryPath());
		params.setUserLoginId(bulkJobParams.getUserId());
		params.setId(bulkJobParams.getId());
		params.setJobType(bulkJobParams.getType().name());
		return params;
	}
	public ImportMetadataParams toImportMetadataParams(BulkJobParams bulkJobParams)  throws IOException, EloaderDbException {
		ImportMetadataParams params = new ImportMetadataParams();
		ImportParams tempParams = toImportParams(bulkJobParams);
		String localMetaDataPath="";
		params.setRepository(tempParams.getRepository());
		params.setUserLoginId(tempParams.getUserLoginId());
		//params.setRepositoryPath(tempParams.getRepositoryPath());
		buildWorkspacePath(bulkJobParams);
		String extractionPath = buildImportPath(bulkJobParams);
		File extractionFolder = new File(extractionPath);
		File[] files = extractionFolder.listFiles();
		for (File file : files) {
				localMetaDataPath=file.getCanonicalPath();
		}
		params.setMetadataFilePath(localMetaDataPath);
		params.setId(bulkJobParams.getId());
		params.setJobType(bulkJobParams.getType().name());
		return params;
	}

	public ArchiverParams toExportArchiverParams(BulkJobParams params) {
		ArchiverParams archiverParams = new ArchiverParams();
		archiverParams.setArchivePath(buildExportArchiveFilePath(params));
		archiverParams.addContentPath(buildExportPath(params));
		archiverParams.setArchiveType(ArchiveType.GZ);
		return archiverParams;
	}

	public String buildExportArchiveFilePath(BulkJobParams params) {
		Logger.debug(getClass(), "buildExportArchiveFilePath : " + buildExportPath(params) + ".tgz");
		return buildExportPath(params) + ".tgz";
	}

	public String buildExportPath(BulkJobParams params) {
		//   	 return buildWorkspacePath(params) + "/" + params.getName().replaceAll("[\\\\/:*?\"<>|]", "_");
		String exportJobname =  params.getName().replaceAll("[\\\\/:*?\"<>|]", "_");
		return buildWorkspacePath(params) + "/" + exportJobname;
	}
	
	public ExportParams toExportParams(BulkJobParams bulkJobParams) throws EloaderDbException {
        ExportParams params = new ExportParams();
        // set user login id
        params.setUserLoginId(bulkJobParams.getUserId());

        RepositoryMaster repositoryMaster = repoMasterRepository.findByRepoId(bulkJobParams.getRepositoryId());
        // set repository
        params.setRepository(repositoryMaster.getName());

        // add source path in repository
        JobMaster jobMaster = jobMasterRepository.findByJobId(bulkJobParams.getId());
        params.setRepositoryPath(jobMaster.getRepositoryPath());
        
        // set target export path (local)
        params.setLocalPath(buildExportPath(bulkJobParams));

        return params;
    }
	
	public CheckoutPlusParams toCheckoutPlusParams(BulkJobParams bulkJobParams) throws EloaderDbException {
        CheckoutPlusParams params = new CheckoutPlusParams();
        ExportParams tempParams = toExportParams(bulkJobParams);

        params.setLocalPath(tempParams.getLocalPath());
        params.setRepository(tempParams.getRepository());
        params.setRepositoryPath(tempParams.getRepositoryPath());
        params.setUserLoginId(bulkJobParams.getUserId());
        return params;
    }
	
	public String buildUploadPath(BulkJobParams params,TransportServerType serverType) throws EloaderDbException {
        String path = null;
        path = buildExportArchiveFilePath(params);
        Logger.debug(getClass(), "buildUploadPath : " +path);
        if (serverType != null && serverType.equals(TransportServerType.EXTERNAL)) {
            Logger.debug(getClass(), "EXTERNAL PATH IN JOB PROCESSOR : " + params.getTransportServerPath());
            path = params.getTransportServerPath() + "/" + FilenameUtils.getName(path);
        } else {
            String date = dateFormat.format(new Date());
            path = params.getType() + "/" + date + "/" + params.getId() + "/" + FilenameUtils.getName(path);
        }
        return path;
    }

	public UploadParams toUploadParams(BulkJobParams params) {
        UploadParams uploadParams = new UploadParams();
        uploadParams.setLocalPath(buildExportArchiveFilePath(params));
        Logger.info(getClass(), "*********uploadParams getLocal Path :"+uploadParams.getLocalPath()+"********");
        uploadParams.setRemotePath(params.getTransportServerPath());
        if(uploadStream !=null){
            int uploadStreamInt = Integer.parseInt(uploadStream);
            uploadParams.setTransferStreams(uploadStreamInt);
        } else{
            uploadParams.setTransferStreams(1);
        }
        if( retryCount!=null){
            int retryCountInt = Integer.parseInt(retryCount);
            uploadParams.setRetryCount(retryCountInt);
        } else{
            uploadParams.setRetryCount(5);
        }
        return uploadParams;
    }
	
	public SessionParams toSessionParam(TransportServerMaster record) {
		SessionParams sessionParams = new SessionParams();
		sessionParams.setHost(record.getHost());
		sessionParams.setPort(record.getPort());
		sessionParams.setUser(record.getUserName());
		sessionParams.setPassword(record.getPassword());
		return sessionParams;
	}
	
	public CheckinParams toCheckinParams(BulkJobParams bulkJobParams) throws EloaderDbException, IOException {
        CheckinParams params = new CheckinParams();
        ImportParams tempParams = toImportParams(bulkJobParams);
        params.setLocalPath(tempParams.getLocalPath());
        params.setMacClient(tempParams.isMacClient());
        params.setOwnerName(tempParams.getOwnerName());
        params.setRepository(tempParams.getRepository());
        params.setRepositoryPath(tempParams.getRepositoryPath());
        params.setUserLoginId(tempParams.getUserLoginId());
        return params;
    }
	
	public CancelCheckoutParams toCancelCheckoutParams(BulkJobParams bulkJobParams) throws EloaderDbException, IOException {
        CancelCheckoutParams params = new CancelCheckoutParams();
        ExportParams tempParams = toExportParams(bulkJobParams);
        List<JobObjectDetails> processedObjects = jobObjectDetailsRepository.findByJobId(bulkJobParams.getId());
        List<String> successfullObjects = new ArrayList<>();
        for(JobObjectDetails processedObject:processedObjects){
            if(processedObject.getObjectId() != null && processedObject.getIsError().equalsIgnoreCase("false") && processedObject.getMessage() == null){
                if(processedObject.getObjectId().startsWith("09")){
                    successfullObjects.add(processedObject.getObjectId());
                }
            }
        }
        params.setSuccessfullObjects(successfullObjects);
        params.setLocalPath(tempParams.getLocalPath());
        params.setMacClient(tempParams.isMacClient());
        params.setRepository(tempParams.getRepository());
        params.setRepositoryPath(tempParams.getRepositoryPath());
        params.setUserLoginId(tempParams.getUserLoginId());
        return params;
    }

}
