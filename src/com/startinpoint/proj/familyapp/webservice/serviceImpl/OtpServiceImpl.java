package com.startinpoint.proj.familyapp.webservice.serviceImpl;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.startinpoint.proj.familyapp.webservice.dao.OtpDao;
import com.startinpoint.proj.familyapp.webservice.entity.VerificationCode;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.OtpService;
/**
 * 
 * @author nankhinmhwe
 *
 */
@Service("otpService")
public class OtpServiceImpl implements OtpService{

	@Autowired
	private OtpDao otpDao;
	
	@Override
	public VerificationCode saveOrUpdateOtp(VerificationCode otp) throws FamilyAppWebserviceException {
		otp = otpDao.saveOrUpdateOtp(otp);
		return otp;
	}

	@Override
	public VerificationCode findOtpByVerificationCode(String code) throws FamilyAppWebserviceException {
		VerificationCode otp = otpDao.findOtpByVerificationCode(code);
		return otp;
	}

	@Override
	public Boolean isValidOtp(VerificationCode otp) throws FamilyAppWebserviceException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(otp.getCreateDate());
		calendar.add(Calendar.MINUTE, 30);
		Date currentDate = new Date();
		if(currentDate.before(calendar.getTime())){
			return true;
		}
		return false;
	}
	
}
