package com.startinpoint.proj.familyapp.webservice.dao;

import java.util.Date;
import java.util.List;

import com.startinpoint.proj.familyapp.webservice.entity.AlertSchedule;
import com.startinpoint.proj.familyapp.webservice.entity.Event;
import com.startinpoint.proj.familyapp.webservice.entity.EventMember;
import com.startinpoint.proj.familyapp.webservice.entity.enums.EventAcceptanceStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * @author ThoonSandy
 * @version 1.0
 * @since 07-05-2018
 *
 */
public interface CalendarEventDao {
	
	public Event createCalendarEvent(Event calendar_event)throws FamilyAppWebserviceException;
	
	public List<Event> getViewAllEventListByChoosedDateAndEventTitle(List<Long>eventIdList,Date choosedDate,String eventTitle)throws FamilyAppWebserviceException;
	
	public List<Object[]> getEventDateListByChoosedMonthAndChoosedYear(List<Long>eventIdList,Date dateFromStartMonth,Date dateFromEndMonth)throws FamilyAppWebserviceException;
	
	public List<Event>getViewInboxAllEventLists()throws FamilyAppWebserviceException;
	
    public Event findByCalendarEventId(Long id)throws FamilyAppWebserviceException;
    
    public void deleteCalendarEvent(Long eventId,Status status)throws FamilyAppWebserviceException;
    
    public EventMember findByScheduleEventId(Long memberId,Long eventId)throws FamilyAppWebserviceException;
    
    public EventMember saveorUpdateEventMember(EventMember event_member)throws FamilyAppWebserviceException;
    
    public EventMember findByEventId(Long eventId)throws FamilyAppWebserviceException;
    
    public List<EventMember>getAllEventMemberLists(Long memberId)throws FamilyAppWebserviceException;
    
    public List<EventMember>findAllEventMemberLists(Long memberId,Long eventId)throws FamilyAppWebserviceException;
    
    public void updateAcceptanceStatusForEventMember(EventAcceptanceStatus status,Long eventId,Long memberId)throws FamilyAppWebserviceException;
    
    public void changeStatusALLEventMemberLists(Long eventId,List<Long>memberIdList,Status status)throws FamilyAppWebserviceException;

	public void replaceFamilyIdInEvent(Long oldFamilyId, Long newFamilyId)throws FamilyAppWebserviceException;

	public void updateEventStatusByFamilyIdCreatorId(Long familyId, Long eventCreatorId, Status status)throws FamilyAppWebserviceException;
    
 

}
