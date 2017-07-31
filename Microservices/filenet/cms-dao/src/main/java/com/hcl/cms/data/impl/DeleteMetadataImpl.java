package com.hcl.cms.data.impl;


import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.hcl.cms.data.params.DeleteMetadataParam;

/**
 * Delete Metadata implementation Class
 * @author sakshi_ja
 *
 */
class DeleteMetadataImpl extends CmsImplBase {

	/**
	 * @param con
	 */
	public DeleteMetadataImpl(Connection con) {
		super(con);
	}
	
	/**
	 * Method to remove value of any properties of the object
	 * @param params
	 * @param objectStoreName
	 * @return
	 * @throws Exception
	 */
	public boolean deleteProperties(DeleteMetadataParam params,String objectStoreName) throws Exception {
		try{
			boolean status = false;
			Document doc=null;
			Folder folder=null;
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IndependentObject object = getObjectFromIdentity.getObject(params.getObjectIdentity(),objectStoreName);		
			if(null == object){
				logger.warn("Unable to update properties for non-existent object identity "+ params.getObjectIdentity() +" in docbase "+objectStoreName);
			}
			else{
				DeleteObjectProperties objectProps = new DeleteObjectProperties(getSession());
				objectProps.deleteProperties(object,params.getObjectIdentity().getObjectType(), params.getAttrList(),objectStoreName);
				if(object instanceof Document){
					doc=(Document)object;
				}else if(object instanceof Folder){
					folder=(Folder)object;
				}if(doc!=null){
					doc.save(RefreshMode.REFRESH);
					status = true;
				}
				if(folder!=null){
					folder.save(RefreshMode.REFRESH);
					status = true;
				}	
				status = true;
			}
			return status;
		}
		catch(Throwable e){
			throw new Exception(e);
		}
	}
	
}