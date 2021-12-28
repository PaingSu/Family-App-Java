package com.startinpoint.proj.familyapp.webservice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.startinpoint.proj.familyapp.webservice.dao.FamilyJoinRequestDao;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyJoinRequest;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.FamilyJoinRequestService;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Service("familyJoinRequestService")
public class FamilyJoinRequestServiceImpl implements FamilyJoinRequestService{

	@Autowired
	private FamilyJoinRequestDao familyJoinRequestDao;
	
	@Override
	public FamilyJoinRequest saveOrUpdateJoinRequest(FamilyJoinRequest joinRequest)
			throws FamilyAppWebserviceException {
		joinRequest = familyJoinRequestDao.saveOrUpdateJoinRequest(joinRequest);
		return joinRequest;
	}
	
	@Override
	public List<FamilyJoinRequest> getPendingJoinRequestByMemberId(Long memberId) throws FamilyAppWebserviceException {
		List<FamilyJoinRequest> list = familyJoinRequestDao.getPendingJoinRequestByMemberId(memberId);
		return list;
	}

	@Override
	public List<FamilyJoinRequest> getPendingJoinRequestByFamilyId(Long familyId) throws FamilyAppWebserviceException {
		List<FamilyJoinRequest> list = familyJoinRequestDao.getPendingJoinRequestByFamilyId(familyId);
		return list;
	}

	@Override
	public FamilyJoinRequest getFamilyJoinRequestById(Long id) throws FamilyAppWebserviceException {
		return familyJoinRequestDao.getFamilyJoinRequestById(id);
	}

	@Override
	public List<FamilyJoinRequest> getJoinRequestListByCreatorId(Long creatorId) throws FamilyAppWebserviceException {
		List<FamilyJoinRequest> list = familyJoinRequestDao.getJoinRequestListByCreatorId(creatorId);
		return list;
	}
	
}
