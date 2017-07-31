/**
 * 
 */
package com.hcl.neo.dctm.microservices.services;

import java.util.ArrayList;
import java.util.List;

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
		return createServiceInfo(serviceInstance);
	}
	
	public List<ServiceInfo> getAllServiceInfo(){
		List<ServiceInfo> serviceInfoList = new ArrayList<ServiceInfo>();
		List<String> serviceList = discoveryClient.getServices();
		for(String serviceName : serviceList){
			List<ServiceInstance> instanceList = discoveryClient.getInstances(serviceName);
			for(ServiceInstance serviceInstance : instanceList){
				serviceInfoList.add(createServiceInfo(serviceInstance));
			}
		}		
		return serviceInfoList;
	}
	
	private ServiceInfo createServiceInfo(ServiceInstance serviceInstance){
		ServiceInfo serviceInfo = new ServiceInfo();
		serviceInfo.setHost(serviceInstance.getHost());
		serviceInfo.setPort(serviceInstance.getPort());
		serviceInfo.setUri(serviceInstance.getUri().toString());
		serviceInfo.setServiceId(serviceInstance.getServiceId());
		serviceInfo.setMetadata(serviceInstance.getMetadata());
		return serviceInfo;
	}

}
