package com.hcl.neo.dctm.microservices.utils;

import java.io.Reader;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Base64Utils;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfEnumeration;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.acs.IDfAcsRequest;
import com.documentum.fc.client.acs.IDfAcsTransferPreferences;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfList;
import com.documentum.fc.common.IDfTime;
import com.documentum.operations.IDfExportNode;
import com.documentum.operations.IDfExportOperation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hcl.dctm.data.params.DctmSessionParams;
import com.hcl.neo.dctm.microservices.constants.Constants;
import com.hcl.neo.dctm.microservices.exceptions.ServiceException;
import com.hcl.neo.dctm.microservices.model.DctmObject;

public class ServiceUtils {

	private static final String HEADER_AUTHORIZATION = "Authorization";
	private static final String BASIC_AUTH = "Basic";
	private static final String UTF8 = "UTF-8";
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public static DctmSessionParams toDctmSessionParams(HttpServletRequest request, String repository) throws ServiceException{
		String auth = request.getHeader(HEADER_AUTHORIZATION);
		if(null == auth || !auth.startsWith(BASIC_AUTH)){
			throw new ServiceException(Constants.MSG_ONLY_HTTP_AUTH_SUPPORTED);
		}

		String credentials = auth.substring(BASIC_AUTH.length()).trim();
		credentials = new String(Base64Utils.decodeFromString(credentials), Charset.forName(UTF8));
		final String[] values = credentials.split(":", 2);
		DctmSessionParams params = new DctmSessionParams();
		params.setRepository(repository);
		params.setUser(values[0]);
		params.setPassword(values[1]);
		return params;
	}

	public static DctmObject toDctmObject(IDfSysObject sysObject,String thumbnailURL) throws ServiceException{
		try{
			DctmObject object = new DctmObject();
			if(null == sysObject){
				throw new ServiceException(Constants.MSG_NULL_SYS_OBJECT);
			}
			object.setCheckoutBy(sysObject.getLockOwner());
			object.setCheckoutDate(sysObject.getLockDate().asString(IDfTime.DF_TIME_PATTERN44));
			object.setCreateDate(sysObject.getCreationDate().asString(IDfTime.DF_TIME_PATTERN44));
			object.setCreatedBy(sysObject.getCreatorName());
			object.setObjectId(sysObject.getObjectId().getId());
			object.setModifiedBy(sysObject.getModifier());
			object.setModifyDate(sysObject.getModifyDate().asString(IDfTime.DF_TIME_PATTERN44));
			object.setName(sysObject.getObjectName());
			object.setOwnerName(sysObject.getOwnerName());
			object.setSize(sysObject.getContentSize());
			object.setSubject(sysObject.getSubject());
			object.setTitle(sysObject.getTitle());
			object.setContentURL(getContentURLOfObject(sysObject));
			object.setThumbnailURL(thumbnailURL);
			return object;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable e){
			throw new ServiceException(e);
		}
	}


	public static String getContentURLOfObject(IDfSysObject object)
	{
		String contentURL="";
		IDfClientX clientX = new DfClientX();
		IDfAcsTransferPreferences prefs = clientX.getAcsTransferPreferences();

		try {
			IDfExportOperation exportOp = clientX.getExportOperation();
			exportOp.setAcsTransferPreferences(prefs);
			exportOp.add(object);
			if (exportOp.execute()) 
			{
				IDfList nodes = exportOp.getNodes();
				for (int i = 0, size = nodes.getCount(); i < size; i++) 
				{
					IDfExportNode node = (IDfExportNode) nodes.get(i);
					IDfEnumeration acsRequests = node.getAcsRequests();
					while (acsRequests.hasMoreElements()) 
					{
						IDfAcsRequest acsRequest = (IDfAcsRequest) acsRequests.nextElement();
						contentURL = acsRequest.makeURL();
						contentURL.replaceAll("amp;amp;","");
					}
				}
			}
		}
		catch (DfException e) { e.printStackTrace(); }

		return contentURL;
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
