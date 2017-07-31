package com.hcl.neo.eloader.microservices.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.neo.eloader.dao.JobMasterRepository;
import com.hcl.neo.eloader.microservices.logger.ServiceLogger;
import com.hcl.neo.eloader.microservices.services.IngestionProcessService;
import com.hcl.neo.eloader.model.JobMaster;

@RestController
public class IngestionProcessController {
	
	@Autowired
	private JobMasterRepository jobMasterRepository;
	
	@Autowired
	private IngestionProcessService ingestionProcessService;

	@RequestMapping(value = "/services/jobs/getPaths/{jobId}", method = RequestMethod.GET, produces={MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8"})
	@ResponseBody
	public List<String> getPathsFromJob(@PathVariable("jobId") Long jobId) {
		try{
			JobMaster job = jobMasterRepository.findByJobId(jobId);
			return job.getRepositoryPath();
		}catch(Throwable th){
			ServiceLogger.error(this, th, "IngestionProcessController : getPathsFromJob Error "+th.getMessage());
		}
		return new ArrayList<String>();
	}
	
	@RequestMapping(value = "/services/jobs/getObjects/{jobId}", method = RequestMethod.GET, produces={MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8"})
	@ResponseBody
	public String getObjectsFromPath(@PathVariable("jobId") Long jobId, @RequestParam("folderPath") String folderPath) {
		return ingestionProcessService.getObjectsFromCms(folderPath, jobId);
	}
	
	
	@RequestMapping(value = "/services/reports/docs/geo/{days}", method = RequestMethod.GET, produces={MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8"})
	@ResponseBody
	public Map<String,Long> getReportAllGeoObjects(@PathVariable("days") Long days) {
		return ingestionProcessService.getReportAllGeoObjects(days);
	}
	
	@RequestMapping(value = "/services/reports/transactions/geo/{days}", method = RequestMethod.GET, produces={MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8"})
	@ResponseBody
	public Map<String,Long> getReportAllGeoTransactions(@PathVariable("days") Long days) {
		return ingestionProcessService.getReportAllGeoTransactions(days);
	}
	
	@RequestMapping(value = "/services/reports/ingestion/status/{days}", method = RequestMethod.GET, produces={MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8"})
	@ResponseBody
	public Map<String,Long> getReportIngestionStatus(@PathVariable("days") Long days) {
		return ingestionProcessService.getReportIngestionStatus(days);
	}
	
	@RequestMapping(value = "/services/reports/transactions/type/{days}", method = RequestMethod.GET, produces={MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8"})
	@ResponseBody
	public Map<String,Long> getReportTransactionTypes(@PathVariable("days") Long days) {
		return ingestionProcessService.getReportTransactionTypes(days);
	}
	
}
