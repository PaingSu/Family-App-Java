package com.startinpoint.proj.familyapp.webservice.daoImpl;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.startinpoint.proj.familyapp.webservice.dao.CheckListCommentDao;
import com.startinpoint.proj.familyapp.webservice.entity.CheckListComment;
import com.startinpoint.proj.familyapp.webservice.entity.enums.ScrollStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @since 07/06/2018
 * @author nankhinmhwe
 *
 */
@Repository("checkListCommentDao")
public class CheckListCommentDaoImpl implements CheckListCommentDao{

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
	public CheckListComment saveOrUpdateCheckListComment(CheckListComment comment) throws FamilyAppWebserviceException {
		getSession().saveOrUpdate(comment);
		return comment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CheckListComment> getCommentsByCheckListIdsMemberId(List<Long> checkListIds, Long memberId)
			throws FamilyAppWebserviceException {
		Criteria criteria = getSession().createCriteria(CheckListComment.class);
		criteria.add(Restrictions.eq("status", Status.A));
		criteria.add(Restrictions.in("checkListId", checkListIds));
		criteria.add(Restrictions.eq("member.id", memberId));
		List<?> comments = criteria.list();
		return (List<CheckListComment>) comments;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CheckListComment> getCommentsByCheckLists(Long checkListId, int size, ScrollStatus scrollStatus,
			Long id) throws FamilyAppWebserviceException {
		Criteria criteria = getSession().createCriteria(CheckListComment.class);
		criteria.add(Restrictions.eq("status", Status.A));
		criteria.add(Restrictions.eq("checkListId", checkListId));
		if(scrollStatus != null){
			if(scrollStatus == ScrollStatus.BEFORE){
				criteria.add(Restrictions.lt("id", id));
			}
			else if(scrollStatus == ScrollStatus.AFTER){
				criteria.add(Restrictions.gt("id", id));
			}
		}
		
		criteria.addOrder(Order.asc("id"));
		List<?> comments = criteria.setMaxResults(size).list();
		return (List<CheckListComment>) comments;
	}

	
	
}
