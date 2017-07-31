package com.hcl.neo.eloader.network.handler.sftp;

import com.hcl.neo.eloader.network.handler.TransportFactory;
import com.hcl.neo.eloader.network.handler.Transporter;

public class SftpTransportFactory extends TransportFactory {

	@Override
	public Transporter createTransporter() {
		return new SftpTransporter();
	}
	
	public static TransportFactory getInstance(){
		return new SftpTransportFactory();
	}
}
