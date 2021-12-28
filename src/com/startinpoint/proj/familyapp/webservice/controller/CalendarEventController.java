package com.startinpoint.proj.familyapp.webservice.controller;


import java.text.ParseException;
import java.text.SimpleDateFormat;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.startinpoint.proj.familyapp.util.CalendarUtil;
import com.startinpoint.proj.familyapp.util.JsonUtil;
import com.startinpoint.proj.familyapp.util.RequestUtil;
import com.startinpoint.proj.familyapp.util.StringUtils;
import com.startinpoint.proj.familyapp.webservice.entity.AlertSchedule;
import com.startinpoint.proj.familyapp.webservice.entity.Event;
import com.startinpoint.proj.familyapp.webservice.entity.EventMember;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyAppConst;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyMember;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyProfile;
import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;
import com.startinpoint.proj.familyapp.webservice.entity.enums.EventAcceptanceStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.JoinStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.AlertScheduleService;
import com.startinpoint.proj.familyapp.webservice.service.CalendarEventService;
import com.startinpoint.proj.familyapp.webservice.service.FamilyMemberService;
import com.startinpoint.proj.familyapp.webservice.service.FamilyProfileService;
import com.startinpoint.proj.familyapp.webservice.service.UserService;



/**
 * @author ThoonSandy
 * @since 07-05-2018
 * @version 1.0
 */
@Transactional
@Controller
public class CalendarEventController {
	
	protected final Log logger=LogFactory.getLog(this.getClass());
	
	@Autowired 
	CalendarEventService calendarEventService;
	
	@Autowired
	AlertScheduleService alertScheduleService;
	
	@Autowired
	FamilyProfileService familyProfileService;
	
	@Autowired
	FamilyMemberService familyMemberService;
	
	@Autowired
	UserService userService;
	
	
	/**
	 * Create Event
	 * @param calendarEvent
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException 
	 * @throws ParseException 
	 */
	@RequestMapping(value="api/auth/schedules/create",method=RequestMethod.POST)
	public ResponseEntity<?> createCalendarEvent(@Valid @RequestBody String EventData,HttpServletRequest request)throws FamilyAppWebserviceException, JSONException, ParseException {
	logger.info("save calendar event....");
	
	String useremail=RequestUtil.getEmailFromAuthentication();
	UserProfile userProfile=userService.findByEmail(useremail);
	if(userProfile ==null){
		logger.info(FamilyAppConst.INVALID_USER_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
	}
	Long userId=userProfile.getId();
	
	JSONObject jsonObj=new JSONObject(EventData);
	//System.out.println("json object..."+jsonObj);
	
	if(!jsonObj.has("eventTitle")){
		logger.info("Event Title is Required.");
		throw new FamilyAppWebserviceException("Event Title is Required.");
	}
	if(!jsonObj.has("description")){
		logger.info("Event Description is Required.");
		throw new FamilyAppWebserviceException("Event Description is Required.");
	}
	if(!jsonObj.has("location")){
		logger.info("Event location is Required.");
		throw new FamilyAppWebserviceException("Event location is Required.");
	}
	if(!jsonObj.has("startDateTime")){
		logger.info("Event Start Date is Required.");
		throw new FamilyAppWebserviceException("Event Start Date is Required.");
	}
	if(!jsonObj.has("endDateTime")){
		logger.info("Event End Date is Required.");
		throw new FamilyAppWebserviceException("Event End Date is Required.");
	}
	String eventTitle=jsonObj.getString("eventTitle");
	String description=jsonObj.getString("description");
	String location=jsonObj.getString("location");
	String startDateTime=jsonObj.getString("startDateTime");
	String endDateTime=jsonObj.getString("endDateTime");
	SimpleDateFormat sdf=new SimpleDateFormat(FamilyAppConst.CALENDAR_DATETIMEZONE_FORMAT);
	sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	Date sDate=(Date)sdf.parse(startDateTime);
	Date eDate=(Date)sdf.parse(endDateTime);	
	//System.out.println("start Date..."+sDate+"  "+eDate);
	Long alertScheduleId=jsonObj.getLong("alertScheduleId");
	Long familyProfileId=null;
	FamilyProfile familyProfile=null;
	
	//Creating Event
	Event calendarEvent = new Event();
    calendarEvent.setEventTitle(eventTitle);
	calendarEvent.setDescription(description);
	calendarEvent.setLocation(location);
	calendarEvent.setStartDateTime(sDate);
	calendarEvent.setEndDateTime(eDate);
	calendarEvent.setInvitee(userId);
	calendarEvent.setCreatedDate(new Date());
	calendarEvent.setUpdatedDate(new Date());
	calendarEvent.setEventStatus(Status.A);
	
	AlertSchedule alertSchedule=alertScheduleService.findByAlertScheduleId(alertScheduleId);
	if(alertSchedule==null){
		logger.info(FamilyAppConst.INVALID_ALERT_SCHEDULE_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_ALERT_SCHEDULE_ID);
	}
   alertSchedule.setId(alertSchedule.getId());
   calendarEvent.setAlertSchedule(alertSchedule);
	
   if(!jsonObj.has("familyProfileId")){
	    familyProfile=familyProfileService.getPersonalFamilyByCreatorId(userId);
		if(familyProfile ==null){
		 	logger.info(FamilyAppConst.NO_EXIST_PERSONAL_PROFILE);
		 	throw new FamilyAppWebserviceException(FamilyAppConst.NO_EXIST_PERSONAL_PROFILE);
		}
		else{
	    calendarEvent.setFamilyProfileId(familyProfile.getId());
		}
	}
	else
	{
	 familyProfileId=jsonObj.getLong("familyProfileId");
	//check family profile active whether exist or not  by familyProfileId
    familyProfile = familyProfileService.findByFamilyProfileId(familyProfileId);
		if (familyProfile == null) {
			logger.info(FamilyAppConst.INVALID_FAMILY_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_ID);
		}
				 	
	}
	 calendarEvent.setFamilyProfileId(familyProfileId);	 
	 calendarEvent.setFamilyProfile(familyProfile);	 
	
	//add member Lists for same family member when save event
	List<FamilyMember> family_memberLists=familyProfile.getFamilyMemberList();//get family member lists by familyProfielId
	System.out.println("member Lists=>"+family_memberLists.size());
	List<EventMember>getMemberList=new ArrayList<EventMember>();
	Boolean flag=false;
	if(family_memberLists.size() > 0)
	{
	    for (FamilyMember family_member : family_memberLists) {	
		if(userId == family_member.getMember().getId()){
			flag=true;
			break;
		}
		else if(family_member.getRequestStatus() != (JoinStatus.JOINED)){
			logger.info(FamilyAppConst.NOT_FAMILY_MEMBER);
			throw new FamilyAppWebserviceException(FamilyAppConst.NOT_FAMILY_MEMBER);
		}
			
	    }
		if(flag == false ){
			System.out.println("member who aren't same as event member");
			logger.info(FamilyAppConst.INVALID_FAMILY_MEMBER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_MEMBER_ID);
		}
		for (FamilyMember familyMember : family_memberLists) {
			EventMember event_member=new EventMember();
			event_member.setCreatedDate(new Date());
			event_member.setEventAcceptanceStatus(EventAcceptanceStatus.ACCEPTED);
			event_member.setUserProfile(familyMember.getMember());
			event_member.setAlertSchedule(alertSchedule);
			event_member.setEvent(calendarEvent);
			event_member.setUpdatedDate(new Date());
			event_member.setIsView(false);
			event_member.setMemberStatus(Status.A);
			getMemberList.add(event_member);
		}
		
	}	
	calendarEvent.setEventMemberLists(getMemberList);
	calendarEventService.saveorUpdateCalendarEvent(calendarEvent);	
	
	// to reply for user member Lists
	List<EventMember>getToReplyEventMemberList=calendarEvent.getEventMemberLists(); //get topic member lists		
	List<UserProfile>getReplyToMemberList=new ArrayList<>();//create empty list for reply user member lists
	for (EventMember eventMember : getToReplyEventMemberList) {
	getReplyToMemberList.add(eventMember.getUserProfile());
	}
	calendarEvent.setMemberLists(getReplyToMemberList);
	return new ResponseEntity<Event>(calendarEvent,HttpStatus.OK);
		
	}
	
	/**
	 * Edit Event
	 * @param scheduleId
	 * @param calendar_event
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException 
	 * @throws ParseException 
	 */
	@RequestMapping(value="api/auth/schedules/edit/{scheduleId}",method=RequestMethod.PUT,produces="application/json")
	public ResponseEntity<?>editCalendarEvent(@PathVariable ("scheduleId") Long scheduleId,@RequestBody String calendarEvent,HttpServletRequest request,HttpServletResponse response)throws FamilyAppWebserviceException, JSONException, ParseException{
	logger.info("update calendar event.....");
	
	String useremail=RequestUtil.getEmailFromAuthentication();
	UserProfile userProfile=userService.findByEmail(useremail);
	if(userProfile ==null){
		logger.info(FamilyAppConst.INVALID_USER_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
	}
	Long userId=userProfile.getId();

	JSONObject jsonObj=new JSONObject(calendarEvent);
	//System.out.println("json object..."+jsonObj);
	if(!jsonObj.has("eventTitle")){
		logger.info("Event Title is Required.");
		throw new FamilyAppWebserviceException("Event Title is Required.");
	}
	if(!jsonObj.has("description")){
		logger.info("Event Description is Required.");
		throw new FamilyAppWebserviceException("Event Description is Required.");
	}
	if(!jsonObj.has("location")){
		logger.info("Event location is Required.");
		throw new FamilyAppWebserviceException("Event location is Required.");
	}
	if(!jsonObj.has("startDateTime")){
		logger.info("Event Start Date is Required.");
		throw new FamilyAppWebserviceException("Event Start Date is Required.");
	}
	if(!jsonObj.has("endDateTime")){
		logger.info("Event End Date is Required.");
		throw new FamilyAppWebserviceException("Event End Date is Required.");
	}
	String eventTitle=jsonObj.getString("eventTitle");
	System.out.println("event title---"+eventTitle);
	String description=jsonObj.getString("description");
	String location=jsonObj.getString("location");
	String startDateTime=jsonObj.getString("startDateTime");
	String endDateTime=jsonObj.getString("endDateTime");
	SimpleDateFormat sdf=new SimpleDateFormat(FamilyAppConst.CALENDAR_DATETIME_FORMAT);
	Date sDate=(Date)sdf.parse(startDateTime);
	Date eDate=(Date)sdf.parse(endDateTime);
	//Long familyProfileId=jsonObj.getLong("familyProfileId");
	
	Event editCalendarEvent=calendarEventService.findByCalendarEventId(scheduleId);
	if(editCalendarEvent ==null){
		logger.info(FamilyAppConst.INVALID_EVENT_SCHEDULE_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_EVENT_SCHEDULE_ID);
	}
	else if(editCalendarEvent.getEventStatus().equals(Status.I)){
		logger.info(FamilyAppConst.INACTIVE_EVENT_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INACTIVE_EVENT_ID);
	}
	Long invitee=editCalendarEvent.getInvitee();
	if(invitee.equals(userId))
	{ 	 //check whether same or not invitee and userId 
	editCalendarEvent.setUpdatedDate(new Date());
	editCalendarEvent.setEventTitle(eventTitle);
	editCalendarEvent.setDescription(description);
	editCalendarEvent.setLocation(location);
	editCalendarEvent.setStartDateTime(sDate);
	editCalendarEvent.setEndDateTime(eDate);	
	editCalendarEvent.setInvitee(userId);
	//editCalendarEvent.setFamilyProfileId(familyProfileId);
	//edit_CalendarEvent.setFamilyProfile(calendar_event.getFamilyProfile());	
	calendarEventService.saveorUpdateCalendarEvent(editCalendarEvent);
	
	}else{
		logger.info(FamilyAppConst.INVALID_EVENT_INVITEE_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_EVENT_INVITEE_ID);
	}

	String editCalendarEventJson=JsonUtil.pojoToJson(editCalendarEvent);	
	return new ResponseEntity<>(editCalendarEventJson,HttpStatus.OK);
	
	}
	/**
	 * Edit Alert Schedule of Event
	 * @param calendarEvent
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 * @throws ParseException
	 */
	@RequestMapping(value="api/auth/schedules/alert/edit",method=RequestMethod.PUT,produces="application/json")
	public ResponseEntity<?>editAlertScheduleEvent(@RequestBody String calendarEvent,HttpServletRequest request,HttpServletResponse response)throws FamilyAppWebserviceException, JSONException, ParseException{
	logger.info("update calendar alert schedule event.....");
	
	String useremail=RequestUtil.getEmailFromAuthentication();
	UserProfile userProfile=userService.findByEmail(useremail);
	if(userProfile ==null){
		logger.info(FamilyAppConst.INVALID_USER_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
	}
	Long userId=userProfile.getId();

	JSONObject jsonObj=new JSONObject(calendarEvent);
	//System.out.println("json object..."+jsonObj);
	Long eventId=jsonObj.getLong("eventId");
	//System.out.println("event Id---"+eventId);
	Long alertScheduleId=jsonObj.getLong("alertScheduleId");
	//System.out.println("alert schedule id---"+alertScheduleId);

	AlertSchedule alertSchedule=alertScheduleService.findByAlertScheduleId(alertScheduleId);
	if(alertSchedule==null){
		logger.info(FamilyAppConst.INVALID_ALERT_SCHEDULE_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_ALERT_SCHEDULE_ID);
	}
	List<EventMember> getEventMemberList=calendarEventService.findAllEventMemberLists(userId, eventId);
	if(getEventMemberList.size() > 0){
		Boolean flag=false;
		for (EventMember eventMember : getEventMemberList) {
			Event event=eventMember.getEvent();
			if(event ==null){
				logger.info(FamilyAppConst.INVALID_EVENT_SCHEDULE_ID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_EVENT_SCHEDULE_ID);
			}
			else if(event.getEventStatus().equals(Status.I)){
				logger.info(FamilyAppConst.INACTIVE_EVENT_ID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INACTIVE_EVENT_ID);
			}
			else if(eventMember.getMemberStatus().equals(Status.A))
			{
				flag=true;
				break;
			}
			
		}//end for loop
			if(flag==false){
				logger.info(FamilyAppConst.INACTIVE_EVENT_MEMBER);
				throw new FamilyAppWebserviceException(FamilyAppConst.INACTIVE_EVENT_MEMBER);
			}
		for (EventMember eventMember : getEventMemberList) {	
		alertSchedule.setId(alertSchedule.getId());
		eventMember.setAlertSchedule(alertSchedule);
		calendarEventService.saveorUpdateEventMember(eventMember);	//change alert schedule update to table
		}
		
	}//end if 
	else{
		return new ResponseEntity<>(StringUtils.responseString(FamilyAppConst.INVALID_EVENT_AND_EVENT_MEMBERID),HttpStatus.OK);
	}
	 //change object in the event member Lists to user member Lists for reply
	for (EventMember eventMember : getEventMemberList) {
		List<UserProfile> getReplyToMemberList = new ArrayList<>();
		getReplyToMemberList.add(eventMember.getUserProfile());
    }
	return new ResponseEntity<>(getEventMemberList,HttpStatus.OK);
	}
	
	/**
	 *  Update Invitation Acceptance's Status
	 * @param eventId
	 * @param status
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 * @throws ParseException
	 */
	@RequestMapping(value="api/auth/schedules/update",method=RequestMethod.PUT,produces="application/json")
	public ResponseEntity<?>editEventAcceptanceStatus(@RequestParam("eventId") Long eventId,
			@RequestParam("status") EventAcceptanceStatus status,HttpServletRequest request,HttpServletResponse response)throws FamilyAppWebserviceException, JSONException, ParseException{
	logger.info("update event acceptance status ....");
	
	String useremail=RequestUtil.getEmailFromAuthentication();
	UserProfile userProfile=userService.findByEmail(useremail);
	if(userProfile ==null){
		logger.info(FamilyAppConst.INVALID_USER_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
	}
	Long userId=userProfile.getId();
	Event event=null;
    List<EventMember> getEventMemberList=calendarEventService.findAllEventMemberLists(userId, eventId);//get all member list from DB	
	if(getEventMemberList.size() > 0){
		Boolean flag=false;
		for (EventMember eventMember : getEventMemberList) {
			 event=eventMember.getEvent();
			if(event ==null){
				logger.info(FamilyAppConst.INVALID_EVENT_SCHEDULE_ID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_EVENT_SCHEDULE_ID);
			}
			else if(event.getEventStatus().equals(Status.I)){
				logger.info(FamilyAppConst.INACTIVE_EVENT_ID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INACTIVE_EVENT_ID);
			}
			else if(eventMember.getMemberStatus().equals(Status.A))
			{
				flag=true;
				break;
			}
			
		}//end for loop
			if(flag==false){
				logger.info(FamilyAppConst.INACTIVE_EVENT_MEMBER);
				throw new FamilyAppWebserviceException(FamilyAppConst.INACTIVE_EVENT_MEMBER);
			}
		for (EventMember eventMember : getEventMemberList) {	
		// update invitation acceptance's status
		event.setEventAcceptanceStatus(status);
		eventMember.setUpdatedDate(new Date());
		calendarEventService.saveorUpdateEventMember(eventMember);
		calendarEventService.updateAcceptanceStatusForEventMember(status,eventMember.getEvent().getId(),eventMember.getId());	
	    getEventMemberList=calendarEventService.findAllEventMemberLists(userId, eventId);
	
		}
		
	}//end if 
	else{
		return new ResponseEntity<>(StringUtils.responseString(FamilyAppConst.INVALID_EVENT_AND_EVENT_MEMBERID),HttpStatus.OK);
	}	
	return new ResponseEntity<>(getEventMemberList,HttpStatus.OK);
		
	}
	
	/**
	 * Delete Event
	 * @param scheduleId
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="api/auth/schedules/delete/{scheduleId}", method = RequestMethod.POST, produces="application/json")
	public ResponseEntity<?> deleteCalendarEvent(@PathVariable ("scheduleId") Long scheduleId,HttpServletRequest request,HttpServletResponse response)throws FamilyAppWebserviceException{
	logger.info("delete Calendar event...");
	String useremail=RequestUtil.getEmailFromAuthentication();
	UserProfile userProfile=userService.findByEmail(useremail);
	if(userProfile ==null){
		logger.info(FamilyAppConst.INVALID_USER_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
	}
	Long userId=userProfile.getId();
	
	Event calendarEvent=calendarEventService.findByCalendarEventId(scheduleId);
	if(calendarEvent == null){
		logger.info(FamilyAppConst.INVALID_EVENT_SCHEDULE_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_EVENT_SCHEDULE_ID);
	}
	else if(calendarEvent.getEventStatus().equals(Status.I)){
		logger.info(FamilyAppConst.INACTIVE_EVENT_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INACTIVE_EVENT_ID);
	}
	Long eventCreator=calendarEvent.getInvitee(); //get event creator id
	
	List<EventMember>getEventMemberList=calendarEvent.getEventMemberLists();//get event member list from eventId
	
	if(userId.equals(eventCreator)){  //check whether user who aren't same as event creator or not
		
	calendarEventService.deleteCalendarEvent(calendarEvent.getId(),Status.I); //update status for event status
    calendarEvent.setUpdatedDate(new Date());
    calendarEventService.saveorUpdateCalendarEvent(calendarEvent);
	for (EventMember eventMember : getEventMemberList) {
	List<Long>memberIdList=new ArrayList<>(); //create empty list for update member status
	memberIdList.add(eventMember.getUserProfile().getId());
	calendarEventService.changeStatusALLEventMemberLists(eventMember.getEvent().getId(),memberIdList,Status.I);//update event member status
		}

	}
	else{
		logger.info(FamilyAppConst.INVALID_INVITEE_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_INVITEE_ID);
	}
	return new ResponseEntity<>(StringUtils.responseString("Success"),HttpStatus.OK);	       
	}
	
	
	/**
	 * Get Event Details 
	 * @param scheduleId
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="api/auth/schedules/view/detail/{scheduleId}",method=RequestMethod.GET)
	public ResponseEntity<?>getEventDetails(@PathVariable ("scheduleId") Long eventId,HttpServletRequest request,HttpServletResponse response)throws FamilyAppWebserviceException{
	logger.info("get schedule event detail");
	
	String useremail=RequestUtil.getEmailFromAuthentication();
	UserProfile userProfile=userService.findByEmail(useremail);
	if(userProfile ==null){
	logger.info(FamilyAppConst.INVALID_USER_ID);
	throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
	}
	Long userId=userProfile.getId();  //get login memberId
		
	 List<EventMember> getEventMemberList=calendarEventService.findAllEventMemberLists(userId, eventId);//get all member list from DB	
		if(getEventMemberList.size() > 0){
			Boolean flag=false;
			Boolean isView=false;
			for (EventMember eventMember : getEventMemberList) {
				Event event=eventMember.getEvent();
				if(event ==null){
					logger.info(FamilyAppConst.INVALID_EVENT_SCHEDULE_ID);
					throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_EVENT_SCHEDULE_ID);
				}
				else if(event.getEventStatus().equals(Status.I)){
					logger.info(FamilyAppConst.INACTIVE_EVENT_ID);
					throw new FamilyAppWebserviceException(FamilyAppConst.INACTIVE_EVENT_ID);
				}
				else if(eventMember.getMemberStatus().equals(Status.A))
				{
					flag=true;
					break;
				}
				
			}//end for loop
				if(flag==false){
					logger.info(FamilyAppConst.INACTIVE_EVENT_MEMBER);
					throw new FamilyAppWebserviceException(FamilyAppConst.INACTIVE_EVENT_MEMBER);
				}	
		for (EventMember member : getEventMemberList) {
			 isView=member.getIsView(); //get isView value	
			 if(isView==false)
				{
					member.setIsView(true);
					calendarEventService.saveorUpdateEventMember(member);
				}
		}
			
		}//end if 
		else{
			return new ResponseEntity<>(StringUtils.responseString(FamilyAppConst.INVALID_EVENT_AND_EVENT_MEMBERID),HttpStatus.OK);
		}	
		
	return new ResponseEntity<>(getEventMemberList,HttpStatus.OK);  
  } 
	    	

	/**
	 * Get Schedule List for a Date
	 * Search Event By Title 
	 * @param choosedDate
	 * @param eventTitle
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws ParseException
	 */
	@RequestMapping(value="api/auth/schedules/search",method=RequestMethod.GET)
	public ResponseEntity<?>getEventListsByChooseDate(@RequestParam (value = "choosedDate",required = false) String choosedDate,
			@RequestParam (value="title",required=false) String eventTitle,
			HttpServletRequest request,HttpServletResponse response)throws FamilyAppWebserviceException, ParseException{
	logger.info("get inbox event Lists");
	System.out.println("choose date..."+choosedDate);
	System.out.println("choose event titile..."+eventTitle);
	
	String useremail=RequestUtil.getEmailFromAuthentication();
	UserProfile userProfile=userService.findByEmail(useremail);
	if(userProfile ==null){
	logger.info(FamilyAppConst.INVALID_USER_ID);
	throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
	}
	Long userId=userProfile.getId();  //get login memberId
	
	// get event member Lists by login memberId by status active
	List<EventMember>getEventMemberList=calendarEventService.getAllEventMemberLists(userId);
	System.out.println("event member list..." + getEventMemberList.size());
	
	Date convertChooseDate=null;
	SimpleDateFormat sdf=new SimpleDateFormat(FamilyAppConst.CALENDAR_DATE_FORMAT);	
	if(choosedDate !=null){
     convertChooseDate= (Date) sdf.parse(choosedDate);
	}
	List<Event>getInboxEventList=new ArrayList<Event>();
	List<Long>getEventIdList=new ArrayList<>();
	
	for (EventMember eventMember : getEventMemberList) {
		if(eventMember.getUserProfileId()==userId)
		{
			getEventIdList.add(eventMember.getEvent().getId());
		}
		else{
			logger.info(FamilyAppConst.INVALID_EVENT_MEMBERID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_EVENT_MEMBERID);	
		}
		getInboxEventList=calendarEventService.getViewAllEventListByChoosedDateAndEventTitle(getEventIdList,convertChooseDate,eventTitle);
	}
	
	 //change object for reply from the event member Lists to user member Lists 
		for (Event event : getInboxEventList) {
			List<EventMember> eventMemberList = event.getEventMemberLists();
			List<UserProfile> getReplyToMemberList = new ArrayList<>();
			for (EventMember eventMem : eventMemberList) {
				getReplyToMemberList.add(eventMem.getUserProfile());
				
				event.setEventAcceptanceStatus(eventMem.getEventAcceptanceStatus());
			}
		event.setMemberLists(getReplyToMemberList);
	    }
	
	return new ResponseEntity<>(getInboxEventList,HttpStatus.OK);
		
	}
	/**
	 * Get Event Date List For All the whole month
	 * @param choosedMonth
	 * @param choosedYear
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws ParseException
	 */
	@RequestMapping(value="api/auth/schedules/event/date",method=RequestMethod.GET)
	public ResponseEntity<?>getEventDateListsByChooseMonthAndYear(@RequestParam (value = "choosedMonth",required = false) String choosedMonth,
			@RequestParam (value="choosedYear",required=false) String choosedYear,
			HttpServletRequest request,HttpServletResponse response)throws FamilyAppWebserviceException, ParseException{
	logger.info("get return date event Lists");
	System.out.println("choose Month...& choose Year..."+choosedMonth+"---"+choosedYear);
	Integer month = null,year = null;
	String defaultDay="01";	
	Integer day = Integer.parseInt(defaultDay);
	if(choosedMonth !=null){
    month = Integer.parseInt(choosedMonth);
	}
	if(choosedYear !=null){
    year = Integer.parseInt(choosedYear);
	}
	//get date from start month
	Calendar calendar = Calendar.getInstance();
	calendar.set(Calendar.YEAR, year);
	if( ( month > Integer.parseInt(String.valueOf(12)) ) )
	{
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_MONTH);
	}
	else{
	calendar.set(Calendar.MONTH, month-1);
	}
	calendar.set(Calendar.DATE, day);
	Date dateFromStartMonth = calendar.getTime();
	System.out.println("Get Date from start month: "+ CalendarUtil.formatDate(dateFromStartMonth, "yyyy-MM-dd"));
	//get date from end month
	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.YEAR, year);
	if(( month > Integer.parseInt(String.valueOf(12)) ) )
	{
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_MONTH);	
	}
	else{
	cal.set(Calendar.MONTH, month);
	}
	cal.set(Calendar.DATE, day);
	Date dateFromEndMonth = cal.getTime();
	System.out.println("Get Date from end month: "+ CalendarUtil.formatDate(dateFromEndMonth, "yyyy-MM-dd"));
	
	//get login user authentication
	String useremail=RequestUtil.getEmailFromAuthentication();
	UserProfile userProfile=userService.findByEmail(useremail);
	if(userProfile ==null){
	logger.info(FamilyAppConst.INVALID_USER_ID);
	throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
	}
	Long userId=userProfile.getId();  //get login memberId
	
	// get event member Lists by login memberId by status active
	List<EventMember>getEventMemberList=calendarEventService.getAllEventMemberLists(userId);
	System.out.println("event member list..." + getEventMemberList.size());
	
	List<Object[]>getDateEventList=new ArrayList<>();
	List<Long>getEventIdList=new ArrayList<>();
	
	for (EventMember eventMember : getEventMemberList) {
		if(eventMember.getUserProfileId()==userId)
		{
			getEventIdList.add(eventMember.getEvent().getId());
		}
		else{
			logger.info(FamilyAppConst.INVALID_EVENT_MEMBERID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_EVENT_MEMBERID);	
		}
	}
	getDateEventList=calendarEventService.getEventDateListByChoosedMonthAndChoosedYear(getEventIdList,dateFromStartMonth,dateFromEndMonth);
	System.out.println("date event List..."+getDateEventList.size());
	
	List<Date>dates=new ArrayList<Date>(); //create empty date list
	List<String>stringDates=new ArrayList<String>();//create empty string date list
	
	for(int i=0; i< getDateEventList.size(); i++){
		Object[] obj=getDateEventList.get(i);
		Date sDate= (Date) obj[0];
		Date eDate= (Date) obj[1];		
		
		Calendar calen = Calendar.getInstance();
		calen.setTime(sDate);
		dates.add(sDate);
		while (calen.getTime().before(eDate)) {
		    calen.add(Calendar.DATE, 1);
		    dates.add(calen.getTime());
		}
		System.out.println("date arrayasList..."+dates);
		stringDates=new ArrayList<String>(dates.size());
		for (Date mydates : dates) {
			String date=CalendarUtil.formatDate(mydates, "yyyy-MM-dd");
			stringDates.add(String.valueOf(date));	
		}
		System.out.println("string date arrayasList..."+stringDates);// prints [one, two, one, three]
	}
	
	HashSet<String>uniqueSetDates=new HashSet<String>();
	uniqueSetDates.addAll(stringDates);
	System.out.println("unique result..."+uniqueSetDates); // prints [two, one, three]

	return new ResponseEntity<>(uniqueSetDates,HttpStatus.OK);
	}
	
	
	
	public static void main(String[] args) {
	/*	Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH,4);
		Date startDate = new Date();
		Date endDate = c.getTime();
		System.out.println("Start Date: "+ CalendarUtil.formatDate(startDate, "yyyy-MM-dd"));
		System.out.println("End Date: "+CalendarUtil.formatDate(endDate, "yyyy-MM-dd"));
			
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		System.out.println();
		while (cal.getTime().before(endDate)) {
		    cal.add(Calendar.DATE, 1);
		   
		    System.out.println( CalendarUtil.formatDate(cal.getTime(), "yyyy-MM-dd"));
		}*/
		/*ArrayList<String> duplicateList = new ArrayList<String>();
	    duplicateList.add("one");
	    duplicateList.add("two");
	    duplicateList.add("one");
	    duplicateList.add("three");

	    System.out.println("duplicate list..."+duplicateList); // prints [one, two, one, three]

	    HashSet<String> uniqueSet = new HashSet<String>();
	    uniqueSet.addAll(duplicateList);
	    System.out.println("unique result..."+uniqueSet); // prints [two, one, three]

	    duplicateList.clear();
	    System.out.println("final result..."+duplicateList);// prints []
*/
		
		  String s = "001";

	      try {
	         long l = Long.parseLong(s);
	         System.out.println("long l = " + l);
	      } catch (NumberFormatException nfe) {
	         System.out.println("NumberFormatException: " + nfe.getMessage());
	      }
	}
	/**
	 * Get Inbox Event List
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="api/auth/schedules/view/inbox",method=RequestMethod.GET)
	public ResponseEntity<?>getInboxEventLists(HttpServletRequest request,HttpServletResponse response)throws FamilyAppWebserviceException{
	logger.info("get inbox event Lists");
	String useremail=RequestUtil.getEmailFromAuthentication();
	UserProfile userProfile=userService.findByEmail(useremail);
	if(userProfile ==null){
	logger.info(FamilyAppConst.INVALID_USER_ID);
	throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
	}
	Long userId=userProfile.getId();  //get login memberId	
	
	List<EventMember>getEventMemberLists=calendarEventService.getAllEventMemberLists(userId);//get active event member Lists by login memberId
	
	List<Event> eventList= new ArrayList<Event>(); //to response event Lists		
	for (EventMember eventMember : getEventMemberLists) {
		
		if(userId == eventMember.getUserProfileId())
		{
			System.out.println("user and member..."+userId+"..."+eventMember.getUserProfileId());
			Event event=eventMember.getEvent();
		    Boolean isView=eventMember.getIsView(); //get event member isView 
			if( isView == true){ //check whether is view or not
			event.setIsView(isView);
			}
		System.out.println("event acceptance status..."+eventMember.getEventAcceptanceStatus());
		event.setEventAcceptanceStatus(eventMember.getEventAcceptanceStatus());
		eventList.add(event);
		}
		else{
			logger.info(FamilyAppConst.INVALID_EVENT_MEMBERID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_EVENT_MEMBERID);	
		}
	}	
	return new ResponseEntity<>(JsonUtil.pojoToJson(eventList),HttpStatus.OK);
		
	}
	
	/**
	 * Get All Schedule List
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="api/auth/schedules/alerts",method=RequestMethod.GET)
	public ResponseEntity<List<AlertSchedule>> scheduleList(HttpServletRequest request,HttpServletResponse response) throws FamilyAppWebserviceException{
		logger.info("In Alert Schedule list");
		List<AlertSchedule> list = alertScheduleService.findAll();
		return  new ResponseEntity<List<AlertSchedule>>(list , HttpStatus.OK);
	}

}
