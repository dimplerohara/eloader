package com.hcl.cms.data.impl;


import java.util.Iterator;

import com.filenet.api.collection.VersionableSet;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.core.Versionable;
import com.hcl.cms.data.params.DeleteObjectParam;
 
/**
 * Delete object implementation Class
 * @author sakshi_ja
 *
 */
class DeleteObjectImpl extends CmsImplBase {

	/**
	 * @param con
	 */
	public DeleteObjectImpl(Connection con) {
		super(con);
	}

	/**
	 * Initial method to delete filenet object
	 * @param params
	 * @param objectStoreName
	 * @return
	 * @throws Exception
	 */
	public boolean delete(DeleteObjectParam params,String objectStoreName) throws Exception{
		try{
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IndependentObject object = getObjectFromIdentity.getObject(params.getObjectIdentity(),objectStoreName);	
			return null == object ? false : deleteObject(object);
		}
		catch(Throwable e){
			throw new Exception(e);
		}
	}

	/**
	 * Method to cancel checkout object if its already checked out
	 * @param object
	 */
	private void cancelCheckoutDeep(IndependentObject object){

		try{
			if(object instanceof Document){
				Document doc=null;
				doc=(Document)object;
				Document reservation = (Document) doc.get_Reservation();
				if(reservation!=null){
					doc.cancelCheckout(); 				
					reservation.save(RefreshMode.REFRESH);
				}
			}
		}
		catch(Throwable e){
			logger.warn("Error occured in cancel checkout", e);
		}
	}

	/**
	 * Method to check either object is document and folder and call method accordingly
	 * @param object
	 * @return
	 * @throws Exception
	 */
	private boolean deleteObject(IndependentObject object) throws Exception{
		boolean status=false;
		try{
			if(object instanceof Document){
				Document doc=null;
				doc=(Document)object;
				cancelCheckoutDeep(object);
				status=deleteDocumentObject(doc);
			}else if(object instanceof Folder){
				Folder folder=(Folder)object;
				deleteFolderObject(folder);              
			}
		}
		catch(Throwable e){
			logger.warn("Error occured in deleting document", e);
		}
		return status;
	}
	/**
	 * Method to delete document object
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	private boolean deleteDocumentObject(Document doc) throws Exception{
		boolean status=false;
		try{				
			//doc.getV
			String guid=doc.get_Id().toString();
			VersionSeries verSeries=doc.get_VersionSeries();
			VersionableSet vss = verSeries.get_Versions();
			Iterator vssiter = vss.iterator();
			while (vssiter.hasNext()){
				Versionable ver = (Versionable)vssiter.next();
				Document verdoc = (Document) ver;

				String verdoc_Id = verdoc.get_Id().toString();

				if(!guid.equals(verdoc_Id)){
					verdoc.delete();
					verdoc.save(RefreshMode.NO_REFRESH);
				}
			}
			doc.delete();
			doc.save(RefreshMode.REFRESH);
			status=true;
		}
		catch(Throwable e){
			logger.warn("Error occured in cancel checkout", e);
		}

		return status;
	}
	/**
	 * Method to delete folder object and all document inside folder
	 * @param folder
	 * @return
	 * @throws Exception
	 */
	private boolean deleteFolderObject(Folder folder) throws Exception{
		boolean status=false;
		try{	
			Iterator<?> docIter = folder.get_ContainedDocuments().iterator();
			while (docIter.hasNext()) {
				Document doc = (Document) docIter.next();
				logger.info("Document details--->"+doc.get_Id());
				status=deleteDocumentObject(doc);
			}             
			Iterator<?> folderIter = folder.get_SubFolders().iterator();
			while (folderIter.hasNext()) {
				Folder subFolder = (Folder) folderIter.next();
				logger.info("Folder Details"+subFolder.get_Id());
				deleteFolderObject(subFolder);
			}
			folder.delete();
			folder.save(RefreshMode.REFRESH);
			status=true;
		}
		catch(Throwable e){
			logger.warn("Error occured in delete Folder", e);
		}

		return status;
	}
}