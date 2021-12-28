package com.startinpoint.proj.familyapp.webservice.service;

import com.startinpoint.proj.familyapp.webservice.entity.VerificationCode;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

public interface OtpService {
	public VerificationCode saveOrUpdateOtp(VerificationCode otp) throws FamilyAppWebserviceException;
	
	public VerificationCode findOtpByVerificationCode(String otp) throws FamilyAppWebserviceException;
	
	/**
	 * Validate verification code is valid or not (will expire in 30 minutes)
	 * @param otp
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public Boolean isValidOtp(VerificationCode otp) throws FamilyAppWebserviceException;
}
