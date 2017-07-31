package com.hcl.neo.eloader.microservices.configuration;

import java.util.logging.Logger;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.client.RestTemplate;

/**
 * The accounts Spring configuration.
 * 
 * @author Souvik Das
 */
@Configuration
@ComponentScan("com.hcl.neo.eloader")
@EntityScan("com.hcl.neo.eloader")
@EnableMongoRepositories("com.hcl.neo.eloader")
public class EloaderConfiguration {

	protected Logger logger;
	
	@Value("${spring.activemq.broker-url}")
	private String JMS_BROKER_URL;


	public EloaderConfiguration() {
		logger = Logger.getLogger(getClass().getName());
	}
	
	@Bean
	public ConnectionFactory connectionFactory() {
		return new ActiveMQConnectionFactory(JMS_BROKER_URL);
	}
	
	@LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
