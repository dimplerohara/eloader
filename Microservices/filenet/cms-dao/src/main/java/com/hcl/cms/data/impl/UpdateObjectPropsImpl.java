package com.hcl.cms.data.impl;



import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.hcl.cms.data.params.UpdateObjectParam;
import com.hcl.cms.data.exceptions.CmsException;
import com.hcl.cms.data.impl.SetObjectProperties;
/**
 * update object Properties implementation Class
 * @author sakshi_ja
 *
 */
class UpdateObjectImpl extends CmsImplBase{

	/**
	 * @param con
	 */
	public UpdateObjectImpl(Connection con) {
		super(con);
	}


	/**
	 * Method to update filenet object metadata
	 * @param params
	 * @param objectStoreName
	 * @return
	 * @throws CmsException
	 */
	public boolean updateProperties(UpdateObjectParam params,String objectStoreName) throws CmsException{
		try{
			boolean status = false;
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IndependentObject object = getObjectFromIdentity.getObject(params.getObjectIdentity(),objectStoreName);			
			if(null == object){
				logger.warn("Unable to update properties for non-existent object identity "+ params.getObjectIdentity() +" in docbase "+objectStoreName);
			}
			else{
				Document doc=null;
				Folder folder=null;
				SetObjectProperties objectProps = new SetObjectProperties(getSession());
				String objectType = params.getObjectIdentity().getObjectType(); 
				if(object instanceof Document){
					doc=(Document)object;
					if(params.getStream()!=null){
						ContentTransfer ct = Factory.ContentTransfer.createInstance();
						ct.setCaptureSource(params.getStream());
						ct.set_RetrievalName(params.getObjectName());
						ct.set_ContentType(params.getFileExtension());
						ContentElementList contEleList = Factory.ContentElement.createList();
						if(!doc.get_IsReserved()){
							doc=(Document) doc.get_CurrentVersion();
							doc.checkout(ReservationType.EXCLUSIVE, null, doc.getClassName(), doc.getProperties());	
							doc.save(RefreshMode.NO_REFRESH);
						}
						Document reservation = (Document) doc.get_Reservation();
						contEleList.add(ct);						
						reservation.set_ContentElements(contEleList);	
						reservation.set_MimeType(params.getFileExtension());
						reservation.save(RefreshMode.REFRESH);
						reservation.checkin(AutoClassify.AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
						reservation.save(RefreshMode.REFRESH);
						doc=(Document) doc.get_CurrentVersion();							
					}
					objectProps.setProperties(doc, objectType, params.getAttrMap(),objectStoreName);

				}else if(object instanceof Folder){
					objectProps.setProperties(object, objectType, params.getAttrMap(),objectStoreName);
					folder=(Folder)object;
				}
				if(doc!=null){
					doc.save(RefreshMode.REFRESH);
					status = true;
				}
				if(folder!=null){
					folder.save(RefreshMode.REFRESH);
					status = true;
				}				
			}
			return status;
		}
		catch(Throwable e){
			throw new CmsException(e);
		}
	}
}
