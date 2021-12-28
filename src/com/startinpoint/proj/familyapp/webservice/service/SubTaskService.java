package com.startinpoint.proj.familyapp.webservice.service;

import com.startinpoint.proj.familyapp.webservice.entity.SubTask;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @since 12/06/2018
 * @author nankhinmhwe
 *
 */
public interface SubTaskService {
	/**
	 * Find sub task by id
	 * @param id
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	SubTask findById(Long id) throws FamilyAppWebserviceException;
	
	/**
	 * Save or update subtask
	 * @param subTask
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public SubTask saveOrUpdateSubTask(SubTask subTask) throws FamilyAppWebserviceException;
	
}
