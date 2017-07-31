package com.hcl.neo.eloader.microservices.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.dao.JobMasterRepository;
import com.hcl.neo.eloader.dao.JobSrcDrpLocDetailsRepo;
import com.hcl.neo.eloader.dao.RepoMasterRepository;
import com.hcl.neo.eloader.model.JobMaster;
import com.hcl.neo.eloader.model.JobSourceDropLocationDetails;
import com.hcl.neo.eloader.model.RepositoryMaster;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class IngestionProcessService {

	@Autowired
	private JobMasterRepository jobMasterRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private RepoMasterRepository repoMasterRepository;
	
	@Autowired
	private JobSrcDrpLocDetailsRepo jobSrcDrpLocDetailsRepo;
	
	@Value("${dctm.getFolderObjectsUrl}")
	private String getFolderObjectsUrl; 
	
	public String getObjectsFromCms(String folderPath, Long jobId){
		JobMaster job = jobMasterRepository.findByJobId(jobId);
		RepositoryMaster repoMaster = repoMasterRepository.findByRepoId(job.getRepositoryId());
		String repositoryName = repoMaster.getName();
		String userName = "dmadmin";
		String password = "Hello123";
		return getCmsObjects(userName, password, repositoryName, folderPath);
	}
	
	@HystrixCommand(fallbackMethod = "errorCmsGetObjects", commandKey = "CallCmsGetObjects" )
	public String getCmsObjects(String userName, String password, String repoName, String folderPath){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String auth = userName + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64( 
				auth.getBytes(Charset.forName("US-ASCII")) );
		String authHeader = "Basic " + new String( encodedAuth );
		headers.set( "Authorization", authHeader );
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		String url = getFolderObjectsUrl+repoName+"?folderPath="+folderPath;
		Logger.info(getClass(), url);
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(new URI(url.replaceAll(" ", "%20")), HttpMethod.GET, entity, String.class);
		} catch (RestClientException | URISyntaxException e) {
			e.printStackTrace();
		}
		Logger.info(getClass(), response);
		HttpStatus status = response.getStatusCode();
		String restCall = response.getBody();
		Logger.info(getClass(), restCall + " Status : "+status);
		return response.getBody();
	}
	
	public String errorCmsGetObjects(String userName, String password, String repoName, String folderPath){
		return "{\"Status\" : \"Failed\"}";		
	}
	
	public Map<String, Long> getReportAllGeoObjects(Long days){
		List<JobSourceDropLocationDetails> list = jobSrcDrpLocDetailsRepo.findAll();
		Map<String, Long> geoDocumentMap = new TreeMap<String, Long>();
		Calendar cal = Calendar.getInstance();
		days = -days;
		cal.add(Calendar.DATE, Integer.parseInt(days.toString()));
		Date date = cal.getTime();
		Collection<String> statusCollection = new ArrayList<String>();
		statusCollection.add("COMPLETED");
		statusCollection.add("PARTIAL_SUCCESS");
		for(JobSourceDropLocationDetails jobSourceDropLocationDetails : list){
			Long objectCount = 0l;
			String geoLocation = jobSourceDropLocationDetails.getGeoLocation();
			Long drpLocId = jobSourceDropLocationDetails.getDropLocId();
			List<JobMaster> jobMasterList = jobMasterRepository.findByCreationDateAfterAndStatusInAndLandZoneId(date, statusCollection, drpLocId);
			for(JobMaster jobMaster : jobMasterList){
				objectCount += jobMaster.getPackageFileCount()+jobMaster.getPackageFolderCount();
			}
			if(geoDocumentMap.containsKey(geoLocation)){
				objectCount += geoDocumentMap.get(geoLocation);
				geoDocumentMap.remove(geoLocation);
				geoDocumentMap.put(geoLocation, objectCount);
			}else{
				geoDocumentMap.put(geoLocation, objectCount);
			}
		}
		return geoDocumentMap;
	}
	public Map<String, Long> getReportAllGeoTransactions(Long days){
		List<JobSourceDropLocationDetails> list = jobSrcDrpLocDetailsRepo.findAll();
		Map<String, Long> geoDocumentMap = new TreeMap<String, Long>();
		Calendar cal = Calendar.getInstance();
		days = -days;
		cal.add(Calendar.DATE, Integer.parseInt(days.toString()));
		Date date = cal.getTime();
		Collection<String> statusCollection = new ArrayList<String>();
		statusCollection.add("FAILED");
		for(JobSourceDropLocationDetails jobSourceDropLocationDetails : list){
			Long objectCount = 0l;
			String geoLocation = jobSourceDropLocationDetails.getGeoLocation();
			Long drpLocId = jobSourceDropLocationDetails.getDropLocId();
			List<JobMaster> jobMasterList = jobMasterRepository.findByCreationDateAfterAndStatusNotInAndLandZoneId(date, statusCollection, drpLocId);
			for(JobMaster jobMaster : jobMasterList){
				objectCount += jobMaster.getPackageFileCount()+jobMaster.getPackageFolderCount();
			}
			if(geoDocumentMap.containsKey(geoLocation)){
				objectCount += geoDocumentMap.get(geoLocation);
				geoDocumentMap.remove(geoLocation);
				geoDocumentMap.put(geoLocation, objectCount);
			}else{
				geoDocumentMap.put(geoLocation, objectCount);
			}
		}
		return geoDocumentMap;
	}

	public Map<String, Long> getReportIngestionStatus(Long days){
		Map<String, Long> geoDocumentMap = new TreeMap<String, Long>();
		Calendar cal = Calendar.getInstance();
		days = -days;
		cal.add(Calendar.DATE, Integer.parseInt(days.toString()));
		Date date = cal.getTime();
		Collection<String> statusCollection = new ArrayList<String>();
		statusCollection.add("COMPLETED");
		statusCollection.add("PARTIAL_SUCCESS");
		List<JobMaster> jobMasterList = jobMasterRepository.findByCreationDateAfterAndStatusIn(date, statusCollection);
		Long objectCount = jobMasterList.size()+0l;
		/*for(JobMaster jobMaster : jobMasterList){
			objectCount += jobMaster.getPackageFileCount()+jobMaster.getPackageFolderCount();
		}*/
		geoDocumentMap.put("Success", objectCount);
		statusCollection = new ArrayList<String>();
		statusCollection.add("FAILED");
		jobMasterList = jobMasterRepository.findByCreationDateAfterAndStatusIn(date, statusCollection);
		objectCount = 0l;
		for(JobMaster jobMaster : jobMasterList){
			objectCount += jobMaster.getPackageFileCount()+jobMaster.getPackageFolderCount();
		}
		geoDocumentMap.put("Failed", objectCount);
		return geoDocumentMap;
	}
	
	public Map<String, Long> getReportTransactionTypes(Long days){
		Map<String, Long> geoDocumentMap = new TreeMap<String, Long>();
		Calendar cal = Calendar.getInstance();
		days = -days;
		cal.add(Calendar.DATE, Integer.parseInt(days.toString()));
		Date date = cal.getTime();
		List<JobMaster> jobMasterList = jobMasterRepository.findByCreationDateAfter(date);
		String jobType = null;
		Long objectCount = 0l;
		for(JobMaster jobMaster : jobMasterList){ 
			jobType = jobMaster.getType();
			if(null == jobType){
				continue;
			}
			if(geoDocumentMap.containsKey(jobType)){
				objectCount += geoDocumentMap.get(jobType);
			}
			objectCount += jobMaster.getPackageFileCount()+jobMaster.getPackageFolderCount();
			if(geoDocumentMap.containsKey(jobType)){
				geoDocumentMap.remove(jobType);
				geoDocumentMap.put(jobType, objectCount);
			}else{
				geoDocumentMap.put(jobType, objectCount);
			}
			objectCount = 0l;
		}
		return geoDocumentMap;
	}
}
