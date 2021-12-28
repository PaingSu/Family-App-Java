package com.startinpoint.proj.familyapp.webservice.daoImpl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.startinpoint.proj.familyapp.webservice.dao.CheckListMemberDao;
import com.startinpoint.proj.familyapp.webservice.entity.CheckListMember;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @since 08/06/2018
 * @author nankhinmhwe
 *
 */
@Repository("checkListMemberDao")
public class CheckListMemberDaoImpl implements CheckListMemberDao{
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
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CheckListMember> getMembersByCheckListId(Long checkListId) throws FamilyAppWebserviceException {
		Criteria criteria = getSession().createCriteria(CheckListMember.class);
		criteria.add(Restrictions.eq("checklist.id", checkListId));
		criteria.add(Restrictions.eq("status", Status.A));				
//		criteria.addOrder(Order.asc("user.username"));
		
		List<?> list = criteria.list();
		return (List<CheckListMember>) list;
	}

	@Override
	public CheckListMember getMemberByCheckListIdMemberId(Long checkListId, Long memberId)
			throws FamilyAppWebserviceException {
		Criteria criteria = getSession().createCriteria(CheckListMember.class);
		criteria.add(Restrictions.eq("checklist.id", checkListId));
		criteria.add(Restrictions.eq("status", Status.A));				
		criteria.add(Restrictions.eq("user.id", memberId));	
		List<?> list = criteria.list();
		if(list.isEmpty())
		{
			return null;
		}
		return (CheckListMember) list.get(0);
	}

	@Override
	public CheckListMember saveOrUpdate(CheckListMember member) throws FamilyAppWebserviceException {
		getSession().saveOrUpdate(member);
		return member;
	}

	@Override
	public void updateStatusNotInMemberIds(List<Long> memberIds ,Long checkListId) throws FamilyAppWebserviceException {
		String hql="update CheckListMember set status=:status where checklist.id=:checkListId and userId not in (:memberIds)";
		Query query=getSession().createQuery(hql);
		query.setString("status", Status.I.toString());
		query.setLong("checkListId", checkListId);
		query.setParameterList("memberIds", memberIds);
		query.executeUpdate();
	}

}
