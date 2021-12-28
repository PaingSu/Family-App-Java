package com.startinpoint.proj.familyapp.webservice.dao;

import com.startinpoint.proj.familyapp.webservice.entity.VerificationCode;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

public interface OtpDao {
	public VerificationCode saveOrUpdateOtp(VerificationCode otp)throws FamilyAppWebserviceException;
	
	public VerificationCode findOtpByVerificationCode(String code)throws FamilyAppWebserviceException;
}
