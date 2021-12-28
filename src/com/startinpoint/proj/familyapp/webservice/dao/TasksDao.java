package com.startinpoint.proj.familyapp.webservice.dao;

import java.util.List;

import com.startinpoint.proj.familyapp.webservice.entity.Tasks;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @since 07/06/2018
 * @author nankhinmhwe
 *
 */
public interface TasksDao {
	public Tasks saveOrUpdateTask(Tasks tasks);
	
	public List<Tasks> getTasksByCheckListIdCreatorId(List<Long> checkListIds , Long memberId) throws FamilyAppWebserviceException;

	public List<Tasks> getTasksByCheckListIdWithSubTasks(Long checkListId) throws FamilyAppWebserviceException;

	public List<Tasks> getTasksByCheckListIdWithoutSubTasks(Long checkListId) throws FamilyAppWebserviceException;

	public Tasks findById(Long id)throws FamilyAppWebserviceException;

}
