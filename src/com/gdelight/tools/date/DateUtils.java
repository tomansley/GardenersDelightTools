package com.gdelight.tools.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {
	
	public static final String DATE_TIME_FORMAT = "MM/dd/yyyy HH:mm:ss.S";
	public static final String DATE_FORMAT = "MM/dd/yyyy";
	
	public static boolean isValidDateString(String format, String dateStr) {
		boolean isValid = true;

		//if its a blank string then return true.
		if (!dateStr.equals("")) {
			
			// test date string matches format structure using regex
			// - weed out illegal characters and enforce 4-digit year
			// - create the regex based on the local format string
			String reFormat = Pattern.compile("d+|M+").matcher(Matcher.quoteReplacement(format)).replaceAll("\\\\d{1,2}");
			reFormat = Pattern.compile("y+").matcher(reFormat).replaceAll("\\\\d{4}");
			
			if ( Pattern.compile(reFormat).matcher(dateStr).matches() ) {
	
				// date string matches format structure, 
				// - now test it can be converted to a valid date
				SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance();
				sdf.applyPattern(format);
				sdf.setLenient(false);
				try { 
					sdf.parse(dateStr); 
				} catch (ParseException e) {
					isValid = false;
				}
			} else {
				isValid = false;
			}
		}
		
		return isValid;
	}
     
	public static String convertDateToString(String format, Date date) {
		String dateStr = "";
		if (date != null) {
			SimpleDateFormat sdf=new SimpleDateFormat(format);
			dateStr = sdf.format(date);
		}
		return dateStr;
	}
	
	public static Date convertStringToDate(String format, String date) {
		Date newDate = null;
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		try {
			newDate = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return newDate;
	}
	
	public static Date addToToday(int years, int months, int days) {
		return DateUtils.addToDate(new Date(), years, months, days);
	}
	
	public static Date addToDate(Date startDate, int years, int months, int days) {
		//get the date employed from
		Calendar cal = new GregorianCalendar();
		cal.setTime(startDate);
		cal.add(Calendar.YEAR, years);
		cal.add(Calendar.MONTH, months); 
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	//-------------------------------
	// DATE VALIDATION METHODS BELOW
	//-------------------------------

	public static boolean isDateGreaterThanXXYears(int years, long dobMSec){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -years);
		long earliestAgeInMilliSecs =  cal.getTimeInMillis();
		if(earliestAgeInMilliSecs >= dobMSec)
			return true;
		else
			return false;
	}

	public static boolean isDateGreaterThanXXYears(int years, String date, String format) {
		boolean isValid = false;
		try {
			Date dob = new SimpleDateFormat(format).parse(date);
			isValid = isDateGreaterThanXXYears(years, dob.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return isValid;
	}

	public static int getAge(Date dateOfBirth) {
		Calendar dob = Calendar.getInstance();  
		dob.setTime(dateOfBirth);  
		Calendar today = Calendar.getInstance();  
		int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);  
		if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
			age--;
		}
		return age;
	}
	
	public static int getMonthsSince(Date since) {
		double millis_per_month = 365.24 * 24 * 60 * 60 * 1000 / 12;
		double months_double = 0.00;
		int months_int = 0;
		Date date = new Date();
		long time = date.getTime() - since.getTime();
		months_double = time / millis_per_month;
		months_int = (int) months_double;
		
		return months_int;
	}
	
	public static double getDaysBetween(Date start, Date end) {

		double millis_per_day = 86400000;
		
		long time = end.getTime() - start.getTime();
		double days = time / millis_per_day;
		
		return Math.abs(days);
	}

}
