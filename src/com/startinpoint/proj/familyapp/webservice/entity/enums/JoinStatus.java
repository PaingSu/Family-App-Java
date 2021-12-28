package com.startinpoint.proj.familyapp.webservice.entity.enums;

/**
 * 
 * @author nankhinmhwe
 *
 */
public enum JoinStatus {
	PENDING,	
	APPROVED,	//only use in familyJoinRequest
	JOINED,		//already joined
	CANCEL,	//cancel by requested member
	REJECT,	//reject request from creator
	LEAVE,	//by member
	REMOVE  //by creator
}
