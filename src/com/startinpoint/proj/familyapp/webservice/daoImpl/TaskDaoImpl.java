package com.startinpoint.proj.familyapp.webservice.daoImpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.startinpoint.proj.familyapp.webservice.dao.TasksDao;
import com.startinpoint.proj.familyapp.webservice.entity.SubTask;
import com.startinpoint.proj.familyapp.webservice.entity.Tasks;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @since 07/06/2018
 * @author nankhinmhwe
 *
 */
@Repository("tasksDao")
public class TaskDaoImpl implements TasksDao{

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
	public Tasks saveOrUpdateTask(Tasks tasks){
		getSession().saveOrUpdate(tasks);
		return tasks;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Tasks> getTasksByCheckListIdCreatorId(List<Long> checkListIds, Long memberId)
			throws FamilyAppWebserviceException {
		Criteria criteria = getSession().createCriteria(Tasks.class);
		criteria.add(Restrictions.eq("status", Status.A));
		criteria.add(Restrictions.in("checkListId", checkListIds));
		if(memberId != null){
			criteria.add(Restrictions.eq("createdBy", memberId));
		}
		List<?> tasks = criteria.list();
		return (List<Tasks>) tasks;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Tasks> getTasksByCheckListIdWithSubTasks(Long checkListId) throws FamilyAppWebserviceException {
		Criteria criteria = getSession().createCriteria(Tasks.class);
		criteria.add(Restrictions.eq("status", Status.A));
		criteria.add(Restrictions.eq("checkListId", checkListId));
		criteria.addOrder(Order.asc("createdDateTime"));
		
		List<Tasks> tasks = criteria.list();
		for(Tasks t: tasks){
			Hibernate.initialize(t.getSubTaskList());			
		}
		return tasks;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Tasks> getTasksByCheckListIdWithoutSubTasks(Long checkListId) throws FamilyAppWebserviceException {
		Criteria criteria = getSession().createCriteria(Tasks.class);
		criteria.add(Restrictions.eq("status", Status.A));
		criteria.add(Restrictions.eq("checkListId", checkListId));
		
	
		List<Tasks> tasks = criteria.list();
		//because lazy collection exception can cause in json response
		for(Tasks t: tasks){
			t.setSubTaskList(new ArrayList<SubTask>());
		}
		
		return tasks;
	}

	@Override
	public Tasks findById(Long id)throws FamilyAppWebserviceException {
		List<?> list = sessionFactory.getCurrentSession().createCriteria(Tasks.class)
				.add(Restrictions.eq("id",id))
				.list();
		
		if(list.size() <= 0 )
		{
			return null;
		}
		
		return (Tasks) list.get(0);
	}

}
