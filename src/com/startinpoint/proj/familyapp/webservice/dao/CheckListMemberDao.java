package com.startinpoint.proj.familyapp.webservice.dao;

import java.util.List;

import com.startinpoint.proj.familyapp.webservice.entity.CheckListMember;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @since 08/06/2018
 * @author nankhinmhwe
 *
 */
public interface CheckListMemberDao {
	
	public List<CheckListMember> getMembersByCheckListId(Long checkListId) throws FamilyAppWebserviceException;

	public CheckListMember getMemberByCheckListIdMemberId(Long checkListId, Long memberId)throws FamilyAppWebserviceException;

	public CheckListMember saveOrUpdate(CheckListMember member)throws FamilyAppWebserviceException;

	public void updateStatusNotInMemberIds(List<Long> memberIds,Long checkListId)throws FamilyAppWebserviceException;
	
}
