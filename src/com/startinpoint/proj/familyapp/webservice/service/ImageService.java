package com.startinpoint.proj.familyapp.webservice.service;

import com.startinpoint.proj.familyapp.webservice.entity.Images;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @author nankhinmhwe
 *
 */
public interface ImageService {
	public Images saveImage(Images image) throws FamilyAppWebserviceException;
	public Images getImageByName(String name) throws FamilyAppWebserviceException;
	public String saveBase64Image(String rawImage)throws FamilyAppWebserviceException;
	public String getImageNameFromImageUrl(String imageUrl);
}
