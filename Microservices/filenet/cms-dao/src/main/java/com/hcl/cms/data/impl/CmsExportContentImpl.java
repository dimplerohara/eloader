package com.hcl.cms.data.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.hcl.cms.data.constants.Constants;
import com.hcl.cms.data.exceptions.CmsException;
import com.hcl.cms.data.params.ExportContentParams;
import com.hcl.cms.data.params.CmsSessionObjectParams;
import com.hcl.cms.data.params.ObjectIdentity;
import com.hcl.cms.data.params.OperationObjectDetail;
import com.hcl.cms.data.params.OperationStatus;
import com.hcl.cms.data.session.CEConnectionManager;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.Id;

@SuppressWarnings("unused")
class CmsExportContentImpl extends CmsImplBase {

	List<OperationObjectDetail> operationObjectList = new ArrayList<>();
	ObjectStore objectStore = null;
	boolean isCheckout = false;

	public CmsExportContentImpl(Connection connection) {
		super(connection);
	}

	public OperationStatus export(ExportContentParams params) {
		OperationStatus operationStatus = new OperationStatus();
		boolean status = false;
		try {
			Document document = null;

			if (Constants.CHECKOUT.equals(params.getJobType())) {
				isCheckout = true;
			}

			CEConnectionManager conManager = new CEConnectionManager(getSession());
			CmsSessionObjectParams objectStoreParams = conManager.getObjectStore(params.getRepository());
			objectStore = objectStoreParams.getStore();

			for (ObjectIdentity identity : params.getObjectList()) {
				if (null == identity) {
					throw new IllegalArgumentException("DocumentIdentity is not valid - " + identity);
				}

				boolean isFolder = fetchFolder(identity.getObjectPath());
				boolean isDocument = fetchDocument(identity.getObjectPath());

				if (isFolder) {
					status = exportFolder(identity, params.getDestDir() + "/");
				}

				if (isDocument) {
					status = exportDocument(identity, params.getDestDir() + "/");
				}

				if (!isFolder && !isDocument) {
					status = false;
				}

			}

		} catch (Throwable ex) {
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

	private boolean exportFolder(ObjectIdentity identity, String destDir) throws Exception {
		boolean status = true;
		Folder parent = null;
		OperationObjectDetail objectDetail = new OperationObjectDetail();
		objectDetail.setSourcePath(identity.getObjectPath());
		objectDetail.setTargetPath(destDir);
		objectDetail.setCreationDate(new Date());
		try{
			if (createFolderPath(destDir)) {
				if (isNotNull(identity.getGuid())) {
					parent = Factory.Folder.fetchInstance(objectStore, new Id(identity.getGuid()), null);
				} else if (isNotNull(identity.getObjectPath())) {
					parent = Factory.Folder.fetchInstance(objectStore, identity.getObjectPath(), null);
				}
				
				objectDetail.setObjectName(parent.get_Name());
				
				Iterator<?> docIter = parent.get_ContainedDocuments().iterator();
				while (docIter.hasNext()) {
					final Document doc = (Document) docIter.next();
					ObjectIdentity objectIdentity = ObjectIdentity.newObject();
					objectIdentity.setGuid(doc.get_Id().toString());
					exportDocument(objectIdentity, destDir);
				}
	
				Iterator<?> folderIter = parent.get_SubFolders().iterator();
				while (folderIter.hasNext()) {
					final Folder folder = (Folder) folderIter.next();
					destDir = destDir + folder.get_Name() + "/";
					ObjectIdentity objectIdentity = ObjectIdentity.newObject();
					objectIdentity.setGuid(folder.get_Id().toString());
					objectIdentity.setObjectPath(folder.get_PathName());
					exportFolder(objectIdentity, destDir);
				}
	
			} else {
				status = false;
				throw new Exception("Unable to create/access directory " + destDir);
			}
		} catch (Throwable e) {
			status = false;
			objectDetail.setError(true);
			objectDetail.setMessage(e.getMessage());
		} finally {
			operationObjectList.add(objectDetail);
		}

		return status;
	}

	private boolean exportDocument(ObjectIdentity identity, String destDir) throws Exception {
		boolean status = true;
		Document document = null;
		OperationObjectDetail objectDetail = new OperationObjectDetail();
		objectDetail.setSourcePath(identity.getObjectPath());
		objectDetail.setCreationDate(new Date());
		
		try {
			if (isNotNull(identity.getGuid())) {
				document = Factory.Document.fetchInstance(objectStore, new Id(identity.getGuid()), null);
			} else if (isNotNull(identity.getObjectPath())) {
				document = Factory.Document.fetchInstance(objectStore, identity.getObjectPath(), null);
			}
			
			objectDetail.setObjectName(document.get_Name());
			objectDetail.setTargetPath(destDir + document.get_Name());
			if (createFolderPath(destDir)) {
				File file = new File(destDir + document.get_Name());
				file.createNewFile();
				ContentElementList docContentList = document.get_ContentElements();
				Iterator<?> iter = docContentList.iterator();
				ContentTransfer ct = (ContentTransfer) iter.next();
				InputStream fileStream = ct.accessContentStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte byteArray[] = new byte[4096];
				int read = 0;
				while ((read = fileStream.read(byteArray)) > 0) {
					fos.write(byteArray, 0, read);
				}
				fos.close();

				if (isCheckout && !document.get_IsReserved()) {
					document.checkout(ReservationType.EXCLUSIVE, null, document.getClassName(),
							document.getProperties());
					document.save(RefreshMode.NO_REFRESH);
				}

			}
		} catch (Throwable e) {
			status = false;
			objectDetail.setError(true);
			objectDetail.setMessage(e.getMessage());
		} finally {
			operationObjectList.add(objectDetail);
		}

		return status;
	}

/*	private boolean doExport(ExportContentParams params, ArrayList<Document> objects) {
		boolean status = true;

		try {
			if (createFolderPath(params.getDestDir())) {
				for (Document doc : objects) {
					File file = new File(params.getDestDir() + doc.get_Name());
					file.createNewFile();
					ContentElementList docContentList = doc.get_ContentElements();
					Iterator<?> iter = docContentList.iterator();
					ContentTransfer ct = (ContentTransfer) iter.next();
					InputStream fileStream = ct.accessContentStream();
					FileOutputStream fos = new FileOutputStream(file);
					byte byteArray[] = new byte[4096];
					int read = 0;
					while ((read = fileStream.read(byteArray)) > 0) {
						fos.write(byteArray, 0, read);
					}
					fos.close();
				}
			}

		} catch (Exception e) {
			status = false;
		}

		return status;
	}*/

	private boolean createFolderPath(String dirPath) {

		boolean dirCreated = false;
		File file = new File(dirPath);
		if (null != file) {
			dirCreated = file.exists() ? true : file.mkdirs();
		}
		return dirCreated;
	}

	public boolean fetchFolder(String path) throws Exception {
		try {
			Factory.Folder.fetchInstance(objectStore, path, null);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean fetchDocument(String path) throws Exception {
		try {
			Factory.Document.fetchInstance(objectStore, path, null);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}