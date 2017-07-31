package com.hcl.neo.eloader.microservices.services;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import com.hcl.neo.eloader.microservices.controller.EloaderConfiguration;

/**
 * Run as a micro-service, registering with the Discovery Server (Eureka).
 * 
 * @author souvik.das
 */

@EnableDiscoveryClient
@Import(EloaderConfiguration.class)
public class EloaderMicroservices {

	protected Logger logger = Logger.getLogger(EloaderMicroservices.class.getName());

	/**
	 * Run the application using Spring Boot and an embedded servlet engine.
	 * 
	 * @param args
	 *            Program arguments - ignored.
	 */
	public static void main(String[] args) {
		// Tell server to look for eloader-microservices.yml
		System.setProperty("spring.config.name", "eloader-microservices");

		SpringApplication.run(EloaderMicroservices.class, args);
	}
}
