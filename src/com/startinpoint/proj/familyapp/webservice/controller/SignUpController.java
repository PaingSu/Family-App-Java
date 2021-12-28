package com.startinpoint.proj.familyapp.webservice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.startinpoint.proj.familyapp.webservice.entity.FamilyMember;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyProfile;
import com.startinpoint.proj.familyapp.webservice.entity.VerificationCode;
import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Gender;
import com.startinpoint.proj.familyapp.webservice.entity.enums.JoinStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.MailType;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.FamilyProfileService;
import com.startinpoint.proj.familyapp.webservice.service.MailService;
import com.startinpoint.proj.familyapp.webservice.service.OtpService;
import com.startinpoint.proj.familyapp.webservice.service.UserService;
import com.startinpoint.utils.DesEncrypter;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Transactional
@Controller
public class SignUpController {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	UserService userService;
	
	@Autowired
	DesEncrypter passwordEncoder;
	
	@Autowired
	MailService mailService;
	
	@Autowired
	OtpService otpService;
	
	@Autowired
	FamilyProfileService familyProfileService;
	
	@Value("${application.folder_path}")
	private String folderPath;
		
	/**
	 * Register User
	 * @param user
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws Exception
	 */
	@RequestMapping(value="api/user/signup", method = RequestMethod.POST)
	public ResponseEntity<UserProfile> saveUser(@RequestBody String data,HttpServletRequest request) throws FamilyAppWebserviceException,Exception{
		logger.info("Register User");	
		JSONObject obj = new JSONObject(data);
		if(!obj.has("username")){
			logger.info("Username is required.");
			throw new FamilyAppWebserviceException("Username is required.");
		}
		if(!obj.has("codeword")){
			logger.info("Password is required.");
			throw new FamilyAppWebserviceException("Password is required.");
		}
		
		if(!obj.has("email")){
			logger.info("Email is required.");
			throw new FamilyAppWebserviceException("Email is required.");
		}
		
		UserProfile user = new UserProfile();
		user.setUsername(obj.getString("username"));
		user.setEmail(obj.getString("email"));
		user.setPassword(obj.getString("codeword"));
		if(obj.has("birthday")){
			user.setBirthday(CalendarUtil.parseDate(obj.getString("birthday"),FamilyAppConst.CALENDAR_DATE_FORMAT));
		}
		if(obj.has("phone_no")){
		user.setPhoneNo(obj.getString("phone_no"));
		}
		if(obj.has("gender")){
			user.setGender(Gender.valueOf(obj.getString("gender")));
		}
		
		if(obj.has("profile_image_url")){
			user.setProfileImageUrl(obj.getString("profile_image_url"));
		}
		if(obj.has("profile_image_url")){
			user.setCoverImageUrl(obj.getString("cover_image_url"));
		}
		
		if(user.getPassword().length()<8){
			logger.info(FamilyAppConst.PASSWORD_LENGHT_INVALID);
			throw new FamilyAppWebserviceException(FamilyAppConst.PASSWORD_LENGHT_INVALID);
		}
	
		if(userService.findByEmail(user.getEmail())!= null){
			logger.info(FamilyAppConst.EMAIL_ALREADY_EXIST);
			throw new FamilyAppWebserviceException(FamilyAppConst.EMAIL_ALREADY_EXIST);
		}

		
		if(user.getProfileImageUrl() != null && !user.getProfileImageUrl().trim().isEmpty()){
			
			if(user.getProfileImageUrl().contains("base64")){
				String imageName = ImageUtil.writeImage(user.getProfileImageUrl(),folderPath);
				user.setProfileImageUrl(imageName);
			}
			else{
				String imageName = ImageUtil.getImageNameFromImageUrl(user.getProfileImageUrl());
				user.setProfileImageUrl(imageName);
			}
		}
		
		if(user.getCoverImageUrl() != null && !user.getCoverImageUrl().trim().isEmpty()){
			
			if(user.getCoverImageUrl().contains("base64")){
				String imageName = ImageUtil.writeImage(user.getCoverImageUrl(),folderPath);
				user.setCoverImageUrl(imageName);
			}
			else{ //image path
				String imageName = ImageUtil.getImageNameFromImageUrl(user.getCoverImageUrl());
				user.setCoverImageUrl(imageName);
			}
		}
		user.setPassword(passwordEncoder.encrypt(user.getPassword()));
		user.setCreatedDate(new Date());	
		user = userService.saveOrUpdateUser(user);
		
		
		//Create Default Family For all Register user
		FamilyProfile family = new FamilyProfile();
		family.setFamilyName(FamilyAppConst.DEFAULT_FAMILY_NAME);
		family.setDescription(FamilyAppConst.DEFAULT_FAMILY_NAME);
		family.setFamilyCode("DF-"+StringUtils.uuidRandomString(8)); //set default family code
		family.setFamilyStartDate(new Date());
		family.setCreatedDate(new Date());
		family.setFamilyCreatorId(user.getId());
		
		
		//save creator as family member
		FamilyMember member = new FamilyMember();
		member.setMember(user);
		member.setIsFamilyCreator(true);
		member.setRequestStatus(JoinStatus.JOINED);
		member.setJoinDate(new Date());
		member.setFamily(family);
		
		List<FamilyMember> members = new ArrayList<FamilyMember>();
		members.add(member);
				
		family.setFamilyMemberList(members);
		family = familyProfileService.saveOrUpdateFamilyProfile(family);
		
		
		String code = StringUtils.uuidRandomString(8);
		
		//Save to otp
		VerificationCode otp = new VerificationCode();
		otp.setCreateDate(new Date());
		otp.setPassword(code);
		otp.setUserId(user.getId());
		otpService.saveOrUpdateOtp(otp);
		
		//send verification mail to user		
		String verifyUrl = "/api/user/verify-email?token=";
		String url = RequestUtil.getBaseUrl(request) + verifyUrl + code;
		mailService.sendVerificationCodeToMail(url, user.getEmail(), MailType.EMAIL_VERIFICATION);
		
		return new ResponseEntity<UserProfile>(user,HttpStatus.OK);
	}
	
	/**
	 * Check email is already register or not in family app
	 * @param email
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value="/api/user/check-email",method=RequestMethod.GET)
	public ResponseEntity<String> checkEmail(@RequestParam("email")String email, HttpServletRequest request) throws FamilyAppWebserviceException{
		if(email == null || email.trim().isEmpty()){
			logger.info("Email is Required.");
			throw new FamilyAppWebserviceException("Email is Required.");
		}
		if(userService.findByEmail(email) != null){
			logger.info(FamilyAppConst.EMAIL_ALREADY_EXIST);
			throw new FamilyAppWebserviceException(FamilyAppConst.EMAIL_ALREADY_EXIST);
		}
		
		return new ResponseEntity<String>(StringUtils.responseString("Success"),HttpStatus.OK);
		
	}
	
}
