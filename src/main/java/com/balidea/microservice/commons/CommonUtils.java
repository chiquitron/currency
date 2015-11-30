package com.balidea.microservice.commons;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Balidea Consulting & Programming
 */
public class CommonUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(CommonUtils.class);

	/**
	 * Method that calculates the dates between the start date and the end date
	 * Calculate all dates per day
	 * @param from 
	 * 			date from to calculate
	 * @param to
	 * 			date to to calculate
	 *
	 * @return List<Date> list with dates between two dates
	 */
	public static List<String> dateInterval(Date from, Date to) {
		
		List<String> dates = new ArrayList<String>();
		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(from);

		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		
		while (calendarFrom.getTime().before(to)) {
			Date result = calendarFrom.getTime();
			dates.add(dateFormat.format(result));
			calendarFrom.add(Calendar.DATE, 1);
		}

		//add the to date to list of interval dates 
		dates.add(dateFormat.format(to));
		
		return dates;
	}

	/**
	 * Method that calculates the dates between the start date and the end date
	 * Calculate all dates per day
	 * @param date 
	 * 			date from calculate the previous working date it could be a holiday or weekend
	 * @return String that indicates the previous working date
	 */
	public static String obtainPreviousWorkingDay (String date){
		String lastWorkingDay = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Calendar calendar= Calendar.getInstance();
		
		try {
			calendar.setTime(dateFormat.parse(date));
			//if date time is Sunday its's necessary to return Friday date
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				calendar.add(Calendar.DATE, - 2);
				lastWorkingDay = dateFormat.format(calendar.getTime());
			} else {
				//if it's Saturday or no working day return the previous day
				calendar.add(Calendar.DATE, - 1);
				lastWorkingDay = dateFormat.format(calendar.getTime());
			}
			
		} catch (ParseException e) {
			logger.info("---> An error have been produced trying to obtain previous working day: " + e.getMessage());
		}
		
		return lastWorkingDay;
	}
}
