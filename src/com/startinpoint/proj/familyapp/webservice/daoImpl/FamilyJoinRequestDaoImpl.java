package com.startinpoint.proj.familyapp.webservice.daoImpl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.startinpoint.proj.familyapp.webservice.dao.FamilyJoinRequestDao;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyAppConst;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyJoinRequest;
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
@Repository("familyJoinRequestDao")
public class FamilyJoinRequestDaoImpl implements FamilyJoinRequestDao{

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
	public FamilyJoinRequest saveOrUpdateJoinRequest(FamilyJoinRequest joinRequest)
			throws FamilyAppWebserviceException {
		getSession().saveOrUpdate(joinRequest);
		return joinRequest;
	}
		
	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyJoinRequest> getPendingJoinRequestByMemberId(Long memberId) throws FamilyAppWebserviceException {
		List<?> list = getSession().createCriteria(FamilyJoinRequest.class)
				.add(Restrictions.eq("user.id", memberId))
				.add(Restrictions.eq("joinStatus", JoinStatus.PENDING))
				.list();

		return (List<FamilyJoinRequest>) list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyJoinRequest> getPendingJoinRequestByFamilyId(Long familyId) throws FamilyAppWebserviceException {
		List<?> list = getSession().createCriteria(FamilyJoinRequest.class)
				.add(Restrictions.eq("family.id", familyId))
				.add(Restrictions.eq("joinStatus", JoinStatus.PENDING))
				.list();

		return (List<FamilyJoinRequest>) list;
	}

	@Override
	public FamilyJoinRequest getFamilyJoinRequestById(Long id) throws FamilyAppWebserviceException {
		List<?> list = getSession().createCriteria(FamilyJoinRequest.class)
				.add(Restrictions.eq("id", id))
				.add(Restrictions.eq("joinStatus", JoinStatus.PENDING))
				.list();
		
		if(list.isEmpty()){
			return null;
		}

		return (FamilyJoinRequest) list.get(0);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyJoinRequest> getJoinRequestListByCreatorId(Long creatorId) throws FamilyAppWebserviceException {

		DetachedCriteria dc = DetachedCriteria.forClass(FamilyJoinRequest.class);
		dc.add(Restrictions.eq("joinStatus", JoinStatus.PENDING));
		
		DetachedCriteria subquery = DetachedCriteria.forClass(FamilyProfile.class);
		subquery.add(Restrictions.eq("familyCreatorId", creatorId));
		subquery.add(Restrictions.eq("status", Status.A));
		subquery.setProjection(Property.forName("id"));
		dc.add(Subqueries.propertyIn("family.id", subquery));
	
		List<FamilyJoinRequest> list = dc.getExecutableCriteria(getSession()).list();			
		return list;
	}

}
