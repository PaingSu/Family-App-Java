package com.startinpoint.proj.familyapp.webservice.daoImpl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.startinpoint.proj.familyapp.webservice.dao.FamilyMemberDao;
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
@Repository("familyMemberDao")
public class FamilyMemberDaoImpl implements FamilyMemberDao{
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
	public FamilyMember saveOrUpdate(FamilyMember member) throws FamilyAppWebserviceException {
		getSession().saveOrUpdate(member);
		return member;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyMember> getFamilyMemberListByMemberIdExceptPersonal(Long memberId) throws FamilyAppWebserviceException {
		//get member list but not contains personal family's members
		DetachedCriteria dc = DetachedCriteria.forClass(FamilyMember.class);
		dc.add(Restrictions.eq("status",Status.A));
		dc.add(Restrictions.eq("member.id", memberId));
		dc.add(Restrictions.eq("requestStatus", JoinStatus.JOINED));
		
		DetachedCriteria subquery = DetachedCriteria.forClass(FamilyProfile.class);
		subquery.add(Restrictions.eq("status", Status.A));
		subquery.add(Restrictions.eq("familyName", FamilyAppConst.DEFAULT_FAMILY_NAME));
		subquery.add(Restrictions.eq("familyCreatorId", memberId));
		subquery.setProjection(Property.forName("id"));
		dc.add(Subqueries.propertyNotIn("family.id", subquery));
		List<FamilyMember> list = dc.getExecutableCriteria(getSession()).list();

		return list;
	}


	@Override
	public FamilyMember getFamilyMemberByMemberIdFamilyId(Long memberId, Long familyId)
			throws FamilyAppWebserviceException {
		List<?> list = getSession().createCriteria(FamilyMember.class)
				.add(Restrictions.eq("member.id", memberId))
				.add(Restrictions.eq("family.id", familyId))
				.add(Restrictions.eq("status", Status.A))
				.add(Restrictions.eq("requestStatus", JoinStatus.JOINED))
				.list();
		
		if(list.isEmpty()){
			return null;
		}

		return (FamilyMember) list.get(0);
	}


	@Override
	public FamilyMember findById(Long id) throws FamilyAppWebserviceException {
		FamilyMember member = (FamilyMember) getSession().get(FamilyMember.class, id);
		return member;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyMember> getMemberListByFamilyId(Long familyId) throws FamilyAppWebserviceException {
		List<?> list = getSession().createCriteria(FamilyMember.class)
				.add(Restrictions.eq("family.id", familyId))
				.add(Restrictions.eq("requestStatus", JoinStatus.JOINED))
				.list();

		return (List<FamilyMember>) list;
	}


	@Override
	public void updateFamilyMemberStatusByFamilyId(Long familyId, JoinStatus joinStatus,Long memberId)
			throws FamilyAppWebserviceException {
		String hql ="UPDATE FamilyMember fm SET fm.requestStatus=:joinStatus,fm.status=:status WHERE fm.family.id=:familyId AND fm.member.id=:memberId  ";
				
		Query query = getSession().createQuery(hql);
		query.setParameter("joinStatus", joinStatus);
		if(joinStatus == JoinStatus.LEAVE){
			query.setParameter("status", Status.I);
		}
		else{
			query.setParameter("status", Status.A);
		}
		query.setParameter("familyId", familyId);
		query.setParameter("memberId", memberId);
		
		int rowCount = query.executeUpdate();
		System.out.println(rowCount+" Rows Updated.");
	}

}
