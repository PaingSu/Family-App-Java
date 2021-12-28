package com.startinpoint.proj.familyapp.webservice.dao;

import java.util.Date;
import java.util.List;

import com.startinpoint.proj.familyapp.webservice.entity.CheckListActivity;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Activity;
import com.startinpoint.proj.familyapp.webservice.entity.enums.ScrollStatus;
import com.startinpoint.proj.familyapp.webservice.entity.pojo.ActivityLog;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @author nankhinmhwe
 *
 */
public interface CheckListActivityDao {
	
	public CheckListActivity saveOrUpdateCheckListActivity(CheckListActivity activity) throws FamilyAppWebserviceException;

	public List<ActivityLog> getCheckListActivityLog(Long checkListId ,Activity activity, int size)throws FamilyAppWebserviceException;

	public List<ActivityLog> getCheckListActivityLogByScrollStatus(Long checkListId, Activity activity, int size,
			ScrollStatus scrollStatus, Date lastDate)throws FamilyAppWebserviceException;
}
