package com.hcl.neo.eloader.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hcl.neo.eloader.dao.BusinessGrpMasterRepository;
import com.hcl.neo.eloader.dao.JobSrcDrpLocDetailsRepo;
import com.hcl.neo.eloader.dao.JobStatusMasterRepository;
import com.hcl.neo.eloader.dao.JobTypeMasterRepository;
import com.hcl.neo.eloader.dao.ObjectTypeMasterRepository;
import com.hcl.neo.eloader.dao.RepoMasterRepository;
import com.hcl.neo.eloader.dao.TransportServerRepository;
import com.hcl.neo.eloader.model.BusinessGroupMaster;
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

	@Before
	public void setUp() {
		//createJobTypeMasterData();
		//createJobStatusMasterData();
		//createObjTypeMasterData();
		//createBuGrpMasterData();
		//createRepoMasterData();
		//createTransportServerData();
		createDropLocationData();
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
		JobSourceDropLocationDetails sourcedropDetails = new JobSourceDropLocationDetails();
		sourcedropDetails.setIp("10.99.18.152");
		sourcedropDetails.setPort("22");
		sourcedropDetails.setUserName("dmadmin");
		sourcedropDetails.setPassword("dmadmin");
		sourcedropDetails.setTargetCMS("Documentum");
		sourcedropDetails.setTargetLocation("/Testing/demo");
		sourcedropDetails.setSrcLocation("/home/dctm/Sakshi");
		sourcedropDetails.setBusinessGroup("bulk_beta_test");
		sourceDropLocationDetailsRepository.save(sourcedropDetails);
		System.out.println("Data added");
	}
	@Test
	public void defaultRunnable(){
		createDropLocationData();
	}
}