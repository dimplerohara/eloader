package com.hcl.neo.dctm.microservices.utils;

import java.io.Reader;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Base64Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hcl.cms.data.params.CmsSessionParams;
import com.hcl.neo.dctm.microservices.constants.Constants;
import com.hcl.neo.dctm.microservices.exceptions.ServiceException;

public class ServiceUtils {

	private static final String HEADER_AUTHORIZATION = "Authorization";
	private static final String HEADER_URI = "URI";
	private static final String HEADER_STANZA = "STANZA";
	private static final String BASIC_AUTH = "Basic";
	private static final String UTF8 = "UTF-8";
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public static CmsSessionParams toCmsSessionParams(HttpServletRequest request, String repository) throws ServiceException{
		String auth = request.getHeader(HEADER_AUTHORIZATION);
		if(null == auth || !auth.startsWith(BASIC_AUTH)){
			throw new ServiceException(Constants.MSG_ONLY_HTTP_AUTH_SUPPORTED);
		}

		String credentials = auth.substring(BASIC_AUTH.length()).trim();
		credentials = new String(Base64Utils.decodeFromString(credentials), Charset.forName(UTF8));
		final String[] values = credentials.split(":", 2);
		CmsSessionParams params = new CmsSessionParams();
		//params.setUri("http://10.137.181.122:9080/wsi/FNCEWS40MTOM");
    	//params.setStanza("FileNetP8WSI");		
		params.setUser(values[0]);
		params.setPassword(values[1]);
		params.setUri(request.getHeader(HEADER_URI));
		params.setStanza(request.getHeader(HEADER_STANZA));
		return params;
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
