package com.startinpoint.proj.familyapp.webservice.dao;

import com.startinpoint.proj.familyapp.webservice.entity.SubTask;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @since 12/06/2018
 * @author nankhinmhwe
 *
 */
public interface SubTaskDao {
	
	public SubTask findById(Long id) throws FamilyAppWebserviceException;
	
	public SubTask saveOrUpdateSubTask(SubTask subTask) throws FamilyAppWebserviceException;
}
