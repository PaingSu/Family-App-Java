package com.startinpoint.proj.familyapp.webservice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.startinpoint.proj.familyapp.webservice.dao.CheckListMemberDao;
import com.startinpoint.proj.familyapp.webservice.entity.CheckListMember;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.CheckListMemberService;

/**
 * 
 * @since 08/06/2018
 * @author nankhinmhwe
 *
 */
@Service("checkListMemberService")
public class CheckListMemberServiceImpl implements CheckListMemberService{

	@Autowired
	CheckListMemberDao checkListMemberDao;
	
	@Override
	public List<CheckListMember> getMembersByCheckListId(Long checkListId) throws FamilyAppWebserviceException {
		return checkListMemberDao.getMembersByCheckListId(checkListId);
	}

	@Override
	public CheckListMember getMemberByCheckListIdMemberId(Long checkListId, Long memberId)
			throws FamilyAppWebserviceException {
		return checkListMemberDao.getMemberByCheckListIdMemberId(checkListId,memberId);
	}

	@Override
	public CheckListMember saveOrUpdate(CheckListMember member) throws FamilyAppWebserviceException {
		return checkListMemberDao.saveOrUpdate(member);
	}

	@Override
	public void updateStatusNotInMemberIds(List<Long> memberIds,Long checkListId) throws FamilyAppWebserviceException {
		checkListMemberDao.updateStatusNotInMemberIds(memberIds,checkListId);
	}

}
