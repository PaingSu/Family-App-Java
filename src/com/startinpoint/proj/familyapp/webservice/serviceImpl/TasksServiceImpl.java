package com.startinpoint.proj.familyapp.webservice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.startinpoint.proj.familyapp.webservice.dao.TasksDao;
import com.startinpoint.proj.familyapp.webservice.entity.Tasks;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.TasksService;

/**
 * 
 * @since 07/06/2018
 * @author nankhinmhwe
 *
 */
@Service("tasksService")
public class TasksServiceImpl implements TasksService{

	@Autowired
	TasksDao tasksDao;
	
	@Override
	public Tasks saveOrUpdateTask(Tasks tasks) throws FamilyAppWebserviceException {
		return tasksDao.saveOrUpdateTask(tasks);
	}

	@Override
	public List<Tasks> getTasksByCheckListIdCreatorId(List<Long> checkListIds, Long memberId)
			throws FamilyAppWebserviceException {
		List<Tasks> tasks = tasksDao.getTasksByCheckListIdCreatorId(checkListIds, memberId);
		return tasks;
	}

	@Override
	public List<Tasks> getTasksByCheckListIdWithSubTasks(Long checkListId) throws FamilyAppWebserviceException {
		List<Tasks> tasks = tasksDao.getTasksByCheckListIdWithSubTasks(checkListId);
		return tasks;
	}

	@Override
	public List<Tasks> getTasksByCheckListIdWithoutSubTasks(Long checkListId) throws FamilyAppWebserviceException {
		List<Tasks> tasks = tasksDao.getTasksByCheckListIdWithoutSubTasks(checkListId);
		return tasks;
	}

	@Override
	public Tasks findById(Long id) throws FamilyAppWebserviceException {
		
		return tasksDao.findById(id);
	}

}
