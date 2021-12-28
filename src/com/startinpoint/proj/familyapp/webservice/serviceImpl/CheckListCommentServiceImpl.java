package com.startinpoint.proj.familyapp.webservice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.startinpoint.proj.familyapp.webservice.dao.CheckListCommentDao;
import com.startinpoint.proj.familyapp.webservice.entity.CheckListComment;
import com.startinpoint.proj.familyapp.webservice.entity.enums.ScrollStatus;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.CheckListCommentService;

/**
 * 
 * @since 07/06/2018
 * @author nankhinmhwe
 *
 */
@Service("checkListCommentService")
public class CheckListCommentServiceImpl implements CheckListCommentService{

	@Autowired
	CheckListCommentDao checkListCommentDao;
	
	@Override
	public CheckListComment saveOrUpdateCheckListComment(CheckListComment comment) throws FamilyAppWebserviceException {
		comment = checkListCommentDao.saveOrUpdateCheckListComment(comment);
		return comment;
	}

	@Override
	public List<CheckListComment> getCommentsByCheckListIdsMemberId(List<Long> checkListIds, Long memberId)
			throws FamilyAppWebserviceException {
		return checkListCommentDao.getCommentsByCheckListIdsMemberId(checkListIds, memberId);
		
	}

	@Override
	public List<CheckListComment> getCommentsByCheckLists(Long checkListId, int size, ScrollStatus scrollStatus,
			Long id) throws FamilyAppWebserviceException {
		return checkListCommentDao.getCommentsByCheckLists(checkListId, size,scrollStatus, id);
	}

}
