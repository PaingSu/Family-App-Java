package com.startinpoint.proj.familyapp.webservice.service;

import javax.mail.MessagingException;

import org.springframework.scheduling.annotation.Async;

import com.startinpoint.proj.familyapp.webservice.entity.FamilyProfile;
import com.startinpoint.proj.familyapp.webservice.entity.enums.MailType;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @author nankhinmhwe
 *
 */
public interface MailService {
	
	/**
	 * Send Verification mail
	 * @param code
	 * @param recipient
	 * @param mailType
	 * @throws FamilyAppWebserviceException
	 * @throws MessagingException
	 */
	@Async
	public void sendVerificationCodeToMail(String code ,String recipient, MailType mailType) throws FamilyAppWebserviceException, MessagingException;

	/**
	 * Send Family Invitation Mail
	 * @param family
	 * @param recipients  - comma sperated email addresses
	 * @throws FamilyAppWebserviceException
	 */
	@Async
	public void sendFamilyInviteMail(FamilyProfile family , String recipients) throws FamilyAppWebserviceException;
}
