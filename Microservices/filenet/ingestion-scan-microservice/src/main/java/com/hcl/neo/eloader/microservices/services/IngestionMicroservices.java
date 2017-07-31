package com.hcl.neo.eloader.microservices.services;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.hcl.neo.eloader.microservices.controller.EloaderConfiguration;

/**
 * Run as a micro-service, registering with the Discovery Server (Eureka).
 * 
 * @author souvik.das
 */

@EnableDiscoveryClient
@Import(EloaderConfiguration.class)
@EnableScheduling
public class IngestionMicroservices {

	protected Logger logger = Logger.getLogger(IngestionMicroservices.class.getName());

	/**
	 * Run the application using Spring Boot and an embedded servlet engine.
	 * 
	 * @param args
	 *            Program arguments - ignored.
	 */
	public static void main(String[] args) {
		// Tell server to look for eloader-microservices.yml
		System.setProperty("spring.config.name", "ingestion-scan-microservice");
		SpringApplication.run(IngestionMicroservices.class, args);
	}
}
