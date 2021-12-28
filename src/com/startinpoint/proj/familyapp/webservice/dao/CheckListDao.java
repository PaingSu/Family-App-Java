package com.startinpoint.proj.familyapp.webservice.dao;

import java.util.List;

import com.startinpoint.proj.familyapp.webservice.entity.CheckList;
import com.startinpoint.proj.familyapp.webservice.entity.enums.ScrollStatus;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @author nankhinmhwe
 * @since 06/05/2018
 *
 */
public interface CheckListDao {
	public CheckList saveOrUpdateCheckList(CheckList checkList) throws FamilyAppWebserviceException;
	
	public CheckList findById(Long id) throws FamilyAppWebserviceException;
	
	public List<CheckList> getCheckListByFamilyIdUserId(Long familyId ,String checkListName,int size,Long userId) throws FamilyAppWebserviceException;

	public List<CheckList> getLoadMoreOrRefreshCheckList(Long familyId, String checkListName, int checklistSize,
			Long id, ScrollStatus scrollStatus,Long userId)throws FamilyAppWebserviceException;

	public void replaceFamilyIdInCheckList(Long oldFamilyId,Long newFamilyId) throws FamilyAppWebserviceException;
}
