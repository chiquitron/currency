package com.balidea.microservice;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.balidea.microservice.commons.Constants;
import com.balidea.microservice.commons.ResourceNotFoundException;
import com.balidea.microservice.controller.CurrencyExchangeController;
import com.balidea.microservice.model.respository.CurrencyExchangeRepository;
import com.balidea.microservice.model.vo.CurrencyExchangeResponse;
import com.balidea.microservice.services.CurrencyExchangeServicePost;
import com.balidea.microservice.services.QuandlCacheManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


/**
 * 
 * @author Balidea Consulting & Programming
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { CurrencyExchangeApplication.class })
public class CurrencyExchangeApplicationFunctionalTests {

	@Autowired
	private CurrencyExchangeRepository financeRepository;
	@Autowired
	private CurrencyExchangeController yahooFinanceController;
	@Autowired
	private CurrencyExchangeServicePost yahooWebService;
	@Autowired
	private QuandlCacheManager cacheManager;


    /**
     * Call the REST webservice that obtain the financial results
     * If the to date is less than the from date 
     * It will be an error and send a ResourceNotFoundException 404 response 
     */
	@Test(expected=ResourceNotFoundException.class)
	public void invalidDateParams() throws Exception  {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2014-01-31");
		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";
		String resolution = "daily";

		ResponseEntity<CurrencyExchangeResponse> financialResponse = this.yahooFinanceController.obtainCurrencyExchange(fromDate, toDate, baseCurrency, quoteCurrency, resolution);
	}
	
    /**
     * Call the REST webservice that obtain the financial results
     * If country name is not mapped at appplication.properties
     * It will be an error and send a ResourceNotFoundException 404 response 
     */
	@Test(expected=ResourceNotFoundException.class)
	public void invalidCountryParams() throws Exception  {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-31");
		String resolution = "daily";
		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";

		ResponseEntity<CurrencyExchangeResponse> financialResponse = this.yahooFinanceController.obtainCurrencyExchange(fromDate, toDate, baseCurrency, quoteCurrency, resolution);
	}
	
    /**
     * Call the REST webservice that obtain the financial results
     * If resolution is not daily/weekly
     * It will be an error and send a ResourceNotFoundException 404 response 
     */
	@Test(expected=ResourceNotFoundException.class)
	public void invalidResolutionParams() throws Exception  {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-31");
		String resolution = "d";
		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";

		ResponseEntity<CurrencyExchangeResponse> financialResponse = this.yahooFinanceController.obtainCurrencyExchange(fromDate, toDate, baseCurrency, quoteCurrency, resolution);
	}
    /**
     * Call the REST webservice that obtain the financial results
     * If ther's no information storage at the cache memory
     * It will make a call to Yahoo Finance API to obtain information since three years ago 
     * Parse this results and calculate data from weekends and holidays
     * Storage this information into cache memory
     * And extract the interval data values to request
     */
	@Test
	public void firstCallCacheEmpty() throws Exception  {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-031");
		String resolution = "daily";
		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";

		try {

			ResponseEntity<CurrencyExchangeResponse> financialResponse = this.yahooFinanceController.obtainCurrencyExchange(fromDate, toDate, baseCurrency, quoteCurrency, resolution);
			
			CurrencyExchangeResponse financialResponseJson = financialResponse.getBody();
			
			//parse to JSON the result
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String jsonResult = ow.writeValueAsString(financialResponseJson);
			
			Assert.assertNotNull(jsonResult);
			Assert.assertThat(jsonResult, 
					com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath("timeseries-response-schema.json"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
    /**
     * Call the REST webservice that obtain the financial results
     * If ther's information storage at the cache memory
     * It will extract the interval data values to request
     */
	@Test
	public void fnextCallCacheStorage() throws Exception  {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-031");
		String resolution = "daily";
		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";

		try {

			ResponseEntity<CurrencyExchangeResponse> financialResponse = this.yahooFinanceController.obtainCurrencyExchange(fromDate, toDate, baseCurrency, quoteCurrency, resolution);
			
			CurrencyExchangeResponse financialResponseJson = financialResponse.getBody();
			
			//parse to JSON the result
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String jsonResult = ow.writeValueAsString(financialResponseJson);
			
			Assert.assertNotNull(jsonResult);
			Assert.assertThat(jsonResult, 
					com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath("timeseries-response-schema.json"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
