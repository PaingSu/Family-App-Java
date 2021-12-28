package com.startinpoint.proj.familyapp.webservice.serviceImpl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.startinpoint.proj.familyapp.webservice.dao.CalendarEventDao;
import com.startinpoint.proj.familyapp.webservice.entity.Event;
import com.startinpoint.proj.familyapp.webservice.entity.EventMember;
import com.startinpoint.proj.familyapp.webservice.entity.enums.EventAcceptanceStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.CalendarEventService;

/**
 * @author ThoonSandy
 * @since 07-05-2018
 *
 */
@Service("calendarEventService")
public class CalendarEventServiceImpl implements CalendarEventService {
	
	protected final Log logger=LogFactory.getLog(this.getClass());
	
	@Autowired
	CalendarEventDao calendarEventDao;
	

	@Override
	public Event saveorUpdateCalendarEvent(Event calendar_event) throws FamilyAppWebserviceException {
		try{
			return calendarEventDao.createCalendarEvent(calendar_event);
			}
			catch(FamilyAppWebserviceException e){
				throw new FamilyAppWebserviceException(e.getMessage(),e);
			}
	}

	@Override
	public List<Event> getViewAllEventListByChoosedDateAndEventTitle(List<Long>eventIdList,Date choosedDate,String eventTitle) throws FamilyAppWebserviceException {
		try{
		return calendarEventDao.getViewAllEventListByChoosedDateAndEventTitle(eventIdList,choosedDate,eventTitle);
		}
		catch(FamilyAppWebserviceException e){
			throw new FamilyAppWebserviceException(e.getMessage(),e);
		}
	}

	@Override
	public Event findByCalendarEventId(Long id) throws FamilyAppWebserviceException {
		try{
		return calendarEventDao.findByCalendarEventId(id);
		}
		catch(FamilyAppWebserviceException e){
			throw new FamilyAppWebserviceException(e.getMessage(),e);
		}
	}

	@Override
	public void deleteCalendarEvent(Long eventId,Status status) throws FamilyAppWebserviceException {
		   try{
			   calendarEventDao.deleteCalendarEvent(eventId,status);
			}
			catch(FamilyAppWebserviceException e){
				throw new FamilyAppWebserviceException(e.getMessage(),e);
			}
	}

	@Override
	public List<Event> getViewInboxAllEventLists() throws FamilyAppWebserviceException {
		try{
			return calendarEventDao.getViewInboxAllEventLists();
			}
			catch(FamilyAppWebserviceException e){
				throw new FamilyAppWebserviceException(e.getMessage(),e);
			}
	}

	@Override
	public EventMember findByScheduleEventId(Long memberId,Long eventId) throws FamilyAppWebserviceException {
		
		try{
			return calendarEventDao.findByScheduleEventId(memberId,eventId);
			}
			catch(FamilyAppWebserviceException e){
				throw new FamilyAppWebserviceException(e.getMessage(),e);
			}
	}

	@Override
	public EventMember saveorUpdateEventMember(EventMember event_member) throws FamilyAppWebserviceException {
	
		try{
			return calendarEventDao.saveorUpdateEventMember(event_member);
			}
			catch(FamilyAppWebserviceException e){
				throw new FamilyAppWebserviceException(e.getMessage(),e);
			}
	}

	@Override
	public EventMember findByEventId(Long eventId) throws FamilyAppWebserviceException {
		
		try{
			return calendarEventDao.findByEventId(eventId);
			}
			catch(FamilyAppWebserviceException e){
				throw new FamilyAppWebserviceException(e.getMessage(),e);
			}
	}

	@Override
	public List<EventMember> getAllEventMemberLists(Long memberId) throws FamilyAppWebserviceException {
	
		try{
			return calendarEventDao.getAllEventMemberLists(memberId);
			}
			catch(FamilyAppWebserviceException e){
				throw new FamilyAppWebserviceException(e.getMessage(),e);
			}
	}

	@Override
	public void updateAcceptanceStatusForEventMember(EventAcceptanceStatus status, Long eventId, Long memberId)
			throws FamilyAppWebserviceException {
		try{
			 calendarEventDao.updateAcceptanceStatusForEventMember(status, eventId, memberId);;
			}
			catch(FamilyAppWebserviceException e){
				throw new FamilyAppWebserviceException(e.getMessage(),e);
			}
		
	}

	@Override
	public void changeStatusALLEventMemberLists(Long eventId, List<Long> memberIdList, Status status)
			throws FamilyAppWebserviceException {
		try{
			 calendarEventDao.changeStatusALLEventMemberLists(eventId, memberIdList, status);
			}
			catch(FamilyAppWebserviceException e){
				throw new FamilyAppWebserviceException(e.getMessage(),e);
			}
		
		
	}

	@Override
	public List<EventMember> findAllEventMemberLists(Long memberId,Long eventId) throws FamilyAppWebserviceException {
		try{
			 return calendarEventDao.findAllEventMemberLists(memberId,eventId);
			}
			catch(FamilyAppWebserviceException e){
				throw new FamilyAppWebserviceException(e.getMessage(),e);
			}
	}

	@Override
	public void replaceFamilyIdInEvent(Long oldFamilyId, Long newFamilyId) throws FamilyAppWebserviceException {
		calendarEventDao.replaceFamilyIdInEvent(oldFamilyId,newFamilyId);
	}

	@Override
	public List<Object[]> getEventDateListByChoosedMonthAndChoosedYear(List<Long> eventIdList, Date dateFromStartMonth,
			Date dateFromEndMonth) throws FamilyAppWebserviceException {
		try{
			return calendarEventDao.getEventDateListByChoosedMonthAndChoosedYear(eventIdList, dateFromStartMonth, dateFromEndMonth);
			}
			catch(FamilyAppWebserviceException e){
				throw new FamilyAppWebserviceException(e.getMessage(),e);
			}
	}

	@Override
	public void updateEventStatusByFamilyIdCreatorId(Long familyId, Long eventCreatorId, Status status)
			throws FamilyAppWebserviceException {
		calendarEventDao.updateEventStatusByFamilyIdCreatorId(familyId,eventCreatorId,status);
	}


}
