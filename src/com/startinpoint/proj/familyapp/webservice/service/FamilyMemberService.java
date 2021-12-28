package com.startinpoint.proj.familyapp.webservice.service;

import java.util.List;

import com.startinpoint.proj.familyapp.webservice.entity.FamilyMember;
import com.startinpoint.proj.familyapp.webservice.entity.enums.JoinStatus;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

public interface FamilyMemberService {
	public FamilyMember saveOrUpdate(FamilyMember member) throws FamilyAppWebserviceException;

	public List<FamilyMember> getFamilyMemberListByMemberIdExceptPersonal(Long memberId)throws FamilyAppWebserviceException;

	public FamilyMember getFamilyMemberByMemberIdFamilyId(Long memberId, Long familyId)throws FamilyAppWebserviceException;
	
	public FamilyMember findById(Long id) throws FamilyAppWebserviceException;

	public List<FamilyMember> getMemberListByFamilyId(Long familyId)throws FamilyAppWebserviceException;

	/**
	 * Update Family Member Join Status By Family Id
	 * @param familyId
	 * @param joinStatus
	 * @param memberId
	 * @throws FamilyAppWebserviceException
	 */
	public void updateFamilyMemberStatusByFamilyId(Long familyId, JoinStatus joinStatus,Long memberId)throws FamilyAppWebserviceException;
}
