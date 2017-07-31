package com.hcl.neo.eloader.microservices.services;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hcl.neo.eloader.common.JsonApi;
import com.hcl.neo.eloader.dao.JobSrcDrpLocDetailsRepo;
import com.hcl.neo.eloader.dao.TransportServerRepository;
import com.hcl.neo.eloader.microservices.exceptions.ServiceException;
import com.hcl.neo.eloader.microservices.logger.ServiceLogger;
import com.hcl.neo.eloader.microservices.model.Job;
import com.hcl.neo.eloader.microservices.model.UploadJobInfo;
import com.hcl.neo.eloader.microservices.model.UploadJobMessage;
import com.hcl.neo.eloader.microservices.model.UserDetail;
import com.hcl.neo.eloader.microservices.producer.JMSProducer;
import com.hcl.neo.eloader.microservices.util.IngestionUtil;
import com.hcl.neo.eloader.model.JobSourceDropLocationDetails;
import com.hcl.neo.eloader.model.TransportServerMaster;
import com.hcl.neo.eloader.network.handler.params.SessionParams;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;


@Service
public class JobService {

	@Autowired
	private JMSProducer jmsProducer;

	@Autowired
	private JobImpl jobImpl;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private IngestionUtil ingestionUtil;

	private static final Logger logger = LoggerFactory.getLogger(JobService.class);

	@Value("${eloader.updateJobStatusURL}")
	private String updateJobStatusUrl;

	@Autowired
	private TransportServerRepository transportServerRepository;

	@Autowired
	private JobSrcDrpLocDetailsRepo jobSrcDrpLocDetailsRepo;

	@Value("${dctm.upload_transfer_stream}")
	private String uploadTransferStream;

	@Value("${dctm.retry_count}")
	private String retryCount;

	@Value("${dctm.transport_server_id}")
	private long xportServerId;

	@Value("${dctm.repo_id}")
	private long repoId;

	@Value("${dctm.SFTPLocation}")
	private String sftpLocation;

	@Value("${dctm.JobType}")
	private String jobType;

	/**+
	 * 
	 *
	 * @param json
	 * @return
	 * @throws Throwable 
	 * @throws ServiceException
	 */
	@Scheduled(fixedRate = 50000)
	public void createUploadJob() throws Throwable {
		ServiceLogger.debug(this, " + creating job details");
		Long jobId = null;
		Job jobDetails = null;

		String json=scanSourceLocation();
		ServiceLogger.info(this, " - Created JSON Values are" + json);
		if(json!=""){
			jobDetails = jsonMapper(json);
			if (jobDetails != null) {
				jobDetails.setStatus("CREATED");
				jobId = jobImpl.addJobDetail(jobDetails,null);
				System.out.println(jobId);
				if(jobId != null){
					if (jobDetails.getRepositoryPaths() != null && !jobDetails.getRepositoryPaths().isEmpty()) {
						jobImpl.addObjectPath(jobId, jobDetails.getRepositoryPaths());
					}
					//callProcessService(json);
					queueJob(jobDetails, jobId);
				} else{
					throw new ServiceException("Issue Occured while getting created Job ID.");
				}

			}
			ServiceLogger.info(this, " - Job created with ID" + jobId);
		}
	}



	public String createJSONParameterFile(UploadJobInfo job,UserDetail user) throws ServiceException {
		String message="";
		try{
			UploadJobMessage job1 = new UploadJobMessage();
			job1.setBusinessGroup(job.getBusinessGroup());                   
			job1.setName(job.getJobName());
			job1.setPackageChecksum(job.getChecksum());
			job1.setPackageFileCount(job.getFileCount());
			job1.setPackageFolderCount(job.getFolderCount());
			job1.setPackageSize(job.getPackageSize());
			job1.setRepositoryId(repoId);
			List<String> repositoryPath = new ArrayList<>();
			repositoryPath.add(job.getRepositoryPath());
			job1.setRepositoryPaths(repositoryPath);
			job1.setTransportServerId(xportServerId);
			job1.setTransportServerPath(job.getTransportServerPath());
			job1.setType(jobType);
			job1.setUserId(user.getUserId());
			job1.setUserName(user.getUserName());
			job1.setUserEmail(user.getUserEmail());
			job1.setFolderTypes(job.getFolderTypes());
			job1.setContentSize(job.getContentSize());
			Gson gson = new Gson();
			Type messageType = new TypeToken<UploadJobMessage>() {
			}.getType();
			message = gson.toJson(job1, messageType);
		} catch (Throwable th) {
			th.printStackTrace();
		}
		return message;
	}

	/**
	 *
	 * @param json
	 * @return
	 * @throws SftpException 
	 * @throws JSchException 
	 * @throws IOException 
	 * @throws SocketException 
	 * @throws ServiceException
	 */
	public String scanSourceLocation(){

		String strUserName="";
		String strSourceLocation="";
		String strTargetLocation="";
		String strBusinessGroup="";
		String strPassword="";
		String json="";

		//ingestionUtil.createObjectMongoDBConnection();

		//fetch value from drop_table
		List<JobSourceDropLocationDetails> jobSrcDrpLocDetailsList =  jobSrcDrpLocDetailsRepo.findAll();
		for(JobSourceDropLocationDetails jobSourceDropLocationDetails : jobSrcDrpLocDetailsList) {
			ServiceLogger.info(this, " - Data Exists in job_source_drop_details table");
			//strNetworkId = jobSourceDropLocationDetails.getIp();
			//strNetworkPort = jobSourceDropLocationDetails.getPort();
			strUserName = jobSourceDropLocationDetails.getUserName();
			strPassword = jobSourceDropLocationDetails.getPassword();
			strSourceLocation = jobSourceDropLocationDetails.getSrcLocation();
			strTargetLocation = jobSourceDropLocationDetails.getTargetLocation();					
			strBusinessGroup = jobSourceDropLocationDetails.getBusinessGroup();	
			System.out.println(strSourceLocation);

			TransportServerMaster record = transportServerRepository.findByServerId(xportServerId);
			if(null == record){
				continue;
			}
			SessionParams sessionParams = ingestionUtil.toSessionParam(record);

			Session session = null;
			Channel channel = null;
			ChannelSftp channelSftp = null;
			try{
				session = ingestionUtil.connectToSFTPServer();
				channel = session.openChannel("sftp");
				channel.connect();
				channelSftp = (ChannelSftp) channel;

				if(ingestionUtil.checkFileTxt(channelSftp, strSourceLocation)){
					UploadJobInfo job = new UploadJobInfo();
					UserDetail user=new UserDetail();
					job.setBusinessGroup(strBusinessGroup);
					job.setRepositoryPath(strTargetLocation);
					user.setUserId(strUserName);
					user.setUserName(strUserName);
					user.setUserEmail(".");
					user.setUserPassword(strPassword);

					job.setFileCount(ingestionUtil.getFileCount(channelSftp, strSourceLocation,0));
					job.setFolderCount(ingestionUtil.getFolderCount(channelSftp, strSourceLocation,0));
					job.setContentSize(ingestionUtil.getFolderSize(channelSftp, strSourceLocation,0));

					ArrayList<String> selectedFilePathList = new ArrayList<>();
					job.setSelectedFilePaths(ingestionUtil.getFilePath(channelSftp, strSourceLocation,selectedFilePathList));

					String jobName=jobType+ dateFormat.format(new Date());
					System.out.println("job name"+jobName);
					job.setJobName(jobName);
					String targetFileName = jobName+ ".tar.gz";

					String uploadDirPath = ingestionUtil.getUploadDirectory(channelSftp);

					System.out.println("uploadDirPath"+uploadDirPath);

					String transportServerPath = uploadDirPath + "/" + targetFileName;

					System.out.println("transportServerPath"+transportServerPath);


					ingestionUtil.createTGZFile(strSourceLocation,transportServerPath,uploadDirPath,targetFileName,sessionParams);
					StringBuffer result=ingestionUtil.createChecksum(transportServerPath,uploadDirPath,targetFileName,sessionParams);
					String[] strChecksumArray=result.toString().split(" ");
					job.setChecksum(strChecksumArray[0]);
					job.setTransportServerPath(transportServerPath);
					job.setPackageSize(ingestionUtil.getPackageSize(channelSftp, uploadDirPath,targetFileName));
					json=createJSONParameterFile(job,user);
				}
				if(json!=null){
					ingestionUtil.renameFileTxt(channelSftp, strSourceLocation); 
				}
			}catch (SftpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(channel.isConnected()){
					channel.disconnect();
				}
				if(session.isConnected()){
					session.disconnect();
				}			
			}
		}
		return json;
	}

	public void queueJob(Job jobDetail, Long Jobid) throws Throwable  {
		jmsProducer.queueJob(jobDetail, Jobid);
	}

	/**
	 * This method converts the json string into Job object
	 *
	 * @param json
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private Job jsonMapper(String json) throws JsonParseException, JsonMappingException, IOException {
		Job jobDetails = null;
		ObjectMapper mapper = new ObjectMapper();
		if (json != null) {
			String jsonContent = json;
			jobDetails = mapper.readValue(jsonContent, Job.class);
		}
		ServiceLogger.info(getClass(), jobDetails.toJsonString());
		return jobDetails;
	}

	/**
	 * This Method updates the Status of Job in job_master table
	 *
	 * @param json
	 * @param jobId
	 * @return
	 * @throws ServiceException
	 */
	public String updateJobStatus(Long jobId, String json) throws ServiceException {
		ServiceLogger.info(this, " + updateJob()");
		try {
			Job jobDetails = jsonMapper(json);
			if (jobDetails != null) {
				// converting received json in Job object
				updateJobMasterStatus(jobId, jobDetails.getStatus());
			}
		} catch (Throwable e) {
			throw new ServiceException(e);
		}
		ServiceLogger.info(this, " - updateJob()");
		return JsonApi.toJson("id", jobId);
	}


	@HystrixCommand(fallbackMethod = "errorService")
	private void callService(String message, String url){
		try{
			logger.info(message);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<String>(message,headers);
			restTemplate.postForEntity(url, entity, JsonObject.class);
			logger.info("Current Time is : "+new Date());
		}catch(Throwable th){
			logger.error( "Call to Service failed. "+th.getMessage(), th);
		}		
	}

	protected void errorService(String message, String url) throws Throwable{
		logger.info( " - fallback method for " + message +" . Queue Item not processed");
		throw new Error("Error in Service calling "+url);
	}

	@HystrixCommand(fallbackMethod = "errorGetService")
	private String callGetService(String url){
		String response = null;
		try{
			response = restTemplate.getForObject(url, String.class);
		}catch(Throwable th){
			logger.error( "Call to Service failed. "+th.getMessage(), th);
		}
		return response;	
	}

	protected void errorGetService(String url) throws Throwable{
		logger.info( " - fallback method for " + url +" . Queue Item not processed");
		throw new Error("Error in Service calling "+url);
	}	

	public void updateJobMasterStatus(Long jobId, String status){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("jobId", jobId);
		jsonObject.addProperty("status", status);
		callService(JsonApi.toJson(jsonObject), updateJobStatusUrl);
	}

	final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

}
