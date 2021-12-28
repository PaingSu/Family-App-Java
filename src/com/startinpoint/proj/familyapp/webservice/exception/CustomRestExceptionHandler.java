package com.startinpoint.proj.familyapp.webservice.exception;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 
 * Global Exception Handling
 * @author nankhinmhwe
 *
 */
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
	protected final Log logger = LogFactory.getLog(this.getClass());
	  
	 /**
	  * Handle all exception
	  * @param ex
	  * @param request
	  * @return
	  */
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
		logger.info("Exception " + ex);
		ex.printStackTrace();
		Map< String, String> responseMap = new HashMap<>();
		responseMap.put("message", ex.getLocalizedMessage());
		
		return new ResponseEntity<Object>(new JSONObject(responseMap).toString(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * Handle all exception come from FamilyAppWebserviceException
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(FamilyAppWebserviceException.class)
	public ResponseEntity<Object> exceptionHandler(Exception exception) {
		Map< String, String> responseMap = new HashMap<>();
		responseMap.put("message", exception.getLocalizedMessage());
		
		return new ResponseEntity<Object>(new JSONObject(responseMap).toString(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
}
