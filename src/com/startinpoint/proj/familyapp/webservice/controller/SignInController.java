package com.startinpoint.proj.familyapp.webservice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.startinpoint.proj.familyapp.util.JsonUtil;
import com.startinpoint.proj.familyapp.util.RequestUtil;
import com.startinpoint.proj.familyapp.util.StringUtils;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyAppConst;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyJoinRequest;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyMember;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyProfile;
import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.security.JwtAuthenticationResponse;
import com.startinpoint.proj.familyapp.webservice.security.JwtTokenUtil;
import com.startinpoint.proj.familyapp.webservice.service.FamilyJoinRequestService;
import com.startinpoint.proj.familyapp.webservice.service.FamilyProfileService;
import com.startinpoint.proj.familyapp.webservice.service.UserService;
import com.startinpoint.proj.familyapp.webservice.social.SocialService;
import com.startinpoint.utils.DesEncrypter;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Transactional
@Controller
public class SignInController {
	protected final Log logger = LogFactory.getLog(this.getClass());
	@Autowired
	UserService userService;

	@Autowired
	SocialService facebookService;
	
	@Autowired
	SocialService googleService;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	DesEncrypter passwordEncoder;
	
	@Autowired
	FamilyJoinRequestService familyJoinRequestService;
	
	@Autowired
	FamilyProfileService familyProfileService;
	
	@Value("${application.web_client_redirecturl}")
	private String webClientRedirectUrl;
	
	private String redirectToApp = "com.startinpoint.proj.familyapp://login?";

	/**
	 * Get Only Authorize Url from facebook
	 * 
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/api/user/facebook/login", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> facebookLogin(@RequestParam (value = "client",required = false) String client,HttpServletResponse response, HttpServletRequest request) {
		logger.info("Get Authorize Url");
		try {
			String authorizeUrl = facebookService.createAuthorizationURL(client);
			response.sendRedirect(authorizeUrl);
			return new ResponseEntity<>(HttpStatus.OK);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	/**
	 * Get Facebook Information and response login related data
	 * 
	 * @param authorizationCode
	 * @param request
	 * @param response
	 * @return 
	 * @return
	 * @throws Exception,FamilyAppWebserviceException 
	 */
	@RequestMapping(value = "/api/user/facebook/login/callback", method = RequestMethod.GET)
	public void callBackFacebook(@RequestParam("code") String code,@RequestParam (value = "client",required = false) String client, HttpServletResponse response,
			HttpServletRequest request)
			throws Exception,FamilyAppWebserviceException {

		UserProfile profile = facebookService.getUserInfoByAuthorizeCode(code,client);
		UserProfile dbUser = userService.findByEmail(profile.getEmail());
		
		Long userId = null;
		if(dbUser != null){
			userId = dbUser.getId();
		}
		else{
			profile = userService.saveOrUpdateUser(profile);
			userId = profile.getId();
		}

		List<FamilyJoinRequest> joinRequestList = familyJoinRequestService.getPendingJoinRequestByMemberId(userId);
		profile.setFamilyPendingList(joinRequestList);
			
		List<FamilyProfile> familyList = new ArrayList<>();
		for(FamilyMember familyMember: profile.getFamilyMemberList() ){
			familyList.add(familyMember.getFamily());
			
		}
		profile.setFamilyList(familyList);
				
		
		JwtAuthenticationResponse jwtResponse = createAuthAndGetToken(profile.getId(), profile.getEmail(),"fbpassword");
		jwtResponse.setUser(profile);
		
		if( client != null && client.equals("MOBILE")){
			String redirectUrl = redirectToApp+"user="+JsonUtil.pojoToJson(jwtResponse);
			response.sendRedirect(redirectUrl);
			logger.info("Mobile Redirect Info:  "+redirectUrl);
		}
		else{
			String redirectUrl = webClientRedirectUrl+"?user="+JsonUtil.pojoToJson(jwtResponse);
			response.sendRedirect(redirectUrl);
			logger.info("Web redirect info: "+redirectUrl);
		}	
		return;
	}
	

	/**
	 * Create Authentication and JWT token
	 * @param email
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private JwtAuthenticationResponse createAuthAndGetToken(Long userId,String email, String password)
			throws Exception {	
		
		String token = jwtTokenUtil.generateToken(userId,email);
		Date expireddate = jwtTokenUtil.getExpirationDateFromToken(token);
		String refreshToken = jwtTokenUtil.generateRefreshToken(userId,email);
		JwtAuthenticationResponse response = new JwtAuthenticationResponse(token, refreshToken, expireddate, new UserProfile());
		return response;
	}

	/**
	 * Normal Login
	 * @param response
	 * @param request
	 * @param data
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/api/user/login", method = RequestMethod.POST)
	public ResponseEntity<?> login(HttpServletResponse response, HttpServletRequest request,
			@RequestBody String data) throws Exception {
		logger.info("In Login Function");
		JSONObject jObject = new JSONObject(data);
		String email = jObject.getString("email");
		String password = jObject.getString("codeword");

		UserProfile user = userService.findByEmail(email);
		if (user == null) // not register
		{
			logger.info(FamilyAppConst.INVALID_USERNAME_PASSWORD);
			return new ResponseEntity<String>(StringUtils.responseString(FamilyAppConst.INVALID_USERNAME_PASSWORD),HttpStatus.UNAUTHORIZED);
		} 
		if(user.getEmailVerified() == false){
			logger.info(FamilyAppConst.ACCOUNT_HAS_NOT_BEEN_VERIFIED);
			Map<String, String> resultMap = new HashMap<String, String>();
			resultMap.put("email", email);
			resultMap.put("message", FamilyAppConst.ACCOUNT_HAS_NOT_BEEN_VERIFIED);
		
			return new ResponseEntity<String>(new JSONObject(resultMap).toString(),HttpStatus.NOT_IMPLEMENTED);
		}
		if(!user.getPassword().equals(passwordEncoder.encrypt(password))){
			logger.info(FamilyAppConst.INCORRECT_PASSWORD);
			return new ResponseEntity<String>(StringUtils.responseString(FamilyAppConst.INCORRECT_PASSWORD),HttpStatus.UNAUTHORIZED);
		}

		
		List<FamilyJoinRequest> joinRequestList = familyJoinRequestService.getPendingJoinRequestByMemberId(user.getId());
		
		user.setFamilyPendingList(joinRequestList);
				
		List<FamilyProfile> familyList = new ArrayList<>();
		for(FamilyMember familyMember: user.getFamilyMemberList() ){
			familyList.add(familyMember.getFamily());
		}
		user.setFamilyList(familyList);
				
		JwtAuthenticationResponse jwtResponse = createAuthAndGetToken(user.getId(),email, password);
		jwtResponse.setUser(user);

		return new ResponseEntity<JwtAuthenticationResponse>(jwtResponse,HttpStatus.OK);
	}
	
	/**
	 * Get Authorize URL
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/api/user/google/login", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> googleLogin(@RequestParam (value = "client",required = false) String client,HttpServletResponse response, HttpServletRequest request) {
		logger.info("Get Google Authorize Url");
		try {
			String authorizeUrl = googleService.createAuthorizationURL(client);
			response.sendRedirect(authorizeUrl);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Get Google Information and response login related data
	 * @param code
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception,FamilyAppWebserviceException 
	 */
	@RequestMapping(value = "/api/user/google/login/callback", method = RequestMethod.GET)
	public void callBackGoogle(@RequestParam("code") String code,@RequestParam (value = "client",required = false) String client,HttpServletResponse response,
			HttpServletRequest request)
			throws Exception,FamilyAppWebserviceException {
		System.out.println("Here"+code);	
		System.out.println("CLient : "+client);

		UserProfile profile = googleService.getUserInfoByAuthorizeCode(code,client);
		UserProfile dbUser = userService.findByEmail(profile.getEmail());
		Long userId = null;
		if(dbUser != null){
			userId = dbUser.getId();
		}
		else{
			profile = userService.saveOrUpdateUser(profile);
			userId = profile.getId();
		}

		List<FamilyJoinRequest> joinRequestList = familyJoinRequestService.getPendingJoinRequestByMemberId(userId);
	
		profile.setFamilyPendingList(joinRequestList);
		
		List<FamilyProfile> familyList = new ArrayList<>();
		for(FamilyMember familyMember: profile.getFamilyMemberList() ){
			familyList.add(familyMember.getFamily());
		}
		profile.setFamilyList(familyList);
		
		JwtAuthenticationResponse jwtResponse = createAuthAndGetToken(userId,profile.getEmail(), profile.getPassword());
		jwtResponse.setUser(profile);
		
		if( client != null && client.equals("MOBILE")){
			String redirectUrl = redirectToApp+"user="+JsonUtil.pojoToJson(jwtResponse);
			response.sendRedirect(redirectUrl);
			logger.info("mobile redirect info: "+redirectUrl);
		}
		else{
			String redirectUrl = webClientRedirectUrl+"?user="+JsonUtil.pojoToJson(jwtResponse);
			response.sendRedirect(redirectUrl);
			logger.info("web redirect info: "+redirectUrl);
		}		
		
		return;
	}

	/**
	 * Change first time login status This api will calll when user skip create/join family profile
	 * @param response
	 * @param request
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	@RequestMapping(value = "api/auth/user/change-firsttime-status", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<String> changeFirstTimeStatus(HttpServletResponse response, HttpServletRequest request) throws FamilyAppWebserviceException {
		String email = RequestUtil.getEmailFromAuthentication();
		UserProfile currentUser = userService.findByEmail(email);
		
		if(currentUser == null){
			logger.info("No User Found");
			throw new FamilyAppWebserviceException("No User found");
		}
		//only change first time status
		currentUser.setIsLogin(true);
		userService.saveOrUpdateUser(currentUser);			
		
		return new ResponseEntity<String>(StringUtils.responseString("Success."),HttpStatus.OK);
	}
	
	

}
