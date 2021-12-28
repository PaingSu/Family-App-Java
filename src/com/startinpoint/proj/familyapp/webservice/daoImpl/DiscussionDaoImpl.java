package com.startinpoint.proj.familyapp.webservice.daoImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.startinpoint.proj.familyapp.webservice.dao.DiscussionDao;
import com.startinpoint.proj.familyapp.webservice.entity.Event;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyMember;
import com.startinpoint.proj.familyapp.webservice.entity.Topic;
import com.startinpoint.proj.familyapp.webservice.entity.TopicComment;
import com.startinpoint.proj.familyapp.webservice.entity.TopicMember;
import com.startinpoint.proj.familyapp.webservice.entity.enums.JoinStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.ScrollStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
/**
 * @author ThoonSandy
 * @version 1.0
 * @since 27-04-2018
 */
@Repository("discussionDao")
public class DiscussionDaoImpl implements DiscussionDao {
	
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
	public Topic createFamilyDiscussion(Topic discussion) throws FamilyAppWebserviceException {
		getSession().saveOrUpdate(discussion);
		return discussion;
	}



	@Override
	public void deleteFamilyDiscussion(Topic discussion) throws FamilyAppWebserviceException {
	
		if(discussion !=null){
			getSession().delete(discussion);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Topic> getViewAllFamilyDiscussion(List<Long>topicIdList,int size,Status inStatus) throws FamilyAppWebserviceException {
		List<Topic> getTopicList=new ArrayList<>();  
	    String hql="FROM Topic WHERE topicStatus=:status AND id  IN (:topicIdList)";
		Query query = getSession().createQuery(hql);
		query.setParameterList("topicIdList", topicIdList);
		query.setString("status", inStatus.toString());
		getTopicList = query.setMaxResults(size).list();
	
		if(getTopicList.isEmpty()){
			return Collections.EMPTY_LIST;
		}
		return getTopicList;
	}

	@Override
	public Topic findById(Long id) throws FamilyAppWebserviceException {
		Criteria criteria= sessionFactory.getCurrentSession().createCriteria(Topic.class);
		criteria.add(Restrictions.eq("id", id));
		criteria.add(Restrictions.eq("topicStatus", Status.A));
		Topic topic = (Topic) criteria.uniqueResult();
		return topic;
	}

	@Override
	public TopicComment createDiscussionComments(TopicComment discussionComments)
			throws FamilyAppWebserviceException {
		
		 getSession().saveOrUpdate(discussionComments);
		 getSession().refresh(discussionComments);
		 return discussionComments;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TopicComment> findAllCommentListByTopicId(Long topicId,int size) throws FamilyAppWebserviceException {	
		List<TopicComment> getCommentList=new ArrayList<>();
		Criteria c =getSession().createCriteria(TopicComment.class);
		c.add(Restrictions.eq("topic.id", topicId));
		//c.add(Restrictions.eq("commentStatus", Status.A));
		if(size != 0){
		c.setMaxResults(size);
		}
		c.addOrder(Order.desc("id")).list();
		getCommentList=c.list();
		return getCommentList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TopicComment> findRefreshCommentListByTopicId(Long topicId, String status, Long id, int size)
			throws FamilyAppWebserviceException {
		
		Criteria c=getSession().createCriteria(TopicComment.class);
		c.add(Restrictions.eq("topic.id", topicId));
		c.add(Restrictions.eq("commentStatus", Status.A));
		c.setMaxResults(size);	
		
		if(status.equals(ScrollStatus.AFTER.getId())){	
		c.add(Restrictions.ge("id", id));
		return c.addOrder(Order.desc("id")).list();			
		}
		else if(status.equals(ScrollStatus.BEFORE.getId())){
		c.add(Restrictions.le("id", id));	
		return c.addOrder(Order.asc("id")).list();				
		}else{
			return c.addOrder(Order.asc("id")).list();				
		}
    }

	@Override
	public TopicMember findByMemberId(Long id) throws FamilyAppWebserviceException {
		Criteria criteria= sessionFactory.getCurrentSession().createCriteria(TopicMember.class);	
		criteria.add(Restrictions.eq("memberId", id));
		criteria.add(Restrictions.eq("memberStatus", Status.A));
		TopicMember topicMember=(TopicMember) criteria.uniqueResult();	
		return topicMember;
	}

	@Override
	public TopicMember createTopicMember(TopicMember topicMember) throws FamilyAppWebserviceException {
		getSession().saveOrUpdate(topicMember);
	    return topicMember;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TopicMember> findAllTopicMemberList(Long memberId,Long topicId,Integer size) throws FamilyAppWebserviceException {
		Criteria criteria= sessionFactory.getCurrentSession().createCriteria(TopicMember.class);
		criteria.add(Restrictions.eq("memberId", memberId));		
		criteria.add(Restrictions.eq("topicId", topicId));
		criteria.add(Restrictions.eq("memberStatus", Status.A));
		if(size !=null){
        criteria.setMaxResults(size);
			}
	
		return (List<TopicMember>) criteria.list();
	}

	@Override
	public void updateFamilyDiscussionByStatus(Long topicId,List<Long> memberList, Status inStatus)
			throws FamilyAppWebserviceException {
		String hql="UPDATE TopicMember SET memberStatus=:inStatus WHERE topicId=:topicId AND memberId NOT IN (:memberList)";
		Query query=getSession().createQuery(hql);
		query.setLong("topicId", topicId);
		query.setString("inStatus", inStatus.toString());
		query.setParameterList("memberList", memberList);
		
		int rowCount=query.executeUpdate();
		System.out.println("Rows Updated Topic Member..."+rowCount);
		
	}

	@Override
	public void updateTopicMemberByStatus(Long topicId,Long topicMemberId, Status inStatus) throws FamilyAppWebserviceException {
		String hql="UPDATE TopicMember SET memberStatus=:inStatus WHERE memberId=:topicMemberId AND topicId=:topicId";
		Query query=getSession().createQuery(hql);
		query.setString("inStatus", inStatus.toString());
		query.setLong("topicMemberId", topicMemberId);
		query.setLong("topicId",topicId);
		int rowCount=query.executeUpdate();
		System.out.println("Rows Updated Topic Member..."+rowCount);
		
	}

	@Override
	public void updateTopicByStatusChange(Long topicId, Status inStatus) throws FamilyAppWebserviceException {
		String hql="UPDATE Topic SET topicStatus=:inStatus WHERE id=:topicId";
		Query query=getSession().createQuery(hql);
		query.setString("inStatus", inStatus.toString());
		query.setLong("topicId",topicId);
		
		int rowCount=query.executeUpdate();
		System.out.println("Rows Updated Topic..."+rowCount);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Topic> getViewAllTopicListByTopicTitle(List<Long> topicIdList, String keyWord) {

		List<Topic> getTopicList=new ArrayList<>();
		Criteria criteria=sessionFactory.getCurrentSession().createCriteria(Topic.class);	
		criteria.add(Restrictions.in("id", topicIdList));
		criteria.add(Restrictions.eq("topicStatus", Status.A));
		if(keyWord != null){
			criteria.add(Restrictions.ilike("topicTitle", "%"+ keyWord+ "%"));	
		}
		//criteria.addOrder(Order.desc("modifiedDate")).list();
		getTopicList=criteria.list();
	    //System.out.println("topic Lists in DaoImpl---"+getTopicList.size());
		return getTopicList;
	
	}

	@Override
	public void changeStatusALLTopicMemberLists(Long topicId, List<Long> memberIdList, Status inStatus) {
		String hql="UPDATE TopicMember SET memberStatus=:inStatus WHERE topicId=:topicId AND memberId IN (:memberIdList)";
		Query query=getSession().createQuery(hql);
		query.setLong("topicId", topicId);
		query.setString("inStatus", inStatus.toString());
		query.setParameterList("memberIdList", memberIdList);
		
		int rowCount=query.executeUpdate();
		System.out.println("Rows Updated Topic Member..."+rowCount);
		
	}

	@Override
	public void changeStatusALLTopicCommentMemberLists(Long topicId, Status inStatus) {
		String hql="UPDATE TopicComment SET commentStatus=:inStatus WHERE topicId=:topicId";
		Query query=getSession().createQuery(hql);
		query.setLong("topicId", topicId);
		query.setString("inStatus", inStatus.toString());
		
		
		int rowCount=query.executeUpdate();
		System.out.println("Rows Updated Topic Comment Member..."+rowCount);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TopicMember> findAllTopicMemberListByActive(Long memberId, Integer size)
			throws FamilyAppWebserviceException {
		
		List<?> list=getSession().createCriteria(TopicMember.class)
				.add(Restrictions.eq("memberId", memberId))
				.add(Restrictions.eq("memberStatus", Status.A))
				.setMaxResults(size)
				.list();
		
			return (List<TopicMember>) list;
	}

	@Override
	public TopicMember findByTopicMemberId(Long id) throws FamilyAppWebserviceException {
		List<?> list = getSession().createCriteria(TopicMember.class)
				.add(Restrictions.eq("id", id))
				.list();
		
		if(list.isEmpty()){
			return null;
		}

		return (TopicMember) list.get(0);
	}

	@Override
	public Topic findTopicById(Long id) throws FamilyAppWebserviceException {
		Criteria criteria= sessionFactory.getCurrentSession().createCriteria(Topic.class);
		criteria.add(Restrictions.eq("id", id));
		Topic topic = (Topic) criteria.uniqueResult();
		return topic;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findAllCommentCountList(Status status,List<Long> topicIdList) throws FamilyAppWebserviceException {
		List<Object[]> getTopicList=new ArrayList<>();  
	    String hql="SELECT count(*),topic.id FROM TopicComment WHERE commentStatus=:status AND topic.id IN (:topicIdList) group by topic.id";
		Query query = getSession().createQuery(hql);
		query.setParameterList("topicIdList", topicIdList);
		query.setString("status", status.toString());
		getTopicList = query.list();
	
		if(getTopicList.isEmpty()){
			return Collections.EMPTY_LIST;
		}
		return getTopicList;
	}

	@Override
	public void replaceFamilyIdInTopic(Long oldFamilyId, Long newFamilyId) throws FamilyAppWebserviceException {
		String hql="UPDATE Topic SET familyProfile.id=:newFamilyId WHERE familyProfile.id=:oldFamilyId";
		Query query=getSession().createQuery(hql);
		query.setLong("newFamilyId", newFamilyId);
		query.setLong("oldFamilyId", oldFamilyId);		
		query.executeUpdate();		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Topic> getViewAllTopicRefreshLoadmore(List<Long> topicIdList, Long topicId,ScrollStatus status, int size) {
		Criteria c=getSession().createCriteria(Topic.class);
		c.add(Restrictions.eq("topicStatus", Status.A));
		c.setMaxResults(size);	
		
		if(status.equals(ScrollStatus.AFTER.getId())){	
		c.add(Restrictions.ge("id", topicId));
	    c.addOrder(Order.desc("id"));			
		}
		else if(status.equals(ScrollStatus.BEFORE.getId())){
		c.add(Restrictions.le("id", topicId));	
		c.addOrder(Order.asc("id"));				
		}else{
		c.addOrder(Order.asc("id"));				
		}
		return c.list();
	}

	@Override
	public void updateTopicStatusByFamilyIdCreatorId(Long familyId, Long topicCreatorId, Status status)
			throws FamilyAppWebserviceException {
		String hql="UPDATE Topic SET topicStatus=:status WHERE familyProfile.id=:familyId AND topicCreator=:topicCreatorId";
		Query query=getSession().createQuery(hql);
		query.setParameter("status", status);
		query.setParameter("familyId", familyId);
		query.setParameter("topicCreatorId", topicCreatorId);
		query.executeUpdate();	
	}

}

