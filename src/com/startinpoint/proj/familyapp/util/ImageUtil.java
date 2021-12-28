package com.startinpoint.proj.familyapp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;


import org.springframework.beans.factory.annotation.Value;

/**
 * 
 * Image Utility Class
 * @author nankhinmhwe
 *
 */
public class ImageUtil {
	
	@Value("${application.folder_path}")
	private static String folderPath;
	
    /** 
     * To make base64 string decoded properly, We need to remove the base64 header from a base64 string.  
     * 
     * @param base64 The Base64 string of an image. 
     * @return Base64 string without header. 
     */  
    public static String removeBase64Header(String base64) {  
        if(base64 == null) {
        	return  null;  
        }
        return base64.trim().replaceFirst("data[:]image[/]([a-z])+;base64,", "");  
    } 
    
    /** 
     * Get the image type. 
     * 
     * @param base64 The base64 string of an image. 
     * @return jpg, png, gif 
     */  
    public static String getImageType(String base64) {  
        String[] header = base64.split("[;]");  
        if(header == null || header.length == 0) {
        	return null;  
        }
        return header[0].split("[/]")[1];  
    }  
   
    /**
     * Decode string to image 
     * @param base64ImageString the string to decode 
     * @return imageName
     * 
     */
    public static String writeImage(String base64ImageString,String folderPath) {  
    	try {
    	String imageType = getImageType(base64ImageString);
    	String imageName = "/" +System.currentTimeMillis() +"."+ imageType;
    	String imageStr = removeBase64Header(base64ImageString);
    	byte[] byteImage = Base64.getDecoder().decode(imageStr);    	
    	
		FileOutputStream fileOutputStream;
		File f = new File(folderPath +  imageName);
		f.getParentFile().mkdirs();
		
		
			fileOutputStream = new FileOutputStream(f);
			fileOutputStream.write(byteImage);
			fileOutputStream.close();
			
			return "/auth"+imageName;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
    }  
    
  
    /**
     * Get Image Name from image url
     * @param imageUrl eg. http://localhost:8080/Family_WebApp/auth/123456.png
     * @return imageName
     */
	public static String getImageNameFromImageUrl(String imageUrl) {
		if(imageUrl == null || imageUrl.trim().isEmpty()){
			return null;
		}
		String[] arr = imageUrl.split("/"); 
		String imageName = arr[arr.length-1];
		return "/auth/"+imageName;
	}
  
}
