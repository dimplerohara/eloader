package com.hcl.neo.cms.microservices.services;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import com.hcl.neo.cms.microservices.configuration.CmsDctmConfiguration;

/**
 * Run as a micro-service, registering with the Discovery Server (Eureka).
 * 
 * @author sakshi_ja
 */
@SpringBootApplication
@EnableDiscoveryClient
@Import(CmsDctmConfiguration.class)
public class CmsDctmService {

	protected Logger logger = Logger.getLogger(CmsDctmService.class.getName());

	/**
	 * Run the application using Spring Boot and an embedded servlet engine.
	 * 
	 * @param args
	 *            Program arguments - ignored.
	 */
	public static void main(String[] args) {
		// Tell server to look for eloader-microservices.yml
		System.setProperty("spring.config.name", "cms-dctm-services");
		SpringApplication.run(CmsDctmService.class, args);
	}
}
