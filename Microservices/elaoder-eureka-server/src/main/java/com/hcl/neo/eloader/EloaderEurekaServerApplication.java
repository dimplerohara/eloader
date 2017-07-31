package com.hcl.neo.eloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EloaderEurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EloaderEurekaServerApplication.class, args);
	}
}
