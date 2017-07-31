package com.hcl.neo.eloader.microservices.services;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Import;

import com.hcl.neo.eloader.microservices.configuration.EloaderConfiguration;

/**
 * Run as a micro-service, registering with the Discovery Server (Eureka).
 * 
 * @author souvik.das
 */
@EnableAutoConfiguration
@EnableDiscoveryClient
@Import(EloaderConfiguration.class)
@EnableHystrix
@EnableCircuitBreaker
@EnableZuulProxy
public class EloaderProcessingService {

	protected Logger logger = Logger.getLogger(EloaderProcessingService.class.getName());

	/**
	 * Run the application using Spring Boot and an embedded servlet engine.
	 * 
	 * @param args
	 *            Program arguments - ignored.
	 */
	public static void main(String[] args) {
		// Tell server to look for eloader-microservices.yml
		System.setProperty("spring.config.name", "eloader-process-service");

		SpringApplication.run(EloaderProcessingService.class, args);
	}
}
