package com.hcl.neo.eloader.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Import;

import com.hcl.neo.eloader.configuration.EloaderConfiguration;

@EnableAutoConfiguration
@SpringBootApplication
@Import(EloaderConfiguration.class)
@EnableDiscoveryClient
@EnableHystrix
public class EloaderConsumerApplication {

	public static void main(String[] args) {
		// Launch the application
       SpringApplication.run(EloaderConsumerApplication.class, args);
	}
}
