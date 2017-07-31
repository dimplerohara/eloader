package com.hcl.neo.cms.microservices.services;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import com.hcl.neo.cms.microservices.configuration.CmsConfiguration;

/**
 * Run as a micro-service, registering with the Discovery Server (Eureka).
 * 
 * @author souvik.das
 */
@SpringBootApplication
@EnableDiscoveryClient
@Import(CmsConfiguration.class)
public class CmsService {

	protected Logger logger = Logger.getLogger(CmsService.class.getName());

	/**
	 * Run the application using Spring Boot and an embedded servlet engine.
	 * 
	 * @param args
	 *            Program arguments - ignored.
	 */
	public static void main(String[] args) {
		// Tell server to look for eloader-microservices.yml
		System.setProperty("spring.config.name", "cms-services");
		SpringApplication.run(CmsService.class, args);
	}
}
