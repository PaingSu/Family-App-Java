package com.startinpoint.proj.familyapp.webservice.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
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

import com.startinpoint.proj.familyapp.util.JsonUtil;
import com.startinpoint.proj.familyapp.util.RequestUtil;
import com.startinpoint.proj.familyapp.util.StringUtils;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyAppConst;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyMember;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyProfile;
import com.startinpoint.proj.familyapp.webservice.entity.Topic;
import com.startinpoint.proj.familyapp.webservice.entity.TopicComment;
import com.startinpoint.proj.familyapp.webservice.entity.TopicMember;
import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;
import com.startinpoint.proj.familyapp.webservice.entity.enums.ScrollStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.entity.pojo.Comment;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.DiscussionService;
import com.startinpoint.proj.familyapp.webservice.service.FamilyMemberService;
import com.startinpoint.proj.familyapp.webservice.service.FamilyProfileService;
import com.startinpoint.proj.familyapp.webservice.service.UserService;

/**
 * @author ThoonSandy
 * @version 1.0
 * @since 27-04-2018
 */
@Transactional
@Controller
public class FamilyDiscussionController {

	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	DiscussionService discussionService;

	@Autowired
	UserService userService;

	@Autowired
	FamilyProfileService familyProfileService;

	@Autowired
	FamilyMemberService familyMemberService;
	


	/**
	 * Create Topic
	 * 
	 * @param discussion
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/api/auth/topic/create", method = RequestMethod.POST)
	public ResponseEntity<?> saveFamilyDiscussion(@Valid @RequestBody String data, HttpServletRequest request)
			throws FamilyAppWebserviceException, JSONException {
		// TODO Notification to send to member in this topic
		logger.info("save family discussion...");

		String userEmail = RequestUtil.getEmailFromAuthentication();
		UserProfile userProfile = userService.findByEmail(userEmail); // get
																		// authentication
																		// userId

		if (userProfile == null) {
			logger.info(FamilyAppConst.INVALID_USER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
		}
		Long userId = userProfile.getId();

		// get data by jsonObject&array from request
		JSONObject json = new JSONObject(data);
		// System.out.println("json data..."+data);
		String topicName = json.getString("topicTitle");
		System.out.println("topic name..." + topicName);
		String topicDesc = json.getString("description");
		Long familyProfileId = json.getLong("familyProfileId");

		// assign to add topicMember lists
		List<TopicMember> topicMemberList = new ArrayList<TopicMember>();
		// assign to member Lists from client
		List<Long> memberList = new ArrayList<Long>();

		JSONArray jsonArray = json.getJSONArray("memberLists");
		// System.out.println("json array..."+jsonArray);
		if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonobject = jsonArray.getJSONObject(i);
				Long memberId = jsonobject.getLong("memberId");

				// String memberName= jsonobject.getString("name");
				// String imgUrl = jsonobject.getString("avatarImage");
				memberList.add(memberId);
				System.out.println("member id");
				System.out.println(memberId);
			}
		}
		//filer memberId List for not want to get creatorId
		List<Long>getMemberList_NotTopicCreator=new ArrayList<>();
		for (Long Not_TopicCreatorId : memberList) {
			if(userId != Not_TopicCreatorId){
				getMemberList_NotTopicCreator.add(Not_TopicCreatorId);
			}
		}
		
		//check family profile whether exist or not and inactive by familyProfileId
		FamilyProfile familyProfile = familyProfileService.findByFamilyProfileId(familyProfileId);
		if (familyProfile == null) {
		logger.info(FamilyAppConst.INVALID_FAMILY_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_ID);
		}else if(familyProfile.getStatus().equals(Status.I)){
		logger.info(FamilyAppConst.INACTIVE_FAMILY_PROFILE);
		throw new FamilyAppWebserviceException(FamilyAppConst.INACTIVE_FAMILY_PROFILE);
		}
				
		//check whether for login userId is family member or not by familyProfileId
		FamilyMember family_Member = familyMemberService.getFamilyMemberByMemberIdFamilyId(userId, familyProfileId);
		if (family_Member == null) {
			logger.info(FamilyAppConst.INVALID_FAMILY_MEMBER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_MEMBER_ID);
		}
		
		//only family_member can create topic
		Topic topic = new Topic();
		topic.setCreatedDate(new Date());
		topic.setDescription(topicDesc);
		topic.setTopicTitle(topicName);
		topic.setTopicCreator(userId);
		topic.setTopicStatus(Status.A);
		// topic.setFamilyProfileId(familyProfileId);
		topic.setModifiedDate(new Date());
		topic.setFamilyProfile(familyProfile);

		List<FamilyMember> getMemberList = familyProfile.getFamilyMemberList(); // get member list by family profile Id form db
																	
		
		for (Long memberId : getMemberList_NotTopicCreator) {
			
			Boolean flag = false;
			for (FamilyMember familyMember : getMemberList) {
				UserProfile member = familyMember.getMember(); 
				if (member.getId() == (memberId)) { // check memberId whether same family memberId or not									
					flag = true;
					TopicMember topicMember = new TopicMember();
					topicMember.setCreatedDate(new Date());
					topicMember.setMemberId(memberId);
					topicMember.setTopic(topic);
					topicMember.setMember(member);
					topicMember.setMemberStatus(Status.A);
					topicMemberList.add(topicMember);
					System.out.println("flag is true==" + member.getId() + "    " + (memberId));
					break;
				}

			} // end for loop
			if (flag == false) {
				//System.out.println("throw exceptions==" + member.getId() + "    " + (memberId));
				logger.info(FamilyAppConst.INVALID_FAMILY_MEMBER_ID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_MEMBER_ID);

			}
			

		} // end for loop

		// add member for topic creator
		TopicMember topicMember = new TopicMember();
		topicMember.setCreatedDate(new Date());
		topicMember.setMemberId(userId);
		topicMember.setTopic(topic);
		topicMember.setMemberStatus(Status.A);
		topicMember.setMember(userProfile);
		topicMemberList.add(topicMember);

		topic.setTopicMemberLists(topicMemberList);
		topic = discussionService.createFamilyDiscussion(topic);  //save family topic
		
		// to reply for user member Lists
		List<TopicMember>getReplyToTopicMemberList=topic.getTopicMemberLists(); //get topic member lists
		
		List<UserProfile>getReplyToMemberList=new ArrayList<>();
		for (TopicMember topicMem : getReplyToTopicMemberList) {
			getReplyToMemberList.add(topicMem.getMember());
		}
		topic.setMemberLists(getReplyToMemberList);
		return new ResponseEntity<>(JsonUtil.pojoToJson(topic), HttpStatus.OK);

	}

	/**
	 * Edit Topic
	 * 
	 * @param editDiscussion
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 */

	@RequestMapping(value = "/api/auth/topic/edit", method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<?> editFamilyDiscussion(@RequestBody String editTopic, HttpServletRequest request,
			HttpServletResponse response) throws FamilyAppWebserviceException, JSONException {
		logger.info("update family all discussion...");		
		String username = RequestUtil.getEmailFromAuthentication();
		UserProfile userProfile = userService.findByEmail(username);
		if (userProfile == null) {
			logger.info(FamilyAppConst.INVALID_USER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
		}
		Long userId = userProfile.getId();
		
		// get data by jsonObject&array from request
		JSONObject json = new JSONObject(editTopic);
		// System.out.println("json data..."+editTopic);
		Long topicId = json.getLong("id");
		String topicName = json.getString("topicTitle");
		String topicDesc = json.getString("description");
			
		// assign to  member Lists from request
		List<Long> memberList = new ArrayList<Long>();
		JSONArray jsonArray = json.getJSONArray("memberLists"); //get  member Lists from request
		// System.out.println("json array..."+jsonArray);
		if (jsonArray != null) 
		{
			for (int i = 0; i < jsonArray.length(); i++) 
			{
			JSONObject jsonobject = jsonArray.getJSONObject(i);
			Long memberId = jsonobject.getLong("memberId");	
			memberList.add(memberId);
		
			}
		}
		
		Topic editDiscuss = discussionService.findTopicById(topicId);
		if (editDiscuss == null) {
			logger.info(FamilyAppConst.INVALID_TOPICID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPICID);
		} 
		else if(editDiscuss.getTopicStatus().equals(Status.I)){
			logger.info(FamilyAppConst.INVALID_TOPIC_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_ID);
		}
	 	
		Long topic_creatorId = editDiscuss.getTopicCreator(); //get topic creator
		FamilyProfile family_profile = editDiscuss.getFamilyProfile(); 
		Long familyProfileId=family_profile.getId();  //get familyprofileId by topicId
		Long topicID=editDiscuss.getId();	//get topic Id from db
		
		if (topic_creatorId.equals(userId)) // check whether same or not topic creator and userId
		{  
		System.out.println("same topic-creator id and userid");
			
		//check whether familyMember or not for login userId by family profile Id
		FamilyMember familyMember=familyMemberService.getFamilyMemberByMemberIdFamilyId(userId, familyProfileId);
		if(familyMember == null){
		logger.info(FamilyAppConst.INVALID_FAMILY_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_ID);
		}
		//get and check new member who are same family profile or not
		UserProfile member=null;
		FamilyProfile familyProfile = familyProfileService.findByFamilyProfileId(familyProfileId);
		if (familyProfile == null) {
		logger.info(FamilyAppConst.INVALID_FAMILY_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_ID);
		}
		List<FamilyMember> getFamilyMemberList = familyProfile.getFamilyMemberList(); // get familyMemberList by familyProfileId
		
		for (Long memberID : memberList) {		
			Boolean flag=false;	
				for (FamilyMember memberFamily : getFamilyMemberList) {
				   member=memberFamily.getMember(); //get family member from familyMemberList
				   if(member.getId() == memberID){  //check whether new member who are same family profile 
					   flag=true;
					   System.out.println("flag is true");
				    }
				}//end for loop
					if(flag == false){
					System.out.println("member who aren't same family profile");
					logger.info(FamilyAppConst.INVALID_FAMILY_MEMBER_ID);
					throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_FAMILY_MEMBER_ID);
					}
					
			}//end for loop
			
		
		//only can edit the topic creator	
		editDiscuss.setTopicTitle(topicName);
		editDiscuss.setDescription(topicDesc);
		editDiscuss.setModifiedDate(new Date());

		List<TopicMember> getMemberList = editDiscuss.getTopicMemberLists(); //get topic member lists from DB
		List<Long> getMemberList_NotTopicCreator=new ArrayList<Long>(); //create empty array to get member List for not topic creator
		List<TopicMember> topicMemberList = new ArrayList<>(); //create empty list to return filter list for topic member lists
		for(Long memberId: memberList) {
			Boolean isContain=false;
			for (TopicMember topicMember : getMemberList) {					
				member=topicMember.getMember(); //get topic member from topicMemberList
			        if (memberId == member.getId() )
					{
						isContain=true;
						System.out.println("request memberId same as with db memberId...");
						if(topicMember.getMemberStatus().equals(Status.I)){
							//System.out.println("change status to active..."+memberId+" ,"+topicID+" ,"+topicMember.getMemberStatus());
							discussionService.updateTopicMemberByStatus(topicID, memberId, Status.A);
						}
					}      
				}//end for loop
					if(isContain == false)
					{
					System.out.println("member who aren't same as with member from db");
					TopicMember topicMember = new TopicMember();
					topicMember.setCreatedDate(new Date());
					topicMember.setMemberId(memberId);
					topicMember.setTopic(editDiscuss);
					topicMember.setMember(member);
					topicMember.setMemberStatus(Status.A);
					topicMemberList.add(topicMember); //add new member by same family member
					
					}//end if contain false
				
			}//end for loop
			editDiscuss.setTopicMemberLists(topicMemberList);
			discussionService.createFamilyDiscussion(editDiscuss); //update Family Topic
								
			if(memberList.size() > 0)
			{	
			memberList.add(topic_creatorId);
			discussionService.updateFamilyDiscussionByStatus(topicID,memberList,Status.I);//update TopicMember 
			}//end if
			else{
				getMemberList_NotTopicCreator.add(topic_creatorId);
				discussionService.updateFamilyDiscussionByStatus(topicID,getMemberList_NotTopicCreator,Status.I);//update TopicMember
			}
		
		} else {
			
			System.out.println("Not same topic-creator id and userid");
			logger.info(FamilyAppConst.INVALID_TOPIC_CREATOR);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_CREATOR);
		}
		
		// to reply for user member Lists
        List<TopicMember>getReplyToTopicMemberList=editDiscuss.getTopicMemberLists(); //get topic member lists
		
		List<UserProfile>getReplyToMemberList=new ArrayList<>();
		for (TopicMember topicMem : getReplyToTopicMemberList) {
			getReplyToMemberList.add(topicMem.getMember());
		}
		editDiscuss.setMemberLists(getReplyToMemberList);
	
		return new ResponseEntity<>(JsonUtil.pojoToJson(editDiscuss), HttpStatus.OK);
	}

/*	public static void main(String[] args) {
		List<Long> list1 = new ArrayList<>();
		list1.add(3l);
		list1.add(4l);
//------------
		List<Long> list2 = new ArrayList<>();
		list2.add(2l);
		list2.add(3l);
		for (Long memberId : list1) {
			Boolean isContain = false;
			for (Long familyMember : list2) {
				if (memberId == familyMember) {
					isContain = true;
				System.out.println(memberId + " " + familyMember);
				}			
			}
			if (isContain == false) {
				System.out.println("throw ex");
			}
		}
	}*/

	/**
	 * Delete Topic
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value = "/api/auth/topic/delete/{id}", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> deleteFamilyDiscussion(@PathVariable("id") Long id, HttpServletRequest request,
			HttpServletResponse response) throws FamilyAppWebserviceException {

		logger.info("delete family discussion...");

		Topic discuss = discussionService.findTopicById(id);
		//System.out.println("topic object..."+discuss);
		if (discuss == null) {
			logger.info(FamilyAppConst.INVALID_TOPICID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPICID);
		} 
		else if(discuss.getTopicStatus().equals(Status.I)){
			logger.info(FamilyAppConst.INVALID_TOPIC_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_ID);
		}
		
		Long topi_creatorId = discuss.getTopicCreator(); //get topic creator
		List<TopicMember> topicMemberList=discuss.getTopicMemberLists(); //get topic member Lists
		
		String userEmail = RequestUtil.getEmailFromAuthentication();
		UserProfile userProfile = userService.findByEmail(userEmail);
		if (userProfile == null) {
			logger.info(FamilyAppConst.INVALID_USER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
		}
		Long userId = userProfile.getId();
		//	Only the topic creator can delete the topic information.
		if (userId == topi_creatorId) { //check whether user who aren't same as topic creator or not
			discussionService.updateTopicByStatusChange(discuss.getId(),Status.I);	//update topic table
			
			for (TopicMember topicMember : topicMemberList) {
				List<Long>memberIdList=new ArrayList<Long>();
				memberIdList.add(topicMember.getMemberId());
				discussionService.changeStatusALLTopicMemberLists(discuss.getId(),memberIdList, Status.I);//update topic member
				
				discussionService.changeStatusALLTopicCommentMemberLists(discuss.getId(),Status.I); //update topic comment
			}
			
		} else {
			logger.info(FamilyAppConst.INVALID_TOPIC_CREATOR_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_CREATOR_ID);
		}
		return new ResponseEntity<>(StringUtils.responseString("Success"), HttpStatus.OK);
	}
	/**
	 * Remove Topic Member by Topic Creator
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value = "/api/auth/topic/remove/{id}", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> removeFamilyMemberDiscussion(@PathVariable("id") Long id, HttpServletRequest request,
			HttpServletResponse response) throws FamilyAppWebserviceException {

		logger.info("remove topic member in discussion...");
		
		String userEmail = RequestUtil.getEmailFromAuthentication();
		UserProfile userProfile = userService.findByEmail(userEmail);
		if (userProfile == null) {
			logger.info(FamilyAppConst.INVALID_USER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
		}
		Long userId = userProfile.getId(); 
		
		TopicMember topicMember= discussionService.findByMemberId(id);
		if (topicMember == null) {
			logger.info(FamilyAppConst.INACTIVE_TOPIC_MEMBER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INACTIVE_TOPIC_MEMBER_ID);
		}
		Topic topic=topicMember.getTopic();
		Long topic_creatorId = topic.getTopicCreator(); //get topic creator
	//	Only the topic creator can remove the topic member.
		if (userId == topic_creatorId) // check whether topic creator same or not userId
		{
			discussionService.updateTopicMemberByStatus(topic.getId(),topicMember.getId(),Status.I);
		} else {
			logger.info(FamilyAppConst.INVALID_TOPIC_CREATOR_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_CREATOR_ID);
		}
		return new ResponseEntity<>(StringUtils.responseString("Success"), HttpStatus.OK);
	}

	/**
	 * View Topic View All Topic By UserId
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value = "/api/auth/topic/view", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> findAllDiscussion(HttpServletRequest request, HttpServletResponse response)
			throws FamilyAppWebserviceException {

		logger.info("view all discussion...");
		String username = RequestUtil.getEmailFromAuthentication();
		UserProfile userProfile = userService.findByEmail(username);// get user
																	// authentication
																	// from
																	// email
		if (userProfile == null) {
			logger.info(FamilyAppConst.INVALID_USER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
		}
		Long userId = userProfile.getId(); // get login memberId
		
		// get topic member Lists by login memberId
		List<TopicMember> getTopicMemberLists = discussionService.findAllTopicMemberListByActive(userId,
				FamilyAppConst.VIEW_TOPIC_SIZE);
		System.out.println("topic member list..." + getTopicMemberLists.size());
		if(getTopicMemberLists.size() > 0){	
		List<Topic> getDiscussionList = new ArrayList<Topic>();	
		List<Long> getTopicIdList=new ArrayList<Long>(); //get topic Id List
		
		for (TopicMember topicMember : getTopicMemberLists) {
			Topic topic = topicMember.getTopic();//get topic status by active
			if(topicMember.getMemberId() == userId ){
				getTopicIdList.add(topic.getId());
			}
			else{
				logger.info(FamilyAppConst.INVALID_TOPIC_MEMBERID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_MEMBERID);	
			}
		
			getDiscussionList=discussionService.getViewAllFamilyDiscussion(getTopicIdList, FamilyAppConst.VIEW_TOPIC_SIZE,Status.A);		
		}
		
		List<Object[]>getCommentCountList = discussionService.findAllCommentCountList(Status.A,getTopicIdList);	
		//System.out.println("comment list..."+getCommentCountList.size());
		for (Topic topic : getDiscussionList){ //change object in the topic member Lists to user member Lists for reply
			
			for(int i=0;i<getCommentCountList.size(); i++)
			{			
				Object[] obj = getCommentCountList.get(i);
				Integer count=Integer.valueOf(obj[0].toString());
				Long topicId=Long.valueOf(obj[1].toString());
				System.out.println("count..."+count+",,,topicId..."+topicId);
				if(topic.getId() == topicId){
					topic.setCommentCount(count);	
				}
			}			
		List<TopicMember> topicMemList = topic.getTopicMemberLists();
		List<UserProfile> getReplyToMemberList = new ArrayList<>();		
			for (TopicMember topicMem : topicMemList) {
				if(topicMem.getMemberStatus().equals(Status.A))
				{
				getReplyToMemberList.add(topicMem.getMember());	
				}
			}
		topic.setMemberLists(getReplyToMemberList);
		topic.setMemberCount(getReplyToMemberList.size());	
		}
		return new ResponseEntity<>(getDiscussionList, HttpStatus.OK);
		
		}
		else{
			logger.info(FamilyAppConst.INVALID_TOPIC_MEMBERID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_MEMBERID);
		}
		
			
	}


	/**
	 * View Topic Detail By topicId
	 * 
	 * @param topic_id
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value = "/api/auth/topic/view/detail/{topic_id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> getTopicDetails(@PathVariable("topic_id") Long topicId, HttpServletRequest request,
			HttpServletResponse response) throws FamilyAppWebserviceException {
		logger.info("get view details toppic...");
		String userEmail = RequestUtil.getEmailFromAuthentication();
		UserProfile userProfile = userService.findByEmail(userEmail);
		if (userProfile == null) {
			logger.info(FamilyAppConst.INVALID_USER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
		}
		Long userId = userProfile.getId(); 
		Topic topic=null;
		
		List<TopicMember> topicMemberList = discussionService.findAllTopicMemberList(userId, topicId, null)	; //get topic member lists
		System.out.println("topic member list..."+topicMemberList.size());
	
		if(topicMemberList.size() > 0)
		{
		for (TopicMember member : topicMemberList) {
			Boolean flag=false;
		        topic= member.getTopic();
				if(topic==null)
				{
					logger.info(FamilyAppConst.INVALID_TOPICID);
					throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPICID);	
				}
				else if(topic.getTopicStatus().equals(Status.I)){
					logger.info(FamilyAppConst.INVALID_TOPIC_ID);
					throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_ID);
				}
				else if(member.getMemberStatus().equals(Status.A)){
				flag=true;	
				break;
				}
					if(flag==false)
					{
						logger.info(FamilyAppConst.INACTIVE_TOPIC_MEMBER_ID);
						throw new FamilyAppWebserviceException(FamilyAppConst.INACTIVE_TOPIC_MEMBER_ID);	
					}
		  }//end for loop
		
		}//end if
		
		else{
			logger.info(FamilyAppConst.INVALID_TOPIC_AND_TOPICMEMBERID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_AND_TOPICMEMBERID);
		}
		List<TopicMember>getTopicMemberList=topic.getTopicMemberLists();//get topic member list from topic data		
		//to reply for user Member Lists	
		List<UserProfile>getReplyToMemberList=new ArrayList<>();
		//System.out.println("topic member list..."+getTopicMemberList.size());
		if(getTopicMemberList.size() > 0)	{
		for (TopicMember topicMem : getTopicMemberList) {
			
			if(topicMem.getMemberStatus().equals(Status.A)){
				getReplyToMemberList.add(topicMem.getMember());
			}
		}
		
	    }
		topic.setMemberLists(getReplyToMemberList);				
		return new ResponseEntity<>(JsonUtil.pojoToJson(topic), HttpStatus.OK);

	}


	/**
	 * Create Comment
	 * 
	 * @param discussionComments
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/api/auth/comment/create", method = RequestMethod.POST)
	public ResponseEntity<?> createDiscussionComments(@Valid @RequestBody String topicComments,
			HttpServletRequest request) throws FamilyAppWebserviceException, JSONException {
		// TODO Notification to send to member in this topicComments
		String username = RequestUtil.getEmailFromAuthentication();
		UserProfile userProfile = userService.findByEmail(username);
		if (userProfile == null) {
			logger.info(FamilyAppConst.INVALID_USER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
		}
		Long userId = userProfile.getId();
		
		// get data by jsonObject from request
		JSONObject json = new JSONObject(topicComments);
	    System.out.println("json data..."+json);
		String comment = json.getString("comment");
	    System.out.println("comment name..." + comment);
		Long topicId = json.getLong("topicId");
		System.out.println("topic id..." + topicId);
				
		// get topic member Lists by login memberId,topicId
		List<TopicMember> getTopicMemberLists = discussionService.findAllTopicMemberList(userId,topicId,
				FamilyAppConst.VIEW_TOPIC_SIZE);
		System.out.println("topic member list..." + getTopicMemberLists.size());
		TopicComment topicComment=null;
		
		if(getTopicMemberLists.size() > 0)
		{	
		Boolean flag=false;
		for (TopicMember topicMember : getTopicMemberLists) {	
		    Topic topic=topicMember.getTopic();
			if(topic==null)
			{
				logger.info(FamilyAppConst.INVALID_TOPICID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPICID);	
			}
			else if(topic.getTopicStatus().equals(Status.I)){
				logger.info(FamilyAppConst.INVALID_TOPIC_ID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_ID);
			}
			else if(topicMember.getMemberStatus().equals(Status.A))
			{	
				topicComment=new TopicComment();
				topicComment.setComment(comment);
				topicComment.setCommentDate(new Date());
				topicComment.setUpdatedDate(new Date());
				topicComment.setTopicId(topicMember.getTopicId());
				//topicComment.setTopic(topicMember.getTopic());
				//topicComment.setTopicMember(currentUser);
				topicComment.setTopicMemberId(topicMember.getId());
				topicComment.setCommentStatus(Status.A);
				topicComment = discussionService.createDiscussionComments(topicComment);
				topic.setModifiedDate(new Date());
				discussionService.createFamilyDiscussion(topic); //update topic modified date
				flag=true;
				break;
			}
			
			
		}//end for loop
			if(flag==false){
			logger.info(FamilyAppConst.INACTIVE_TOPIC_MEMBER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INACTIVE_TOPIC_MEMBER_ID);
			}
		return new ResponseEntity<>(topicComment, HttpStatus.OK);
	}//end if	
		else{
			return new ResponseEntity<>(StringUtils.responseString(FamilyAppConst.INVALID_TOPIC_AND_TOPICMEMBERID), HttpStatus.OK);
		}
	
	}

	/**
	 * Get Comments
	 * 
	 * View All Comments by TopicId
	 * 
	 * @param topic_id
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value = "/api/auth/comment", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> viewCommentsDetail(@RequestParam("topic_id") Long topicId, HttpServletRequest request)
			throws FamilyAppWebserviceException {

		logger.info("view all comments...");
		
		String username = RequestUtil.getEmailFromAuthentication();
		UserProfile currentUser = userService.findByEmail(username);
		if (currentUser == null) {
			logger.info(FamilyAppConst.INVALID_USER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
		}
		Long userId = currentUser.getId();	
		List<TopicMember> topicMemberList = discussionService.findAllTopicMemberList(userId, topicId, null); //get topic member list
		
		if(topicMemberList.size() > 0) {
		Boolean flag=false;
		for (TopicMember topicMember : topicMemberList) {
			Topic topic=topicMember.getTopic();
			if(topic==null)
			{
				logger.info(FamilyAppConst.INVALID_TOPICID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPICID);	
			}
			else if(topic.getTopicStatus().equals(Status.I)){
				logger.info(FamilyAppConst.INVALID_TOPIC_ID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_ID);
			}
			else if(topicMember.getMemberStatus().equals(Status.A))
			{
				flag=true;
				break;
			}
			
		}//end for loop
			if(flag == false ){
			logger.info(FamilyAppConst.INACTIVE_TOPIC_MEMBER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INACTIVE_TOPIC_MEMBER_ID);
			}
		}//end if
		else{
			logger.info(FamilyAppConst.INVALID_TOPIC_AND_TOPICMEMBERID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_AND_TOPICMEMBERID);	
		}
					
		List<TopicComment> getCommentList = discussionService.findAllCommentListByTopicId(topicId,
				FamilyAppConst.VIEW_TOPIC_SIZE); //get comment List from DB
		System.out.println("get Comment List size..." + getCommentList.size());
		List<Comment> commentList = new ArrayList<>(); //create empty comment List		
		if(getCommentList.size() > 0) {
		
		for (TopicComment topicComment : getCommentList) { //change object in the topic comment Lists  for reply					 		
		    TopicMember topicMember=topicComment.getTopicMember(); //get member from TopicMember by comment's member    
		   // System.out.println("find member in topic member..."+topicMember.getMemberId());	  	    
		    currentUser=topicMember.getMember();   //get user profile for reply 	    
		    System.out.println("user profile id & name..."+currentUser.getId()+"---"+currentUser.getUsername());	   
		    Comment comment=new Comment();
		    comment.setComment(topicComment.getComment());
			comment.setCommentedDate(topicComment.getCommentDate());
			comment.setCommentId(topicComment.getId());
			comment.setUpdatedDate(topicComment.getUpdatedDate());
			if(currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().trim().isEmpty())
			{
				comment.setImageUrl(RequestUtil.getBaseUrl(request)+currentUser.getProfileImageUrl());
			}
			//comment.setImageUrl(userProfile.getProfileImageUrl());
			comment.setUserId(currentUser.getId());
			comment.setUsername(currentUser.getUsername());
			commentList.add(comment);
		}// end for loop
				
	}//end if
			
		String gtCommtListtoJson = JsonUtil.pojoToJson(commentList);
		return new ResponseEntity<>(gtCommtListtoJson, HttpStatus.OK);
			
	}
	/**
	 * Refresh And Loadmore Topic
	 * @param topicId
	 * @param status
	 * @param id
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value = "/api/auth/topic/reload", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> viewRefreshAndLoadMoreTopic(@RequestParam("topicId") Long topicId,
			@RequestParam("status") ScrollStatus status, HttpServletRequest request)
			throws FamilyAppWebserviceException {
		logger.info("view refresh comments...");
		System.out.println("topic id..." + topicId);
		System.out.println("status for reload topic..." + status);
		
		String username = RequestUtil.getEmailFromAuthentication();
		UserProfile currentUser = userService.findByEmail(username);
		if (currentUser == null) {
			logger.info(FamilyAppConst.INVALID_USER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
		}
		Long userId = currentUser.getId();
		
		// get topic member Lists by login memberId
		List<TopicMember> getTopicMemberLists = discussionService.findAllTopicMemberListByActive(userId,
						FamilyAppConst.VIEW_TOPIC_SIZE);
		System.out.println("topic member list..." + getTopicMemberLists.size());
		if(getTopicMemberLists.size() > 0)
		{
		List<Topic> getDiscussionList = new ArrayList<Topic>();	
		List<Long> getTopicIdList=new ArrayList<Long>(); //get topic Id List
				
		for (TopicMember topicMember : getTopicMemberLists) {
			Topic topic = topicMember.getTopic();//get topic status by active
			if(topicMember.getMemberId() == userId ){
			getTopicIdList.add(topic.getId());
			}
			else{
			logger.info(FamilyAppConst.INVALID_TOPIC_MEMBERID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_MEMBERID);	
			}
				
		   getDiscussionList=discussionService.getViewAllTopicRefreshLoadmore(getTopicIdList, topicId,status,FamilyAppConst.VIEW_TOPIC_SIZE);	
			}
			
		System.out.println("topic list..."+getDiscussionList.size());
		
		List<Object[]>getCommentCountList = discussionService.findAllCommentCountList(Status.A,getTopicIdList);	
		//System.out.println("comment list..."+getCommentCountList.size());
		for (Topic topic : getDiscussionList){ //change object in the topic member Lists to user member Lists for reply
			
			for(int i=0;i<getCommentCountList.size(); i++)
			{			
				Object[] obj = getCommentCountList.get(i);
				Integer count=Integer.valueOf(obj[0].toString());
				Long topic_Id=Long.valueOf(obj[1].toString());
				System.out.println("count..."+count+",,,topicId..."+topic_Id);
				if(topic.getId() == topic_Id){
					topic.setCommentCount(count);	
				}
			}			
		List<TopicMember> topicMemList = topic.getTopicMemberLists();
		List<UserProfile> getReplyToMemberList = new ArrayList<>();		
			for (TopicMember topicMem : topicMemList) {
				if(topicMem.getMemberStatus().equals(Status.A))
				{
				getReplyToMemberList.add(topicMem.getMember());	
				}
			}
		topic.setMemberLists(getReplyToMemberList);
		topic.setMemberCount(getReplyToMemberList.size());	
		return new ResponseEntity<>(getDiscussionList,HttpStatus.OK);
		}
	}
		else{
			logger.info(FamilyAppConst.INVALID_TOPIC_MEMBERID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_MEMBERID);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Refresh Comments
	 * 
	 * @param topic_id
	 * @param status
	 * @param id
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value = "/api/auth/comment/reload", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> viewRefreshComments(@RequestParam("topic_id") Long topicId,
			@RequestParam("status") ScrollStatus status, @RequestParam("id") Long id, HttpServletRequest request)
			throws FamilyAppWebserviceException {
		logger.info("view refresh comments...");
		System.out.println("topic id for refresh comments..." + topicId);
		System.out.println("status for refresh comments..." + status);
		System.out.println("cmt id for refresh comments..." + id);
		String username = RequestUtil.getEmailFromAuthentication();
		UserProfile currentUser = userService.findByEmail(username);
		if (currentUser == null) {
			logger.info(FamilyAppConst.INVALID_USER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
		}
		Long userId = currentUser.getId();
		
	    List<TopicMember> topicMemberList = discussionService.findAllTopicMemberList(userId, topicId, null); //get topic member list	
		if(topicMemberList.size() > 0) {
		Boolean flag=false;
		for (TopicMember topicMember : topicMemberList) {
			Topic topic=topicMember.getTopic();
			if(topic==null)
			{
				logger.info(FamilyAppConst.INVALID_TOPICID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPICID);	
			}
			else if(topic.getTopicStatus().equals(Status.I)){
				logger.info(FamilyAppConst.INVALID_TOPIC_ID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_ID);
			}
			else if(topicMember.getMemberStatus().equals(Status.A))
			{
				flag=true;
				break;
			}
			
		}//end for loop
			if(flag == false ){
			logger.info(FamilyAppConst.INACTIVE_TOPIC_MEMBER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INACTIVE_TOPIC_MEMBER_ID);
			}
		}//end if
		else{
			logger.info(FamilyAppConst.INVALID_TOPIC_AND_TOPICMEMBERID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_AND_TOPICMEMBERID);	
		}
			
		List<TopicComment> refreshCommentList = discussionService.findRefreshCommentListByTopicId(topicId,
				status.getId(), id, FamilyAppConst.VIEW_TOPIC_SIZE); //get comment list from DB
		System.out.println("Refresh Comment List size..." + refreshCommentList.size());
		List<Comment> commentList = new ArrayList<>(); //create empty comment List
       
		if(refreshCommentList.size() > 0)
        {
        	for (TopicComment topicComment : refreshCommentList) { //change object in the topic comment Lists  for reply			
        		
        		TopicMember topicMember=topicComment.getTopicMember(); //get member from TopicMember by comment's member    
       		    System.out.println("find member in topic member..."+topicMember.getMemberId());
       		  	    
       		    currentUser=topicMember.getMember();   //get user profile for reply     		   
    		    System.out.println("user profile id & name..."+currentUser.getId()+"---"+currentUser.getUsername());
    		    
    			Comment comment=new Comment();
    			comment.setComment(topicComment.getComment());
    			comment.setCommentedDate(topicComment.getCommentDate());
    			comment.setCommentId(topicComment.getId());
    			comment.setUpdatedDate(topicComment.getUpdatedDate());
    			if(currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().trim().isEmpty())
    			{
    				comment.setImageUrl(RequestUtil.getBaseUrl(request)+currentUser.getProfileImageUrl());
    			}
    			//comment.setImageUrl(userProfile.getProfileImageUrl());
    			comment.setUserId(currentUser.getId());
    			comment.setUsername(currentUser.getUsername());
    			commentList.add(comment);			
    			
    		}
        }
        
		String gtCommtListtoJson = JsonUtil.pojoToJson(commentList);
		return new ResponseEntity<>(gtCommtListtoJson, HttpStatus.OK);

	}

	/**
	 * Load More Comments
	 * 
	 * @param topic_id
	 * @param status
	 * @param id
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value = "/api/auth/comment/loadmore", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> viewLoadMoreComments(@RequestParam("topic_id") Long topicId,
			@RequestParam("status") ScrollStatus status, @RequestParam("id") Long id, HttpServletRequest request)
			throws FamilyAppWebserviceException {
		logger.info("view load more comments...");
		System.out.println("topic id for refresh comments..." + topicId);
		System.out.println("status for refresh comments..." + status);
		System.out.println("cmt id for refresh comments..." + id);
		String username = RequestUtil.getEmailFromAuthentication();
		UserProfile currentUser = userService.findByEmail(username);
		if (currentUser == null) {
			logger.info(FamilyAppConst.INVALID_USER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
		}
		Long userId = currentUser.getId();
		
	    List<TopicMember> topicMemberList = discussionService.findAllTopicMemberList(userId, topicId, null); //get topic member list	
		if(topicMemberList.size() > 0) {
		Boolean flag=false;
		for (TopicMember topicMember : topicMemberList) {
			Topic topic=topicMember.getTopic();
			if(topic==null)
			{
				logger.info(FamilyAppConst.INVALID_TOPICID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPICID);	
			}
			else if(topic.getTopicStatus().equals(Status.I)){
				logger.info(FamilyAppConst.INVALID_TOPIC_ID);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_ID);
			}
			else if(topicMember.getMemberStatus().equals(Status.A))
			{
				flag=true;
				break;
			}
			
		}//end for loop
			if(flag == false ){
			logger.info(FamilyAppConst.INACTIVE_TOPIC_MEMBER_ID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INACTIVE_TOPIC_MEMBER_ID);
			}
		}//end if
		else{
			logger.info(FamilyAppConst.INVALID_TOPIC_AND_TOPICMEMBERID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_AND_TOPICMEMBERID);	
		}
				
		List<TopicComment> loadMoreCommentList = discussionService.findRefreshCommentListByTopicId(topicId,
				status.getId(), id, FamilyAppConst.VIEW_TOPIC_SIZE);//get comment list from DB
		System.out.println("Load More Comment List size..." + loadMoreCommentList.size());
		List<Comment> commentList = new ArrayList<>(); //create empty comment List
	       
		if(loadMoreCommentList.size() > 0)
		{
			for (TopicComment topicComment : loadMoreCommentList) { //change object in the topic comment Lists  for reply			
				TopicMember topicMember=topicComment.getTopicMember(); //get member from TopicMember by comment's member    
       		    System.out.println("find member in topic member..."+topicMember.getMemberId());
       		  	    
       		    currentUser=topicMember.getMember();   //get user profile for reply     		   
    		    System.out.println("user profile id & name..."+currentUser.getId()+"---"+currentUser.getUsername());
				
    			Comment comment=new Comment();
    			comment.setComment(topicComment.getComment());
    			comment.setCommentedDate(topicComment.getCommentDate());
    			comment.setCommentId(topicComment.getId());
    			comment.setUpdatedDate(topicComment.getUpdatedDate());
    			if(currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().trim().isEmpty())
    			{
    				comment.setImageUrl(RequestUtil.getBaseUrl(request)+currentUser.getProfileImageUrl());
    			}
    			//comment.setImageUrl(userProfile.getProfileImageUrl());
    			comment.setUserId(currentUser.getId());
    			comment.setUsername(currentUser.getUsername());
    			commentList.add(comment);
    				
    		}
		}
		String gtCommtListtoJson = JsonUtil.pojoToJson(commentList);
		return new ResponseEntity<>(gtCommtListtoJson, HttpStatus.OK);

	}
	
	/**
	 * 
	 * @param topicTitle
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws ParseException
	 */
	@RequestMapping(value="api/auth/topic/search",method=RequestMethod.GET)
	public ResponseEntity<?>getTopicListsByTopicTitle(@RequestParam (value = "topic") String keyWord,HttpServletRequest request,HttpServletResponse response)
			throws FamilyAppWebserviceException, ParseException{
	logger.info("get search topic Lists");
	
	System.out.println("choose topic titile..."+keyWord);
	
	String username = RequestUtil.getEmailFromAuthentication();
	UserProfile userProfile = userService.findByEmail(username);
	if (userProfile == null) {
		logger.info(FamilyAppConst.INVALID_USER_ID);
		throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_USER_ID);
	}
	Long userId=userProfile.getId();
	// get topic member Lists by login memberId
	List<TopicMember> getTopicMemberLists = discussionService.findAllTopicMemberListByActive(userId,
			FamilyAppConst.VIEW_TOPIC_SIZE);
	
	System.out.println("topic member list..." + getTopicMemberLists.size());
	
	List<Topic>getTopicList=new ArrayList<Topic>();
	List<Long>topicIdList=new ArrayList<>();
	for (TopicMember topicMember : getTopicMemberLists) {
			
		if(topicMember.getMemberId()==userId)
		{
			topicIdList.add(topicMember.getTopicId());
		}
		else{
			logger.info(FamilyAppConst.INVALID_TOPIC_MEMBERID);
			throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_TOPIC_MEMBERID);
		}
		getTopicList=discussionService.getViewAllTopicListByTopicTitle(topicIdList,keyWord);
	}
	
	 //change object in the topic member Lists to user member Lists for reply
	for (Topic topic : getTopicList) {
		List<TopicMember> topicMemList = topic.getTopicMemberLists();
		List<UserProfile> getReplyToMemberList = new ArrayList<>();
		for (TopicMember topicMem : topicMemList) {
			getReplyToMemberList.add(topicMem.getMember());
		}
	topic.setMemberLists(getReplyToMemberList);
    }
	return new ResponseEntity<>(getTopicList,HttpStatus.OK);
	}	
}
