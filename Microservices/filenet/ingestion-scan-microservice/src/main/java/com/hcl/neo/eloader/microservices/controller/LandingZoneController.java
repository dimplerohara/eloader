package com.hcl.neo.eloader.microservices.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hcl.neo.eloader.microservices.services.LandingZoneServices;
import com.hcl.neo.eloader.model.JobSourceDropLocationDetails;

@Controller
public class LandingZoneController {
	
	@Autowired
	private LandingZoneServices services;
	
	@RequestMapping(value = "/services/landzones/getAll", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public @ResponseBody List<JobSourceDropLocationDetails> getAllLandingZones() throws Throwable {
        return services.getAllLandingZones();
    }

	@RequestMapping(value = "/services/landzone/get/{serverId}", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public @ResponseBody String getLandingZone(@PathVariable("serverId") Long serverId) throws Throwable {
        String response = services.getLandingZone(serverId);
        return response;
    }
	
	@RequestMapping(value = "/services/landzone/create", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", 
			produces = "application/json;charset=UTF-8")
    public @ResponseBody String createLandingZone(@RequestBody String json) throws Throwable {
        String response = services.createLandingZone(json);
        return response;
    }
	
	@RequestMapping(value = "/services/landzone/update/{serverId}", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", 
			produces = "application/json;charset=UTF-8")
    public @ResponseBody String updateLandingZone(@PathVariable("serverId") Long serverId, @RequestBody String json) throws Throwable {
        String response = services.updateLandingZone(json, serverId);
        return response;
    }
	
	@RequestMapping(value = "/services/landzone/delete/{serverId}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    public @ResponseBody String deleteLandingZone(@PathVariable("serverId") Long serverId) throws Throwable {
        String response = services.deleteLandingZone(serverId);
        return response;
    }
}
