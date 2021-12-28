package com.startinpoint.proj.familyapp.webservice.serviceImpl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.startinpoint.proj.familyapp.webservice.dao.CheckListActivityDao;
import com.startinpoint.proj.familyapp.webservice.entity.CheckListActivity;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Activity;
import com.startinpoint.proj.familyapp.webservice.entity.enums.ScrollStatus;
import com.startinpoint.proj.familyapp.webservice.entity.pojo.ActivityLog;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.CheckListActivityService;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Service("checkListActivityService")
public class CheckListActivityServiceImpl implements CheckListActivityService {

	@Autowired
	CheckListActivityDao checkListActivityDao;
	
	@Override
	public CheckListActivity saveOrUpdateCheckListActivity(CheckListActivity activity)
			throws FamilyAppWebserviceException {
		activity = checkListActivityDao.saveOrUpdateCheckListActivity(activity);
		return activity;
	}

	@Override
	public List<ActivityLog> getCheckListActivityLog(Long checkListId, Activity activity, int size)
			throws FamilyAppWebserviceException {
		return checkListActivityDao.getCheckListActivityLog(checkListId, activity, size);
	}

	@Override
	public List<ActivityLog> getCheckListActivityLogByScrollStatus(Long checkListId, Activity activity, int size,
			ScrollStatus scrollStatus, Date lastDate) throws FamilyAppWebserviceException {
		return checkListActivityDao.getCheckListActivityLogByScrollStatus(checkListId, activity, size,scrollStatus, lastDate);
	}

}
