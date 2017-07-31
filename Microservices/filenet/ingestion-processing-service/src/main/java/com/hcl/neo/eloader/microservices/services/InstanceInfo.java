/**
 * 
 */
package com.hcl.neo.eloader.microservices.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import com.hcl.neo.eloader.model.ServiceInfo;

/**
 * @author souvik.das
 *
 */
@Service
public class InstanceInfo {
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	public ServiceInfo getInstanceInfo(){
		ServiceInstance serviceInstance = discoveryClient.getLocalServiceInstance();
		ServiceInfo serviceInfo = new ServiceInfo();
		serviceInfo.setHost(serviceInstance.getHost());
		serviceInfo.setPort(serviceInstance.getPort());
		serviceInfo.setUri(serviceInstance.getUri().toString());
		serviceInfo.setServiceId(serviceInstance.getServiceId());
		serviceInfo.setMetadata(serviceInstance.getMetadata());
		return serviceInfo;
	}

}
