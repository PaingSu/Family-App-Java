package com.startinpoint.proj.familyapp.webservice.service;

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
public interface CheckListService {
	/**
	 * Save or Update Check List
	 * @param checkList
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public CheckList saveOrUpdateCheckList(CheckList checkList) throws FamilyAppWebserviceException;
	
	/**
	 * Find Check List By ID
	 * @param id
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public CheckList findById(Long id) throws FamilyAppWebserviceException;
	
	/**
	 * Get Check List By Family Id
	 * @param familyId
	 * @param checkListName
	 * @param size
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public List<CheckList> getCheckListByFamilyIdUserId(Long familyId ,String checkListName,int size,Long userId) throws FamilyAppWebserviceException;

	/**
	 * Get Load More / Refresh Check List
	 * @param familyId
	 * @param checkListName
	 * @param checklistSize
	 * @param id
	 * @param scrollStatus
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public List<CheckList> getLoadMoreOrRefreshCheckList(Long familyId, String checkListName, int checklistSize,
			Long id, ScrollStatus scrollStatus,Long userId)throws FamilyAppWebserviceException;

	/**
	 * Replace family Id
	 * 
	 * @param oldFamilyId
	 * @param newFamilyId
	 * @throws FamilyAppWebserviceException
	 */
	public void replaceFamilyIdInCheckList(Long oldFamilyId, Long newFamilyId) throws FamilyAppWebserviceException;
}
