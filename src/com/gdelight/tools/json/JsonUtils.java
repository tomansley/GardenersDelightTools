package com.gdelight.tools.json;

import com.google.gson.Gson;

public class JsonUtils {

	public static Object parseJSonDocument(String json, Class clazz) {
		
		Object n = null;
		
		Gson gson = new Gson();
		
		Object obj = gson.fromJson(json, clazz);
		
		return n;
	}
	
}
