package com.startinpoint.proj.familyapp.webservice.dao;

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
public interface CheckListCommentDao {
	
	public CheckListComment saveOrUpdateCheckListComment(CheckListComment comment) throws FamilyAppWebserviceException;
	
	public List<CheckListComment> getCommentsByCheckListIdsMemberId(List<Long> checkListIds , Long memberId) throws FamilyAppWebserviceException;

	public List<CheckListComment> getCommentsByCheckLists(Long checkListId, int size, ScrollStatus scrollStatus,
			Long id) throws FamilyAppWebserviceException;

}
