package com.hcl.neo.eloader.common;

import java.io.Reader;

public class JsonApi {

	private static com.google.gson.Gson gson = new com.google.gson.Gson();

	public static String toJson(Object object){
		return gson.toJson(object);
	}

	public static <T> T fromJson(String json, Class<T> classOfT){
		return gson.fromJson(json, classOfT);
	}

	public static <T> T fromJson(Reader json, Class<T> classOfT){
		return gson.fromJson(json, classOfT);
	}

	public static String toJson(String key, Object value) {
		StringBuilder str = new StringBuilder();
		str.append("{");
		str.append("\"").append(key).append("\"").append(":").append("\"").append(value).append("\"");
		str.append("}");
		return str.toString();
	}

}