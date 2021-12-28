package com.startinpoint.proj.familyapp.webservice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.startinpoint.proj.familyapp.webservice.dao.FamilyProfileDao;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyProfile;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.FamilyProfileService;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Service("familyProfileService")
public class FamilyProfileServiceImpl implements FamilyProfileService{

	@Autowired
	private FamilyProfileDao familyProfileDao;
	
	@Override
	public FamilyProfile saveOrUpdateFamilyProfile(FamilyProfile profile) throws FamilyAppWebserviceException {
		profile = familyProfileDao.saveOrUpdateFamilyProfile(profile);
		return profile;
	}

	@Override
	public FamilyProfile getFamilyProfileByFamilyCode(String familyCode) throws FamilyAppWebserviceException {
		FamilyProfile family = familyProfileDao.getFamilyProfileByFamilyCode(familyCode);
		return family;
	}

	@Override
	public FamilyProfile findByFamilyProfileId(Long familyProfileId) throws FamilyAppWebserviceException {
		FamilyProfile family =familyProfileDao.findByFamilyProfileId(familyProfileId);
		return family;
	}

	@Override
	public FamilyProfile getPersonalFamilyByCreatorId(Long creatorId) throws FamilyAppWebserviceException {
		FamilyProfile family =familyProfileDao.getPersonalFamilyByCreatorId(creatorId);
		return family;
		
	}

	@Override
	public List<FamilyProfile> getFamilyExceptPersonal(Long userId) throws FamilyAppWebserviceException {
		List<FamilyProfile> list =familyProfileDao.getFamilyExceptPersonal(userId);
		return list;
	}

	@Override
	public FamilyProfile getInactivePersonalFamily(Long creatorId) throws FamilyAppWebserviceException {
		return familyProfileDao.getInactivePersonalFamily(creatorId);
	}

}
