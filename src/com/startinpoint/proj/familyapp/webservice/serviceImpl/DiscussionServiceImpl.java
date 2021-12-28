package com.startinpoint.proj.familyapp.webservice.serviceImpl;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.startinpoint.proj.familyapp.webservice.dao.DiscussionDao;
import com.startinpoint.proj.familyapp.webservice.entity.Topic;
import com.startinpoint.proj.familyapp.webservice.entity.TopicComment;
import com.startinpoint.proj.familyapp.webservice.entity.TopicMember;
import com.startinpoint.proj.familyapp.webservice.entity.enums.ScrollStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.DiscussionService;

/**
 * @author ThoonSandy
 * @version 1.0
 * @since 27-04-2018
 */
@Service("discussionService")
public class DiscussionServiceImpl implements DiscussionService{

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	
	@Autowired
	DiscussionDao discussionDao;	
	
	@Override
	public Topic createFamilyDiscussion(Topic discussion) throws FamilyAppWebserviceException {
		 
		try{
			discussionDao.createFamilyDiscussion(discussion);
			return discussion;
			
		   }catch(FamilyAppWebserviceException e){
		     e.printStackTrace();	
		     throw new FamilyAppWebserviceException(e.getMessage(),e);
		   }
	
		
	}


	@Override
	public void deleteFamilyDiscussion(Topic disucssion) throws FamilyAppWebserviceException {

		try{
			discussionDao.deleteFamilyDiscussion(disucssion);
		}
		catch(FamilyAppWebserviceException e){
			e.printStackTrace();
			throw new FamilyAppWebserviceException(e.getMessage(),e);
		}
	}

	@Override
	public List<Topic> getViewAllFamilyDiscussion(List<Long> topicIdList,int size,Status inStatus) throws FamilyAppWebserviceException {
		
		try{
		return discussionDao.getViewAllFamilyDiscussion(topicIdList,size,inStatus);
		}
		catch(FamilyAppWebserviceException e){
			e.printStackTrace();
			throw new FamilyAppWebserviceException(e.getMessage(),e);
		}
	}


	public Topic findById(Long id) throws FamilyAppWebserviceException {
		
		 try{
			return discussionDao.findById(id);	
		 }
		 catch(FamilyAppWebserviceException e){
			 e.printStackTrace();
			 throw new FamilyAppWebserviceException(e.getMessage(),e);
		 }
	}


	@Override
	public TopicComment createDiscussionComments(TopicComment discussionComments) throws FamilyAppWebserviceException {

		try{
			discussionDao.createDiscussionComments(discussionComments);
			return discussionComments;
		}catch(FamilyAppWebserviceException e){
			e.printStackTrace();
			throw new FamilyAppWebserviceException(e.getMessage(),e);
		}
		
	}


	@Override
	public List<TopicComment> findAllCommentListByTopicId(Long topicId,int size) throws FamilyAppWebserviceException {
		try{
		 return	discussionDao.findAllCommentListByTopicId(topicId,size);
		
		}catch(FamilyAppWebserviceException e){
			e.printStackTrace();
			throw new FamilyAppWebserviceException(e.getMessage(),e);
		}
		
		
	}


	@Override
	public List<TopicComment> findRefreshCommentListByTopicId(Long topicId,String status, Long id, int size)
			throws FamilyAppWebserviceException {
		try{
			 return	discussionDao.findRefreshCommentListByTopicId(topicId, status, id, size);
			
			}catch(FamilyAppWebserviceException e){
				e.printStackTrace();
				throw new FamilyAppWebserviceException(e.getMessage(),e);
			}
	}


	@Override
	public TopicMember findByMemberId(Long id) throws FamilyAppWebserviceException {
		
		return discussionDao.findByMemberId(id);
	}


	@Override
	public TopicMember createTopicMember(TopicMember topicMember) throws FamilyAppWebserviceException {
		try{
			discussionDao.createTopicMember(topicMember);
			return topicMember;
			
		   }catch(FamilyAppWebserviceException e){
		     e.printStackTrace();	
		     throw new FamilyAppWebserviceException(e.getMessage(),e);
		   }
	}


	@Override
	public List<TopicMember> findAllTopicMemberList(Long memberId,Long topicId,Integer size) throws FamilyAppWebserviceException {

		return discussionDao.findAllTopicMemberList(memberId,topicId,size);

    }


	@Override
	public void updateFamilyDiscussionByStatus(Long topicId,List<Long> memberList, Status inStatus)
			throws FamilyAppWebserviceException {
		 discussionDao.updateFamilyDiscussionByStatus(topicId,memberList, inStatus);
		
	}


	@Override
	public void updateTopicMemberByStatus(Long topicId,Long topicMemberId, Status inStatus) throws FamilyAppWebserviceException {
		
		discussionDao.updateTopicMemberByStatus(topicId,topicMemberId, inStatus);
		
	}


	@Override
	public void updateTopicByStatusChange(Long topicId, Status inStatus) throws FamilyAppWebserviceException {
		
		discussionDao.updateTopicByStatusChange(topicId, inStatus);
		
	}


	@Override
	public List<Topic> getViewAllTopicListByTopicTitle(List<Long> topicIdList, String topicTitle) {

		return discussionDao.getViewAllTopicListByTopicTitle(topicIdList, topicTitle);
	}


	@Override
	public void changeStatusALLTopicMemberLists(Long topicId, List<Long> memberIdList, Status inStatus) {
		
		discussionDao.changeStatusALLTopicMemberLists(topicId, memberIdList, inStatus);
	}


	@Override
	public void changeStatusALLTopicCommentMemberLists(Long topicId, Status inStatus) {
		discussionDao.changeStatusALLTopicCommentMemberLists(topicId,  inStatus);
		
	}


	@Override
	public List<TopicMember> findAllTopicMemberListByActive(Long memberId, Integer size)
			throws FamilyAppWebserviceException {
		
		return discussionDao.findAllTopicMemberListByActive(memberId, size);
	}


	@Override
	public TopicMember findByTopicMemberId(Long id) throws FamilyAppWebserviceException {
		
		return discussionDao.findByTopicMemberId(id);
	}


	@Override
	public Topic findTopicById(Long id) throws FamilyAppWebserviceException {
		return discussionDao.findTopicById(id);
	}


	@Override
	public List<Object[]> findAllCommentCountList(Status status,List<Long> topicIdList) throws FamilyAppWebserviceException {
		return discussionDao.findAllCommentCountList(status,topicIdList);
	}


	@Override
	public void replaceFamilyIdInTopic(Long oldFamilyId, Long newFamilyId) throws FamilyAppWebserviceException {
		discussionDao.replaceFamilyIdInTopic(oldFamilyId,newFamilyId);
	}


	@Override
	public List<Topic> getViewAllTopicRefreshLoadmore(List<Long> topicIdList, Long topicId, ScrollStatus status, int size) {
		return discussionDao.getViewAllTopicRefreshLoadmore(topicIdList, topicId, status, size);
	}


	@Override
	public void updateTopicStatusByFamilyIdCreatorId(Long familyId, Long topicCreatorId, Status status)
			throws FamilyAppWebserviceException {
		discussionDao.updateTopicStatusByFamilyIdCreatorId(familyId, topicCreatorId, status);
	}
	
}
