package com.gdelight.tools.json;

import com.google.gson.Gson;

public class JsonUtils {

	public static Object parseJSonDocument(String json, Class clazz) {

		Gson gson = new Gson();
		
		Object obj = gson.fromJson(json, clazz);
		
		return obj;
	}
	
	public static String getJSonDocument(Object obj) {
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		return json;
	}
	
}
