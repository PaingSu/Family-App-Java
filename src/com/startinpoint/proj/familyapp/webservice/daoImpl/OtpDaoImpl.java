package com.startinpoint.proj.familyapp.webservice.daoImpl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.startinpoint.proj.familyapp.webservice.dao.OtpDao;
import com.startinpoint.proj.familyapp.webservice.entity.VerificationCode;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

@Repository("otpDao")
public class OtpDaoImpl implements OtpDao{
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
	public VerificationCode saveOrUpdateOtp(VerificationCode otp) throws FamilyAppWebserviceException {
		getSession().saveOrUpdate(otp);
		return otp;
	}

	@Override
	public VerificationCode findOtpByVerificationCode(String code) throws FamilyAppWebserviceException {
		List<?> list = sessionFactory.getCurrentSession().createCriteria(VerificationCode.class)
				.add(Restrictions.eq("password",code))
				.list();
		
		if(list.size() <= 0 )
		{
			return null;
		}
		
		return (VerificationCode) list.get(0);
	}

}
