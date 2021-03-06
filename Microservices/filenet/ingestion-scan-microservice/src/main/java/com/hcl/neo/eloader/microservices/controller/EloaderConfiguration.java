package com.hcl.neo.eloader.microservices.controller;

import java.util.logging.Logger;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

/**
 * The accounts Spring configuration.
 * 
 * @author Souvik Das
 */
//@Configuration
@ComponentScan("com.hcl.neo.eloader")
@EntityScan("com.hcl.neo.eloader")
/*@EnableHystrix
@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableZuulProxy*/
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

	/*@Bean
	public HystrixCommandAspect hystrixAspect() {
		return new HystrixCommandAspect();
	}*/
	
	@LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
