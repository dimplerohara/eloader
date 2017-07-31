package com.hcl.neo.eloader.microservices.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcl.neo.eloader.common.JsonApi;
import com.hcl.neo.eloader.dao.JobSrcDrpLocDetailsRepo;
import com.hcl.neo.eloader.impl.SequenceDaoImpl;
import com.hcl.neo.eloader.model.JobSourceDropLocationDetails;

@Service
public class LandingZoneServices {

	@Autowired
	private JobSrcDrpLocDetailsRepo landingZoneRepository;

	@Autowired
	SequenceDaoImpl sequenceDao;

	public String getLandingZone(Long dropLocId){
		JobSourceDropLocationDetails landingZoneDetails = landingZoneRepository.findByDropLocId(dropLocId);
		if(null == landingZoneDetails){
			return "{}";
		}
		return JsonApi.toJson(landingZoneDetails);
	}
	
	public List<JobSourceDropLocationDetails> getAllLandingZones(){
		return landingZoneRepository.findAll();
	}

	public String createLandingZone(String json){
		String responseString = null;
		try {
			JobSourceDropLocationDetails landingZoneDetails = new JobSourceDropLocationDetails();
			JobSourceDropLocationDetails landingZoneDetails1 = JsonApi.fromJson(json, JobSourceDropLocationDetails.class);
			landingZoneDetails.setName(landingZoneDetails.getName());
			landingZoneDetails.setIp(landingZoneDetails1.getIp());
			landingZoneDetails.setPort(landingZoneDetails1.getPort());
			landingZoneDetails.setUserName(landingZoneDetails1.getUserName());
			landingZoneDetails.setPassword(landingZoneDetails1.getPassword());
			landingZoneDetails.setLocationType(landingZoneDetails1.getLocationType());
			landingZoneDetails.setDropLocId(sequenceDao.getNextSequenceId("serverId"));
			landingZoneDetails.setGeoLocation(landingZoneDetails1.getGeoLocation());
			landingZoneDetails.setTargetCMS(landingZoneDetails1.getTargetCMS());
			landingZoneDetails.setErrorLocation(landingZoneDetails1.getErrorLocation());
			landingZoneDetails.setBackupLocation(landingZoneDetails1.getBackupLocation());
			landingZoneDetails.setSrcLocation(landingZoneDetails1.getSrcLocation());
			landingZoneDetails.setTargetLocation(landingZoneDetails1.getTargetLocation());
			landingZoneDetails = landingZoneRepository.save(landingZoneDetails);
			if(landingZoneDetails.getId() != null){
				responseString = "New Landing zone details has been saved. Id : "+landingZoneDetails.getDropLocId();
			}else{
				responseString = "Something went wrong.";
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseString = "Something went wrong.";
		}
		return JsonApi.toJson("status", responseString);
	}
	
	public String updateLandingZone(String json, Long dropLocId){
		String responseString = null;
		JobSourceDropLocationDetails landingZoneDetails = null;
		try {
			landingZoneDetails = landingZoneRepository.findByDropLocId(dropLocId);
			JobSourceDropLocationDetails landingZoneDetails1 = JsonApi.fromJson(json, JobSourceDropLocationDetails.class);
			landingZoneDetails.setName(landingZoneDetails1.getName() == null ? landingZoneDetails.getName() : landingZoneDetails1.getName());
			landingZoneDetails.setIp(landingZoneDetails1.getIp() == null ? landingZoneDetails.getIp() : landingZoneDetails1.getIp());
			landingZoneDetails.setPort(landingZoneDetails1.getPort() == null ? landingZoneDetails.getPort() : landingZoneDetails1.getPort());
			landingZoneDetails.setUserName(landingZoneDetails1.getUserName() == null ? landingZoneDetails.getUserName() : landingZoneDetails1.getUserName());
			landingZoneDetails.setPassword(landingZoneDetails1.getPassword() == null ? landingZoneDetails.getPassword() : landingZoneDetails1.getPassword());
			landingZoneDetails.setLocationType(landingZoneDetails1.getLocationType() == null ? landingZoneDetails.getLocationType() : landingZoneDetails1.getLocationType());
			landingZoneDetails.setGeoLocation(landingZoneDetails1.getGeoLocation() == null ? landingZoneDetails.getGeoLocation() : landingZoneDetails1.getGeoLocation());
			landingZoneDetails.setTargetCMS(landingZoneDetails1.getTargetCMS() == null ? landingZoneDetails.getTargetCMS() : landingZoneDetails1.getTargetCMS());
			landingZoneDetails.setErrorLocation(landingZoneDetails1.getErrorLocation() == null ? landingZoneDetails.getErrorLocation() : landingZoneDetails1.getErrorLocation());
			landingZoneDetails.setBackupLocation(landingZoneDetails1.getBackupLocation() == null ? landingZoneDetails.getBackupLocation() : landingZoneDetails1.getBackupLocation());
			landingZoneDetails.setSrcLocation(landingZoneDetails1.getSrcLocation() == null ? landingZoneDetails1.getSrcLocation() : landingZoneDetails1.getSrcLocation());
			landingZoneDetails.setTargetLocation(landingZoneDetails1.getTargetLocation() == null ? landingZoneDetails1.getTargetLocation() : landingZoneDetails1.getTargetLocation());
			landingZoneDetails = landingZoneRepository.save(landingZoneDetails);
			if(landingZoneDetails.getId() != null){
				responseString = "Landing zone details has been updated. Id : "+landingZoneDetails.getDropLocId();
			}else{
				responseString = "Landing zone details update failed for id "+landingZoneDetails.getDropLocId();
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseString = "Landing zone details update failed for id "+landingZoneDetails.getDropLocId();
		}
		return JsonApi.toJson("status", responseString);
	}
	
	public String deleteLandingZone(Long serverId){
		String responseString = null;
		JobSourceDropLocationDetails landingZoneDetails = null;
		try {
			landingZoneDetails = landingZoneRepository.findByDropLocId(serverId);
			if(landingZoneDetails != null){
				landingZoneRepository.delete(landingZoneDetails);
				responseString = "Landing zone details has been deleted. Id : "+serverId;
			}else{
				responseString = "Landing zone deletion failed for id "+serverId;
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseString = "Landing zone deletion failed for id "+serverId;
		}
		return JsonApi.toJson("status", responseString);
	}
}
