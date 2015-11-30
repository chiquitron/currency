package com.balidea.microservice.controller;


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
import com.balidea.microservice.commons.ApplicationProperties;
import com.balidea.microservice.commons.CommonUtils;
import com.balidea.microservice.commons.Constants;
import com.balidea.microservice.model.vo.CurrencyExchangeResponse;
import com.balidea.microservice.model.vo.Exchange;


/**
 * 
 * @author Balidea Consulting & Programming
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { CurrencyExchangeApplication.class })
public class YahooFinanceControllerUnitTests extends CurrencyExchangeController {

	@Autowired
	private CurrencyExchangeController yahooFinanceController;
	  
	@Autowired 
	private ApplicationProperties propertiesConfig;

    /**
     * Call method that validates the input params
     * Test that Country input param invalid
     */
	@Test
	public void invalidDateParam() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-02-01");
		Date toDate = dateFormat.parse("2015-01-31");
		String resolution = "daily";
		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";

		Boolean validateResult = this.yahooFinanceController.validateInputParameters(fromDate, toDate, baseCurrency, quoteCurrency, resolution);
		Assert.assertNotNull(validateResult);	
		Assert.assertFalse(validateResult);	
	}
	
    /**
     * Call method that validates the input params
     * Test that Country input param invalid
     */
	@Test
	public void invalidCountryInputParam() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-31");
		String resolution = "daily";
		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";

		Boolean validateResult = this.yahooFinanceController.validateInputParameters(fromDate, toDate, baseCurrency, quoteCurrency, resolution);
		
		Assert.assertNotNull(validateResult);	
		Assert.assertFalse(validateResult);	
	}
	
    /**
     * Call method that validates the input params
     * Test that Resolution input param invalid
     */
	@Test
	public void invalidResolutionInputParam() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-31");
		String resolution = "d";
		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";

		Boolean validateResult = this.yahooFinanceController.validateInputParameters(fromDate, toDate, baseCurrency, quoteCurrency, resolution);
		
		Assert.assertNotNull(validateResult);	
		Assert.assertFalse(validateResult);
				
	}
	
    /**
     * Call method that validates the input params
     * Test that all input params are valid
     */
	@Test
	public void validInputParams() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-31");
		String resolution = "daily";
		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";

		Boolean validateResult = this.yahooFinanceController.validateInputParameters(fromDate, toDate, baseCurrency, quoteCurrency, resolution);
		Assert.assertNotNull(validateResult);	
		Assert.assertTrue(validateResult);
				
	}
	
	
    /**
     * Call method that makes the call to Yahoo Finance API locking for information 
     * three years ago since execution date, and three days ago since execution date
     */
	@Test
	public void threeYearsCallYahooFinance() throws Exception {

		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";
		
		List<Exchange> financeValues = this.yahooFinanceController.caheYearsAgoData(baseCurrency+quoteCurrency);
		Assert.assertNotNull(financeValues);
		Assert.assertFalse(financeValues.isEmpty());
				
	}
	
    /**
     * Call method that extract country information from cache memory 
     * if the extract process fail it automatically makes a call to Yahoo Finance API 
     * to provide the information
     * 
     */
	@Test
	public void extractCacheData() throws Exception {

		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-31");
		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";
		String currencyPair = baseCurrency + quoteCurrency;
		String resolution = "daily";
		
		List<String> dateInterval = CommonUtils.dateInterval(fromDate, toDate);
		Assert.assertFalse(dateInterval.isEmpty());	
		
		CurrencyExchangeResponse financialResponseJson = this.yahooFinanceController.extractCacheData(fromDate, toDate,
				baseCurrency, quoteCurrency, currencyPair, resolution, Constants.CACHE_CURRENCY_DATA, dateInterval);
		Assert.assertNotNull(financialResponseJson);
		Assert.assertFalse(financialResponseJson.getSeries().isEmpty());
	}
}
