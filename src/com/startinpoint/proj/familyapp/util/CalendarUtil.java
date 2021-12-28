package com.startinpoint.proj.familyapp.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * 
 * @author NanKhinMhwe
 *
 */
public class CalendarUtil {
	
	/**
	 * Format the date to string formatted
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDate(Date date,String format){
		String result = null;
		if(date == null){
			result = null;
		}
		else{
			DateFormat formatter = new SimpleDateFormat(format);
			result = formatter.format(date);
		}
		return result;
	}
	
	/**
	 * Parse String to Date object
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Date parseDate(String dateStr, String format){
		if(dateStr == null || dateStr.trim().equals("")){
			return null;
		}
		Date date = null;
		
		try {			
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			formatter.setTimeZone(TimeZone.getDefault());
			date = formatter.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * Get the start of the date
	 * @param date
	 * @return the date object with time is 00:00 AM
	 */
	public static Date getStartDateTime(Date date){
		if(date == null){
			throw new IllegalArgumentException("Argument date must not be null.");
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONDAY), calendar.get(Calendar.DATE), 0, 0, 0);
		
		return calendar.getTime();
	}
	
	/**
	 * Get the end of the date
	 * @param date
	 * @return the date object with time is 23:59 PM
	 */
	public static Date getEndDateTime(Date date){
		if(date == null){
			throw new IllegalArgumentException("Argument date must not be null.");
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONDAY), calendar.get(Calendar.DATE), 23, 59, 59);
		
		return calendar.getTime();
	}
	
}
