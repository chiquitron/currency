package com.balidea.microservice.commons;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.balidea.microservice.CurrencyExchangeApplication;


/**
 * 
 * @author Balidea Consulting & Programming
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { CurrencyExchangeApplication.class })
public class CommonUtilsUnitTests {

    /**
     * Call method who obtains the interval dates into from date and to date
     */
	@Test
	public void obtainDateInterval() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-31");

		List<String> dateInterval = CommonUtils.dateInterval(fromDate, toDate);
		Assert.assertNotNull(dateInterval);	
		Assert.assertFalse(dateInterval.isEmpty());	
		
		List<String> expectedResultList = Arrays.asList(new String[] {"2015-01-01", "2015-01-02", "2015-01-03", "2015-01-04", 
				"2015-01-05", "2015-01-06", "2015-01-07", "2015-01-08", "2015-01-09", "2015-01-10",
				"2015-01-11", "2015-01-12", "2015-01-13", "2015-01-14", "2015-01-15", "2015-01-16", 
				"2015-01-17", "2015-01-18", "2015-01-19", "2015-01-20", "2015-01-21", "2015-01-22", "2015-01-23",
				"2015-01-24", "2015-01-25", "2015-01-26", "2015-01-27", "2015-01-28", "2015-01-29", "2015-01-30", "2015-01-31"});
		
		Assert.assertTrue(dateInterval.equals(expectedResultList));
		
	}
	
    /**
     * Call method who obtains the previous working day from a weekend day or a holiday
     */
	@Test
	public void obtainPreviousWorkingDay() throws Exception {

		//it's SUNDAY day
		String date = "2015-01-04";

		String previousDate = CommonUtils.obtainPreviousWorkingDay(date);
		Assert.assertNotNull(previousDate);	
		Assert.assertEquals(new String("2015-01-02"), previousDate);	
	}
	
	
}
