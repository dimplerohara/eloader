package com.hcl.neo.dctm.microservices.configuration;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The accounts Spring configuration.
 * 
 * @author Souvik Das
 */
@Configuration
@ComponentScan("com.hcl.neo.dctm")
@EntityScan("com.hcl.neo.dctm")
public class CmsDctmConfiguration {

	protected Logger logger;

	public CmsDctmConfiguration() {
		logger = Logger.getLogger(getClass().getName());
	}
	
	@Value("${spring.activemq.broker-url}")
	private String JMS_BROKER_URL;
	
	

}
