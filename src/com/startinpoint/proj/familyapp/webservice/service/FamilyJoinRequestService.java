package com.startinpoint.proj.familyapp.webservice.service;

import java.util.List;

import com.startinpoint.proj.familyapp.webservice.entity.FamilyJoinRequest;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @author nankhinmhwe
 *
 */
public interface FamilyJoinRequestService {
	public FamilyJoinRequest saveOrUpdateJoinRequest(FamilyJoinRequest joinRequest) throws FamilyAppWebserviceException;
	
	public List<FamilyJoinRequest> getPendingJoinRequestByMemberId(Long memberId) throws FamilyAppWebserviceException;

	public List<FamilyJoinRequest> getPendingJoinRequestByFamilyId(Long familyId) throws FamilyAppWebserviceException;
	
	public List<FamilyJoinRequest> getJoinRequestListByCreatorId(Long creatorId) throws FamilyAppWebserviceException;
	
	public FamilyJoinRequest getFamilyJoinRequestById(Long id) throws FamilyAppWebserviceException;
}
