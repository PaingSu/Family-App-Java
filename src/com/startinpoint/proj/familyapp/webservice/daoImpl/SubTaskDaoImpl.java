package com.startinpoint.proj.familyapp.webservice.daoImpl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.startinpoint.proj.familyapp.webservice.dao.SubTaskDao;
import com.startinpoint.proj.familyapp.webservice.entity.SubTask;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @since 12/06/2018
 * @author nankhinmhwe
 *
 */
@Repository("subTaskDao")
public class SubTaskDaoImpl implements SubTaskDao{

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		Session session;
		try {
			session = sessionFactory.getCurrentSession();
		} catch (Exception e) {
			session = sessionFactory.openSession();
		}
		return session;
	}
	
	@Override
	public SubTask findById(Long id) throws FamilyAppWebserviceException {
		List<?> list = sessionFactory.getCurrentSession().createCriteria(SubTask.class)
				.add(Restrictions.eq("id",id))
				.list();
		
		if(list.size() <= 0 )
		{
			return null;
		}
		
		return (SubTask) list.get(0);
	}

	@Override
	public SubTask saveOrUpdateSubTask(SubTask subTask) throws FamilyAppWebserviceException {
		getSession().saveOrUpdate(subTask);
		return subTask;
	}

}
