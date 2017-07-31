package archiver;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.filesystem.handler.ArchiveType;
import com.hcl.neo.eloader.filesystem.handler.Archiver;
import com.hcl.neo.eloader.filesystem.handler.ArchiverFactory;
import com.hcl.neo.eloader.filesystem.handler.ProgressMonitor;
import com.hcl.neo.eloader.filesystem.handler.exceptions.ArchiverException;
import com.hcl.neo.eloader.filesystem.handler.impl.ProgressMonitorImpl;
import com.hcl.neo.eloader.filesystem.handler.params.ArchiverParams;
import com.hcl.neo.eloader.filesystem.handler.util.ArchiverUtil;

public class ArchiverTest {

	public static void main(String[] s){
		try{
			Logger.info(ArchiverTest.class, " - started");
			createTgzArchive();
//			createZipArchive();
//			extractTgzArchive();
			Logger.info(ArchiverTest.class, " - created");
			//extractZipArchive();
			//Logger.info(ArchiverTest.class, " - extracted");
			//identifyFileFormat();
			//Logger.info(ArchiverTest.class, " - end");
		}
		catch(Throwable e){
			e.printStackTrace();
		}
	}
	
	public static void createTgzArchive() throws ArchiverException{
		ArchiverParams params = new ArchiverParams();
		params.setArchivePath("D:\\aaaaaatest\\Files of Portugeseàçéíôú\\ãáâéíóóúú.tar.gz");
		params.setArchiveType(ArchiveType.GZ);
		params.addContentPath("D:\\aaaaaatest\\Files of Portugeseàçéíôú");
		//params.addContentPath("C:/Users/jasneets/Downloads/spring-jdbc-dao-128673/test");
		//params.addContentPath("C:/Users/jasneets/Desktop/work/Speeding_Up_Data_Transfers_2012_03_07.pdf");
		ProgressMonitor monitor = new ProgressMonitorImpl();
//		params.setProgressMonitor(monitor);
		Archiver a = ArchiverFactory.createTgzArchiver();
		a.createArchive(params);
		Logger.info(ArchiverTest.class, "checksum: "+a.getMd5HexChecksum(params.getArchivePath()));
	}
	
	public static void extractTgzArchive() throws ArchiverException{
		ArchiverParams params = new ArchiverParams();
		String path = "D:\\aaaaaatest\\ãáâéíóóúú.tar.gz";
		params.setArchivePath(path);
		params.setArchiveType(ArchiverUtil.identifyArchiveType(path));
		params.addContentPath("D:\\aaaaaatest");
		ProgressMonitor monitor = new ProgressMonitorImpl();
//		params.setProgressMonitor(monitor);
		Archiver a = ArchiverFactory.createTgzArchiver();
		a.extractArchive(params);
		Logger.info(ArchiverTest.class, "checksum: "+a.getMd5HexChecksum(params.getArchivePath()));
	}
	
	public static void createZipArchive() throws ArchiverException{
		ArchiverParams params = new ArchiverParams();
		params.setArchivePath("C:/Users/jasneets/Downloads/spring-jdbc-dao-128673/tt.zip");
		params.addContentPath("C:/Users/jasneets/Downloads/spring-jdbc-dao-128673/src");
		params.addContentPath("C:/Users/jasneets/Downloads/spring-jdbc-dao-128673/test");
		params.addContentPath("C:/Users/jasneets/Downloads/spring-jdbc-dao-128673/d2.pdf");
		Archiver a = ArchiverFactory.createZipArchiver();
		a.createArchive(params);
		Logger.info(ArchiverTest.class, "checksum: "+a.getMd5HexChecksum(params.getArchivePath()));
	}
	
	public static void extractZipArchive() throws ArchiverException{
		ArchiverParams params = new ArchiverParams();
		params.setArchivePath("C:/Users/jasneets/Downloads/spring-jdbc-dao-128673/tt.zip");
		params.addContentPath("C:/Users/jasneets/Downloads/spring-jdbc-dao-128673/extract");
		Archiver a = ArchiverFactory.createZipArchiver();
		a.extractArchive(params);
		Logger.info(ArchiverTest.class, "checksum: "+a.getMd5HexChecksum(params.getArchivePath()));
	}
	
	public static void identifyFileFormat() throws ArchiverException{
		ArchiveType type = ArchiverUtil.identifyArchiveType("C:/Users/jasneets/Downloads/spring-jdbc-dao-128673/tt.zip");
		Logger.info(ArchiverTest.class, "ArchiveType: "+type);
	}
        public static void getChecksum() throws ArchiverException{
            Archiver tgzArchiver = ArchiverFactory.createTgzArchiver();
            String checksum = tgzArchiver.getMd5HexChecksum("");
            Logger.info(ArchiverTest.class, "checksum:"+ checksum);
        }
}
