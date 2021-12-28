package com.startinpoint.proj.familyapp.webservice.daoImpl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.startinpoint.proj.familyapp.webservice.dao.UserDao;
import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

@Repository("userDao")
public class UserDaoImpl implements UserDao{

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
	public UserProfile saveUser(UserProfile user) throws FamilyAppWebserviceException{
		getSession().saveOrUpdate(user);
		return user;
	}

	@Override
	public UserProfile findByUsername(String username)throws FamilyAppWebserviceException {
		
		Criteria criteria = getSession().createCriteria(UserProfile.class);
	    criteria.add(Restrictions.eq("username", username));

	    UserProfile user=(UserProfile) criteria.uniqueResult();
	    
		return user;
	}
	
	@Override
	public UserProfile findByEmail(String email) throws FamilyAppWebserviceException{
		List<?> list = getSession().createCriteria(UserProfile.class)
				.add(Restrictions.eq("email",email))
				.list();
		
		if(list.size() <= 0 )
		{
			return null;
		}
		
		return (UserProfile) list.get(0);
	}

	@Override
	public UserProfile findById(Long id) throws FamilyAppWebserviceException {
		List<?> list = getSession().createCriteria(UserProfile.class)
				.add(Restrictions.eq("id",id))
				.list();
		
		if(list.size() <= 0 )
		{
			return null;
		}
		
		return (UserProfile) list.get(0);
	}


}
