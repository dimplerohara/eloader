package transporter;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.network.handler.TransportFactory;
import com.hcl.neo.eloader.network.handler.Transporter;
import com.hcl.neo.eloader.network.handler.common.TransportServerType;
import com.hcl.neo.eloader.network.handler.ftp.FtpTransportFactory;
import com.hcl.neo.eloader.network.handler.operation.ContentTransferMonitor;
import com.hcl.neo.eloader.network.handler.operation.ContentTransferMonitorImpl;
import com.hcl.neo.eloader.network.handler.params.DownloadParams;
import com.hcl.neo.eloader.network.handler.params.SessionParams;
import com.hcl.neo.eloader.network.handler.params.UploadParams;
import com.hcl.neo.eloader.network.handler.sftp.SftpTransportFactory;

public class Test {

	public static void main(String[] s){
		try{
			Logger.info(Test.class, "started");
			//doSomething();
			//testConnection();
			//uploadSftp();
//			downloadSftp();
			uploadFtp();
			//downloadFtp();
			Logger.info(Test.class, "finished");
		}
		catch(Throwable e){
			e.printStackTrace();
		}
		finally{
			System.exit(0);
		}
	}
	
	public static void doSomething(){
		Logger.info(Test.class, TransportServerType.EXTERNAL.equals("E"));
	}
	
	public static SessionParams getFtpSessionParams(){
		SessionParams params = new SessionParams();
		params.setHost("10.160.51.80");
		params.setPort(21);
		params.setUser("ftp");
		params.setPassword("ftp");
		
//		params.setHost("selfashishbansal.sharefileftp.com");
//		params.setPort(21);
//		params.setUser("ude5e2ed9e");
//		params.setPassword("ab465968");

		return params;
	}
	
	public static SessionParams getSftpSessionParams(){
		SessionParams params = new SessionParams();
		//params.setHost("cmsftpstg.pearson.com");
		//params.setPort(443);
		//params.setUser("cmsftpdevadmin");
		//params.setPassword("ficesneLl89");
		
		params.setHost("ussanfux010.pearsontc.com");
		params.setPort(22);
		params.setUser("cmsftpadmin");
		params.setPassword("sftp#2aug14");
		
		//params.setHost("usmillvalleyux001.pearsoncms.com");
		//params.setPort(22);
		//params.setUser("cmsftpadmin");
		//params.setPassword("sftp#may#14");
		
		return params;
	}
	
	public static void testConnection() throws Throwable{
		TransportFactory factory = FtpTransportFactory.getInstance();
		Transporter transporter = factory.createTransporter();
		transporter.setSessionParams(getFtpSessionParams());
		transporter.testConnection();
		
		factory = SftpTransportFactory.getInstance();
		transporter = factory.createTransporter();
		transporter.setSessionParams(getSftpSessionParams());
		//transporter.testConnection();
	}
	
	public static void uploadFtp() throws Throwable{
		UploadParams uploadParams = new UploadParams();
		//uploadParams.setLocalPath("C:/dev/content/upload/bulk");
		uploadParams.setLocalPath("C:/dev/bulk/workspace/sche+ma.tgz");
		//uploadParams.setRemotePath("/");
		uploadParams.setRemotePath("EXPORT/20140924/10274/sche+ma.tgz");
		ContentTransferMonitor monitor = new ContentTransferMonitorImpl();
		uploadParams.setContentTransferMonitor(monitor);
		
		TransportFactory factory = FtpTransportFactory.getInstance();
		Transporter transporter = factory.createTransporter();
		transporter.setSessionParams(getFtpSessionParams());
		transporter.upload(uploadParams);
	}
	
	public static void downloadFtp() throws Throwable{
		
		DownloadParams downloadParams = new DownloadParams();
		downloadParams.setLocalPath("C:/dev/content/download/ftp");
		downloadParams.setRemotePath("EXPORT/Dimple/Exorwerr.tgz");
		downloadParams.setTransferStreams(1);
		ContentTransferMonitor monitor = new ContentTransferMonitorImpl();
		downloadParams.setContentTransferMonitor(monitor);
		
		TransportFactory factory = FtpTransportFactory.getInstance();
		Transporter transporter = factory.createTransporter();
		transporter.setSessionParams(getFtpSessionParams());
		transporter.download(downloadParams);
	}
	
	public static void uploadSftp() throws Throwable{
		
		UploadParams uploadParams = new UploadParams();
		uploadParams.setLocalPath("C:/dev/content/upload/text.txt");//PXE-authoring.tar.gz");
		uploadParams.setRemotePath("IMPORT/20141807/test");
		uploadParams.setTransferStreams(5);
		ContentTransferMonitor monitor = new ContentTransferMonitorImpl();
		uploadParams.setContentTransferMonitor(monitor);
		
		TransportFactory factory = SftpTransportFactory.getInstance();
		Transporter transporter = factory.createTransporter();
		transporter.setSessionParams(getSftpSessionParams());
		transporter.upload(uploadParams);
	}
	
	public static void downloadSftp() throws Throwable{
		
		DownloadParams downloadParams = new DownloadParams();
		downloadParams.setLocalPath("C:/dev/content/download");
		downloadParams.setRemotePath("IMPORT/20141807/test123/text.txt");//PXE-authoring.tar.gz");
		downloadParams.setTransferStreams(5);
		ContentTransferMonitor monitor = new ContentTransferMonitorImpl();
		downloadParams.setContentTransferMonitor(monitor);
		
		TransportFactory factory = SftpTransportFactory.getInstance();
		Transporter transporter = factory.createTransporter();
		transporter.setSessionParams(getSftpSessionParams());
		transporter.download(downloadParams);
	}
}