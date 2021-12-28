package com.startinpoint.proj.familyapp.webservice.service;

import java.util.List;

import com.startinpoint.proj.familyapp.webservice.entity.Tasks;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @since 07/06/2018
 * @author nankhinmhwe
 *
 */
public interface TasksService {
	/**
	 * Find By id
	 * @param id
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	Tasks findById(Long id) throws FamilyAppWebserviceException;
	
	/**
	 * save or update checklist comment
	 * @param tasks
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public Tasks saveOrUpdateTask(Tasks tasks) throws FamilyAppWebserviceException;
	
	/**
	 * Get Task List by Checklist id list and creator id //creator id is optional
	 * @param checkListIds
	 * @param memberId
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public List<Tasks> getTasksByCheckListIdCreatorId(List<Long> checkListIds , Long memberId) throws FamilyAppWebserviceException;

	/**
	 * Get Task List by checkListid including subtask list
	 * @param checkListId
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public List<Tasks> getTasksByCheckListIdWithSubTasks(Long checkListId) throws FamilyAppWebserviceException;
	
	/**
	 * Get Task List by checkListid without subtask list
	 * @param checkListId
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public List<Tasks> getTasksByCheckListIdWithoutSubTasks(Long checkListId) throws FamilyAppWebserviceException;
	
}
 