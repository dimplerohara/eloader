package com.hcl.neo.eloader.microservices.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.commons.io.FileSystemUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hcl.neo.eloader.microservices.logger.ServiceLogger;
import com.hcl.neo.eloader.microservices.properties.Constants;
import com.hcl.neo.eloader.model.JobSourceDropLocationDetails;
import com.hcl.neo.eloader.network.handler.params.SessionParams;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

@Service
public class IngestionUtil {

	@Value("${dctm.upload_transfer_stream}")
	private String uploadTransferStream;

	@Value("${dctm.retry_count}")
	private String retryCount;

	@Value("${dctm.transport_server_id}")
	private long xportServerId;

	@Value("${dctm.repo_id}")
	private long repoId;

	@Value("${dctm.SFTPLocation}")
	private String sftpLocation;

	@Value("${dctm.JobType}")
	private String jobType;

	private static final TimeZone clientTimeZone = Calendar.getInstance().getTimeZone();
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");


	public SessionParams toSessionParam(JobSourceDropLocationDetails record) {
		SessionParams sessionParams = new SessionParams();
		sessionParams.setHost(record.getIp());
		sessionParams.setPort(Integer.parseInt(record.getPort()));
		sessionParams.setUser(record.getUserName());
		sessionParams.setPassword(record.getPassword());
		return sessionParams;
	}

	/*private TransportFactory getTransportFactory(TransportType xportType){
		TransportFactory xportFactory = null;

		if(TransportType.FTP.equals(xportType)){
			xportFactory = FtpTransportFactory.getInstance();
		}
		else if(TransportType.SFTP.equals(xportType)){
			xportFactory = SftpTransportFactory.getInstance();
		}

		return xportFactory;
	}*/

	public String getUploadDirectory(ChannelSftp channelSftp) throws SocketException, IOException, JSchException, SftpException {
		String strDate=dateFormat.format(new Date());
		String jobName=jobType+strDate;
		String randomNo = Integer.toString(getRandom());
		createDirectories(channelSftp, sftpLocation,jobType);
		createDirectories(channelSftp, sftpLocation+"/"+jobType,strDate);
		createDirectories(channelSftp, sftpLocation+"/"+jobType+"/"+strDate,jobName+ "_" +randomNo);
		return jobType+"/"+strDate+"/"+jobName+ "_" +randomNo;
	}

	public static String getFTPDirDateFormat() {
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT1);
		String date = sdf.format(Calendar.getInstance(TimeZone.getTimeZone(Constants.TIME_ZONE)).getTime());
		return date;
	}
	public static int getRandom() {
		Random randomGenerator = new Random();
		return randomGenerator.nextInt(30000);
	}



	public void createTGZFile(String strSourceLocation,String transportServerPath, String uploadDirPath,String targetFileName,SessionParams sessionParams, String fileName) throws SocketException, IOException, JSchException, SftpException{ 
		Channel channel = null;
		Session session = null;
		try{			
			JSch jsch = new JSch();
			session = jsch.getSession(sessionParams.getUser(), sessionParams.getHost(),sessionParams.getPort());
			session.setPassword(sessionParams.getPassword());
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			System.out.println("Host connected.");
			channel = session.openChannel("exec");
			String TGZUploadPath=sftpLocation+"/"+transportServerPath;
			String command="cd "+strSourceLocation+"; tar czf "+TGZUploadPath+" --exclude "+fileName+" .";

			System.out.println("Command is "+command);
			((ChannelExec)channel).setCommand(command);
			System.out.println("Command executed "+command);
			OutputStream toServer = channel.getOutputStream();
			channel.connect();  
			toServer.write((command + "\r\n").getBytes());
			toServer.flush();
		}finally{
			if(channel.isConnected()){
				channel.disconnect();
			}
			if(session.isConnected()){
				session.disconnect();
			}	
		}
	}

	public StringBuffer createChecksum(String transportServerPath, String uploadDirPath,String targetFileName,SessionParams sessionParams) throws SocketException, IOException, JSchException, SftpException{ 
		Channel channel = null;
		Session session = null;
		try{
			JSch jsch = new JSch();
			session = jsch.getSession(sessionParams.getUser(), sessionParams.getHost(), sessionParams.getPort());
			session.setPassword(sessionParams.getPassword());
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			System.out.println("Host connected.");
			channel = session.openChannel("exec");
			String command="cd "+sftpLocation+"/"+uploadDirPath+"; md5sum "+targetFileName;
			StringBuffer result = new StringBuffer();
			System.out.println("Command is "+command);
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			System.out.println("Command executed "+command);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					result.append(new String(tmp, 0, i));
					System.out.print(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					System.out.println("exit-status: " + channel.getExitStatus());
					break;
				}
			}

			System.out.println(result);
			return result;
		}finally{
			if(channel.isConnected()){
				channel.disconnect();
			}
			if(session.isConnected()){
				session.disconnect();
			}	
		}

	}
	public boolean createDirectories(ChannelSftp channelSftp, String directory,String folderToCreate) throws SocketException, IOException, JSchException, SftpException{
		SftpATTRS attrs=null;
		try {
			attrs = channelSftp.stat(directory+"/"+folderToCreate);
		} catch (Exception e) {
			System.out.println(directory+"/"+folderToCreate+" not found");
		}

		if (attrs != null) {
			System.out.println("Directory exists IsDir="+attrs.isDir());
			return false;
		} else {
			System.out.println("Creating dir "+folderToCreate);
			channelSftp.cd(directory);
			channelSftp.mkdir(folderToCreate);
			return true;
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean checkFileTxt(ChannelSftp channelSftp, String strSourceLocation, String fileName) throws SftpException{
		Vector filelist = channelSftp.ls(strSourceLocation);
		boolean fileExists=false;
		for(int i=0; i<filelist.size();i++){
			LsEntry entry = (LsEntry) filelist.get(i);
			if (entry.getAttrs().isDir()) {
				continue;
			}
			else{
				if(entry.getFilename().equalsIgnoreCase(fileName)){
					fileExists=true;
				}else{
					continue;
				}
			}
		}  
		if(fileExists){
			return true;
		}else{
			return false;
		}
	}
	@SuppressWarnings("rawtypes")
	public boolean renameFileTxt(ChannelSftp channelSftp, String strSourceLocation) throws SftpException{ 
		Vector filelist = channelSftp.ls(strSourceLocation);
		boolean fileExists=false;
		for(int i=0; i<filelist.size();i++){
			LsEntry entry = (LsEntry) filelist.get(i);
			if (entry.getAttrs().isDir()) {
				continue;
			}
			else{
				if(entry.getFilename().equalsIgnoreCase("file.txt")){
					channelSftp.rename(strSourceLocation+"/"+"file.txt", strSourceLocation+"/"+"file1.txt");
				}else{
					continue;
				}
			}
		}  
		if(fileExists){
			return true;
		}else{
			return false;
		}
	}

	public Session connectToSFTPServer(JobSourceDropLocationDetails jobSourceDropLocationDetails) throws JSchException {
		SessionParams sessionParams = toSessionParam(jobSourceDropLocationDetails);

		String SFTPUSER = sessionParams.getUser();
		String SFTPPASS = sessionParams.getPassword();
		String SFTPHOST=sessionParams.getHost();
		int SFTPPORT=sessionParams.getPort();
		JSch jsch = new JSch();

		Session session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
		session.setPassword(SFTPPASS);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		ServiceLogger.debug(this, SFTPHOST+" Host connected.");
		return session;
	}

	@SuppressWarnings("rawtypes")
	public long getFileCount(ChannelSftp channelSftp, String strSourceLocation,long fileCount) throws JSchException, SftpException {
		Vector filelist = channelSftp.ls(strSourceLocation);
		for(int i=0; i<filelist.size();i++){
			LsEntry entry = (LsEntry) filelist.get(i);
			if (entry.getAttrs().isDir()) {
				if(entry.getFilename().equalsIgnoreCase(".") || entry.getFilename().equalsIgnoreCase("..")){
					continue;
				}else{
					fileCount=getFileCount(channelSftp, strSourceLocation+"/"+entry.getFilename(),fileCount);
				}
			}else{
				if(entry.getFilename().equalsIgnoreCase("file.txt")){
					continue;
				}else{
					fileCount++;
				}
			}
			ServiceLogger.debug(this,filelist.get(i).toString());
		}     
		ServiceLogger.debug(this, "File Count : "+fileCount);
		return fileCount;
	}
	@SuppressWarnings("rawtypes")
	public ArrayList<String> getFilePath(ChannelSftp channelSftp, String strSourceLocation,ArrayList<String> selectedFilePaths) throws SftpException {
		Vector filelist = channelSftp.ls(strSourceLocation);
		for(int i=0; i<filelist.size();i++){
			LsEntry entry = (LsEntry) filelist.get(i);
			if (entry.getAttrs().isDir()) {
				if(entry.getFilename().equalsIgnoreCase(".") || entry.getFilename().equalsIgnoreCase("..")){
					continue;
				}else{
					selectedFilePaths.add(strSourceLocation+"/"+entry.getFilename());
					selectedFilePaths=getFilePath(channelSftp, strSourceLocation+"/"+entry.getFilename(),selectedFilePaths);
				}
			}else{
				if(entry.getFilename().equalsIgnoreCase("file.txt")){
					continue;
				}else{
					selectedFilePaths.add(strSourceLocation+"/"+entry.getFilename());
				}
			}
			ServiceLogger.debug(this, filelist.get(i).toString());
		}
		return selectedFilePaths;
	}

	@SuppressWarnings("rawtypes")
	public long getFolderCount(ChannelSftp channelSftp, String strSourceLocation, long folderCount) throws SftpException { 
		Vector filelist = channelSftp.ls(strSourceLocation);
		for(int i=0; i<filelist.size();i++){
			LsEntry entry = (LsEntry) filelist.get(i);
			if (entry.getAttrs().isDir()) {
				if(entry.getFilename().equalsIgnoreCase(".") || entry.getFilename().equalsIgnoreCase("..")){
					continue;
				}else{
					folderCount++;
					folderCount=getFolderCount(channelSftp, strSourceLocation+"/"+entry.getFilename(),folderCount);
				}
			}else{
				continue;
			}

			System.out.println(filelist.get(i).toString());
		}     
		System.out.println(folderCount);
		return folderCount;
	}
	@SuppressWarnings("rawtypes")
	public long getFolderSize(ChannelSftp channelSftp, String strSourceLocation,long length) throws SftpException {
		Vector filelist = channelSftp.ls(strSourceLocation);
		for(int i=0; i<filelist.size();i++){
			LsEntry entry = (LsEntry) filelist.get(i);
			if (entry.getAttrs().isDir()) {
				if(entry.getFilename().equalsIgnoreCase(".") || entry.getFilename().equalsIgnoreCase("..")){
					continue;
				}else{
					length=getFolderSize(channelSftp, strSourceLocation+"/"+entry.getFilename(),length);
				}
			}else{
				length+=entry.getAttrs().getSize();
			}
			ServiceLogger.debug(this, filelist.get(i).toString());
		}    
		ServiceLogger.debug(this, "Folder Size :"+length);
		return length;
	}

	@SuppressWarnings("rawtypes")
	public long getPackageSize(ChannelSftp channelSftp, String uploadDirPath,String targetFileName) throws SftpException {
		String TGZUploadPath=sftpLocation+"/"+uploadDirPath;
		System.out.println(TGZUploadPath);
		long packageSize=0;
		Vector filelist = channelSftp.ls(TGZUploadPath);
		for(int i=0; i<filelist.size();i++){
			LsEntry entry = (LsEntry) filelist.get(i);
			if (entry.getAttrs().isDir()) {
				continue;
			}else{

				ServiceLogger.debug(this, targetFileName);
				if(entry.getFilename().equalsIgnoreCase(targetFileName)){
					ServiceLogger.debug(this, "File Name matched");
					packageSize=entry.getAttrs().getSize();
				}
			}
			ServiceLogger.debug(this, filelist.get(i).toString());
		}    
		ServiceLogger.info(this, "Package Size : "+packageSize);
		return packageSize;
	}

	/*public void createObjectMongoDBConnection() throws UnknownHostException{        
		ServiceLogger.info(this, " - Creating Connection with Mongo DB");
		MongoClient client = new MongoClient(mongoDBHost,mongoDBPort);
		DB db = client.getDB(mongoDBName);
		DBCollection collection = db.getCollection("job_object_details");
		DBCursor cursor = collection.find();
		DBObject dbObject;
		while(cursor.hasNext()) {

			ServiceLogger.info(this, " - Data Exists in job_object_details table");
			dbObject = cursor.next();				    				    
			System.out.println("details found--->"+dbObject.get("id"));
			System.out.println("details found--->"+dbObject.get("jobId"));	
			System.out.println("details found--->"+dbObject.get("objectId"));	
			System.out.println("details found--->"+dbObject.get("objectName"));	
			System.out.println("details found--->"+dbObject.get("sourcePath"));	
			System.out.println("details found--->"+dbObject.get("targetPath"));	
			System.out.println("details found--->"+dbObject.get("isFile"));	
			System.out.println("details found--->"+dbObject.get("message"));	
			System.out.println("details found--->"+dbObject.get("isError"));
			System.out.println("details found--->"+dbObject.get("creationDate"));	

		}
	}*/


	public static String getStackTrace(Throwable t) {
		StringWriter stringWritter = new StringWriter();
		PrintWriter printWritter = new PrintWriter(stringWritter, true);
		t.printStackTrace(printWritter);
		printWritter.flush();
		stringWritter.flush();
		return stringWritter.toString();
	}

	public static long getSizeInBytes(long size) {
		return size * 1024L * 1024L * 1024L;
	}

	public static String getBytesToMegaBytesStr(double bValue) {
		double mbValue = ((double) bValue / (1024 * 1024));
		DecimalFormat df = new DecimalFormat("#.##");

		if (mbValue > 1) {
			return df.format(mbValue) + " MB";
		} else {
			return bValue + " B";
		}
	}

	public static String getBytesToGigaBytesStr(double bValue) {
		double mbValue = ((double) bValue / (1024 * 1024 * 1024));
		DecimalFormat df = new DecimalFormat("#.##");
		if (mbValue > 1) {
			return df.format(mbValue) + " gb";
		} else {
			return bValue + " bytes";
		}
	}

	public static long getFreeSpaceInBytes(File file) {
		long freeSpaceInKb = 0;
		try {
			freeSpaceInKb = FileSystemUtils.freeSpaceKb(file.getAbsolutePath()) * 1024L;
		} catch (IOException e) {
			ServiceLogger.error(IngestionUtil.class, e, e.getMessage());
		}
		return freeSpaceInKb;
	}

	public static boolean checkEnoughSpace(File file, long contentSize) {
		if (file.exists()) {
			long requiredFreeSpaceInBytes = contentSize * 2;
			long freeSpaceInBytes = IngestionUtil.getFreeSpaceInBytes(file);
			return (requiredFreeSpaceInBytes < freeSpaceInBytes); // available free space should be twice content size
		} else {
			return false;
		}
	}

	public static String formatDate(String dateSent, String inFormat, String outFormat) {
		if (dateSent == null) {
			return null;
		} else {
			SimpleDateFormat inFormatter = new SimpleDateFormat(inFormat, Locale.US);
			SimpleDateFormat outFormatter = new SimpleDateFormat(outFormat, Locale.US);
			Date date = null;
			try {
				date = inFormatter.parse(dateSent);
			} catch (ParseException ex) {
				ServiceLogger.error(IngestionUtil.class, ex, ex.getMessage());
			}
			return outFormatter.format(date);
		}
	}

	public static String formatDate(Date dateSent, String outFormat) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(outFormat, Locale.US);
		String finalDate;
		finalDate = dateFormatter.format(dateSent);
		return finalDate;
	}

	public static String readableFileSize(long size) {
		if (size <= 0) {
			return "0";
		}
		final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static String convertGMTToClientTimeZone(String dateStr) {
		String convertedStr = null;
		DateFormat converter1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DateFormat converter2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			converter1.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date convertedDate = converter1.parse(dateStr);
			converter2.setTimeZone(clientTimeZone);
			convertedStr = converter2.format(convertedDate);            
		} catch (ParseException e) {
			ServiceLogger.error(IngestionUtil.class, e, e.getMessage());
		}
		return convertedStr;
	}    
}
