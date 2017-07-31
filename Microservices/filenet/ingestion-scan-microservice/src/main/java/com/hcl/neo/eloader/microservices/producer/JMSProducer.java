package com.hcl.neo.eloader.microservices.producer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.hcl.neo.eloader.microservices.logger.ServiceLogger;
import com.hcl.neo.eloader.microservices.model.Job;
import com.hcl.neo.eloader.microservices.services.JobService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * @author Juhi.Jain This class is created to Push the details of newly created
 * Job in activemq
 *
 */
@Component
public class JMSProducer {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private JobService jobService;

	@Autowired
	private JmsTemplate bulkJobHeavyQueue;

	@Autowired
	private JmsTemplate bulkJobLightQueue;

	@Value("${heavyJobMinSize}")
	private String heavyJobMinSize;

	@Value("${jms.queue.heavyjob}")
	private String heavyJobDestination;

	@Value("${jms.queue.lightjob}")
	private String lightJobDestination;

	public void queueJob(Job jobDetail, Long Jobid) throws Throwable {
		ServiceLogger.info(this, " + queueJob()");
		sendBulkJobMsg(jobDetail, Jobid);
		ServiceLogger.info(this, " - queueJob()");
	}

	public void sendBulkJobMsg(final Job jobDetails, final Long jobId) throws Throwable {
		ServiceLogger.info(this, " + sendBulkJobMsg()");
		jobDetails.setId(jobId);
		long size = jobDetails.getContentSize();
		long minSize = Long.parseLong(heavyJobMinSize);
		ServiceLogger.info(this, " - sendBulkJobMsg() "+jobDetails.toJsonString());
		if (size > minSize) {
			System.out.println(heavyJobDestination);
			bulkJobHeavyQueue.send(heavyJobDestination,
					new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					TextMessage message = session.createTextMessage(jobDetails.toJsonString());
					return message;
				}

			});
		} else {
			boolean flag = createLightJobQueue(jobDetails);
			ServiceLogger.info(this, " - flag() "+flag);
		}
		jobService.addJobStatus(jobId, "Job Queued");
		jobService.updateJobMasterStatus(jobDetails.getId(), "QUEUED");
		ServiceLogger.info(this, " - sendBulkJobMsg() ");
	}

	@HystrixCommand(fallbackMethod = "errorQueue")
	public boolean createLightJobQueue(final Job jobDetails){
		boolean flag = true;
		try{
			bulkJobLightQueue.send(lightJobDestination,
					new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					TextMessage message = session.createTextMessage(jobDetails.toJsonString());
					return message;
				}

			});
		}catch(Throwable th){
			flag = false;
			throw th;
		}
		return flag;
	}

	public boolean errorLightJobQueue(Job jobDetails) throws Throwable{
		ServiceLogger.info(this, " - fallback method for " + jobDetails.getId() +" . Not queued in activeMQ");
		throw new Error("Error in queue");
	}
}

