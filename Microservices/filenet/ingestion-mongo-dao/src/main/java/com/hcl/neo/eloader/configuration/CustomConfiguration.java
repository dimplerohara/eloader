package com.hcl.neo.eloader.configuration;

import java.util.logging.Logger;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * The accounts Spring configuration.
 * 
 * @author Souvik Das
 */
@Configuration
@ComponentScan("com.hcl.neo.eloader")
@EnableMongoRepositories("com.hcl.neo.eloader")
public class CustomConfiguration {

	protected Logger logger;

	public CustomConfiguration() {
		logger = Logger.getLogger(getClass().getName());
	}
}
