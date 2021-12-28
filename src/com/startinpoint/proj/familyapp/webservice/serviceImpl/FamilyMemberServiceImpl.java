package com.startinpoint.proj.familyapp.webservice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.startinpoint.proj.familyapp.webservice.dao.FamilyMemberDao;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyMember;
import com.startinpoint.proj.familyapp.webservice.entity.enums.JoinStatus;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.FamilyMemberService;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Service("familyMemberService")
public class FamilyMemberServiceImpl implements FamilyMemberService{

	@Autowired
	FamilyMemberDao familyMemberDao;
	
	@Override
	public FamilyMember saveOrUpdate(FamilyMember member) throws FamilyAppWebserviceException {
		member = familyMemberDao.saveOrUpdate(member);
		return member;
	}

	@Override
	public List<FamilyMember> getFamilyMemberListByMemberIdExceptPersonal(Long memberId) throws FamilyAppWebserviceException {
		List<FamilyMember> members = familyMemberDao.getFamilyMemberListByMemberIdExceptPersonal(memberId);
		return members;
	}

	
	@Override
	public FamilyMember findById(Long id) throws FamilyAppWebserviceException {
		FamilyMember member = familyMemberDao.findById(id);
		return member;
	}

	@Override
	public List<FamilyMember> getMemberListByFamilyId(Long familyId) throws FamilyAppWebserviceException {
		List<FamilyMember> members = familyMemberDao.getMemberListByFamilyId(familyId);
		return members;
	}
	
	@Override
	public void updateFamilyMemberStatusByFamilyId(Long familyId, JoinStatus joinStatus,Long memberId) throws FamilyAppWebserviceException {
		familyMemberDao.updateFamilyMemberStatusByFamilyId(familyId,joinStatus,memberId);
	}

	@Override
	public FamilyMember getFamilyMemberByMemberIdFamilyId(Long memberId, Long familyId)
			throws FamilyAppWebserviceException {
		FamilyMember member = familyMemberDao.getFamilyMemberByMemberIdFamilyId(memberId,familyId);
		return member;
	}

}
