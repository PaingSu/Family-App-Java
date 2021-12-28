package com.startinpoint.proj.familyapp.webservice.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FamilyAppWebserviceException extends Exception{
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public FamilyAppWebserviceException(){
		super();
	}
	
	public FamilyAppWebserviceException(String message){
		super(message);
	}
	
	public FamilyAppWebserviceException(Throwable cause){
		super(cause);
	}
	
	public FamilyAppWebserviceException(String message, Throwable cause){
		super(message, cause);
	}

}
