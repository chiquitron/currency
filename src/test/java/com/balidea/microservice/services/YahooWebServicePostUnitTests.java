package com.balidea.microservice.services;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.balidea.microservice.CurrencyExchangeApplication;
import com.balidea.microservice.commons.CommonUtils;
import com.balidea.microservice.commons.Constants;
import com.balidea.microservice.controller.CurrencyExchangeController;
import com.balidea.microservice.model.vo.CurrencyExchangeResponse;



/**
 * 
 * @author Balidea Consulting & Programming
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { CurrencyExchangeApplication.class })
public class YahooWebServicePostUnitTests {

	@Autowired
	private CurrencyExchangeController yahooFinanceController;
	@Autowired
	private CurrencyExchangeServicePost yahooWebService;
	  

    /**
     * Call method that makes the http call to Yahoo Finance API
     * Test that it's a invalid call because one input parameter is incorrect
     */
	@Test(expected=Exception.class)
	public void invalidHttpCallYahooFinance() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-31");
		String indexCountry = "";
		String resolution = "d";
		
		byte[] byteArrayResult = this.yahooWebService.callQuandlCurrencyExchange(fromDate, toDate, indexCountry, resolution);	
	}
	
    /**
     * Call method that makes the http call to Yahoo Finance API
     * Test that it's a valid call and the results it's a valid csv file with finance close information
     * The finance results will be with a daily resolution
     */
	@Test
	public void validDailyHttpCallYahooFinance() throws Exception  {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-31");
		String indexCountry = "^gdaxi";
		String resolution = "d";
		
		byte[] byteArrayResult = this.yahooWebService.callQuandlCurrencyExchange(fromDate, toDate, indexCountry, resolution);
		Assert.assertNotNull(byteArrayResult);
				
	}
	
    /**
     * Call method that makes the http call to Yahoo Finance API
     * Test that it's a valid call and the results it's a valid csv file with finance close information
     * The finance results will be with a weekly resolution
     */
	@Test
	public void validWeeklyHttpCallYahooFinance() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-31");
		String indexCountry = "^gdaxi";
		String resolution = "w";
		
		byte[] byteArrayResult = this.yahooWebService.callQuandlCurrencyExchange(fromDate, toDate, indexCountry, resolution);
		Assert.assertNotNull(byteArrayResult);
				
	}

    /**
     * Call method that makes the call to Yahoo Finance API locking for information 
     * three years ago since execution date, and three days ago since execution date
     */
	@Test
	public void threeYearsCallYahooFinance() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-31");
		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";
		String currencyPair = baseCurrency + quoteCurrency;
		String resolution = "daily";
		
		List<String> dateInterval = CommonUtils.dateInterval(fromDate, toDate);
		Assert.assertFalse(dateInterval.isEmpty());	
		
		CurrencyExchangeResponse financialResponseJson = this.yahooFinanceController.callQuandlApi(fromDate, toDate, baseCurrency, 
				quoteCurrency, currencyPair, resolution,false, Constants.CACHE_CURRENCY_DATA, dateInterval);
		Assert.assertNotNull(financialResponseJson);
		Assert.assertFalse(financialResponseJson.getSeries().isEmpty());
				
	}
	
}
