package com.hcl.neo.cms.microservices.configuration;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * The accounts Spring configuration.
 * 
 * @author sakshi_ja
 * 
 */
@Configuration
@ComponentScan("com.hcl.neo.cms")
@EntityScan("com.hcl.neo.cms")
public class CmsDctmConfiguration {

	@Autowired
	private ApplicationContext applicationContext;
	protected Logger logger;

	public CmsDctmConfiguration() {
		logger = Logger.getLogger(getClass().getName());
	}
	
	@Bean(name={"jaxb2Marshaller1"})
	public Jaxb2Marshaller jaxb2Marshaller1Bean() {
		Jaxb2Marshaller jaxb2Marshaller1 = new Jaxb2Marshaller();
		jaxb2Marshaller1.setClassesToBeBound(com.hcl.neo.cms.microservices.excel.schema_metadata.Objects.class);
		jaxb2Marshaller1.setSchema(applicationContext.getResource("classpath:xsd/metadata.xsd"));
		return jaxb2Marshaller1;
	}
	
	@Bean(name={"jaxb2Marshaller2"})
	public Jaxb2Marshaller jaxb2Marshaller2Bean() {
		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
		jaxb2Marshaller.setClassesToBeBound(com.hcl.neo.cms.microservices.excel.schema_objecttype.ObjectTypeSet.class);
		jaxb2Marshaller.setSchema(applicationContext.getResource("classpath:xsd/objectType.xsd"));
		return jaxb2Marshaller;
	}
	
	@Value("${spring.activemq.broker-url}")
	private String JMS_BROKER_URL;
	
}
