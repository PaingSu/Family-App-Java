package com.startinpoint.proj.familyapp.webservice.service;

import java.util.Date;
import java.util.List;

import com.startinpoint.proj.familyapp.webservice.entity.CheckListActivity;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Activity;
import com.startinpoint.proj.familyapp.webservice.entity.enums.ScrollStatus;
import com.startinpoint.proj.familyapp.webservice.entity.pojo.ActivityLog;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

public interface CheckListActivityService {
	
	/**
	 * Save or Update Check List Activity
	 * @param activity
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public CheckListActivity saveOrUpdateCheckListActivity(CheckListActivity activity) throws FamilyAppWebserviceException;

	/**
	 * Get Check List activity log shuffle with two table comment and activity
	 * @param checkListId
	 * @param activity
	 * @param size
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public List<ActivityLog> getCheckListActivityLog(Long checkListId ,Activity activity, int size)throws FamilyAppWebserviceException;

	/**
	 * Get Check List activity log shuffle with two table comment and activity by scroll status
	 * @param checkListId
	 * @param activity
	 * @param size
	 * @param scrollStatus  //BEFORE / AFTER
	 * @param lastDate
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public List<ActivityLog> getCheckListActivityLogByScrollStatus(Long checkListId ,Activity activity, int size,ScrollStatus scrollStatus,Date lastDate)throws FamilyAppWebserviceException;

}
