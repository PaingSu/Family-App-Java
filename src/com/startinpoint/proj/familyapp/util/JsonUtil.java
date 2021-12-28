package com.startinpoint.proj.familyapp.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author nankhinmhwe
 *
 */
public class JsonUtil {
	
	/**
	 * Convert JSON String to Pojo class
	 * @param jsonData
	 * @param pojoClass
	 * @return 
	 * @return
	 */
	public static <T> T jsonToPojo(String jsonData,Class<T> pojoClass){
		ObjectMapper mapper = new ObjectMapper();		
		T pojoToJson = null;
		try {			
			pojoToJson = mapper.readValue(jsonData,pojoClass);				
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return pojoToJson;	
		
	}	
	
	/**
	 * Parse pojo to json 
	 * @param pojo
	 * @return
	 */
	public static String pojoToJson(Object pojo){
		ObjectMapper mapper = new ObjectMapper();
		try {
			String pojoToJson =mapper.writeValueAsString(pojo);
			return pojoToJson;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
		
	}
}
