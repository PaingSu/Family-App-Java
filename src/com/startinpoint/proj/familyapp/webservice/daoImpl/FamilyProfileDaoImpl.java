package com.startinpoint.proj.familyapp.webservice.daoImpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.startinpoint.proj.familyapp.webservice.dao.FamilyProfileDao;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyAppConst;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyMember;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyProfile;
import com.startinpoint.proj.familyapp.webservice.entity.enums.JoinStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Repository("familyProfileDao")
public class FamilyProfileDaoImpl implements FamilyProfileDao{
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
	public FamilyProfile saveOrUpdateFamilyProfile(FamilyProfile profile) throws FamilyAppWebserviceException {
		getSession().saveOrUpdate(profile);
		return profile;
	}


	@Override
	public FamilyProfile getFamilyProfileByFamilyCode(String familyCode) throws FamilyAppWebserviceException {
		
		List<?> list = getSession().createCriteria(FamilyProfile.class)
				.add(Restrictions.eq("familyCode",familyCode))
				.add(Restrictions.eq("status", Status.A))
				.list();
		
		FamilyProfile family = null;
		if(list.size() > 0 )
		{
			family = (FamilyProfile) list.get(0);
			family.setMembers(family.getMembers()); //lazy initialize
		}
		
		return family;
	}
	

	@Override
	public FamilyProfile findByFamilyProfileId(Long familyProfileId) throws FamilyAppWebserviceException {
		Criteria criteria=sessionFactory.getCurrentSession().createCriteria(FamilyProfile.class);
		criteria.add(Restrictions.eq("id", familyProfileId));
		criteria.add(Restrictions.eq("status", Status.A));
		FamilyProfile familyProfile=(FamilyProfile) criteria.uniqueResult();
		return familyProfile;
	}


	@Override
	public FamilyProfile getInactivePersonalFamily(Long creatorId) throws FamilyAppWebserviceException {
			
		Criteria criteria = getSession().createCriteria(FamilyProfile.class);
		criteria.add(Restrictions.eq("status", Status.I));
		criteria.add(Restrictions.eq("familyCreatorId", creatorId));
		criteria.add(Restrictions.eq("familyName", FamilyAppConst.DEFAULT_FAMILY_NAME));
		List<?> list = criteria.list();
		if(list.size()> 0){
			return (FamilyProfile) list.get(0);
		}
		return null;
	}

	@Override
	public FamilyProfile getPersonalFamilyByCreatorId(Long creatorId) throws FamilyAppWebserviceException {
				
		Criteria criteria = getSession().createCriteria(FamilyProfile.class);
		criteria.add(Restrictions.eq("status", Status.A)); //coz leave may be inactive
		criteria.add(Restrictions.eq("familyCreatorId", creatorId));
		criteria.add(Restrictions.eq("familyName", FamilyAppConst.DEFAULT_FAMILY_NAME));
		List<?> list = criteria.list();
		if(list.size()> 0){
			return (FamilyProfile) list.get(0);
		}
		return null;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyProfile> getFamilyExceptPersonal(Long userId) throws FamilyAppWebserviceException {
		
		DetachedCriteria dc = DetachedCriteria.forClass(FamilyProfile.class);
		dc.add(Restrictions.eq("status", Status.A));
		dc.add(Restrictions.ne("familyName", FamilyAppConst.DEFAULT_FAMILY_NAME));
		
		
		DetachedCriteria subquery = DetachedCriteria.forClass(FamilyMember.class);
		subquery.add(Restrictions.eq("status",Status.A));
		subquery.add(Restrictions.eq("member.id", userId));
		subquery.add(Restrictions.eq("requestStatus", JoinStatus.JOINED));
		subquery.setProjection(Property.forName("family.id"));
		dc.add(Subqueries.propertyIn("id", subquery));
			
		List<FamilyProfile> list = dc.getExecutableCriteria(getSession()).list();	
		
		return (List<FamilyProfile>) list;
	}

}
