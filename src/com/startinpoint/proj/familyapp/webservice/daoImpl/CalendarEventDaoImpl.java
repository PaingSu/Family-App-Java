package com.startinpoint.proj.familyapp.webservice.daoImpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.startinpoint.proj.familyapp.util.CalendarUtil;
import com.startinpoint.proj.familyapp.webservice.dao.CalendarEventDao;
import com.startinpoint.proj.familyapp.webservice.entity.AlertSchedule;
import com.startinpoint.proj.familyapp.webservice.entity.Event;
import com.startinpoint.proj.familyapp.webservice.entity.EventMember;
import com.startinpoint.proj.familyapp.webservice.entity.Topic;
import com.startinpoint.proj.familyapp.webservice.entity.enums.EventAcceptanceStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * @author ThoonSandy
 * @since 07-05-2018
 *
 */
@Repository("calendarEventDao")
public class CalendarEventDaoImpl implements CalendarEventDao {
	
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
	public Event createCalendarEvent(Event calendar_event) throws FamilyAppWebserviceException {

		getSession().saveOrUpdate(calendar_event);
		//getSession().refresh(calendar_event);
		return calendar_event;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> getViewAllEventListByChoosedDateAndEventTitle(List<Long> eventIdList,Date choosedDate,String eventTitle)
			throws FamilyAppWebserviceException {		
		Criteria cirte = getSession().createCriteria(Event.class);
		if(choosedDate != null){
		Date startDate=CalendarUtil.getStartDateTime(choosedDate);
		//System.out.println("start date..."+startDate);
		Date endDate = CalendarUtil.getEndDateTime(choosedDate);
		//System.out.println("end date..."+endDate);	
		
		Criterion sDate=Restrictions.between("startDateTime",startDate,endDate);
		Criterion eDate=Restrictions.between("endDateTime", startDate, endDate);
		LogicalExpression orExp=Restrictions.or(sDate, eDate);
		cirte.add(orExp);
		}
		else if(eventTitle != null){
			cirte.add(Restrictions.ilike("eventTitle","%"+eventTitle+"%"));	
		}
		cirte.add(Restrictions.in("id", eventIdList));
		cirte.add(Restrictions.eq("eventStatus", Status.A));
		return cirte.addOrder(Order.desc("id")).list();
	}

	@Override
	public Event findByCalendarEventId(Long id) {	
		List<?> list = getSession().createCriteria(Event.class)
				.add(Restrictions.eq("id", id))
				.list();
		if(list.size() <= 0){
			return null;
		}

		return (Event)list.get(0);
	}

	@Override
	public void deleteCalendarEvent(Long eventId,Status status) throws FamilyAppWebserviceException {
			/*if(calendarEvent !=null){
			getSession().delete(calendarEvent);}*/
		String hql="UPDATE Event SET eventStatus=:status WHERE id=:eventId";
		Query query=getSession().createQuery(hql);
		query.setString("status", status.toString());
		query.setLong("eventId",eventId);
		
		int rowCount=query.executeUpdate();
		System.out.println("Rows Updated Event..."+rowCount);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> getViewInboxAllEventLists() throws FamilyAppWebserviceException {
		
		List<?> list = getSession().createCriteria(Event.class)
				.list();
		
		return (List<Event>) list;
	}

	@Override
	public EventMember findByScheduleEventId(Long memberId,Long eventId) throws FamilyAppWebserviceException {
		
		List<?> list = getSession().createCriteria(EventMember.class)
				.add(Restrictions.eq("userProfile.id", memberId))
				.add(Restrictions.eq("event.id", eventId))
				.add(Restrictions.eq("memberStatus", Status.A))
				.list();
		if(list.size() <= 0){
			return null;
		}

		return (EventMember) list.get(0);
	}

	@Override
	public EventMember saveorUpdateEventMember(EventMember event_member) throws FamilyAppWebserviceException {
		getSession().saveOrUpdate(event_member);
		return event_member;
	}

	@Override
	public EventMember findByEventId(Long eventId) throws FamilyAppWebserviceException {
		
		List<?> list = getSession().createCriteria(EventMember.class)
				.add(Restrictions.eq("event.id", eventId))
				.list();
		if(list.size() <= 0){
			return null;
		}

		return (EventMember) list.get(0);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventMember> getAllEventMemberLists(Long memberId) throws FamilyAppWebserviceException {
		List<?> list=getSession().createCriteria(EventMember.class)
				.add(Restrictions.eq("userProfile.id", memberId))
				.add(Restrictions.eq("memberStatus", Status.A))
				.list();
		return (List<EventMember>) list;
	}

	@Override
	public void updateAcceptanceStatusForEventMember(EventAcceptanceStatus status, Long eventId, Long memberId)
			throws FamilyAppWebserviceException {
	
		String hql="UPDATE EventMember SET eventAcceptanceStatus=:status WHERE event.id=:eventId AND id=:memberId";
		Query query=getSession().createQuery(hql);
		query.setString("status", status.toString());
		query.setLong("eventId", eventId);
		query.setLong("memberId",memberId);
		query.executeUpdate();
		int rowCount=query.executeUpdate();
		System.out.println("Rows Updated Event Member..."+rowCount);
		
	}

	@Override
	public void changeStatusALLEventMemberLists(Long eventId, List<Long> memberIdList, Status status)
			throws FamilyAppWebserviceException {
		String hql="UPDATE EventMember SET memberStatus=:status WHERE event.id =:eventId AND userProfile.id IN (:memberIdList)";
		Query query=getSession().createQuery(hql);
		System.out.println("query result.."+hql);
		query.setString("status", status.toString());
		query.setLong("eventId", eventId);
		query.setParameterList("memberIdList", memberIdList);
		
		int rowCount=query.executeUpdate();
		System.out.println("Rows Updated Event Member..."+rowCount);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventMember> findAllEventMemberLists(Long memberId,Long eventId) throws FamilyAppWebserviceException {
		List<?> list=getSession().createCriteria(EventMember.class)	
				.add(Restrictions.eq("userProfile.id", memberId))
				.add(Restrictions.eq("event.id", eventId))
				.list();
		return (List<EventMember>) list;
	}

	@Override
	public void replaceFamilyIdInEvent(Long oldFamilyId, Long newFamilyId) throws FamilyAppWebserviceException {
		String hql="UPDATE Event SET familyProfile.id=:newFamilyId WHERE familyProfile.id=:oldFamilyId";
		Query query=getSession().createQuery(hql);
		query.setLong("newFamilyId", newFamilyId);
		query.setLong("oldFamilyId", oldFamilyId);		
		query.executeUpdate();		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getEventDateListByChoosedMonthAndChoosedYear(List<Long> eventIdList, Date dateFromStartMonth,
			Date dateFromEndMonth) throws FamilyAppWebserviceException {
		List<Object[]> getEventDateList=new ArrayList<>();  
		if(dateFromStartMonth != null && dateFromEndMonth!=null){
			String hql="SELECT startDateTime,endDateTime FROM Event WHERE eventStatus=:status AND startDateTime >= :dateFromStartMonth AND endDateTime < :dateFromEndMonth AND id IN (:eventIdList) group by startDateTime,endDateTime ";
			Query query = getSession().createQuery(hql);
			query.setParameterList("eventIdList", eventIdList);
			query.setString("status", Status.A.toString());
			query.setParameter("dateFromStartMonth", dateFromStartMonth);
			query.setParameter("dateFromEndMonth", dateFromEndMonth);
			//query.uniqueResult();	
			getEventDateList=query.list();		
		}
	   System.out.println("event date list in daoImpl..."+getEventDateList.size());
		if(getEventDateList.isEmpty()){
			return Collections.EMPTY_LIST;
		}
		return getEventDateList;
	}

	@Override
	public void updateEventStatusByFamilyIdCreatorId(Long familyId, Long eventCreatorId, Status status)
			throws FamilyAppWebserviceException {
		String hql="UPDATE Event SET eventStatus=:status WHERE familyProfileId=:familyId AND invitee=:eventCreatorId";
		Query query=getSession().createQuery(hql);
		query.setParameter("status", status);
		query.setParameter("familyId", familyId);
		query.setParameter("topicCreatorId", eventCreatorId);
		query.executeUpdate();	
	}

}
