package com.startinpoint.proj.familyapp.webservice.serviceImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.startinpoint.proj.familyapp.webservice.dao.UserDao;
import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.UserService;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Service("userService")
public class UserServiceImpl implements UserService{
	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	UserDao userDao;
	
	@Override
	public UserProfile findById(Long id) throws FamilyAppWebserviceException {
		UserProfile user = userDao.findById(id);
		return user;
	}

	@Override
	public UserProfile saveOrUpdateUser(UserProfile user) throws FamilyAppWebserviceException {
		user = userDao.saveUser(user);
		return user;
	}

	@Override
	public UserProfile findByUsername(String username) throws FamilyAppWebserviceException {
		UserProfile user = userDao.findByUsername(username);
		return user;
	}

	@Override
	public UserProfile findByEmail(String email) throws FamilyAppWebserviceException {
		UserProfile user = userDao.findByEmail(email);
		return user;
	}

	
	
	

}
