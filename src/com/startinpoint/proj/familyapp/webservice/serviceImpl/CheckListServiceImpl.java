package com.startinpoint.proj.familyapp.webservice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.startinpoint.proj.familyapp.webservice.dao.CheckListDao;
import com.startinpoint.proj.familyapp.webservice.entity.CheckList;
import com.startinpoint.proj.familyapp.webservice.entity.enums.ScrollStatus;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.CheckListService;

/**
 * 
 * @author nankhinmhwe
 * @since 06/05/2018
 *
 */
@Service("checkListService")
public class CheckListServiceImpl implements CheckListService{

	@Autowired
	CheckListDao checkListDao;
	
	@Override
	public CheckList saveOrUpdateCheckList(CheckList checkList) throws FamilyAppWebserviceException {
		checkList = checkListDao.saveOrUpdateCheckList(checkList);
		return checkList;
	}

	@Override
	public CheckList findById(Long id) throws FamilyAppWebserviceException {
		CheckList checkList = checkListDao.findById(id);
		return checkList;
	}

	@Override
	public List<CheckList> getCheckListByFamilyIdUserId(Long familyId, String checkListName, int size,Long userId)
			throws FamilyAppWebserviceException {
		return checkListDao.getCheckListByFamilyIdUserId(familyId, checkListName, size,userId);
	}

	@Override
	public List<CheckList> getLoadMoreOrRefreshCheckList(Long familyId, String checkListName, int checklistSize,
			Long id, ScrollStatus scrollStatus,Long userId) throws FamilyAppWebserviceException {
		return checkListDao.getLoadMoreOrRefreshCheckList(familyId, checkListName, checklistSize,id,scrollStatus,userId);
	}

	@Override
	public void replaceFamilyIdInCheckList(Long oldFamilyId, Long newFamilyId) throws FamilyAppWebserviceException {
		checkListDao.replaceFamilyIdInCheckList(oldFamilyId,newFamilyId);
		
	}

}
