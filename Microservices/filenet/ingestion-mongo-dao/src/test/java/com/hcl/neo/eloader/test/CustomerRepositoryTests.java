package com.hcl.neo.eloader.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hcl.neo.eloader.dao.BusinessGrpMasterRepository;
import com.hcl.neo.eloader.dao.JobMasterRepository;
import com.hcl.neo.eloader.dao.JobSrcDrpLocDetailsRepo;
import com.hcl.neo.eloader.dao.JobStatusMasterRepository;
import com.hcl.neo.eloader.dao.JobTypeMasterRepository;
import com.hcl.neo.eloader.dao.ObjectTypeMasterRepository;
import com.hcl.neo.eloader.dao.RepoMasterRepository;
import com.hcl.neo.eloader.dao.TransportServerRepository;
import com.hcl.neo.eloader.model.BusinessGroupMaster;
import com.hcl.neo.eloader.model.JobMaster;
import com.hcl.neo.eloader.model.JobSourceDropLocationDetails;
import com.hcl.neo.eloader.model.JobStatusMaster;
import com.hcl.neo.eloader.model.JobTypeMaster;
import com.hcl.neo.eloader.model.ObjectTypeMaster;
import com.hcl.neo.eloader.model.RepositoryMaster;
import com.hcl.neo.eloader.model.TransportServerMaster;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerRepositoryTests {

	@Autowired
	private JobTypeMasterRepository jobTypeMasterRepository;

	@Autowired
	private JobStatusMasterRepository jobStatusMasterRepository;

	@Autowired
	private ObjectTypeMasterRepository objectTypeMasterRepository;

	@Autowired
	private BusinessGrpMasterRepository businessGrpMasterRepository;

	@Autowired
	private RepoMasterRepository repoMasterRepository;

	@Autowired
	private JobSrcDrpLocDetailsRepo sourceDropLocationDetailsRepository;

	@Autowired
	private TransportServerRepository transportServerRepository;
	
	@Autowired
	private JobMasterRepository jobMasterRepository;
	
	@Autowired
	private JobSrcDrpLocDetailsRepo jobSrcDrpLocDetailsRepo;

	@Before
	public void setUp() {
		//createJobTypeMasterData();
		//createJobStatusMasterData();
		//createObjTypeMasterData();
		//createBuGrpMasterData();
		//createRepoMasterData();
		//createTransportServerData();
		//createDropLocationData();
	}

	private void createJobTypeMasterData(){
		jobTypeMasterRepository.deleteAll();
		String [] jobTypeMasterNames = {"IMPORT", "EXPORT", "CHECKIN", "CHECKOUT", "CANCEL_CHECKOUT", "IMPORT_PLUS", "IMPORT_METADATA", "EXPORT_METADATA"};
		String [] jobTypeMasterDisplayNames = {"Import", "Export", "Checkin", "Checkout", "Cancel Checkout", "Import with metadata", "Import metadata", "Export metadata"};
		for(int counter=0; counter<jobTypeMasterNames.length; counter++){
			JobTypeMaster jobTypeMaster = new JobTypeMaster();
			jobTypeMaster.setName(jobTypeMasterNames[counter]);
			jobTypeMaster.setDisplayName(jobTypeMasterDisplayNames[counter]);
			jobTypeMasterRepository.save(jobTypeMaster);
		}
	}

	private void createJobStatusMasterData(){
		jobStatusMasterRepository.deleteAll();
		String [] jobStatusMasterNames = {"CREATED", "QUEUED_TRANSPORTER", "QUEUED_ARCHIVER", "QUEUED_REPO", "IN_PROGRESS_TRANSPORTER", "IN_PROGRESS_ARCHIVER", "IN_PROGRESS_REPO", "COMPLETED", "FAILED", "QUEUED", "PARTIAL_SUCCESS", "CANCELLED"};
		String [] jobStatusMasterDisplayNames = {"Created", "Queued for package transfer", "Queued for package processing", "Queued for content transfer to/from repository", "Package transfer in progress", "Package processing in progress", "Repository content transfer in progress", "Complete", "Failed", "Queued in Active MQ", "Some objects not transfered sucessfully. See error deatils for more information.", "Cancelled by User."};
		for(int counter=0; counter<jobStatusMasterNames.length; counter++){
			JobStatusMaster jobStatusMaster = new JobStatusMaster();
			jobStatusMaster.setName(jobStatusMasterNames[counter]);
			jobStatusMaster.setDisplayName(jobStatusMasterDisplayNames[counter]);
			jobStatusMasterRepository.save(jobStatusMaster);
		}
	}

	private void createObjTypeMasterData(){
		objectTypeMasterRepository.deleteAll();
		String [] objTypeMasterNames = {"dm_folder", "Folder"};
		String [] objTypeMasterDisplayNames = {"dm_document", "Document"};
		for(int counter=0; counter<objTypeMasterNames.length; counter++){
			ObjectTypeMaster objTypeMaster = new ObjectTypeMaster();
			objTypeMaster.setName(objTypeMasterNames[counter]);
			objTypeMaster.setDisplayName(objTypeMasterDisplayNames[counter]);
			objectTypeMasterRepository.save(objTypeMaster);
		}
	}

	private void createBuGrpMasterData(){
		businessGrpMasterRepository.deleteAll();
		String [] buGrpNames = {"bulk_beta_test", "pe_us_curriculum_all"};
		String [] buGrpDisplayNames = {"Bulk", "Curriculum"};
		String [] buGrpKmGrps = {"bulk_beta_test", "pe_us_curriculum_km"};
		for(int counter=0; counter<buGrpDisplayNames.length; counter++){
			BusinessGroupMaster businessGroupMaster = new BusinessGroupMaster();
			businessGroupMaster.setName(buGrpNames[counter]);
			businessGroupMaster.setDisplayName(buGrpDisplayNames[counter]);
			businessGroupMaster.setKmGroup(buGrpKmGrps[counter]);
			businessGrpMasterRepository.save(businessGroupMaster);
		}
	}

	private void createRepoMasterData(){
		repoMasterRepository.deleteAll();
		String [] repoNames = {"Development"};
		String [] repoDisplayNames = {"Repository for development"};
		String [] repoTypes = {"DCTM"};
		for(int counter=0; counter<repoNames.length; counter++){
			RepositoryMaster repositoryMaster = new RepositoryMaster();
			repositoryMaster.setRepoId((long) (counter+1));
			repositoryMaster.setName(repoNames[counter]);
			repositoryMaster.setDisplayName(repoDisplayNames[counter]);
			repositoryMaster.setRepositoryType(repoTypes[counter]);
			repoMasterRepository.save(repositoryMaster);
		}
	}

	private void createTransportServerData(){
		transportServerRepository.deleteAll();
		TransportServerMaster transportServerMaster = new TransportServerMaster();
		transportServerMaster.setServerId(1);
		transportServerMaster.setProtocol("SFTP");
		transportServerMaster.setHost("10.99.18.152");
		transportServerMaster.setPort(22);
		transportServerMaster.setUserName("dctm");
		transportServerMaster.setPassword("hclserver123#$");
		transportServerMaster.setType("C");
		transportServerMaster.setDispNetworkLoc("10.99.18.149");
		transportServerRepository.save(transportServerMaster);
	}

	private void createDropLocationData(){
		sourceDropLocationDetailsRepository.deleteAll();

		String [] ip = {"10.99.18.152", "10.99.18.152", "10.99.18.152"};
		String [] port = {"22","22", "22"};
		String [] userName = {"dctm","dctm", "dctm"};
		String [] password = {"hclserver123#$","hclserver123#$", "hclserver123#$"};
		String [] targetDesc = {"Documentum","Documentum", "Documentum"};
		String [] targetLocation = {"/Testing/LZ_us","/Testing/LZ_uk","/Testing/LZ_Aus"};
		String [] srcLocation = {"/home/dctm/Sakshi","/home/dctm/Sakshi", "/home/dctm/Sakshi"};
		String [] businessGroup = {"bulk_beta_test","bulk_beta_test", "bulk_beta_test"};  
		String [] geoLocation = {"us","uk", "aus"}; 
		for(int counter=0; counter<targetDesc.length; counter++){
			JobSourceDropLocationDetails sourcedropDetails = new JobSourceDropLocationDetails();
			sourcedropDetails.setIp(ip[counter]);
			sourcedropDetails.setPort(port[counter]);
			sourcedropDetails.setUserName(userName[counter]);
			sourcedropDetails.setPassword(password[counter]);
			sourcedropDetails.setTargetCMS(targetDesc[counter]);
			sourcedropDetails.setTargetLocation(targetLocation[counter]);
			sourcedropDetails.setSrcLocation(srcLocation[counter]);
			sourcedropDetails.setBusinessGroup(businessGroup[counter]);
			sourcedropDetails.setDropLocId(Long.parseLong(""+(counter+1)));
			sourcedropDetails.setGeoLocation(geoLocation[counter]);
			sourceDropLocationDetailsRepository.save(sourcedropDetails);
			System.out.println("Data added"); 
		}
		System.out.println("Data added");
	}
	
	private void checkCode(){
		Long days = 30l;
		long drpLocId = 2;
		Calendar cal = Calendar.getInstance();
		days = -days;
		cal.add(Calendar.DATE, Integer.parseInt(days.toString()));
		Date date = cal.getTime();
		Collection<String> statusCollection = new ArrayList<String>();
		statusCollection.add("COMPLETED");
		statusCollection.add("PARTIAL_SUCCESS");
		List<JobMaster> jobMasterList = jobMasterRepository.findByCreationDateAfterAndStatusInAndLandZoneId(date, statusCollection, drpLocId);
		System.out.println(jobMasterList);
	}
	
	private void checkCode1(){
		List<JobMaster> jobMasterList = jobMasterRepository.findAll();
		int index =0;
		for(JobMaster jobMaster : jobMasterList){
			if(index % 2 ==0){
				jobMaster.setLandZoneId(1l);
			}else{
				jobMaster.setLandZoneId(2l);
			}
			jobMasterRepository.save(jobMaster);
			index++;
		}
	}
	private void updateDropLocationData(){
		String responseString = null;
		JobSourceDropLocationDetails landingZoneDetails = null;
		try {
			landingZoneDetails = jobSrcDrpLocDetailsRepo.findByDropLocId(1l);
			landingZoneDetails.setSrcLocation("/home/dctm/Sakshi");
			landingZoneDetails.setTargetLocation("/Testing/LZ_us");
			landingZoneDetails.setBusinessGroup("bulk_beta_test");
			landingZoneDetails.setTargetCMS("Documentum");
			landingZoneDetails = jobSrcDrpLocDetailsRepo.save(landingZoneDetails);
			if(landingZoneDetails.getId() != null){
				responseString = "Landing zone details has been updated. Id : "+landingZoneDetails.getDropLocId();
			}else{
				responseString = "Landing zone details update failed for id "+landingZoneDetails.getDropLocId();
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseString = "Landing zone details update failed for id "+landingZoneDetails.getDropLocId();
		}
	}
	
	/*public Map<String, Long> getReportAllGeoObjects(Long days){
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
	}*/
	@Test
	public void defaultRunnable(){
		//createDropLocationData();
		//updateDropLocationData();
	}
}