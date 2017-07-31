package com.hcl.cms.data.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import com.filenet.api.core.Connection;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ReferentialContainmentRelationship;

import com.hcl.cms.data.params.ImportContentParams;
import com.hcl.cms.data.params.ObjectIdentity;
import com.hcl.cms.data.params.OperationObjectDetail;
import com.hcl.cms.data.params.OperationStatus;
import com.hcl.cms.data.constants.Constants;
import com.hcl.cms.data.exceptions.CmsException;
import com.hcl.cms.data.params.CmsSessionObjectParams;
import com.hcl.cms.data.session.CEConnectionManager;

/**
 * Import content implementation Class
 * @author sakshi_ja
 *
 */
class CmsImportContentImpl extends CmsImplBase {

	Logger logger = Logger.getLogger(this.getClass().getName());

	CEConnectionManager conManager;
	CmsSessionObjectParams objectStoreParams;
	List<OperationObjectDetail> operationObjectList = new ArrayList<>();
	int noOfSuccessCount=0;
	int noOfFailureCount=0;
	public CmsImportContentImpl(Connection con) {
		super(con);
	}

	/**
	 * Initial Method to import documents 
	 * @param params
	 * @return
	 * @throws CmsException
	 */
	public OperationStatus import1(ImportContentParams params) throws CmsException{
		try{
			return doImport(params);
		}
		catch(Throwable e){
			throw new CmsException(e);
		}
	}

	/**
	 * Initial Method to import documents 
	 * @param params
	 * @return
	 */
	private OperationStatus doImport(ImportContentParams params) {
		boolean status = true;
		OperationStatus operationStatus = new OperationStatus();
		try {
			conManager=new CEConnectionManager(getSession());
			objectStoreParams=conManager.getObjectStore(params.getRepository());
			ObjectIdentity ob=params.getDestFolder();
			for(String srcPath: params.getSrcPathList()){
				logger.info("Source Path-->"+srcPath);
				logger.info("Destination Path-->"+ob.getObjectPath());
				status=importDocument(srcPath,ob.getObjectPath(),params);
			}
		}catch (Throwable ex) {
			ex.printStackTrace();
			OperationObjectDetail errorDetail = new OperationObjectDetail();
			errorDetail.setError(true);
			errorDetail.setMessage(ex.getMessage());
			operationObjectList.add(errorDetail);
		} finally {
			operationStatus.setOperationObjectDetails(operationObjectList);
			operationStatus.setStatus(status);
		}

		return operationStatus;
	}

	/**
	 * Method to import documents with stream in filenet object store
	 * @param sourcePath
	 * @param folderPath
	 * @param inputStream
	 * @param mimeType
	 * @param strDocName
	 * @param docClass
	 * @param params
	 */
	public void addDocumentWithStream(String sourcePath,String folderPath,InputStream inputStream, String mimeType, String strDocName, String docClass,ImportContentParams params) {
		OperationObjectDetail objectDetail = new OperationObjectDetail();
		objectDetail.setObjectName(strDocName);
		objectDetail.setSourcePath(sourcePath);
		objectDetail.setTargetPath(folderPath);
		objectDetail.setCreationDate(new Date());
		Document doc=null;
		try{			
			Folder folder = Factory.Folder.fetchInstance(objectStoreParams.getStore(),folderPath, null);
			logger.info("\n\n Folder ID: " + folder.get_Id());
			ContentTransfer ct = Factory.ContentTransfer.createInstance();
			ct.setCaptureSource(inputStream);
			ct.set_ContentType(mimeType);
			ct.set_RetrievalName(strDocName);
			ContentElementList contEleList = Factory.ContentElement.createList();
			if(!fetchDocument(folderPath+"/"+strDocName)){
				logger.info("Document Not exists--->"+folderPath+"/"+strDocName);
				doc = Factory.Document.createInstance(objectStoreParams.getStore(), null);
				contEleList.add(ct);
				doc.set_ContentElements(contEleList);
				doc.getProperties().putValue(Constants.ATTR_DOCUMENT_TITLE, strDocName);
				doc.set_MimeType(mimeType);
				doc.checkin(AutoClassify.AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
				doc.save(RefreshMode.REFRESH);
				ReferentialContainmentRelationship rcr = folder.file(doc,AutoUniqueName.AUTO_UNIQUE, strDocName, DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
				rcr.save(RefreshMode.REFRESH);  
			}else{						
				try{
					logger.info("Document exists--->"+folderPath+"/"+strDocName);
					doc=(Document)Factory.Document.fetchInstance(objectStoreParams.getStore(), folderPath+"/"+strDocName, null);
					if(!doc.get_IsReserved()){
						doc=(Document) doc.get_CurrentVersion();
						doc.checkout(ReservationType.EXCLUSIVE, null, doc.getClassName(), doc.getProperties());	
						doc.save(RefreshMode.NO_REFRESH);
					}
					Document reservation = (Document) doc.get_Reservation();
					contEleList.add(ct);
					reservation.set_ContentElements(contEleList);	
					reservation.save(RefreshMode.REFRESH);
					reservation.checkin(AutoClassify.AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
					reservation.save(RefreshMode.REFRESH);
				}catch(Exception e){
					objectDetail.setError(true);
					objectDetail.setMessage(e.getMessage());
				}
			}	
		}catch(Exception ex){
			objectDetail.setError(true);
			objectDetail.setMessage(ex.getMessage());
		}
		finally{
			operationObjectList.add(objectDetail);
		}
	}


	/**
	 * Method to import documents in filenet object store
	 * @param srcPath
	 * @param destFolderPath
	 * @param params
	 * @return
	 * @throws CmsException
	 */
	public boolean importDocument(String srcPath, String destFolderPath,ImportContentParams params) throws CmsException
	{
		boolean status = true;

		try {
			String strFileName;
			File srcfile = new File(srcPath);
			strFileName = srcfile.getName();
			if (!srcfile.isDirectory()){	
				logger.info("File Name is--->"+strFileName);
				String mimeType=FilenameUtils.getExtension(strFileName);
				InputStream inputStream = null;
				inputStream = new FileInputStream(srcfile);
				addDocumentWithStream(srcPath,destFolderPath, inputStream,mimeType, strFileName, Constants.DEFAULT_DOCUMENT_TYPE,params);
			}else{
				logger.info("Folder Name is -->"+strFileName);
				String destFolderPathToCreate=destFolderPath+"/"+strFileName;
				if(!fetchFolder(destFolderPathToCreate)){
					if(fetchFolder(destFolderPath)){
						logger.info("folder not exists in destination location so creating folder-->"+destFolderPath+"/"+strFileName);
						createFolder(strFileName,fetchFolderObject(destFolderPath));
					}
					else{
						logger.info("parent folder does not exists in object store-->"+destFolderPath);
					}
				}
				File[] listOfFiles = srcfile.listFiles();
				if(listOfFiles!=null){
					for (int count = 0; count < listOfFiles.length; count++) 
					{
						strFileName = listOfFiles[count].getName();	
						importDocument(srcPath+"/"+strFileName,destFolderPathToCreate,params);	
					}
				}

			}
		} catch (FileNotFoundException e) 
		{
			status=false;
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			status=false;
			e.printStackTrace();
		}
		return status;
	}//end of method

	/**
	 * Method to  fetch folder filenet object
	 * @param path
	 * @return
	 * @throws CmsException
	 */
	public boolean fetchFolder(String path) throws  CmsException {
		try{
			Factory.Folder.fetchInstance(objectStoreParams.getStore(), path, null);
			return true;
		}catch(Exception e){
			return false;
		}
	}

	/**
	 * Method to fetch document filenet object
	 * @param path
	 * @return
	 * @throws CmsException
	 */
	public boolean fetchDocument(String path) throws  CmsException {
		try{
			Factory.Document.fetchInstance(objectStoreParams.getStore(), path, null);
			return true;
		}catch(Exception e){
			return false;
		}
	}

	/**
	 * Method to fetch folder filenet object
	 * @param path
	 * @return
	 * @throws CmsException
	 */
	public Folder fetchFolderObject(String path) throws CmsException {
		return Factory.Folder.fetchInstance(objectStoreParams.getStore(), path, null);
	}

	/**
	 * Method to create folder
	 * @param name
	 * @param parent
	 * @return
	 */
	public Folder createFolder(String name, Folder parent) {
		logger.info("Creating Folder-->"+name);
		Folder subFolder = parent.createSubFolder(name); 															// set parent
		subFolder.save(RefreshMode.REFRESH);
		logger.info("Folder created-->"+name);
		return subFolder;
	}


	/**
	 * Method to remove trailing spaces
	 * @param params
	 * @throws IOException
	 */
	private void removeTrailingSpaces(ImportContentParams params) throws IOException{

		if(params.getSrcPathList().size() > 0) {
			for(int index=0; index<params.getSrcPathList().size(); index++ ){
				File temp = new File(params.getSrcPathList().get(index));
				String nameWithoutTS = temp.getName().replaceAll("\\s+$", "");
				if( null == nameWithoutTS || "".equals(nameWithoutTS) ){
					params.getSrcPathList().remove(index);
				}
				else{
					removeTrailingSpaces(params.getSrcPathList().get(index));
					params.getSrcPathList().set(index, params.getSrcPathList().get(index).replaceAll("\\s+$", ""));
				}
			}
		}
	}

	/**
	 * Method to remove trailing spaces
	 * @param path
	 * @throws IOException
	 */
	private void removeTrailingSpaces(String path) throws IOException{
		File src = new File(path);
		String name = src.getName().replaceAll("\\s+$", "");
		File des = new File(src.getParentFile().getAbsolutePath()+File.separator+name);
		if( src.getName().length() > name.length() ){
			if( null != name && !"".equals(name) ) {
				if( src.isDirectory() ){
					des.mkdirs();
					src.renameTo(des);
					des = new File(src.getParentFile().getAbsolutePath()+File.separator+name);
				}
				else{
					src.renameTo(des);
				}
			}
		}
		if( des.isDirectory() && des.exists() ){
			for(File chileFile : des.listFiles()){
				removeTrailingSpaces(chileFile.getAbsolutePath());
			}
		}
	}


}