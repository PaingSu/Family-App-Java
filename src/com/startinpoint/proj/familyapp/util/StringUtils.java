package com.startinpoint.proj.familyapp.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONObject;

public class StringUtils {
	/**
	 * Generate random UUID string
	 * @param length
	 * @return
	 */
    public static String uuidRandomString(int length) {
        String temp = UUID.randomUUID().toString();
        if (length <= temp.length()) {
            return temp.substring(0, length);
        } else {
            return temp;
        }
    }
    
    /**
     * Change String to JSON Format String
     * @param message
     * @return
     */
    public static String responseString(String message){
    	Map< String, String> responseMap = new HashMap<>();
		responseMap.put("message", message);
		
		return new JSONObject(responseMap).toString();
    }

}
