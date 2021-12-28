package com.startinpoint.proj.familyapp.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.startinpoint.proj.familyapp.webservice.dao.AlertScheduleDao;
import com.startinpoint.proj.familyapp.webservice.entity.AlertSchedule;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;


public class FamilyAppDeployListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		System.out.println("at FamilyAppDeployListener...");
		
		ServletContext ctx=servletContextEvent.getServletContext();
		WebApplicationContext applicationContext=WebApplicationContextUtils.getWebApplicationContext(ctx);
		
		AlertScheduleDao alertScheduleDao=(AlertScheduleDao)applicationContext.getBean("alertScheduleDao");
		
		try {
			AlertSchedule alertSchedule=alertScheduleDao.findByAlertScheduleId((long) 1);
			//System.out.println("alert schedule obj..."+alertSchedule);
			if(alertSchedule == null){
			alertScheduleDao.createAlertSchedule(new AlertSchedule("1 hour",  60));
			alertScheduleDao.createAlertSchedule(new AlertSchedule("30 mins", 30));
			}
		} catch (FamilyAppWebserviceException e) {
			e.printStackTrace();
		}
	}

}
