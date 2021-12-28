package com.startinpoint.proj.familyapp.webservice.daoImpl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.startinpoint.proj.familyapp.webservice.dao.CheckListDao;
import com.startinpoint.proj.familyapp.webservice.entity.CheckList;
import com.startinpoint.proj.familyapp.webservice.entity.CheckListMember;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyAppConst;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyMember;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyProfile;
import com.startinpoint.proj.familyapp.webservice.entity.enums.JoinStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.ScrollStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @author nankhinmhwe
 * @since 06/05/2018
 *
 */
@Repository("checkListDao")
public class CheckListDaoImpl implements CheckListDao {
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
	public CheckList saveOrUpdateCheckList(CheckList checkList) throws FamilyAppWebserviceException {
		getSession().saveOrUpdate(checkList);
		return checkList;
	}

	@Override
	public CheckList findById(Long id) throws FamilyAppWebserviceException {
		Criteria criteria = getSession().createCriteria(CheckList.class);
		criteria.add(Restrictions.eq("id", id));
		criteria.add(Restrictions.eq("status", Status.A));
		CheckList checkList = (CheckList) criteria.uniqueResult();
		return checkList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CheckList> getCheckListByFamilyIdUserId(Long familyId, String checkListName, int size,Long userId)
			throws FamilyAppWebserviceException {
		//Get checklist by related user id
		DetachedCriteria dc = DetachedCriteria.forClass(CheckList.class);
		dc.add(Restrictions.eq("status",Status.A));
		if(familyId !=null){
			dc.add(Restrictions.eq("familyId", familyId));
		}
		if(checkListName != null && !checkListName.trim().isEmpty()){
			dc.add(Restrictions.ilike("checkListName", checkListName,MatchMode.ANYWHERE));
		}
		
		DetachedCriteria subquery = DetachedCriteria.forClass(CheckListMember.class);
		subquery.add(Restrictions.eq("status", Status.A));
		subquery.add(Restrictions.eq("userId", userId));
		subquery.setProjection(Property.forName("checklist.id"));
		dc.add(Subqueries.propertyIn("id", subquery));
		dc.addOrder(Order.desc("createdDate"));
		
		List<CheckList> list = dc.getExecutableCriteria(getSession()).setMaxResults(size).list();
		return list;		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CheckList> getLoadMoreOrRefreshCheckList(Long familyId, String checkListName, int checklistSize,
			Long id, ScrollStatus scrollStatus,Long userId) throws FamilyAppWebserviceException {
		
		// Get checklist by related user id
		DetachedCriteria dc = DetachedCriteria.forClass(CheckList.class);
		dc.add(Restrictions.eq("status", Status.A));
		if (familyId != null) {
			dc.add(Restrictions.eq("familyId", familyId));
		}
		if (checkListName != null && !checkListName.trim().isEmpty()) {
			dc.add(Restrictions.ilike("checkListName", checkListName, MatchMode.ANYWHERE));
		}
		if(scrollStatus == ScrollStatus.AFTER){
			dc.add(Restrictions.gt("id", id));
		}
		else{
			dc.add(Restrictions.lt("id", id));
		}

		DetachedCriteria subquery = DetachedCriteria.forClass(CheckListMember.class);
		subquery.add(Restrictions.eq("status", Status.A));
		subquery.add(Restrictions.eq("userId", userId));
		subquery.setProjection(Property.forName("checklist.id"));
		dc.add(Subqueries.propertyIn("id", subquery));
		dc.addOrder(Order.desc("createdDate"));

		List<CheckList> list = dc.getExecutableCriteria(getSession()).setMaxResults(checklistSize).list();
		return list;		
	}

	@Override
	public void replaceFamilyIdInCheckList(Long oldFamilyId, Long newFamilyId) throws FamilyAppWebserviceException {
		String hql="UPDATE CheckList SET familyId=:newFamilyId WHERE familyId=:oldFamilyId";
		Query query=getSession().createQuery(hql);
		query.setLong("newFamilyId", newFamilyId);
		query.setLong("oldFamilyId", oldFamilyId);		
		query.executeUpdate();		
	}


}
