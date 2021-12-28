package com.startinpoint.proj.familyapp.webservice.dao;

import java.util.List;

import com.startinpoint.proj.familyapp.webservice.entity.FamilyMember;
import com.startinpoint.proj.familyapp.webservice.entity.enums.JoinStatus;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

public interface FamilyMemberDao {
	public FamilyMember saveOrUpdate(FamilyMember member) throws FamilyAppWebserviceException;

	public List<FamilyMember> getFamilyMemberListByMemberIdExceptPersonal(Long memberId)throws FamilyAppWebserviceException;

	public FamilyMember getFamilyMemberByMemberIdFamilyId(Long memberId, Long familyId)throws FamilyAppWebserviceException;

	public FamilyMember findById(Long id) throws FamilyAppWebserviceException;

	public List<FamilyMember> getMemberListByFamilyId(Long familyId)throws FamilyAppWebserviceException;

	public void updateFamilyMemberStatusByFamilyId(Long familyId, JoinStatus joinStatus,Long memberId)throws FamilyAppWebserviceException;
	
}
