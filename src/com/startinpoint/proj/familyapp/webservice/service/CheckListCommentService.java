package com.startinpoint.proj.familyapp.webservice.service;

import java.util.List;

import com.startinpoint.proj.familyapp.webservice.entity.CheckListComment;
import com.startinpoint.proj.familyapp.webservice.entity.enums.ScrollStatus;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @since 07/06/2018
 * @author nankhinmhwe
 *
 */
public interface CheckListCommentService {
	
	/**
	 * Save or update checklist comment
	 * @param comment
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public CheckListComment saveOrUpdateCheckListComment(CheckListComment comment) throws FamilyAppWebserviceException;
	
	/**
	 * 
	 * Get checklist comments By CheckListIds & MemberId
	 * @param checkListIds
	 * @param memberId
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public List<CheckListComment> getCommentsByCheckListIdsMemberId(List<Long> checkListIds , Long memberId) throws FamilyAppWebserviceException;

	/**
	 * Get CheckLists by size and scroll status
	 * @param checkListId
	 * @param size
	 * @param scrollStatus
	 * @param id
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public List<CheckListComment> getCommentsByCheckLists(Long checkListId,int size ,ScrollStatus scrollStatus, Long id) throws FamilyAppWebserviceException;

}
