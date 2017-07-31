package com.hcl.neo.cms.microservices.producer;

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

/**
 * @author Juhi.Jain This class is created to Push the details of newly created
 * Job in activemq
 *
 */
@Component
public class JMSProducer {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${jms.queue.processService}")
	private String destination;

	public void queueJob(String textMessage) throws Throwable {
		logger.info(" + queueJob()");
		sendMsg(textMessage);
		logger.info(" - queueJob()");
	}

	public void sendMsg(final String textMessage) throws Throwable {
		logger.info(" + sendBulkJobMsg()");
		logger.info(" - sendBulkJobMsg() "+textMessage);
		jmsTemplate.send(destination,
				new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage message = session.createTextMessage(textMessage);
				return message;
			}

		});
		logger.info(" - sendBulkJobMsg() ");
	}
}

