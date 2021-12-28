package com.startinpoint.proj.familyapp.webservice.service;

import java.util.List;

import com.startinpoint.proj.familyapp.webservice.entity.FamilyProfile;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @author nankhinmhwe
 *
 */
public interface FamilyProfileService {
	
	public FamilyProfile saveOrUpdateFamilyProfile(FamilyProfile profile) throws FamilyAppWebserviceException;

	public FamilyProfile getFamilyProfileByFamilyCode(String familyCode)throws FamilyAppWebserviceException;
	
	//added by ThoonSandy
	public FamilyProfile findByFamilyProfileId(Long familyProfileId)throws FamilyAppWebserviceException;

	public FamilyProfile getPersonalFamilyByCreatorId(Long creatorId)throws FamilyAppWebserviceException;
	
	public List<FamilyProfile> getFamilyExceptPersonal(Long userId) throws FamilyAppWebserviceException;

	public FamilyProfile getInactivePersonalFamily(Long creatorId)throws FamilyAppWebserviceException;
}
