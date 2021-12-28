package com.startinpoint.proj.familyapp.webservice.dao;
import java.util.List;

/**
 * @author ThoonSandy
 * @version 1.0
 * @since 21-05-2018
 *
 */
import com.startinpoint.proj.familyapp.webservice.entity.AlertSchedule;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

public interface AlertScheduleDao {
	   public AlertSchedule createAlertSchedule(AlertSchedule alert_schedule)throws FamilyAppWebserviceException;
	    
	    public AlertSchedule findByAlertScheduleId(Long id)throws FamilyAppWebserviceException;

		public List<AlertSchedule> findAll() throws FamilyAppWebserviceException;
}
