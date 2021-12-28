package com.startinpoint.proj.familyapp.webservice.service;

import java.util.List;

import com.startinpoint.proj.familyapp.webservice.entity.CheckListMember;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @since 08/06/2018
 * @author nankhinmhwe
 *
 */
public interface CheckListMemberService {

	/**
	 * Save or update checklist member
	 * @param member
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public CheckListMember saveOrUpdate(CheckListMember member) throws FamilyAppWebserviceException;

	
	/**
	 * Get Member List By Check List Id
	 * @param checkListId
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public List<CheckListMember> getMembersByCheckListId(Long checkListId) throws FamilyAppWebserviceException;
	
	/**
	 * Get Check List Member By Check List Id & Member Id
	 * @param checkListId
	 * @param memberId
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public CheckListMember getMemberByCheckListIdMemberId(Long checkListId,Long memberId) throws FamilyAppWebserviceException;

	/**
	 * updateStatusNotInMemberIds
	 * @param memberIds
	 * @throws FamilyAppWebserviceException
	 */
	public void updateStatusNotInMemberIds(List<Long> memberIds,Long checkListId) throws FamilyAppWebserviceException;

}
