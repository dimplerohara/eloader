package com.hcl.neo.eloader.consumer;

import java.nio.charset.Charset;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.hcl.neo.eloader.common.JsonApi;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Component
@EnableHystrix
public class JmsConsumer {
	
    @Autowired
    JmsTemplate jmsTemplate;
     
    @Value("${jms.queue.heavyjob}")
    String heavyJobQueue;
    
    @Value("${jms.queue.lightjob}")
    String lightJobQueue;
    
    @Value("${eloader.processServiceUrl}")
    String processServiceUrl;
    
    @Value("${eloader.importServiceUrl}")
    String importServiceUrl;
    
    @Value("${eloader.exportServiceUrl}")
    String exportServiceUrl;
    
    @Value("${eloader.importMetadataServiceUrl}")
    String importMetadataServiceUrl;
    
    @Value("${eloader.exportMetadataServiceUrl}")
    String exportMetadataServiceUrl;
    
    @Value("${eloader.processQueueUrl}")
    String processQueueUrl;
    
    @Value("${eloader.dctm.userName}")
    String dctmUserName;
    
    @Value("${eloader.dctm.password}")
    String dctmPassword;
    
    @Autowired
	private RestTemplate restTemplate;
    
	static final Logger logger = LoggerFactory.getLogger(JmsConsumer.class);

    
    @JmsListener(destination="queue.eloader.heavy")
    public void receiveHeavyJob(final Message message){
    	logger.info("Inside receiveHeavyJob");
    	try {
        	String textMessage = ((ActiveMQTextMessage)message).getText();
        	logger.info(textMessage);
        	callService(textMessage, processServiceUrl);
		} catch (JMSException e) {
			logger.error("Error : ", e);
		}  
    	logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
    
    @JmsListener(destination = "queue.eloader.light")
    public void receiveMessage(final Message message) {
        logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        try {
        	String textMessage = ((ActiveMQTextMessage)message).getText();
        	logger.info(textMessage);
        	callService(textMessage, processServiceUrl);
		} catch (JMSException e) {
			logger.error("Error : ", e);
		}  
        logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
    
    @HystrixCommand(fallbackMethod = "errorQueue")
    private void callService(String message, String url){
    	try{
    		logger.info(message);
    		HttpHeaders headers = new HttpHeaders();
    		headers.setContentType(MediaType.APPLICATION_JSON);
    		HttpEntity<String> entity = new HttpEntity<String>(message,headers);
    		restTemplate.postForEntity(url, entity, JsonObject.class);
    		logger.info("Current Time is : "+new Date());
    	}catch(Throwable th){
    		logger.error( "Call to Service failed. "+th.getMessage(), th);
    	}
		
	}
    
    @HystrixCommand(fallbackMethod = "errorQueue")
    private void callServiceWithHeader(String message, String url){
    	try{
    		logger.info(message);
    		HttpHeaders headers = new HttpHeaders();
    		headers.setContentType(MediaType.APPLICATION_JSON);
    		String auth = dctmUserName + ":" + dctmPassword;
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            headers.set( "Authorization", authHeader );
            HttpEntity<String> entity = new HttpEntity<String>(message,headers);
    		restTemplate.postForEntity(url, entity, String.class);
    		logger.info("Current Time is : "+new Date());
    	}catch(Throwable th){
    		logger.error( "Call to Service failed. "+th.getMessage(), th);
    	}
		
	}
    
    public void errorQueue(String message, String url) throws Throwable{
    	logger.info( " - fallback method for " + message +" . Queue Item not processed");
		throw new Error("Error in queue");
    }
    
    @JmsListener(destination="queue.eloader.heavyWrapper")
    public void receiveHeavyWrapperJob(final Message message){
    	logger.info("Inside receiveHeavyWrapperJob");
    	try {
        	String textMessage = ((ActiveMQTextMessage)message).getText();
        	logger.info(textMessage);
        	String repository = null;
        	String jobId = null;
        	String type = null;
        	String url = null;
    		JsonObject object = JsonApi.fromJson(textMessage, JsonObject.class);
    		if(null != object){
    			repository = object.get("repository").toString().replaceAll("\"", "");
    			jobId = object.get("id").toString().replaceAll("\"", "");
    			type = object.get("type").toString().replaceAll("\"", "");
    		}
    		if(type.equalsIgnoreCase("IMPORT")){
    			url = importServiceUrl;
    		}else if(type.equalsIgnoreCase("IMPORT_METADATA")){
    			url = importMetadataServiceUrl;
    		}else if(type.equalsIgnoreCase("EXPORT_METADATA")){
    			url = exportMetadataServiceUrl;
    		}else if(type.equalsIgnoreCase("EXPORT")){
    			url = exportServiceUrl;
    		}
    		logger.info("Calling URL : "+url+repository+"/"+jobId);
        	callServiceWithHeader(textMessage, url+repository+"/"+jobId);
		} catch (JMSException e) {
			logger.error("Error : ", e);
		}  
    	logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
    
    @JmsListener(destination="queue.eloader.lightWrapper")
    public void receiveLightWrapperJob(final Message message){
    	logger.info("Inside receiveLightWrapperJob");
    	try {
        	String textMessage = ((ActiveMQTextMessage)message).getText();
        	logger.info(textMessage);
        	String repository = null;
        	String jobId = null;
        	String type = null;
        	String url = null;
    		JsonObject object = JsonApi.fromJson(textMessage, JsonObject.class);
    		if(null != object){
    			repository = object.get("repository").toString().replaceAll("\"", "");
    			jobId = object.get("id").toString().replaceAll("\"", "");
    			type = object.get("jobType").toString().replaceAll("\"", "");
    		}
    		if(type.equalsIgnoreCase("IMPORT")){
    			url = importServiceUrl;
    		}else if(type.equalsIgnoreCase("IMPORT_METADATA")){
    			url = importMetadataServiceUrl;
    		}else if(type.equalsIgnoreCase("EXPORT_METADATA")){
    			url = exportMetadataServiceUrl;
    		}else if(type.equalsIgnoreCase("EXPORT")){
    			url = exportServiceUrl;
    		}
    		logger.info("Calling URL : "+url+repository+"/"+jobId);
        	callServiceWithHeader(textMessage, url+repository+"/"+jobId);
		} catch (JMSException e) {
			logger.error("Error : ", e);
		}  
    	logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
    
    @JmsListener(destination="processQueue")
    public void receiveProcessQueueMsg(final Message message){
    	logger.info("Inside receiveProcessQueueMsg");
    	try {
        	String textMessage = ((ActiveMQTextMessage)message).getText();
    		logger.info("Calling URL : "+processQueueUrl);
        	callService(textMessage, processQueueUrl);
		} catch (JMSException e) {
			logger.error("Error : ", e);
		}  
    	logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
}