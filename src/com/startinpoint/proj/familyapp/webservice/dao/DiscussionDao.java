package com.startinpoint.proj.familyapp.webservice.dao;

import java.util.List;

import com.startinpoint.proj.familyapp.webservice.entity.FamilyAppConst;
import com.startinpoint.proj.familyapp.webservice.entity.Topic;
import com.startinpoint.proj.familyapp.webservice.entity.TopicComment;
import com.startinpoint.proj.familyapp.webservice.entity.TopicMember;
import com.startinpoint.proj.familyapp.webservice.entity.enums.ScrollStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * @author ThoonSandy
 * @version 1.0
 * @since 27-04-2018
 */
public interface DiscussionDao {
    
	public Topic createFamilyDiscussion(Topic discussion) throws FamilyAppWebserviceException;
	
	public void deleteFamilyDiscussion(Topic discussion)throws FamilyAppWebserviceException;
	
	public List<Topic> getViewAllFamilyDiscussion(List<Long>topicIdList ,int size,Status inStatus) throws FamilyAppWebserviceException;
	
	public Topic findById(Long id) throws FamilyAppWebserviceException;
	
	public Topic findTopicById(Long id) throws FamilyAppWebserviceException;
	
	public void updateTopicByStatusChange(Long topicId,Status inStatus)throws FamilyAppWebserviceException;
	
	public void changeStatusALLTopicMemberLists(Long topicId,List<Long>memberIdList,Status inStatus);
	
	public List<Topic> getViewAllTopicListByTopicTitle(List<Long> topicIdList,String topicTitle);
	
	public List<Topic> getViewAllTopicRefreshLoadmore(List<Long> topicIdList,Long topicId,ScrollStatus status,int size);
	
	public TopicMember createTopicMember(TopicMember topicMember)throws FamilyAppWebserviceException;
	
	public TopicMember findByMemberId(Long id)throws FamilyAppWebserviceException;
	
	public TopicMember findByTopicMemberId(Long id)throws FamilyAppWebserviceException;
	
	public List<TopicMember> findAllTopicMemberListByActive(Long memberId,Integer size)throws FamilyAppWebserviceException;
	
	public List<TopicMember> findAllTopicMemberList(Long memberId,Long topicId,Integer size)throws FamilyAppWebserviceException;
	
	public void updateFamilyDiscussionByStatus(Long topicId,List<Long>memberList,Status inStatus)throws FamilyAppWebserviceException;
	
	public void updateTopicMemberByStatus(Long topicId,Long topicMemberId,Status inStatus)throws FamilyAppWebserviceException;
	
    public TopicComment createDiscussionComments(TopicComment discussionComments)throws FamilyAppWebserviceException;
    
	public List<TopicComment> findAllCommentListByTopicId(Long topicId,int size)throws FamilyAppWebserviceException;
    
	public List<TopicComment> findRefreshCommentListByTopicId(Long topicId,String status,Long id,int size)throws FamilyAppWebserviceException;
	
	public void changeStatusALLTopicCommentMemberLists(Long topicId,Status inStatus);
	
	public List<Object[]> findAllCommentCountList(Status status,List<Long>topicIdList)throws FamilyAppWebserviceException;

	public void replaceFamilyIdInTopic(Long oldFamilyId,Long newFamilyId) throws FamilyAppWebserviceException;

	public void updateTopicStatusByFamilyIdCreatorId(Long familyId, Long topicCreatorId, Status status) throws FamilyAppWebserviceException;
}
