package com.startinpoint.proj.familyapp.webservice.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.startinpoint.proj.familyapp.webservice.dao.SubTaskDao;
import com.startinpoint.proj.familyapp.webservice.entity.SubTask;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.SubTaskService;

/**
 * 
 * @since 12/06/2018
 * @author nankhinmhwe
 *
 */
@Service("subTaskService")
public class SubTaskServiceImpl implements SubTaskService{

	@Autowired
	SubTaskDao subTaskDao;
	
	@Override
	public SubTask findById(Long id) throws FamilyAppWebserviceException {
		return subTaskDao.findById(id);
	}

	@Override
	public SubTask saveOrUpdateSubTask(SubTask subTask) throws FamilyAppWebserviceException {
		return subTaskDao.saveOrUpdateSubTask(subTask);
	}

}
