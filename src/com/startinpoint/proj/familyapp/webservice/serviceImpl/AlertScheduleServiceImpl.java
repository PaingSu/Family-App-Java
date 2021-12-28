package com.startinpoint.proj.familyapp.webservice.serviceImpl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.startinpoint.proj.familyapp.webservice.dao.AlertScheduleDao;

import com.startinpoint.proj.familyapp.webservice.entity.AlertSchedule;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.AlertScheduleService;

/**
 * @author ThoonSandy
 * @version 1.0
 * @since 21-05-2018
 *
 */
@Service("alertScheduleService")
public class AlertScheduleServiceImpl implements AlertScheduleService{
	
	protected final Log logger=LogFactory.getLog(this.getClass());
	
	@Autowired
	AlertScheduleDao alertScheduleDao;

	@Override
	public AlertSchedule createAlertSchedule(AlertSchedule alert_schedule) throws FamilyAppWebserviceException {
		return alertScheduleDao.createAlertSchedule(alert_schedule);
	}

	@Override
	public AlertSchedule findByAlertScheduleId(Long id) throws FamilyAppWebserviceException {
	
		return alertScheduleDao.findByAlertScheduleId(id);
	}

	@Override
	public List<AlertSchedule> findAll() throws FamilyAppWebserviceException {
		
		return alertScheduleDao.findAll();
	}


}
