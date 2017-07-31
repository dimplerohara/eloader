package com.hcl.neo.eloader.network.handler.ftp;

import com.hcl.neo.eloader.network.handler.TransportFactory;
import com.hcl.neo.eloader.network.handler.Transporter;

public class FtpTransportFactory extends TransportFactory {

	@Override
	public Transporter createTransporter() {
		return new FtpTransporter();
	}
	
	public static TransportFactory getInstance(){
		return new FtpTransportFactory();
	}
}
