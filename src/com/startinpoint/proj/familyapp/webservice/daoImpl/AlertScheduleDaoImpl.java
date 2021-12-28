package com.startinpoint.proj.familyapp.webservice.daoImpl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.startinpoint.proj.familyapp.webservice.dao.AlertScheduleDao;
import com.startinpoint.proj.familyapp.webservice.entity.AlertSchedule;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * @author ThoonSandy
 * @version 1.0
 * @since 21-05-2018
 *
 */
@Repository("alertScheduleDao")
@Transactional
public class AlertScheduleDaoImpl implements AlertScheduleDao {
	
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		Session session;
		try {
			session = sessionFactory.getCurrentSession();
		} catch (Exception e) {
			session = sessionFactory.openSession();
		}
		return session;
	}

	@Override
	public AlertSchedule createAlertSchedule(AlertSchedule alert_schedule) throws FamilyAppWebserviceException {	
		getSession().saveOrUpdate(alert_schedule);
		return alert_schedule;
	}

	

	@Override
	public AlertSchedule findByAlertScheduleId(Long id) throws FamilyAppWebserviceException {
	
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AlertSchedule.class);
		
		criteria.add(Restrictions.eq("id", id));
		
		AlertSchedule alertSchedule=(AlertSchedule)criteria.uniqueResult();
		return alertSchedule;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AlertSchedule> findAll() throws FamilyAppWebserviceException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AlertSchedule.class);		
		return (List<AlertSchedule>)criteria.list();
	}

}
