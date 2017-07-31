package com.hcl.neo.eloader.configuration;

import java.util.logging.Logger;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.web.client.RestTemplate;

/**
 * The Eloader Consumer Spring configuration.
 * 
 * @author Souvik Das
 */
@Configuration
@ComponentScan("com.hcl.neo.eloader")
@EntityScan("com.hcl.neo.eloader")
@EnableJms
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
     
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrency("1-1");
        return factory;
    }

}
