package com.hcl.neo.eloader.microservices.producer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import com.hcl.neo.eloader.common.JsonApi;
import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.microservices.logger.ServiceLogger;
import com.hcl.neo.eloader.microservices.params.ArchiveManager;
import com.hcl.neo.eloader.microservices.params.BulkJobParams;
import com.hcl.neo.eloader.microservices.params.BulkJobType;
import com.hcl.neo.eloader.microservices.params.ExportContentParams;
import com.hcl.neo.eloader.microservices.params.ExportMetadataParams;
import com.hcl.neo.eloader.microservices.params.ImportContentParams;
import com.hcl.neo.eloader.microservices.params.ImportMetadataParams;
import com.hcl.neo.eloader.microservices.params.ImportParams;
import com.hcl.neo.eloader.microservices.params.ObjectIdentity;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * @author souvik.das
 * 
 * This class is created to Push the details of newly created Job in activemq
 *
 */
@Component
public class JMSProducer {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private JmsTemplate bulkJobHeavyQueue;

	@Autowired
	private JmsTemplate bulkJobLightQueue;

	@Value("${heavyJobMinSize}")
	private String heavyJobMinSize;

	@Value("${jms.queue.heavyjobWrapper}")
	private String heavyJobDestination;

	@Value("${jms.queue.lightjobWrapper}")
	private String lightJobDestination;

	@Autowired
	private ArchiveManager archiveManager;

	public void queueJob(BulkJobParams params) throws Throwable {
		ServiceLogger.info(this, " + queueJob()");
		sendImportWrapperMsg(params);
		ServiceLogger.info(this, " - queueJob()");
	}

	public void sendImportWrapperMsg(final BulkJobParams params) throws Throwable {
		ServiceLogger.info(this, " + sendBulkJobMsg()");
		long size = params.getPackageSize();
		long minSize = Long.parseLong(heavyJobMinSize);
		if (BulkJobType.IMPORT.equals(params.getType())) {
			final ImportParams operationParams = archiveManager.toImportParams(params);
			Logger.info(getClass(), JsonApi.toJson(operationParams));
			Logger.info(getClass(), "operationParams - " + operationParams + "repo name -- " +operationParams.getRepository());
			final ImportContentParams importContentParams = toImportContentParams(operationParams);
			ServiceLogger.info(this, " - sendBulkJobMsg() "+importContentParams.toString());
			Logger.info(getClass(), importContentParams.toString());
			if (size > minSize) {
				bulkJobHeavyQueue.send(heavyJobDestination,
						new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage message = session.createTextMessage(JsonApi.toJson(importContentParams));
						return message;
					}
				});
			} else {
				boolean flag = createLightJobQueue(JsonApi.toJson(importContentParams));
				ServiceLogger.info(this, " - flag() "+flag);
			}
			ServiceLogger.info(this, " - sendBulkJobMsg() ");
		}else if(BulkJobType.IMPORT_METADATA.equals(params.getType())){
			final ImportMetadataParams operationParams = archiveManager.toImportMetadataParams(params);
			ServiceLogger.info(this, " - sendBulkJobMsg() "+operationParams.toString());
			Logger.info(getClass(), operationParams.toString());
			if (size > minSize) {
				bulkJobHeavyQueue.send(heavyJobDestination,
						new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage message = session.createTextMessage(JsonApi.toJson(operationParams));
						return message;
					}
				});
			} else {
				boolean flag = createLightJobQueue(JsonApi.toJson(operationParams));
				ServiceLogger.info(this, " - flag() "+flag);
			}
			ServiceLogger.info(this, " - sendBulkJobMsg() ");
		}else if(BulkJobType.EXPORT_METADATA.equals(params.getType())){
			final ExportMetadataParams operationParams = archiveManager.toExportMetadataParams(params);
			ServiceLogger.info(this, " - sendBulkJobMsg() "+operationParams.toString());
			final ExportContentParams exportContentParams = toExportMetadataParams(operationParams);
			Logger.info(getClass(), exportContentParams.toString());
			if (size > minSize) {
				bulkJobHeavyQueue.send(heavyJobDestination,
						new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage message = session.createTextMessage(JsonApi.toJson(exportContentParams));
						return message;
					}
				});
			} else {
				boolean flag = createLightJobQueue(JsonApi.toJson(exportContentParams));
				ServiceLogger.info(this, " - flag() "+flag);
			}
			ServiceLogger.info(this, " - sendBulkJobMsg() ");
		}
	}

	@HystrixCommand(fallbackMethod = "errorQueue")
	private boolean createLightJobQueue(final String messageText){
		boolean flag = true;
		try{
			bulkJobLightQueue.send(lightJobDestination,
					new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					TextMessage message = session.createTextMessage(messageText);
					return message;
				}

			});
		}catch(Throwable th){
			flag = false;
			throw th;
		}
		return flag;
	}

	@SuppressWarnings("unused")
	private boolean errorLightJobQueue(final String message) throws Throwable{
		ServiceLogger.info(this, " - fallback method for " + message +" . Not queued in activeMQ");
		throw new Error("Error in queue");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ImportContentParams toImportContentParams(ImportParams operationParams){
		ImportContentParams importContentParams = ImportContentParams.newObject();
		importContentParams.setImportResourceFork(false);
		importContentParams.setObjectTypes(new HashMap());
		importContentParams.setOwnerName(operationParams.getOwnerName());
		ObjectIdentity destFolder = new ObjectIdentity();
		destFolder.setObjectPath(operationParams.getRepositoryPath());
		importContentParams.setDestFolder(destFolder);
		importContentParams.setSrcPathList(operationParams.getLocalPath());
		importContentParams.setId((operationParams.getId() == null) ? 0 :  operationParams.getId()); 
		importContentParams.setRepository(operationParams.getRepository());
		importContentParams.setJobType(operationParams.getJobType());
		return importContentParams;
	}
	
	private ExportContentParams toExportMetadataParams(ExportMetadataParams params){
		ExportContentParams exportContentParams = new ExportContentParams();
		exportContentParams.setDestDir(params.getLocalPath());
		List<ObjectIdentity> list = new ArrayList<ObjectIdentity>();
		for(String path : params.getRepositoryPath()){
			ObjectIdentity objectIdentity = new ObjectIdentity();
			objectIdentity.setObjectPath(path);
			list.add(objectIdentity);
		}
		exportContentParams.setObjectList(list);
		exportContentParams.setId(params.getId());
		exportContentParams.setRepository(params.getRepository());
		return exportContentParams;
	}
}

