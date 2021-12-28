package com.startinpoint.proj.familyapp.webservice.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.startinpoint.proj.familyapp.util.CalendarUtil;
import com.startinpoint.proj.familyapp.util.ImageUtil;
import com.startinpoint.proj.familyapp.util.RequestUtil;
import com.startinpoint.proj.familyapp.util.StringUtils;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyAppConst;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyJoinRequest;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyMember;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyProfile;
import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;
import com.startinpoint.proj.familyapp.webservice.entity.enums.JoinStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.CalendarEventService;
import com.startinpoint.proj.familyapp.webservice.service.CheckListService;
import com.startinpoint.proj.familyapp.webservice.service.DiscussionService;
import com.startinpoint.proj.familyapp.webservice.service.FamilyJoinRequestService;
import com.startinpoint.proj.familyapp.webservice.service.FamilyMemberService;
import com.startinpoint.proj.familyapp.webservice.service.FamilyProfileService;
import com.startinpoint.proj.familyapp.webservice.service.MailService;
import com.startinpoint.proj.familyapp.webservice.service.UserService;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Transactional
@Controller
public class FamilyProfileController {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	FamilyProfileService familyProfileService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	FamilyMemberService familyMemberService;
	
	@Autowired
	FamilyJoinRequestService familyJoinRequestService;
	
	@Autowired
	MailService mailService;
	
	@Autowired
	DiscussionService discussionService;
	
	@Autowired 
	CheckListService checkListService;
	
	@Autowired
	CalendarEventService calendarEventService;
		
	@Value("${application.folder_path}")
	private String folderPath;
	
	@Value("${application.is_allow_multi_family}")
	private Boolean isAllowMultiFamily;
	
	/**
	 * Create Family Profile
	 * @param data
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException 
	 * @throws IOException 
	 */
	@RequestMapping(value = "/api/auth/familyprofile/create", method = RequestMethod.POST)
	public ResponseEntity<FamilyProfile> createFamilyProfile(@RequestBody String data, HttpServletRequest request) throws FamilyAppWebserviceException, JSONException, IOException {
		logger.info("Create Family Profile::: ");
		JSONObject obj = new JSONObject(data);
		if(!obj.has("familyName")){
			logger.info("Family Name is Required.");
			throw new FamilyAppWebserviceException("Family Name is Required.");
		}
		
		if(!obj.has("description")){
			logger.info("Description is Required.");
			throw new FamilyAppWebserviceException("Description is Required.");
		}
		
		if(!obj.has("familyStartDate")){
			logger.info("Family Start Date is Required.");
			throw new FamilyAppWebserviceException("Fly Start Date is Required.");
		}
		String familyName = obj.getString("familyName");
		String description = obj.getString("description");
		
		
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info("No User Found");
			throw new FamilyAppWebserviceException("No User Found");
		}
		
		//validate family already create or not
		if(isAllowMultiFamily == false){
			validateFamilyAlreadyExistOrNot(currentUser.getId());
		}	
		
		
		
		//Create Family Profile
		FamilyProfile profile = new FamilyProfile();
		profile.setFamilyName(familyName);
		profile.setDescription(description);
		profile.setFamilyCreatorId(currentUser.getId());
		profile.setFamilyStartDate(CalendarUtil.parseDate(obj.getString("familyStartDate"), FamilyAppConst.CALENDAR_DATE_FORMAT));
		profile.setCreatedDate(new Date());
		profile.setFamilyCode("F-"+StringUtils.uuidRandomString(8));	
		
		//Get Image Name
		if(obj.has("imageUrl")){
			String imageUrl = obj.getString("imageUrl");
			
			if(imageUrl.contains("base64")){
				String imageName = ImageUtil.writeImage(imageUrl,folderPath);
				profile.setImageUrl(imageName);
			}
			else{
				String imageName = ImageUtil.getImageNameFromImageUrl(imageUrl);
				profile.setImageUrl(imageName);
			}
		}	
		
		
		//save creator as family member
		FamilyMember member = new FamilyMember();
		member.setMember(currentUser);
		member.setIsFamilyCreator(true);
		member.setRequestStatus(JoinStatus.JOINED);
		member.setJoinDate(new Date());
		member.setFamily(profile);
		
		List<FamilyMember> members = new ArrayList<FamilyMember>();
		members.add(member);
				
		profile.setFamilyMemberList(members);
		profile = familyProfileService.saveOrUpdateFamilyProfile(profile);
				
		if(obj.has("members")){
			List<String> inviteeList = new ArrayList<>();
			//invite member
			JSONArray jsonArr = obj.getJSONArray("members");
			for(int i=0;i<jsonArr.length();i++){
				JSONObject jsonObj = jsonArr.getJSONObject(i);
				String invitee = jsonObj.getString("member");
				inviteeList.add(invitee);
			}		
			if(!inviteeList.isEmpty()){
				String commaSperatedValue = inviteeList.stream().collect(Collectors.joining(","));
				mailService.sendFamilyInviteMail(profile, commaSperatedValue);
			}
			
		}
			
		//update current user is_login flag
		currentUser.setIsLogin(true);
		userService.saveOrUpdateUser(currentUser);
		
		List<UserProfile> tempUsers = new ArrayList<>();
		tempUsers.add(currentUser);
		profile.setMembers(tempUsers);
		
		FamilyProfile personalFamily = familyProfileService.getPersonalFamilyByCreatorId(currentUser.getId());
		
		if(personalFamily != null){
			//first time personal family will be exist
			personalFamily.setStatus(Status.I);
			familyProfileService.saveOrUpdateFamilyProfile(personalFamily);
			
			//all topic move to new family	
			discussionService.replaceFamilyIdInTopic(personalFamily.getId(), profile.getId());
			
			//all event move to new family
			calendarEventService.replaceFamilyIdInEvent(personalFamily.getId(), profile.getId());
			
			//all checklist move to new family
			checkListService.replaceFamilyIdInCheckList(personalFamily.getId(), profile.getId());
			
			familyMemberService.updateFamilyMemberStatusByFamilyId(personalFamily.getId(), JoinStatus.LEAVE,currentUser.getId());
					
		}			
		
		
		return new ResponseEntity<FamilyProfile>(profile,HttpStatus.OK);
	}
	
	/**
	 * Family Profile Details
	 * @param familyCode
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value = "/api/auth/user/familyprofile/details", method = RequestMethod.GET)
	public ResponseEntity<?> getFamilyProfileDetails(@RequestParam("familyCode") String familyCode,HttpServletRequest request) throws FamilyAppWebserviceException {
		
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		if(familyCode == null || familyCode.isEmpty()){
			logger.info("Family Code is Required");
			throw new FamilyAppWebserviceException("Family Code is Required");
		}
		
		FamilyProfile family = familyProfileService.getFamilyProfileByFamilyCode(familyCode);
		if(family == null){
			logger.info(FamilyAppConst.INVALID_FAMILY_CODE);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_CODE);
		}
		
		List<UserProfile> members = new ArrayList<>();
		Boolean isMember = false;
		for(FamilyMember familyMember: family.getFamilyMemberList()){
			
			if(familyMember.getMember().getId() == currentUser.getId()){
				isMember = true;				
			}
			members.add(familyMember.getMember());
		}
		
		family.setMembers(members);
		
		if(isMember == false){
			logger.info(FamilyAppConst.NOT_FAMILY_MEMBER);
			return new ResponseEntity<String>(StringUtils.responseString(FamilyAppConst.NOT_FAMILY_MEMBER),HttpStatus.UNAUTHORIZED);
		}
		
		//pending list will be show only creator
		if(currentUser.getId() == family.getFamilyCreatorId()){
			List<FamilyJoinRequest> joinRequestList = familyJoinRequestService.getPendingJoinRequestByFamilyId(family.getId());
			family.setJoinRequestList(joinRequestList);
		}		
		
		return new ResponseEntity<FamilyProfile>(family,HttpStatus.OK);
	}
	
	/**
	 * edit family profile
	 * @param familyProfile
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/api/auth/familyprofile/edit", method = RequestMethod.POST)
	public ResponseEntity<FamilyProfile> editFamilyProfile(@RequestBody String data, HttpServletRequest request) throws FamilyAppWebserviceException, JSONException {
		logger.info("Edit Family Profile::: ");
		JSONObject obj = new JSONObject(data);
		if(!obj.has("familyName")){
			logger.info("Family Name is Required.");
			throw new FamilyAppWebserviceException("Family Name is Required.");
		}
		
		if(!obj.has("description")){
			logger.info("Description is Required.");
			throw new FamilyAppWebserviceException("Description is Required.");
		}
		
		if(!obj.has("id")){
			logger.info("ID is Required");
			throw new FamilyAppWebserviceException("ID is Required");
		}
		
		if(!obj.has("familyCode")){
			logger.info("Family Code is Required");
			throw new FamilyAppWebserviceException("Family Code is Required");
		}
		
		String familyName = obj.getString("familyName");
		String description = obj.getString("description");
		String familyCode = obj.getString("familyCode");
		String familyStartDateStr = obj.getString("familyStartDate");
		Date familyStartDate = CalendarUtil.parseDate(familyStartDateStr, FamilyAppConst.CALENDAR_DATE_FORMAT);
		
		FamilyProfile dbFamily = familyProfileService.getFamilyProfileByFamilyCode(familyCode);
		if(dbFamily == null){
			logger.info(FamilyAppConst.INVALID_FAMILY_CODE);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_CODE);
		}
		dbFamily.setDescription(description);
		dbFamily.setFamilyName(familyName);
		dbFamily.setFamilyStartDate(familyStartDate);
		
		if(obj.has("imageUrl")){
			String imageUrl = obj.getString("imageUrl");
			
			if(imageUrl.contains("base64")){
				String imageName = ImageUtil.writeImage(imageUrl,folderPath);
				dbFamily.setImageUrl(imageName);
			}
			else{
				String imageName = ImageUtil.getImageNameFromImageUrl(imageUrl);
				dbFamily.setImageUrl(imageName);
			}
		}	

		FamilyProfile familyProfile = familyProfileService.saveOrUpdateFamilyProfile(dbFamily);		
		familyProfile.getMembers();	
		
	
		//invite member
		if(obj.has("members")){
			List<String> inviteeList = new ArrayList<>();
			//invite member
			JSONArray jsonArr = obj.getJSONArray("members");
			for(int i=0;i<jsonArr.length();i++){
				JSONObject jsonObj = jsonArr.getJSONObject(i);
				String invitee = jsonObj.getString("member");
				inviteeList.add(invitee);
			}		
			if(!inviteeList.isEmpty()){
				String commaSperatedValue = inviteeList.stream().collect(Collectors.joining(","));
				mailService.sendFamilyInviteMail(familyProfile, commaSperatedValue);
			}
			
		}
		
		return new ResponseEntity<FamilyProfile>(familyProfile,HttpStatus.OK);
	}
	
	/**
	 * Get Family Name by Family Code
	 * @param familyCode
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/api/auth/familyprofile/get-familyname", method = RequestMethod.GET)
	public ResponseEntity<String> getFamilyNameByCode(@RequestParam("familyCode") String familyCode, HttpServletRequest request) throws FamilyAppWebserviceException, JSONException {
		logger.info("Get Family Name from Family Code::: ");
		FamilyProfile familyProfile = familyProfileService.getFamilyProfileByFamilyCode(familyCode);
		if(familyProfile == null){
			logger.info(FamilyAppConst.INVALID_FAMILY_CODE);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_CODE);
		}
		
		Map<String, String> resultMap = new HashMap<>();
		resultMap.put("familyName", familyProfile.getFamilyName());
		resultMap.put("message", "Success.");
		return new ResponseEntity<String>(new JSONObject(resultMap).toString(),HttpStatus.OK);
	}
	
	/**
	 * Invite Family Member by Family Creator
	 * @param familyCode
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/api/auth/familyprofile/invite", method = RequestMethod.POST)
	public ResponseEntity<String> inviteFamilyMember(@RequestBody String data, HttpServletRequest request) throws FamilyAppWebserviceException, JSONException {
		logger.info("Invite Family Member::: "+data);
		String currentUserEmail = RequestUtil.getEmailFromAuthentication();
		UserProfile currentUser = userService.findByEmail(currentUserEmail);
	
		JSONObject jsonObj = new JSONObject(data);
		String familyCode = jsonObj.getString("familyCode");
		String emails = jsonObj.getString("emails");
//		String[] recipients = emails.split(",");
		
		if(familyCode == null || familyCode.trim().isEmpty()){
			logger.info("Family Code is Required.");
			throw new FamilyAppWebserviceException("Family Code is Required.");
		}
		if(emails == null || emails.trim().isEmpty()){
			logger.info("Emails is Required.");
			throw new FamilyAppWebserviceException("Emails is Required.");
		}
		
		FamilyProfile familyProfile = familyProfileService.getFamilyProfileByFamilyCode(familyCode);
		
		//validate familyCode is valid or not
		if(familyProfile == null){
			logger.info(FamilyAppConst.INVALID_FAMILY_CODE);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_CODE);
		}
		
		//validate current user is family creator or not
		if(familyProfile.getFamilyCreatorId() != currentUser.getId()){
			logger.info("You don't have permission to add member.");
			throw new FamilyAppWebserviceException("You don't have permission to add member.");
		}

		mailService.sendFamilyInviteMail(familyProfile, emails);
		return new ResponseEntity<String>(StringUtils.responseString("Invite Success."),HttpStatus.OK);
	}
	
	/**
	 * Join Family
	 * @param data
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/api/auth/familyprofile/join", method = RequestMethod.POST)
	public ResponseEntity<String> joinFamily(@RequestBody String data, HttpServletRequest request) throws FamilyAppWebserviceException, JSONException {
		logger.info("Join Family ::: ");
		String currentUserEmail = RequestUtil.getEmailFromAuthentication();
		UserProfile currentUser = userService.findByEmail(currentUserEmail);
	
		JSONObject jsonObj = new JSONObject(data);
		String familyCode = jsonObj.getString("familyCode");
		
		if(familyCode == null || familyCode.trim().isEmpty()){
			logger.info("Family Code is Required.");
			throw new FamilyAppWebserviceException("Family Code is Required.");
		}
		
		FamilyProfile familyProfile = familyProfileService.getFamilyProfileByFamilyCode(familyCode);
		
		//validate familyCode is valid or not
		if(familyProfile == null){
			logger.info(FamilyAppConst.INVALID_FAMILY_CODE);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_CODE);
		}
		

		if(familyProfile.getFamilyName().equals(FamilyAppConst.DEFAULT_FAMILY_NAME)){
			logger.info(FamilyAppConst.NOT_ALLOW_TO_JOIN_PERSONAL);
			throw new FamilyAppWebserviceException(FamilyAppConst.NOT_ALLOW_TO_JOIN_PERSONAL);
		}
		
		//validate family already exist or not
		if(isAllowMultiFamily == false){
			validateFamilyAlreadyExistOrNot(currentUser.getId());		
		}
		else{
			//allow multi family
			//validate already join or not
			if(isAlreadyJoin(currentUser.getId(),familyProfile.getId()) == true){
				logger.info(FamilyAppConst.FAMILY_ALREADY_JOIN);
				throw new FamilyAppWebserviceException(FamilyAppConst.FAMILY_ALREADY_JOIN);
			}
		}
		FamilyJoinRequest joinRequest = new FamilyJoinRequest();
		joinRequest.setFamily(familyProfile);
		joinRequest.setUser(currentUser);
		joinRequest.setJoinStatus(JoinStatus.PENDING);
		joinRequest.setRequestDate(new Date());
		familyJoinRequestService.saveOrUpdateJoinRequest(joinRequest);
		
		return new ResponseEntity<String>(StringUtils.responseString("Join Request Success."),HttpStatus.OK);
	}
	
	/**
	 * Validate family already join or not
	 * @param userId
	 * @param familyId
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	private Boolean isAlreadyJoin(Long userId ,Long familyId ) throws FamilyAppWebserviceException{
		List<FamilyJoinRequest> joinRequests = familyJoinRequestService.getPendingJoinRequestByMemberId(userId);
		Boolean isJoin = false;
		for(FamilyJoinRequest req: joinRequests){
			if(req.getFamily().getId() == familyId && req.getUser().getId() == userId){
				isJoin = true;
			}
		}
		return isJoin;
	}
	
	/**
	 * validate family already create /join
	 * @param memberId
	 * @throws FamilyAppWebserviceException
	 */
	private void validateFamilyAlreadyExistOrNot(Long memberId) throws FamilyAppWebserviceException{
		List<FamilyMember> members = familyMemberService.getFamilyMemberListByMemberIdExceptPersonal(memberId);
		if(members.size() > 0){
			//already join
			logger.info(FamilyAppConst.FAMILY_ALREADY_CREATE_JOIN);
			throw new FamilyAppWebserviceException(FamilyAppConst.FAMILY_ALREADY_CREATE_JOIN);
		}
		
		List<FamilyJoinRequest> requestList = familyJoinRequestService.getPendingJoinRequestByMemberId(memberId);
		if(requestList.size() > 0){
			//pending request exist
			logger.info(FamilyAppConst.FAMILY_ALREADY_CREATE_JOIN);
			throw new FamilyAppWebserviceException(FamilyAppConst.FAMILY_ALREADY_CREATE_JOIN);
		}
	}
	
	/**
	 * Get Family Join Request List
	 * @param familyCode
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value = "/api/auth/familyprofile/join-request-list", method = RequestMethod.GET)
	public ResponseEntity<List<FamilyJoinRequest>> getJoinRequestList(@RequestParam("familyCode")String familyCode,HttpServletRequest request) throws FamilyAppWebserviceException{
		logger.info("Get Family Join Request List::");
		FamilyProfile family = familyProfileService.getFamilyProfileByFamilyCode(familyCode);
		if(family == null){
			logger.info(FamilyAppConst.INVALID_FAMILY_CODE);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_CODE);
		}
		
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		if(currentUser.getId() != family.getFamilyCreatorId()){
			logger.info(FamilyAppConst.MUST_BE_CREATOR);
			throw new FamilyAppWebserviceException(FamilyAppConst.MUST_BE_CREATOR);
		}
		List<FamilyJoinRequest> joinRequestList = familyJoinRequestService.getPendingJoinRequestByFamilyId(family.getId());
		return new ResponseEntity<List<FamilyJoinRequest>>(joinRequestList,HttpStatus.OK);
	}
	
	/**
	 * Get Family Member by Family ID
	 * @param familyId
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value = "/api/auth/get-family-member", method = RequestMethod.GET)
	public ResponseEntity<?> getFamilyMemberListByCurrentUser(@RequestParam("familyId") Long familyId,HttpServletRequest request) throws FamilyAppWebserviceException{
		logger.info("Get Family Members::");
				
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		FamilyProfile family = familyProfileService.findByFamilyProfileId(familyId);
		if(family == null){
			logger.info(FamilyAppConst.INVALID_FAMILY_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_ID);
		}
		
		List<FamilyMember> list = familyMemberService.getMemberListByFamilyId(familyId);
		
		List<UserProfile> members = new ArrayList<>();
		for(FamilyMember familyMember: list){
			members.add(familyMember.getMember());
		}
				
		return new ResponseEntity<List<UserProfile>>(members,HttpStatus.OK);
	}
	
	/**
	 * Approve or Reject Family Join Request by family creator
	 * @param data
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/api/auth/familyprofile/reply-joinrequest", method = RequestMethod.POST)
	public ResponseEntity<String> replyFamilyJoinRequest(@RequestBody String data,HttpServletRequest request) throws FamilyAppWebserviceException, JSONException{
		logger.info("Reply Family Join Request::");
		JSONObject obj = new JSONObject(data);		
		if(!obj.has("id")){
			logger.info("Id is required.");
			throw new FamilyAppWebserviceException("Id is required.");
		}
		if(!obj.has("joinStatus")){
			logger.info("Join Status Required.");
			throw new FamilyAppWebserviceException("Join Status Required.");
		}
		
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		FamilyJoinRequest joinRequest = familyJoinRequestService.getFamilyJoinRequestById(obj.getLong("id"));
		if(joinRequest == null){
			logger.info("Invalid ID.");
			throw new FamilyAppWebserviceException("Invalid ID.");
		}
		
		if(joinRequest.getFamily().getFamilyCreatorId() != currentUser.getId()){
			logger.info(FamilyAppConst.PERMISSION_DENIED);
			throw new FamilyAppWebserviceException(FamilyAppConst.PERMISSION_DENIED);
		}
		JoinStatus joinStatus = JoinStatus.valueOf(obj.getString("joinStatus"));
		if(joinStatus == JoinStatus.APPROVED){
			joinRequest.setJoinStatus(JoinStatus.APPROVED);
			joinRequest.setResponseDate(new Date());
			
			//save to member table
			FamilyMember member = new FamilyMember();
			member.setMember(joinRequest.getUser());
			member.setFamily(joinRequest.getFamily());
			member.setJoinDate(new Date());
			member.setRequestStatus(JoinStatus.JOINED);
			member.setIsFamilyCreator(false);
			familyMemberService.saveOrUpdate(member);
			
			//Get personal family for requested user
			FamilyProfile personalFamily = familyProfileService.getPersonalFamilyByCreatorId(joinRequest.getUser().getId());
			
			if(personalFamily != null){
				//leave from family
				personalFamily.setStatus(Status.I);
				familyProfileService.saveOrUpdateFamilyProfile(personalFamily);
				
				//all topic move to new family	
				discussionService.replaceFamilyIdInTopic(personalFamily.getId(), joinRequest.getFamily().getId());
				
				//all event move to new family
				calendarEventService.replaceFamilyIdInEvent(personalFamily.getId(), joinRequest.getFamily().getId());
				
				//all checklist move to new family
				checkListService.replaceFamilyIdInCheckList(personalFamily.getId(), joinRequest.getFamily().getId());
				
				//update personal family as leave family
				familyMemberService.updateFamilyMemberStatusByFamilyId(personalFamily.getId(), JoinStatus.LEAVE,joinRequest.getUser().getId());
				
			}
			
			//update current user is_login flag
			currentUser.setIsLogin(true);
			userService.saveOrUpdateUser(currentUser);
					
		}
		else if(joinStatus == JoinStatus.REJECT){
			joinRequest.setJoinStatus(JoinStatus.REJECT);
			joinRequest.setResponseDate(new Date());
		}
		else{
			logger.info("Invalid Join Status.");
			throw new FamilyAppWebserviceException("Invalid Join Status.");
		}
		familyJoinRequestService.saveOrUpdateJoinRequest(joinRequest);
				
		return new ResponseEntity<String>(StringUtils.responseString("Success."),HttpStatus.OK);
	}
	
	/**
	 * Cancel Family Join Request by requested user
	 * @param data
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/api/auth/familyprofile/cancel", method = RequestMethod.POST)
	public ResponseEntity<String> cancelFamilyJoinRequest(@RequestBody String data,HttpServletRequest request) throws FamilyAppWebserviceException, JSONException{
		logger.info("Cancel Family Join Request::");
		JSONObject obj = new JSONObject(data);		
		if(!obj.has("id")){
			logger.info("Id is required.");
			throw new FamilyAppWebserviceException("Id is required.");
		}
		
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		FamilyJoinRequest joinRequest = familyJoinRequestService.getFamilyJoinRequestById(obj.getLong("id"));
		if(joinRequest == null){
			logger.info("Invalid ID.");
			throw new FamilyAppWebserviceException("Invalid ID.");
		}
		
		//only accessable by requested user
		if(joinRequest.getUser().getId() != currentUser.getId()){
			logger.info(FamilyAppConst.PERMISSION_DENIED);
			throw new FamilyAppWebserviceException(FamilyAppConst.PERMISSION_DENIED);
		}
		
		joinRequest.setJoinStatus(JoinStatus.CANCEL);
		joinRequest.setResponseDate(new Date());
		
		familyJoinRequestService.saveOrUpdateJoinRequest(joinRequest);
				
		return new ResponseEntity<String>(StringUtils.responseString("Cancel Join Request Success."),HttpStatus.OK);
	}
	

	/**
	 * Remove member from family by family creator
	 * @param data
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/api/auth/familyprofile/remove-member", method = RequestMethod.POST)
	public ResponseEntity<String> removeFamilyMember(@RequestBody String data,HttpServletRequest request) throws FamilyAppWebserviceException, JSONException{
		logger.info("Remove Family Member::");
		JSONObject obj = new JSONObject(data);		
		if(!obj.has("memberId")){
			logger.info("Member Id is required.");
			throw new FamilyAppWebserviceException("Member Id is required.");
		}
		if(!obj.has("familyId")){
			logger.info("Family Id is required.");
			throw new FamilyAppWebserviceException("Family Id is required.");
		}
		Long memberId = obj.getLong("memberId");
		Long familyId = obj.getLong("familyId");
		
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		FamilyMember familyMember = familyMemberService.getFamilyMemberByMemberIdFamilyId(memberId,familyId);
		if(familyMember == null){
			logger.info("Invalid Family Id or Invalid member Id.");
			throw new FamilyAppWebserviceException("Invalid Family Id or Invalid Member Id.");
		}
		
		
		//only allow creator
		if(familyMember.getFamily().getFamilyCreatorId() != currentUser.getId()){
			logger.info(FamilyAppConst.PERMISSION_DENIED);
			throw new FamilyAppWebserviceException(FamilyAppConst.PERMISSION_DENIED);
		}
		
		if(memberId == familyMember.getFamily().getFamilyCreatorId()){
			logger.info(FamilyAppConst.NOT_ALLOW_TO_DELETE_CREATOR);
			throw new FamilyAppWebserviceException(FamilyAppConst.NOT_ALLOW_TO_DELETE_CREATOR);
		}
		
		familyMember.setRequestStatus(JoinStatus.REMOVE);
		familyMember.setStatus(Status.I);
		
		familyMemberService.saveOrUpdate(familyMember);
		
		List<FamilyProfile> familyList = familyProfileService.getFamilyExceptPersonal(memberId);
		Boolean isExistOtherFamily = false;
		for(FamilyProfile fp: familyList){
			if(fp.getId() != familyId){
				isExistOtherFamily = true;
				break;
			}
		}	
		
		if(isExistOtherFamily == false){
			//no family 
			FamilyProfile personalFamily = familyProfileService.getInactivePersonalFamily(memberId);
			if(personalFamily == null){
				logger.info("Dont have personal family "+memberId);
				throw new FamilyAppWebserviceException("Don't have inactive personal family.");
			}
			personalFamily.setStatus(Status.A);
			familyProfileService.saveOrUpdateFamilyProfile(personalFamily);
			
			//update personal family as JOIN family
			familyMemberService.updateFamilyMemberStatusByFamilyId(personalFamily.getId(), JoinStatus.JOINED,memberId);
			
		}
		else{
			//exist family so this user will continue using existing family
			//nothing to do
		}
		
//		// update removing member's topic status to I
//		discussionService.updateTopicStatusByFamilyIdCreatorId(familyId,memberId,Status.I);
//		
//		// update removing member's event status to I
//		calendarEventService.updateEventStatusByFamilyIdCreatorId(familyId,memberId,Status.I);
//						
		return new ResponseEntity<String>(StringUtils.responseString("Remove Family Member Success."),HttpStatus.OK);
	}
	
	/**
	 * Leave From Family
	 * @param data
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/api/auth/familyprofile/leave-family", method = RequestMethod.POST)
	public ResponseEntity<String> leaveFromFamily(@RequestBody String data,HttpServletRequest request) throws FamilyAppWebserviceException, JSONException{
		logger.info("Leave Family Member::");
		JSONObject obj = new JSONObject(data);		
		if(!obj.has("familyCode")){
			logger.info("Family Code is required.");
			throw new FamilyAppWebserviceException("Family Code is required.");
		}
		
		UserProfile currentUser = userService.findByEmail(RequestUtil.getEmailFromAuthentication());
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		
		
		FamilyProfile family = familyProfileService.getFamilyProfileByFamilyCode(obj.getString("familyCode"));
		
		if(family == null){
			logger.info(FamilyAppConst.INVALID_FAMILY_CODE);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_CODE);
		}
		
		if(family.getFamilyCreatorId() == currentUser.getId()){
			//Creator
			if(family.getFamilyMemberList().size() > 1){ //coz default one member can have for creator
				//not allow to leave because family have members
				logger.info(FamilyAppConst.NOT_ALLOW_TO_LEAVE_FAMILY);
				throw new FamilyAppWebserviceException(FamilyAppConst.NOT_ALLOW_TO_LEAVE_FAMILY);
			}
			else{ 
				family.setStatus(Status.I);
				familyProfileService.saveOrUpdateFamilyProfile(family);
				familyMemberService.updateFamilyMemberStatusByFamilyId(family.getId(),JoinStatus.LEAVE,currentUser.getId());
			}	
			
		}
		else{
			//Just Member
			FamilyMember member = familyMemberService.getFamilyMemberByMemberIdFamilyId(currentUser.getId(),family.getId());
			if(member == null){
				logger.info("not a member of "+family.getFamilyName());
				throw new FamilyAppWebserviceException("Not a member of "+family.getFamilyName());
			}
			member.setRequestStatus(JoinStatus.LEAVE);
			member.setStatus(Status.I);
			familyMemberService.saveOrUpdate(member);
		}		
				
		Boolean isExistOtherFamily = false;
		for(FamilyMember fm: currentUser.getFamilyMemberList()){
			//not personal and current leaving family
			if(!fm.getFamily().getFamilyName().equals(FamilyAppConst.DEFAULT_FAMILY_NAME) && family.getId() != fm.getFamily().getId()){
				//other family exist 
				isExistOtherFamily = true;break;
			}
		}
		if(isExistOtherFamily == false){
			//no family 
			FamilyProfile personalFamily = familyProfileService.getInactivePersonalFamily(currentUser.getId());
			if(personalFamily == null){
				logger.info("Dont have personal family "+currentUser.getUsername());
				throw new FamilyAppWebserviceException("Don't have personal family.");
			}
			personalFamily.setStatus(Status.A);
			familyProfileService.saveOrUpdateFamilyProfile(personalFamily);
			
			//update personal family as JOIN family
			familyMemberService.updateFamilyMemberStatusByFamilyId(personalFamily.getId(), JoinStatus.JOINED,currentUser.getId());
			
		}
		else{
			//exist family so this user will continue using existing family
			//nothing to do
		}
		
		return new ResponseEntity<String>(StringUtils.responseString("Leave Family Success."),HttpStatus.OK);
	}
	
	/**
	 * Get All Family List except Personal Family
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value = "/api/auth/familyprofile/family-enquiry", method = RequestMethod.GET)	
	public ResponseEntity<List<FamilyProfile>> getFamilyList(HttpServletRequest request) throws FamilyAppWebserviceException{
		
		String email = RequestUtil.getEmailFromAuthentication();
		UserProfile currentUser =  userService.findByEmail(email);
		if(currentUser == null){
			logger.info(FamilyAppConst.NO_USER_FOUND);
			throw new FamilyAppWebserviceException(FamilyAppConst.NO_USER_FOUND);
		}
		List<FamilyJoinRequest> joinRequestList = familyJoinRequestService.getJoinRequestListByCreatorId(currentUser.getId());
		Map<Long,List<FamilyJoinRequest>> requestMap = joinRequestList.stream().collect(
													Collectors.groupingBy(
																result-> result.getFamily().getId()));
		
		List<FamilyProfile> list = familyProfileService.getFamilyExceptPersonal(currentUser.getId());
		for(FamilyProfile profile: list){
			
			//flag for creator or not
			if(profile.getFamilyCreatorId() == currentUser.getId()){
				profile.setIsFamilyCreator(true);
			}
			else{
				profile.setIsFamilyCreator(false);
			}
			if(requestMap.containsKey(profile.getId())){
				profile.setJoinRequestList(requestMap.get(profile.getId()));
			}			
			
			List<UserProfile> members = new ArrayList<>();
			for(FamilyMember familymember: profile.getFamilyMemberList()){
				UserProfile temp = new UserProfile();
				temp = familymember.getMember();				
				members.add(temp);
			}
			profile.setMembers(members);
				
		}		
		
		return new ResponseEntity<List<FamilyProfile>>(list,HttpStatus.OK);
	}
}
