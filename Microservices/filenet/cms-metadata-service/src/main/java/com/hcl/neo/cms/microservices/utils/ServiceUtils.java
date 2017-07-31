package com.hcl.neo.cms.microservices.utils;

import java.io.Reader;
import java.nio.charset.Charset;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Base64Utils;

import com.filenet.api.core.Document;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hcl.cms.data.params.CmsSessionParams;
import com.hcl.neo.cms.microservices.constants.Constants;
import com.hcl.neo.cms.microservices.exceptions.ServiceException;
import com.hcl.neo.cms.microservices.model.DctmObject;

/**
 * @author sakshi_ja
 *
 */
public class ServiceUtils {

	private static final String HEADER_AUTHORIZATION = "Authorization";

	private static final String HEADER_URI = "URI";
	private static final String HEADER_STANZA = "STANZA";
	private static final String BASIC_AUTH = "Basic";
	private static final String UTF8 = "UTF-8";
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	/**
	 * Method to create Session params
	 * @param request
	 * @param repository
	 * @return
	 * @throws ServiceException
	 */
	public static CmsSessionParams toCmsSessionParams(HttpServletRequest request, String repository) throws ServiceException{
		String auth = request.getHeader(HEADER_AUTHORIZATION);
		if(null == auth || !auth.startsWith(BASIC_AUTH)){
			throw new ServiceException(Constants.MSG_ONLY_HTTP_AUTH_SUPPORTED);
		}

		String credentials = auth.substring(BASIC_AUTH.length()).trim();
		credentials = new String(Base64Utils.decodeFromString(credentials), Charset.forName(UTF8));
		final String[] values = credentials.split(":", 2);
		CmsSessionParams params = new CmsSessionParams();
		params.setUser(values[0]);
		params.setPassword(values[1]);
		params.setUri(request.getHeader(HEADER_URI));
		params.setStanza(request.getHeader(HEADER_STANZA));
		return params;
	}

	/**
	 * Method to get Filenet object either folder or document
	 * @param object
	 * @return
	 * @throws ServiceException
	 */
	public static DctmObject toDctmObject(IndependentObject object) throws ServiceException{
		try{

			Document doc=null;
			Folder folder=null;
			if(object instanceof Document){
				doc=(Document)object;
			}else{
				if(object instanceof Folder){
					folder=(Folder)object;
				}
			}

			DctmObject newObj = new DctmObject();
			if(null == object){
				throw new ServiceException(Constants.MSG_NULL_INDE_OBJECT);
			}else if(doc!=null){
				Date d=object.getProperties().getDateTimeValue(Constants.ATTR_MODIFIED_DATE);
				if(doc.get_IsReserved()){
					newObj.setCheckoutBy(object.getProperties().getStringValue(Constants.ATTR_LAST_MODIFIER));
					newObj.setCheckoutDate(d.toString());
				}
				newObj.setCreateDate(object.getProperties().getDateTimeValue(Constants.ATTR_CREATED_DATE).toString());
				newObj.setCreatedBy(object.getProperties().getStringValue(Constants.ATTR_CREATOR));
				newObj.setModifiedBy(object.getProperties().getStringValue(Constants.ATTR_LAST_MODIFIER));
				newObj.setModifyDate(d.toString());
				newObj.setName(object.getProperties().getStringValue(Constants.ATTR_NAME));
				newObj.setObjectId(object.getProperties().getIdValue(Constants.ATTR_ID).toString());
				newObj.setOwnerName(object.getProperties().getStringValue(Constants.ATTR_OWNER));
				newObj.setSize(new Double(object.getProperties().getFloat64Value(Constants.ATTR_CONTENT_SIZE)).longValue());
				newObj.setTitle(object.getProperties().getStringValue(Constants.ATTR_DOCUMENT_TITLE));
			}else if(folder!=null){
				Date d=object.getProperties().getDateTimeValue(Constants.ATTR_MODIFIED_DATE);
				newObj.setCreateDate(object.getProperties().getDateTimeValue(Constants.ATTR_CREATED_DATE).toString());
				newObj.setCreatedBy(object.getProperties().getStringValue(Constants.ATTR_CREATOR));
				newObj.setModifiedBy(object.getProperties().getStringValue(Constants.ATTR_LAST_MODIFIER));				
				newObj.setModifyDate(d.toString());
				newObj.setName(object.getProperties().getStringValue(Constants.ATTR_NAME));
				newObj.setObjectId(object.getProperties().getIdValue(Constants.ATTR_ID).toString());
				newObj.setOwnerName(object.getProperties().getStringValue(Constants.ATTR_OWNER));
				newObj.setTitle(object.getProperties().getStringValue(Constants.ATTR_FOLDER_NAME));
			}else{
				throw new ServiceException(Constants.MSG_INVALID_OBJECT);
			}
			return newObj;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable e){
			throw new ServiceException(e);
		}
	}

	public static String toJson(Object object){
		return gson.toJson(object);
	}

	public static <T> T fromJson(String json, Class<T> classOfT){
		return gson.fromJson(json, classOfT);
	}

	public static <T> T fromJson(Reader json, Class<T> classOfT){
		return gson.fromJson(json, classOfT);
	}
}
