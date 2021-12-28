package com.startinpoint.proj.familyapp.webservice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
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
import com.startinpoint.proj.familyapp.webservice.entity.CheckList;
import com.startinpoint.proj.familyapp.webservice.entity.CheckListActivity;
import com.startinpoint.proj.familyapp.webservice.entity.CheckListComment;
import com.startinpoint.proj.familyapp.webservice.entity.CheckListConst;
import com.startinpoint.proj.familyapp.webservice.entity.CheckListMember;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyAppConst;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyMember;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyProfile;
import com.startinpoint.proj.familyapp.webservice.entity.SubTask;
import com.startinpoint.proj.familyapp.webservice.entity.Tasks;
import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Activity;
import com.startinpoint.proj.familyapp.webservice.entity.enums.ScrollStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.entity.pojo.ActivityLog;
import com.startinpoint.proj.familyapp.webservice.entity.pojo.CheckListOverview;
import com.startinpoint.proj.familyapp.webservice.entity.pojo.Comment;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.CheckListActivityService;
import com.startinpoint.proj.familyapp.webservice.service.CheckListCommentService;
import com.startinpoint.proj.familyapp.webservice.service.CheckListMemberService;
import com.startinpoint.proj.familyapp.webservice.service.CheckListService;
import com.startinpoint.proj.familyapp.webservice.service.FamilyMemberService;
import com.startinpoint.proj.familyapp.webservice.service.FamilyProfileService;
import com.startinpoint.proj.familyapp.webservice.service.SubTaskService;
import com.startinpoint.proj.familyapp.webservice.service.TasksService;
import com.startinpoint.proj.familyapp.webservice.service.UserService;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Transactional
@Controller
public class CheckListController {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	UserService userService;
	
	@Autowired
	FamilyProfileService familyProfileService;
	
	@Autowired
	CheckListService checkListService;
	
	@Autowired
	CheckListActivityService checkListActivityService;
	
	@Autowired
	CheckListCommentService checkListCommentService;
	
	@Autowired
	TasksService tasksService;
	
	@Autowired
	CheckListMemberService checkListMemberService;
	
	@Autowired
	FamilyMemberService familyMemberService;
	
	@Autowired
	SubTaskService subTaskService;
	
	/**
	 * Check Family is current user's family or not
	 * @param family
	 * @param currentUser
	 * @return
	 */
	private Boolean checkFamilyIsCurrentUserFamily(FamilyProfile family, UserProfile currentUser) {
		List<FamilyMember> members = family.getFamilyMemberList();
		List<FamilyMember> resultList = members.stream()
				.filter(member -> member.getMember().getId() == currentUser.getId())
				.collect(Collectors.toList());
		if(resultList == null || resultList.isEmpty()){
			return false;
		}
		return true;
	}
	
	/**
	 * Create Check List activity
	 * @param action
	 * @param parentTaskName
	 * @param taskName
	 * @param user
	 * @param checkListId
	 * @throws FamilyAppWebserviceException
	 */
	public ActivityLog createCheckListActivity(String action , String parentTaskName ,String taskName,UserProfile user, Long checkListId) throws FamilyAppWebserviceException{
		CheckListActivity activity = new CheckListActivity();
		activity.setAction(action);
		activity.setTaskName(taskName);
		activity.setUsername(user.getUsername());
		if(parentTaskName != null){
			activity.setParentTaskName(parentTaskName);
		}
		activity.setCheckListId(checkListId);
		activity.setCreatedDate(new Date());
		checkListActivityService.saveOrUpdateCheckListActivity(activity);
		
		
		ActivityLog log = new ActivityLog();
		log.setAction(action);
		log.setParentTaskName(parentTaskName);
		log.setTaskName(taskName);
		log.setUsername(user.getUsername());
		log.setImageUrl(user.getProfileImageUrl());
		log.setCreatedDate(activity.getCreatedDate());
		log.setCheckListId(checkListId);
		log.setType(Activity.ACTIVITY);
		return log;
	}
	
	/**
	 * Create Checklist
	 * @param data
	 * @param request
	 * @return
	 * @throws JSONException
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="/api/auth/checklist/create-checklist" , method = RequestMethod.POST)
	public ResponseEntity<CheckList> createCheckList(@RequestBody String data, HttpServletRequest request) throws JSONException, FamilyAppWebserviceException{
		JSONObject obj = new JSONObject(data);
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		
		if(!obj.has("checkListName")){
			logger.info("Check List Name is Required.");
			throw new FamilyAppWebserviceException("Check list name is Required.");
		}
		
		if(!obj.has("description")){
			logger.info("Description is Required.");
			throw new FamilyAppWebserviceException("Description is Required.");
		}
		
		Long familyId = null;
		if(!obj.has("familyId")){ //if not contain familyId use personal family
			FamilyProfile tempfamily = familyProfileService.getPersonalFamilyByCreatorId(currentUser.getId());
			if(tempfamily == null){
				logger.info("No Personal Family.");
				throw new FamilyAppWebserviceException("No Personal Family.");
			}
			familyId = tempfamily.getId();
		}
		else{
			familyId = obj.getLong("familyId");
		}
		
		String checkListName = obj.getString("checkListName");
		String description = obj.getString("description");
		Date targetDate = null;
		if(obj.has("targetDate")){
			targetDate = CalendarUtil.parseDate(obj.getString("targetDate"), FamilyAppConst.CALENDAR_DATE_FORMAT);
		}
		//validate family id
		FamilyProfile family = familyProfileService.findByFamilyProfileId(familyId);
		if(family == null){
			logger.info(FamilyAppConst.INVALID_FAMILY_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_ID);
		}
		
		
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		//check family is current user's family
		if(!checkFamilyIsCurrentUserFamily(family, currentUser)){
			logger.info(FamilyAppConst.NOT_FAMILY_MEMBER);
			throw new FamilyAppWebserviceException(FamilyAppConst.NOT_FAMILY_MEMBER);
		}
		
		CheckList checkList = new CheckList();
		checkList.setCheckListName(checkListName);
		checkList.setDescription(description);
		checkList.setTargetDate(targetDate);
		checkList.setCreatorId(currentUser.getId());
		checkList.setCreatedDate(new Date());
		checkList.setUpdatedDate(new Date());
		checkList.setFamilyId(familyId);
		checkList = checkListService.saveOrUpdateCheckList(checkList);
		
		CheckListMember member = new CheckListMember();
		member.setUserId(currentUser.getId());
		member.setChecklist(checkList);
		member.setCreatedDate(new Date());
		checkListMemberService.saveOrUpdate(member);
		
		List<UserProfile> members = new ArrayList<>();
		members.add(currentUser);
		checkList.setMembers(members);
		
		checkList.setFamily(family);
		//Create Activity
		createCheckListActivity(CheckListConst.CREATE_CHECKLIST,null, checkList.getCheckListName(), currentUser, checkList.getId());
		
		return new ResponseEntity<CheckList>(checkList ,HttpStatus.OK);
	}
	
	
	/**
	 * Edit Checklist
	 * @param data
	 * @param request
	 * @return
	 * @throws JSONException
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="/api/auth/checklist/edit-checklist" ,method=RequestMethod.PUT)
	public ResponseEntity<?> editCheckList(@RequestBody String data, HttpServletRequest request) throws JSONException, FamilyAppWebserviceException{
		JSONObject obj = new JSONObject(data);
		
		if(!obj.has("id")){
			logger.info("Check List Id is Required.");
			throw new FamilyAppWebserviceException("Check List Id is Required.");
		}
		Long checkListId = obj.getLong("id");
		CheckList checkList = checkListService.findById(checkListId);
		if(checkList == null){
			logger.info("Invalid checklist id.");
			throw new FamilyAppWebserviceException("Invalid Check List ID.");
		}
		
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		//check creator or not
		if(currentUser.getId() != checkList.getCreatorId()){
			logger.info(FamilyAppConst.PERMISSION_DENIED);
			throw new FamilyAppWebserviceException(FamilyAppConst.PERMISSION_DENIED);
		}
		
		if(obj.has("checkListName")){
			checkList.setCheckListName(obj.getString("checkListName"));
		}
		
		if(obj.has("description")){
			checkList.setDescription(obj.getString("description"));
		}
		
		if(obj.has("targetDate")){
			Date targetDate = CalendarUtil.parseDate(obj.getString("targetDate"), FamilyAppConst.CALENDAR_DATE_FORMAT);
			checkList.setTargetDate(targetDate);
		}
		checkList.setUpdatedDate(new Date());
		checkList = checkListService.saveOrUpdateCheckList(checkList);
		
		//Create Activity
		ActivityLog log = createCheckListActivity(CheckListConst.EDIT_CHECKLIST,null, checkList.getCheckListName(), currentUser, checkList.getId());
				
				
		return new ResponseEntity<ActivityLog>(log ,HttpStatus.OK);
	}
	

	/**
	 * Delete Check List
	 * @param id
	 * @param request
	 * @return
	 * @throws JSONException
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="/api/auth/checklist/delete/{id}" ,method=RequestMethod.DELETE)
	public ResponseEntity<String> deleteCheckList(@PathVariable("id") Long id, HttpServletRequest request) throws JSONException, FamilyAppWebserviceException{
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		CheckList checkList = checkListService.findById(id);
		if(checkList == null){
			logger.info("Invalid CheckList ID");
			throw new FamilyAppWebserviceException("Invalid CheckList ID.");
		}
		if(currentUser.getId() != checkList.getCreatorId()){
			logger.info(FamilyAppConst.PERMISSION_DENIED);
			throw new FamilyAppWebserviceException(FamilyAppConst.PERMISSION_DENIED);
		}
		checkList.setStatus(Status.I);
		checkList.setUpdatedDate(new Date());
		checkList = checkListService.saveOrUpdateCheckList(checkList);
		
		return new ResponseEntity<String>(StringUtils.responseString("Checklist delete success.") ,HttpStatus.OK);
		//TODO to change task and subtask status to I
	}
	
	/**
	 * Delete Check List
	 * @param id
	 * @param request
	 * @return
	 * @throws JSONException
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="/api/auth/checklist/update-member" ,method=RequestMethod.POST)
	public ResponseEntity<List<UserProfile>> addOrUpdateMember(@RequestBody String data, HttpServletRequest request) throws JSONException, FamilyAppWebserviceException{
		JSONObject obj = new JSONObject(data);
		Long checkListId = obj.getLong("checkListId");
		JSONArray memberJsonArr = obj.getJSONArray("members");

		List<UserProfile> resultMembers = new ArrayList<>();
		
		List<Long> memberIds = new ArrayList<>();
		for(int i =0;i<memberJsonArr.length() ;i++){
			JSONObject memberObj = memberJsonArr.getJSONObject(i);
			Long memberId = memberObj.getLong("id");
			memberIds.add(memberId);
		}
		
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		CheckList checkList = checkListService.findById(checkListId);
		if(checkList == null){
			logger.info("Invalid CheckList ID");
			throw new FamilyAppWebserviceException("Invalid CheckList ID.");
		}
		
		if(currentUser.getId() != checkList.getCreatorId()){
			logger.info(FamilyAppConst.PERMISSION_DENIED);
			throw new FamilyAppWebserviceException(FamilyAppConst.PERMISSION_DENIED);
		}
		
		
		List<FamilyMember> familyMembers = checkList.getFamily().getFamilyMemberList();
		
		
		Boolean isContainCreator = false;
		for(Long memberId: memberIds){
			//validate family members or not
			Boolean isFamilyMember = false;
			for(FamilyMember fm: familyMembers){
				if(memberId == fm.getMember().getId()){
					resultMembers.add(fm.getMember()); //add normal member
					isFamilyMember = true;
					break;
				}
			}
			if(!isFamilyMember){
				logger.info(FamilyAppConst.INVALID_FAMILY_MEMBER_ID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_MEMBER_ID);
			}		
			if(memberId == checkList.getCreatorId()){
				isContainCreator = true;
			}
			
		}
		if(isContainCreator == false){//check creator id contains if not contain add auto
			memberIds.add(checkList.getCreatorId());
		}		
		
		
		List<CheckListMember> dbMembers = checkListMemberService.getMembersByCheckListId(checkListId);
		
		
		for(Long memberId: memberIds){
			
			Boolean isFoundinDb = false;
			for(CheckListMember clm: dbMembers){
				if(clm.getUser().getId() == memberId){
					isFoundinDb = true;
				}
			}
			if(!isFoundinDb){//not found in db
				CheckListMember member = new CheckListMember();
				member.setChecklist(checkList);
				member.setUserId(memberId);
				member.setCreatedDate(new Date());
				member = checkListMemberService.saveOrUpdate(member);
			}
		}
		
		//push creator
		resultMembers.add(currentUser);
		
		//update remain member status to I
		checkListMemberService.updateStatusNotInMemberIds(memberIds,checkListId);
		
		return new ResponseEntity<List<UserProfile>>(resultMembers ,HttpStatus.OK);
		//TODO to change task and subtask status to I
	}
	
	
	/**
	 * View All Check List
	 * @param request
	 * @return
	 * @throws JSONException
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="/api/auth/checklist/view-checklist" ,method=RequestMethod.GET)
	public ResponseEntity<List<CheckListOverview>> viewCheckList(@RequestParam (value = "checkListName",required = false) String checkListName,@RequestParam (value = "familyId",required = false) Long familyId, HttpServletRequest request) throws JSONException, FamilyAppWebserviceException{
		
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		if(familyId != null){
			FamilyProfile family = familyProfileService.findByFamilyProfileId(familyId);

			if(family == null){
				logger.info(FamilyAppConst.INVALID_FAMILY_ID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_ID);
			}
			
		}
		
		
		List<CheckList> checkLists = checkListService.getCheckListByFamilyIdUserId(familyId, checkListName, CheckListConst.CHECKLIST_SIZE,currentUser.getId());
		List<CheckListOverview> overviewList = new ArrayList<>();
		if(checkLists.isEmpty()){//no checklist
			return new ResponseEntity<List<CheckListOverview>> (overviewList,HttpStatus.OK);
		}
		//Get all checklist id
		List<Long> checkListIds = checkLists.stream()
				.map(CheckList :: getId).collect(Collectors.toList());
		
		
		List<CheckListComment> comments = checkListCommentService.getCommentsByCheckListIdsMemberId(checkListIds, currentUser.getId());
		List<Tasks> taskList = tasksService.getTasksByCheckListIdCreatorId(checkListIds, null);
		
		//Group comment count by checklist id
		Map<Long,Long> commentCounts = comments.stream().collect(
				Collectors.groupingBy(
							CheckListComment::getCheckListId , Collectors.counting()));
		
		//Group tasks list by checklist id
		Map<Long,List<Tasks>> taskListByCheckListIdMap = taskList.stream()
											.collect(Collectors.groupingBy(
														result->result.getCheckListId()	
													));
		for(CheckList checkList: checkLists){
			CheckListOverview overview = new CheckListOverview();
			overview.setCheckListId(checkList.getId());
			overview.setCheckListName(checkList.getCheckListName());
			overview.setDescription(checkList.getDescription());
			overview.setTargetDate(checkList.getTargetDate());
			Long commentCount = commentCounts.get(checkList.getId());
			if(commentCount == null)
			{
				overview.setCommentCount(0);
			}
			else{
				overview.setCommentCount((int)commentCount.longValue());
			}
			//assign totalTaskCount and complete taskcount
			List<Tasks> tempTasks = taskListByCheckListIdMap.get(checkList.getId());
			if(tempTasks== null || tempTasks.isEmpty()){
				overview.setTotalTaskCount(0);
				overview.setCompleteTaskCount(0);
			}
			else{
				overview.setTotalTaskCount(tempTasks.size());
				int completeCount = 0;
				for(Tasks t : tempTasks){
					if(t.getIsComplete()){
						completeCount++;
					}
				}
				overview.setCompleteTaskCount(completeCount);
			}
			overviewList.add(overview);
			
		}
		return new ResponseEntity<List<CheckListOverview>>(overviewList ,HttpStatus.OK);
	
	}	
	
	/**
	 * Scroll Checklist before / after
	 * @param checkListName
	 * @param familyId
	 * @param id
	 * @param scrollStatus
	 * @param request
	 * @return
	 * @throws JSONException
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="/api/auth/checklist/scroll" ,method=RequestMethod.GET)
	public ResponseEntity<List<CheckListOverview>> scrollCheckList(
			@RequestParam (value = "checkListName",required = false) String checkListName,
			@RequestParam (value = "familyId",required = false) Long familyId,
			@RequestParam (value = "id") Long id,
			@RequestParam("scrollStatus") ScrollStatus scrollStatus,
			HttpServletRequest request) throws JSONException, FamilyAppWebserviceException{
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		List<CheckListOverview> overviewList = getCheckListOverviews(checkListName,familyId,id,scrollStatus,currentUser);
		return new ResponseEntity<List<CheckListOverview>>(overviewList ,HttpStatus.OK);
	
	}

	/**
	 * Get checklist overview for loadmore/refresh checklist
	 * @param checkListName
	 * @param familyId
	 * @param id
	 * @param scrollStatus
	 * @param currentUser
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	private List<CheckListOverview> getCheckListOverviews(String checkListName, Long familyId, Long id, ScrollStatus scrollStatus,UserProfile currentUser) throws FamilyAppWebserviceException {
	
		if(familyId != null){
			FamilyProfile family = familyProfileService.findByFamilyProfileId(familyId);
			if(family == null){
				logger.info(FamilyAppConst.INVALID_FAMILY_ID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_ID);
			}
		}
		
		
		List<CheckList> checkLists = checkListService.getLoadMoreOrRefreshCheckList(familyId, checkListName, CheckListConst.CHECKLIST_SIZE,id,scrollStatus,currentUser.getId());
		List<CheckListOverview> overviewList = new ArrayList<>();
		if(checkLists.isEmpty()){//no checklist
			return overviewList;
		}
		//Get all checklist id
		List<Long> checkListIds = checkLists.stream()
				.map(CheckList :: getId).collect(Collectors.toList());
		
		
		List<CheckListComment> comments = checkListCommentService.getCommentsByCheckListIdsMemberId(checkListIds, currentUser.getId());
		List<Tasks> taskList = tasksService.getTasksByCheckListIdCreatorId(checkListIds, null);
		
		//Group comment count by checklist id
		Map<Long,Long> commentCounts = comments.stream().collect(
				Collectors.groupingBy(
							CheckListComment::getCheckListId , Collectors.counting()));
		
		//Group tasks list by checklist id
		Map<Long,List<Tasks>> taskListByCheckListIdMap = taskList.stream()
											.collect(Collectors.groupingBy(
														result->result.getCheckListId()	
													));
		for(CheckList checkList: checkLists){
			CheckListOverview overview = new CheckListOverview();
			overview.setCheckListId(checkList.getId());
			overview.setCheckListName(checkList.getCheckListName());
			overview.setDescription(checkList.getDescription());
			overview.setTargetDate(checkList.getTargetDate());
			Long commentCount = commentCounts.get(checkList.getId());
			if(commentCount == null)
			{
				overview.setCommentCount(0);
			}
			else{
				overview.setCommentCount((int)commentCount.longValue());
			}
			//assign totalTaskCount and complete taskcount
			List<Tasks> tempTasks = taskListByCheckListIdMap.get(checkList.getId());
			if(tempTasks== null || tempTasks.isEmpty()){
				overview.setTotalTaskCount(0);
				overview.setCompleteTaskCount(0);
			}
			else{
				overview.setTotalTaskCount(tempTasks.size());
				int completeCount = 0;
				for(Tasks t : tempTasks){
					if(t.getIsComplete()){
						completeCount++;
					}
				}
				overview.setCompleteTaskCount(completeCount);
			}
			overviewList.add(overview);
			
		}
		return overviewList;
	}	
	
	/**
	 * Check List Details
	 * @param id
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="/api/auth/checklist/checklist-details/{id}" ,method=RequestMethod.GET)
	public ResponseEntity<CheckList> getCheckListDetails(@PathVariable("id")Long id,HttpServletRequest request) throws FamilyAppWebserviceException{
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		CheckList checkList = checkListService.findById(id);
		if(checkList == null){
			logger.info("Invalid CheckList ID.");
			throw new FamilyAppWebserviceException("Invalid CheckList ID.");
		}
		
		List<Tasks> taskList = tasksService.getTasksByCheckListIdWithSubTasks(checkList.getId());
						
		checkList.setTaskList(taskList);
		//get Family Members
		List<UserProfile> familyMembers = new ArrayList<>();
		for(FamilyMember fm: checkList.getFamily().getFamilyMemberList()){
			familyMembers.add(fm.getMember());
		}
		checkList.getFamily().setMembers(familyMembers);
		
		//get checklist members
		List<CheckListMember> checkListMembers = checkListMemberService.getMembersByCheckListId(id);
		
		//validate checklist member or not
		Boolean isFoundInMember = false;
		for(CheckListMember cm: checkListMembers){
			if(cm.getUserId() == currentUser.getId()){
				isFoundInMember = true;
				break;
			}
		}
		if(isFoundInMember == false){
			logger.info(CheckListConst.NOT_CHECKLIST_MEMBER);
			throw new FamilyAppWebserviceException(CheckListConst.NOT_CHECKLIST_MEMBER);
		}
		List<UserProfile> members = new ArrayList<>();
		for(CheckListMember member: checkListMembers){
			members.add(member.getUser());
		}
		checkList.setMembers(members);
		
		//Get Check List activity log shuffle with two table comment and activity
		List<ActivityLog> activityLogs = checkListActivityService.getCheckListActivityLog(checkList.getId(),Activity.ACTIVITY,CheckListConst.ACTIVITY_SIZE);
		checkList.setActivityLogs(activityLogs);
		
		return new ResponseEntity<CheckList>(checkList ,HttpStatus.OK);
	}
	
		
	/**
	 * Create/Update Task for a checklist
	 * @param data
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor={Exception.class})
	@RequestMapping(value="/api/auth/task/create-update-task",method=RequestMethod.POST)
	public ResponseEntity<?> createOrUpdateTask(@RequestBody String data,HttpServletRequest request) throws FamilyAppWebserviceException, JSONException{
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		JSONObject obj = new JSONObject(data);		
		
		if(!obj.has("taskName")){
			logger.info("Task Name is Required.");
			throw new FamilyAppWebserviceException("Task Name is Required.");
		}
		
		if(!obj.has("isComplete")){
			logger.info("Is Complete flag is Required.");
			throw new FamilyAppWebserviceException("Is Complete Flag is Required.");
		}
		
		String taskName = obj.getString("taskName");
		Boolean isComplete = obj.getBoolean("isComplete");
		Tasks task = new Tasks();
		

		if(!obj.has("checkListId")){
			logger.info("Check List Id is Required.");
			throw new FamilyAppWebserviceException("Check List Id is Required.");
		}
		Long checkListId = obj.getLong("checkListId");
		CheckList checkList = checkListService.findById(checkListId);
		if(checkList == null){
			logger.info("Invalid CheckList ID.");
			throw new FamilyAppWebserviceException("Invalid CheckList ID.");
		}	
		
		if(obj.has("taskId")){
			//edit task
			Long id = obj.getLong("taskId"); 
			task = tasksService.findById(id);
			if(task == null){
				logger.info("Invalid Task ID.");
				throw new FamilyAppWebserviceException("Invalid Task ID.");
			}
			checkListId = task.getCheckListId();
		}
		else{
			// new task			
			task.setCreatedBy(currentUser.getId());
			task.setCreatedDateTime(new Date());
			
		}
		
		CheckListMember member = checkListMemberService.getMemberByCheckListIdMemberId(checkListId, currentUser.getId());
		if(member == null){
			logger.info(CheckListConst.NOT_CHECKLIST_MEMBER);
			throw new FamilyAppWebserviceException(CheckListConst.NOT_CHECKLIST_MEMBER);
		}
				
		task.setCheckListId(checkListId);
		task.setTaskName(taskName);
		task.setIsComplete(isComplete);
		task.setUpdatedDateTime(new Date());
		if(isComplete == true){
			task.setCompletedBy(currentUser.getId());
			task.setCompletedDateTime(new Date());
		}
		task = tasksService.saveOrUpdateTask(task);
		
		ActivityLog log = null;
		//log the activity
		if(obj.has("id")){
			log = createCheckListActivity(CheckListConst.EDIT_TASK, checkList.getCheckListName(),task.getTaskName(), currentUser, checkListId);
		}
		else{
			log = createCheckListActivity(CheckListConst.CREATE_TASK,checkList.getCheckListName(), task.getTaskName(), currentUser, checkListId);
		}
		
		String result = "{"
				+"\"task\":"+JsonUtil.pojoToJson(task)
				+",\"activity\":"+JsonUtil.pojoToJson(log)
				+ "}";		
		
		return new ResponseEntity<String>(result ,HttpStatus.OK);
	}
	
	/**
	 * Delete Task
	 * @param id
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 */
	@RequestMapping(value="/api/auth/task/delete/{id}",method=RequestMethod.DELETE)
	public ResponseEntity<ActivityLog> deleteTask(@PathVariable("id") Long id, HttpServletRequest request) throws FamilyAppWebserviceException, JSONException{
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		Tasks task = tasksService.findById(id);
		if(task == null){
			logger.info("Invalid Task ID.");
			throw new FamilyAppWebserviceException("Invalid Task ID.");
		}
		
		CheckListMember member = checkListMemberService.getMemberByCheckListIdMemberId(task.getCheckListId(), currentUser.getId());
		if(member == null){
			logger.info(CheckListConst.NOT_CHECKLIST_MEMBER);
			throw new FamilyAppWebserviceException(CheckListConst.NOT_CHECKLIST_MEMBER);
		}
				
		task.setStatus(Status.I);
		task.setUpdatedDateTime(new Date());
		task = tasksService.saveOrUpdateTask(task);
		
		//log the activity
		ActivityLog log = createCheckListActivity(CheckListConst.DELETE_TASK, task.getCheckList().getCheckListName(),task.getTaskName(), currentUser, task.getCheckListId());
		
		return new ResponseEntity<ActivityLog>(log ,HttpStatus.OK);
	}
	
	/**
	 * Update Status of the Task
	 * @param id
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 */
	@RequestMapping(value="/api/auth/task/update-status/{id}",method=RequestMethod.POST)
	public ResponseEntity<ActivityLog> updateStatusTask(@PathVariable("id") Long id, HttpServletRequest request) throws FamilyAppWebserviceException, JSONException{
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		Tasks task = tasksService.findById(id);
		if(task == null){
			logger.info("Invalid Task ID.");
			throw new FamilyAppWebserviceException("Invalid Task ID.");
		}
		
		CheckListMember member = checkListMemberService.getMemberByCheckListIdMemberId(task.getCheckListId(), currentUser.getId());
		if(member == null){
			logger.info(CheckListConst.NOT_CHECKLIST_MEMBER);
			throw new FamilyAppWebserviceException(CheckListConst.NOT_CHECKLIST_MEMBER);
		}
			
		ActivityLog log = null;
		if(task.getIsComplete() == false){
			task.setIsComplete(true);
			//log the activity
			log = createCheckListActivity(CheckListConst.COMPLETE_TASK,task.getCheckList().getCheckListName(), task.getTaskName(), currentUser, task.getCheckListId());
				
		}
		else{
			task.setIsComplete(false);
			//log the activity
			log = createCheckListActivity(CheckListConst.UNCOMPLETE_TASK,task.getCheckList().getCheckListName(), task.getTaskName(), currentUser, task.getCheckListId());
				
		}
		task.setUpdatedDateTime(new Date());
		task = tasksService.saveOrUpdateTask(task);
					
		
		return new ResponseEntity<ActivityLog>(log ,HttpStatus.OK);
	}
	
	/**
	 * Create/Update Sub Task
	 * @param data
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 */
	@RequestMapping(value="/api/auth/subtask/create",method=RequestMethod.POST)
	public ResponseEntity<SubTask> createSubTask(@RequestBody String data,HttpServletRequest request) throws FamilyAppWebserviceException, JSONException{
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		JSONObject obj = new JSONObject(data);
		if(!obj.has("taskId")){
			logger.info("Task Id is Required.");
			throw new FamilyAppWebserviceException("Task Id is Required.");
		}
		
		if(!obj.has("subTaskName")){
			logger.info("Sub Task Name is Required.");
			throw new FamilyAppWebserviceException("Sub Task Name is Required.");
		}
		
		if(!obj.has("isComplete")){
			logger.info("Is Complete flag is Required.");
			throw new FamilyAppWebserviceException("Is Complete Flag is Required.");
		}
		
		Long taskId = obj.getLong("taskId");
		String subTaskName = obj.getString("subTaskName");
		Boolean isComplete = obj.getBoolean("isComplete");
		
		Tasks task = tasksService.findById(taskId);
		if(task == null){
			logger.info("Invalid Task ID.");
			throw new FamilyAppWebserviceException("Invalid Task ID.");
		}
		
		CheckListMember member = checkListMemberService.getMemberByCheckListIdMemberId(task.getCheckListId(), currentUser.getId());
		if(member == null){
			logger.info(CheckListConst.NOT_CHECKLIST_MEMBER);
			throw new FamilyAppWebserviceException(CheckListConst.NOT_CHECKLIST_MEMBER);
		}
		
		SubTask subTask = new SubTask();
		subTask.setTask(task);
		subTask.setCreatedDateTime(new Date());
		subTask.setCreatedBy(currentUser.getId());
		subTask.setSubTaskName(subTaskName);
		subTask.setIsComplete(isComplete);
		if(isComplete == true){
			subTask.setCompletedBy(currentUser.getId());
			subTask.setCompletedDateTime(new Date());
		}
		subTask = subTaskService.saveOrUpdateSubTask(subTask);
		
		//they need only id
		return new ResponseEntity<SubTask>(subTask ,HttpStatus.OK);
	}

	/**
	 * Get Activity Log shuffling comment and activity tables
	 * @param checkListId
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="/api/auth/checklist/activity/get-activity",method=RequestMethod.GET)
	public ResponseEntity<List<ActivityLog>> getActivityLogs(@RequestParam (value = "checkListId") Long checkListId,HttpServletRequest request) throws FamilyAppWebserviceException{
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		CheckList checkList = checkListService.findById(checkListId);
		if(checkList == null){
			logger.info("Invalid CheckList ID.");
			throw new FamilyAppWebserviceException("Invalid CheckList ID.");
		}	

		List<ActivityLog> logs = checkListActivityService.getCheckListActivityLog(checkListId, Activity.ACTIVITY, CheckListConst.ACTIVITY_SIZE);
		return new ResponseEntity<List<ActivityLog>>(logs ,HttpStatus.OK);
	}
	
	/**
	 * Load more / Refresh activity logs
	 * @param familyId
	 * @param checkListId
	 * @param scrollStatus
	 * @param scrollDate
	 * @param request
	 * @return
	 * @throws JSONException
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="/api/auth/checklist/activity/scroll" ,method=RequestMethod.GET)
	public ResponseEntity<List<ActivityLog>> scrollActivityLog(
			@RequestParam (value = "familyId",required = false) Long familyId,
			@RequestParam (value = "checkListId") Long checkListId,
			@RequestParam("scrollStatus") ScrollStatus scrollStatus,
			@RequestParam("scrollDate") String scrollDate,
			HttpServletRequest request) throws JSONException, FamilyAppWebserviceException{
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		CheckList checkList = checkListService.findById(checkListId);
		if(checkList == null){
			logger.info("Invalid CheckList ID.");
			throw new FamilyAppWebserviceException("Invalid CheckList ID.");
		}	
			
		if(scrollDate == null){
			logger.info("Scroll date is required.");
			throw new FamilyAppWebserviceException("Scroll Date is required.");
		}
		Date date = CalendarUtil.parseDate(scrollDate, FamilyAppConst.CALENDAR_DATETIME_FORMAT);
		
		List<ActivityLog> logs = checkListActivityService.getCheckListActivityLogByScrollStatus(checkListId, Activity.ACTIVITY, CheckListConst.ACTIVITY_SIZE, scrollStatus, date);
		return new ResponseEntity<List<ActivityLog>>(logs ,HttpStatus.OK);
	}

	/**
	 * Create Check List Comment
	 * @param data
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 */
	@RequestMapping(value="/api/auth/checklist/comment",method=RequestMethod.POST)
	public ResponseEntity<ActivityLog> writeComment(@RequestBody String data,HttpServletRequest request) throws FamilyAppWebserviceException, JSONException{
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		JSONObject obj = new JSONObject(data);
		if(!obj.has("comment")){
			logger.info("Comment is Required.");
			throw new FamilyAppWebserviceException("Comment is Required.");
		}
		
		if(!obj.has("checkListId")){
			logger.info("Check List Id is Required.");
			throw new FamilyAppWebserviceException("Check List Id is Required.");
		}
		
		String comment = obj.getString("comment");
		Long checkListId = obj.getLong("checkListId");
		
		//validate checklist id
		CheckList checkList = checkListService.findById(checkListId);
		if(checkList == null){
			logger.info("Invalid Check List ID.");
			throw new FamilyAppWebserviceException("Invalid Check List ID.");
		}
		
		//check current checklist member or not
		CheckListMember member = checkListMemberService.getMemberByCheckListIdMemberId(checkListId, currentUser.getId());
		if(member == null){
			logger.info(CheckListConst.NOT_CHECKLIST_MEMBER);
			throw new FamilyAppWebserviceException(CheckListConst.NOT_CHECKLIST_MEMBER);
		}
		CheckListComment clComment = new CheckListComment();
		clComment.setCheckListId(checkListId);
		clComment.setComment(comment);
		clComment.setCreatedDate(new Date());
		clComment.setMember(currentUser);
		clComment = checkListCommentService.saveOrUpdateCheckListComment(clComment);
		
		ActivityLog log = new ActivityLog();
		log.setAction(CheckListConst.COMMENT);
		log.setTaskName(checkList.getCheckListName());
		log.setUsername(currentUser.getUsername());
		log.setImageUrl(currentUser.getProfileImageUrl());
		log.setCreatedDate(clComment.getCreatedDate());
		log.setCheckListId(checkListId);
		log.setComment(clComment.getComment());
		log.setType(Activity.COMMENT);
		
		//they need only id
		return new ResponseEntity<ActivityLog>(log ,HttpStatus.OK);
	}
	
	/**
	 * Get Activity Log shuffling comment and activity tables
	 * @param checkListId
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="/api/auth/checklist/comment/get-comments",method=RequestMethod.GET)
	public ResponseEntity<List<Comment>> getCommentList(@RequestParam (value = "checkListId") Long checkListId,HttpServletRequest request) throws FamilyAppWebserviceException{
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		CheckList checkList = checkListService.findById(checkListId);
		if(checkList == null){
			logger.info("Invalid CheckList ID.");
			throw new FamilyAppWebserviceException("Invalid CheckList ID.");
		}	

		List<CheckListComment> comments = checkListCommentService.getCommentsByCheckLists(checkListId, CheckListConst.ACTIVITY_SIZE, null, null);
	
		List<Comment> resultComments = new ArrayList<>();
		for(CheckListComment comment: comments){
			Comment cm = new Comment();
			cm.setComment(comment.getComment());
			cm.setCommentedDate(comment.getCreatedDate());
			cm.setImageUrl(comment.getMember().getProfileImageUrl());
			cm.setCommentId(comment.getId());
			cm.setUsername(comment.getMember().getUsername());
			cm.setUserId(comment.getMember().getId());
			cm.setUpdatedDate(comment.getUpdatedDate());
			resultComments.add(cm);
		}
			
		
		return new ResponseEntity<List<Comment>>(resultComments ,HttpStatus.OK);
	}
	
	/**
	 * Load more / Refresh Comment logs
	 * @param familyId
	 * @param checkListId
	 * @param scrollStatus
	 * @param scrollDate
	 * @param request
	 * @return
	 * @throws JSONException
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="/api/auth/checklist/comment/scroll" ,method=RequestMethod.GET)
	public ResponseEntity<List<Comment>> scrollComments(
			@RequestParam (value = "familyId",required = false) Long familyId,
			@RequestParam (value = "checkListId") Long checkListId,
			@RequestParam("scrollStatus") ScrollStatus scrollStatus,
			@RequestParam("id") Long id,
			HttpServletRequest request) throws JSONException, FamilyAppWebserviceException{
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		CheckList checkList = checkListService.findById(checkListId);
		if(checkList == null){
			logger.info("Invalid CheckList ID.");
			throw new FamilyAppWebserviceException("Invalid CheckList ID.");
		}	
			
		if(id == null){
			logger.info("ID is required.");
			throw new FamilyAppWebserviceException("ID is required.");
		}
		List<CheckListComment> comments = checkListCommentService.getCommentsByCheckLists(checkListId, CheckListConst.ACTIVITY_SIZE, scrollStatus, id);
		
		List<Comment> resultComments = new ArrayList<>();
		for(CheckListComment comment: comments){
			Comment cm = new Comment();
			cm.setComment(comment.getComment());
			cm.setCommentedDate(comment.getCreatedDate());
			cm.setImageUrl(comment.getMember().getProfileImageUrl());
			cm.setCommentId(comment.getId());
			cm.setUsername(comment.getMember().getUsername());
			cm.setUserId(comment.getMember().getId());
			cm.setUpdatedDate(comment.getUpdatedDate());
			resultComments.add(cm);
		}
			
		
		return new ResponseEntity<List<Comment>>(resultComments ,HttpStatus.OK);
	}



}
 