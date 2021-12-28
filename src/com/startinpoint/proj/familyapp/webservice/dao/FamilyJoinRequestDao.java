package com.startinpoint.proj.familyapp.webservice.dao;

import java.util.List;

import com.startinpoint.proj.familyapp.webservice.entity.FamilyJoinRequest;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @author nankhinmhwe
 *
 */
public interface FamilyJoinRequestDao {
	public FamilyJoinRequest saveOrUpdateJoinRequest(FamilyJoinRequest joinRequest) throws FamilyAppWebserviceException;
	
	public List<FamilyJoinRequest> getPendingJoinRequestByMemberId(Long memberId)throws FamilyAppWebserviceException;

	public List<FamilyJoinRequest> getPendingJoinRequestByFamilyId(Long familyId)throws FamilyAppWebserviceException;

	public FamilyJoinRequest getFamilyJoinRequestById(Long id)throws FamilyAppWebserviceException;
	
	public List<FamilyJoinRequest> getJoinRequestListByCreatorId(Long creatorId) throws FamilyAppWebserviceException;
}
