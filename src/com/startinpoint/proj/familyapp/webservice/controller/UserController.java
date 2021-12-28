package com.startinpoint.proj.familyapp.webservice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.springframework.web.servlet.ModelAndView;

import com.startinpoint.proj.familyapp.util.CalendarUtil;
import com.startinpoint.proj.familyapp.util.ImageUtil;
import com.startinpoint.proj.familyapp.util.RequestUtil;
import com.startinpoint.proj.familyapp.util.StringUtils;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyAppConst;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyJoinRequest;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyMember;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyProfile;
import com.startinpoint.proj.familyapp.webservice.entity.VerificationCode;
import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Gender;
import com.startinpoint.proj.familyapp.webservice.entity.enums.MailType;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.FamilyJoinRequestService;
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
public class UserController {
	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	UserService userService;

	@Autowired
	OtpService otpService;

	@Autowired
	MailService mailService;
	
	@Autowired
	FamilyJoinRequestService familyJoinRequestService;
		
	@Value("${application.folder_path}")
	private String folderPath;

	/**
	 * Getting My Profile
	 * 
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value = "/api/auth/user/myprofile", method = RequestMethod.GET)
	public ResponseEntity<UserProfile> getProfile(HttpServletRequest request) throws FamilyAppWebserviceException {
		logger.info("Getting User's Profile");
		String email = RequestUtil.getEmailFromAuthentication();
		UserProfile user = userService.findByEmail(email);
	
		List<FamilyJoinRequest> joinRequestList = familyJoinRequestService.getPendingJoinRequestByMemberId(user.getId());
		
//		List<FamilyProfile> pendingList = new ArrayList<>();
//		for(FamilyJoinRequest joinRequest: joinRequestList){
//			FamilyProfile temp = joinRequest.getFamily();
//			pendingList.add(temp);
//		}
		user.setFamilyPendingList(joinRequestList);
				
		List<FamilyProfile> familyList = new ArrayList<>();
		for(FamilyMember familyMember: user.getFamilyMemberList() ){
			familyList.add(familyMember.getFamily());
		}
		user.setFamilyList(familyList);
		if(user.getProfileImageUrl() == null){
			user.setProfileImageUrl("");
		}
		if(user.getCoverImageUrl() == null){
			user.setCoverImageUrl("");
		}
		
		return new ResponseEntity<UserProfile>(user, HttpStatus.OK);
	}


	/**
	 * Edit My Profile
	 * 
	 * @param userProfile
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/api/auth/user/myprofile", method = RequestMethod.PUT)
	public ResponseEntity<UserProfile> editProfile(@RequestBody String data, HttpServletRequest request)
			throws FamilyAppWebserviceException, JSONException {
		logger.info("Editing User's Profile");
		JSONObject obj = new JSONObject(data);
		
		if(!obj.has("username")){
			logger.info("Username is required.");
			throw new FamilyAppWebserviceException("Username is required.");
		}
		if(!obj.has("gender")){
			logger.info("Gender is required.");
			throw new FamilyAppWebserviceException("Gender is required.");
		}
		if(!obj.has("birthday")){
			logger.info("Birthday is required.");
			throw new FamilyAppWebserviceException("Birthday is required.");
		}
		String username = obj.getString("username");
		String genderStr = obj.getString("gender");
		Gender gender = Gender.valueOf(genderStr);
		Date birthday = CalendarUtil.parseDate(obj.getString("birthday"), FamilyAppConst.CALENDAR_DATE_FORMAT);
		String phoneNo = obj.getString("phone_no");
		String profileImageUrl = obj.getString("profile_image_url");
		String coverImageUrl = obj.getString("cover_image_url");
		
		String email = RequestUtil.getEmailFromAuthentication();
		
		UserProfile currentUser = userService.findByEmail(email);
		if (currentUser == null) {
			logger.info("No User Found");
			throw new FamilyAppWebserviceException("No User found!");
		}
		currentUser.setUsername(username);
		currentUser.setGender(gender);
		currentUser.setBirthday(birthday);
		currentUser.setPhoneNo(phoneNo);
		
		if(profileImageUrl != null && !profileImageUrl.trim().isEmpty()){
			
			if(profileImageUrl.contains("base64")){
				String imageName = ImageUtil.writeImage(profileImageUrl,folderPath);
				currentUser.setProfileImageUrl(imageName);
			}
			else{
				String imageName = ImageUtil.getImageNameFromImageUrl(profileImageUrl);
				currentUser.setProfileImageUrl(imageName);
			}
		}
		else {
			currentUser.setProfileImageUrl(null);
		}
		
		if(coverImageUrl != null && !coverImageUrl.trim().isEmpty()){
			
			if(coverImageUrl.contains("base64")){
				String imageName = ImageUtil.writeImage(coverImageUrl,folderPath);
				currentUser.setCoverImageUrl(imageName);
			}
			else{ //image path
				String imageName = ImageUtil.getImageNameFromImageUrl(coverImageUrl);
				currentUser.setCoverImageUrl(imageName);
			}
		}
		else {
			currentUser.setCoverImageUrl(null);
		}
		currentUser = userService.saveOrUpdateUser(currentUser);
		
		if(currentUser.getProfileImageUrl() == null){
			currentUser.setProfileImageUrl("");
		}
		if(currentUser.getCoverImageUrl() == null){
			currentUser.setCoverImageUrl("");
		}
			
		List<FamilyJoinRequest> joinRequestList = familyJoinRequestService.getPendingJoinRequestByMemberId(currentUser.getId());
		
		currentUser.setFamilyPendingList(joinRequestList);
				
		List<FamilyProfile> familyList = new ArrayList<>();
		for(FamilyMember familyMember: currentUser.getFamilyMemberList() ){
			familyList.add(familyMember.getFamily());
		}
		currentUser.setFamilyList(familyList);

		return new ResponseEntity<UserProfile>(currentUser, HttpStatus.OK);
	}

	/**
	 * Forgot Password
	 * 
	 * @param data
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value = "/api/user/forgot-password", method = RequestMethod.POST)
	public ResponseEntity<?> forgotPassword(@RequestBody String data, HttpServletRequest request,
			HttpServletResponse response) throws FamilyAppWebserviceException {
		try {

			JSONObject jObject = new JSONObject(data);
			String email = jObject.getString("email");
			UserProfile profile = userService.findByEmail(email);

			if (profile == null) {
				logger.info("Invalid Email.");
				throw new FamilyAppWebserviceException("Invalid Email.");
			} else {
				// send otp to email
				String token = StringUtils.uuidRandomString(8);
				mailService.sendVerificationCodeToMail(token, email, MailType.FORGOT_PASSWORD);

				// save otp
				VerificationCode otp = new VerificationCode();
				otp.setUserId(profile.getId());
				otp.setPassword(token);
				otp.setCreateDate(new Date());
				otp = otpService.saveOrUpdateOtp(otp);

				return new ResponseEntity<String>(StringUtils.responseString("Success"), HttpStatus.OK);
			}
		} catch (JSONException | MessagingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Forgot Update Password
	 * 
	 * @param data
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/api/user/forgot-update-password", method = RequestMethod.POST)
	public ResponseEntity<?> forgotUpdatePassword(@RequestBody String data, HttpServletRequest request,
			HttpServletResponse response) throws FamilyAppWebserviceException, JSONException {
		JSONObject jObject = new JSONObject(data);
		String verificationCode = jObject.getString("otp");
		String newPassword = jObject.getString("newPassword");

		if (verificationCode == null || verificationCode.trim().isEmpty()) {
			logger.info("Invalid Otp!!");
			throw new FamilyAppWebserviceException("Invalid otp!");
		}
		if (newPassword == null || newPassword.trim().isEmpty()) {
			logger.info("New Password is Required!");
			throw new FamilyAppWebserviceException("New Password is Required!");
		}
		

		VerificationCode otp = otpService.findOtpByVerificationCode(verificationCode);
		if (otp == null) {
			logger.info("Invalid Otp!");
			throw new FamilyAppWebserviceException("Invalid Otp!");
		}

		if(!otpService.isValidOtp(otp)){
			logger.info("otp expired.");
			throw new FamilyAppWebserviceException("Your otp is expired!");
		}		
		
		try {
			DesEncrypter enc = new DesEncrypter();
			UserProfile userProfile = userService.findById(otp.getUserId());
			userProfile.setPassword(enc.encrypt(newPassword));
			userService.saveOrUpdateUser(userProfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		return new ResponseEntity<String>(StringUtils.responseString("Success"), HttpStatus.OK);

	}

	/**
	 * Logout
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "api/user/auth/logout", method = RequestMethod.POST)
	public ResponseEntity<?> logout(HttpServletRequest request) {
		// Nothing to do?
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Verify Email
	 * @param token
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/api/user/verify-email", method = RequestMethod.GET)
	public ModelAndView verifyEmail(@RequestParam("token") String token, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			ModelAndView modelview = new ModelAndView("success");
			VerificationCode otp = otpService.findOtpByVerificationCode(token);
			if(otp == null){
				logger.info(FamilyAppConst.INVALID_OTP);
				throw new FamilyAppWebserviceException(FamilyAppConst.INVALID_OTP);
			}

			if (!otpService.isValidOtp(otp)) {
				logger.info(FamilyAppConst.OTP_EXPIRE);
				modelview.addObject("message", FamilyAppConst.OTP_EXPIRE);
				return modelview;
			}

			UserProfile userProfile = userService.findById(otp.getUserId());
			if (userProfile == null) {
				logger.info("Invalid userID");
				throw new FamilyAppWebserviceException("Invalid User ID!");
			}
			userProfile.setEmailVerified(true);
			userService.saveOrUpdateUser(userProfile);
			
			modelview.addObject("message", "Congratulations. Your account has been successfully verified.");
			return modelview;
		} catch (Exception e) {
			ModelAndView modelview = new ModelAndView("error");
			return modelview;
		}
	
	}
	
	/**
	 * Resending Verification mail
	 * @param data
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 * @throws MessagingException
	 */
	@RequestMapping(value = "/api/user/resend-email", method = RequestMethod.POST)
	public ResponseEntity<String> resendEmail(@RequestBody String data, HttpServletRequest request,
			HttpServletResponse response) throws FamilyAppWebserviceException, JSONException, MessagingException {
		JSONObject jObject = new JSONObject(data);
		String email = jObject.getString("email");

		if (email == null || email.trim().isEmpty()) {
			logger.info("Email is required!!");
			throw new FamilyAppWebserviceException("Email is required!");
		}		
		
		UserProfile profile = userService.findByEmail(email);

		if (profile == null) {
			logger.info("Invalid Email.");
			throw new FamilyAppWebserviceException("Invalid Email.");
		} 

		String token = StringUtils.uuidRandomString(8);
		// save otp
		VerificationCode otp = new VerificationCode();
		otp.setUserId(profile.getId());
		otp.setPassword(token);
		otp.setCreateDate(new Date());
		otp = otpService.saveOrUpdateOtp(otp);
		
		// send verification link to email		
		String verifyUrl = "/api/user/verify-email?token=";
		String link = RequestUtil.getBaseUrl(request) + verifyUrl + token;
	    mailService.sendVerificationCodeToMail(link, email, MailType.EMAIL_VERIFICATION);
		
		return new ResponseEntity<String>(StringUtils.responseString("Please Check Your Email."), HttpStatus.OK);

	}
	
	/**
	 * Change Password
	 * @param data
	 * @param request
	 * @param response
	 * @return
	 * @throws FamilyAppWebserviceException
	 * @throws JSONException
	 * @throws Exception
	 */
	@RequestMapping(value = "/api/auth/user/changepassword", method = RequestMethod.POST)
	public ResponseEntity<String> changePassword(@RequestBody String data, HttpServletRequest request,
			HttpServletResponse response) throws FamilyAppWebserviceException, JSONException,Exception{
		JSONObject jObject = new JSONObject(data);
		String oldPassword = jObject.getString("oldPassword");
		String newPassword = jObject.getString("newPassword");

		if (oldPassword == null || oldPassword.trim().isEmpty()) {
			logger.info("Old Password is Required!!");
			throw new FamilyAppWebserviceException("Old Password is Required!");
		}
		if (newPassword == null || newPassword.trim().isEmpty()) {
			logger.info("New Password is Required!");
			throw new FamilyAppWebserviceException("New Password is Required!");
		}
		
		DesEncrypter enc = new DesEncrypter();
	
		String email = RequestUtil.getEmailFromAuthentication();
		UserProfile currentUser = userService.findByEmail(email);
		if (currentUser == null) {
			logger.info("User not found!");
			throw new FamilyAppWebserviceException("User not found!");
		}

		if(!currentUser.getPassword().equals(enc.encrypt(oldPassword).toString())){
			logger.info("Invalid Old Password.");
			return new ResponseEntity<String>(StringUtils.responseString("Invalid Old Password."), HttpStatus.NOT_IMPLEMENTED);
		}
		
		currentUser.setPassword(enc.encrypt(newPassword));
		userService.saveOrUpdateUser(currentUser);

		return new ResponseEntity<String>(StringUtils.responseString("Success"), HttpStatus.OK);

	}
	
	

}
