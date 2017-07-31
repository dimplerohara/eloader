package com.hcl.neo.eloader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.hcl.neo.eloader.configuration.CustomConfiguration;
import com.hcl.neo.eloader.dao.TransportServerRepository;

@SpringBootApplication
@EnableAutoConfiguration
@Import(CustomConfiguration.class)
@ComponentScan("com.hcl.neo.eloader")
public class Application implements CommandLineRunner {

	
	@Autowired
	private TransportServerRepository transportSeverRepository;

	public static void main(String[] args) {
		try{
			SpringApplication.run(Application.class, args);
		}catch(Throwable th){
			th.printStackTrace();
		}
	}
	
	public void run(String... args) {
		/*TransportServerMaster t1 = new TransportServerMaster();
		t1.setHost("10.99.18.146");
		t1.setProtocol("SFTP");
		t1.setPort("22.0");
		t1.setUserName("dctm");
		t1.setServerId(1);
		t1.setType("I");
		transportSeverRepository.save(t1);*/
		/*TransportServerMaster t1 = transportSeverRepository.findByServerId(1);
		t1.setPassword("hclserver123#$");
		transportSeverRepository.save(t1);
		System.out.println(transportSeverRepository.findAll());*/

	}

}
