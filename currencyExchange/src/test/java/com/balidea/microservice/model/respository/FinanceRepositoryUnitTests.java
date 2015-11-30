package com.balidea.microservice.model.respository;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.sf.ehcache.Cache;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.balidea.microservice.CurrencyExchangeApplication;
import com.balidea.microservice.commons.Constants;
import com.balidea.microservice.controller.CurrencyExchangeController;
import com.balidea.microservice.model.vo.CurrencyExchangeResponse;
import com.balidea.microservice.model.vo.Exchange;
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
public class FinanceRepositoryUnitTests extends CurrencyExchangeController {

	@Autowired
	private CurrencyExchangeRepository currencyExchangeRepository;
	@Autowired
	private CurrencyExchangeController currencyExcahngeController;
	@Autowired
	private CurrencyExchangeServicePost currencyWebService;
	@Autowired
	private QuandlCacheManager cacheManager;

    /**
     * Call method that makes the storage the Currency Exchange Information into cache memory
     * this method will storage the finance information into cache where the cache 
     * key value will be the country name
     */
	@Test
	public void storageReultsCache() throws Exception  {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-31");
		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";
		String currencyPair = baseCurrency + quoteCurrency;
		String resolution = "daily";
		
		List<Exchange> excahngeResults  = this.currencyExchangeRepository.cacheQuandlResponse(fromDate, toDate, currencyPair, resolution);
		Assert.assertNotNull(excahngeResults);
		Assert.assertFalse(excahngeResults.isEmpty());

		//verify if the method storage in cache memory the results
		//date and currencyPair Cached Data Storage
		Cache cacheDateResults = this.cacheManager.getCache(Constants.CACHE_CURRENCY_DATA);
		
		Boolean informationCached =  (cacheDateResults.getSize() > 0 && cacheDateResults.get("EURDKK") != null) ? true : false;
		Assert.assertTrue(informationCached);
		
	}
	
	
    /**
     * Call method that makes the call to Quandl API
     * this structure will parse csv into a List of object which contains the currency exchange date, 
     * the Currency Exchange Value and if it's a real value (if is weekend or holiday it won't be a real value)
     */
	@Test
	public void callQuandlApiParseRes() throws Exception  {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-31");
		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";
		String currencyPair = baseCurrency + quoteCurrency;
		String resolution = "daily";
		
		List<Exchange> financialResults  = this.currencyExchangeRepository.currencyExchangeResults(fromDate, toDate, currencyPair, resolution, true);
		Assert.assertNotNull(financialResults);
		Assert.assertFalse(financialResults.isEmpty());
	}
	
    /**
     * Call method that makes the parse from the csv obtain in the Quandl API call
     * Test that this csv is parse correctly into a particular structure
     * this structure will parse csv into a List of object which contains the currency exchange date, 
     * the currency exchange Value and if it's a real value (if is weekend or holiday it won't be a real value)
     */
	@Test
	public void parseApiResultFormat() throws Exception  {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		// we call with three days ago real date we want because is the real function "2015-01-01"
		Date callDate = dateFormat.parse("2014-12-29");
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-31");
		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";
		String currencyPair = baseCurrency + quoteCurrency;
		String resolution = "daily";
		
		byte[] byteArrayResult = this.currencyWebService.callQuandlCurrencyExchange(callDate, toDate, currencyPair, resolution);
		Assert.assertNotNull(byteArrayResult);

		List<Exchange> exchangeResults = this.currencyExchangeRepository.parseQuandlExchangeResponse(byteArrayResult,fromDate, toDate, true);
		Assert.assertNotNull(exchangeResults);
		Assert.assertFalse(exchangeResults.isEmpty());
		
		for (Exchange exchangeItem : exchangeResults){
			Assert.assertNotNull(exchangeItem);
			Assert.assertNotNull(exchangeItem.getDateExcahnge());
			Assert.assertNotNull(exchangeItem.getExchangeValue());
			Assert.assertNotNull(exchangeItem.getRealExchangeValue());
			
			//it's a holiday so it isn't a real value
			if ("2015-01-01".equals(exchangeItem.getDateExcahnge())){
				Assert.assertEquals(new String("N"), exchangeItem.getRealExchangeValue());
			}
			
			//it's a weekend so it isn't a real value
			if ("2015-01-04".equals(exchangeItem.getDateExcahnge())){
				Assert.assertEquals(new String("N"), exchangeItem.getRealExchangeValue());
			}
		}
		
	}
	
    /**
     * Call method that makes the parse financial results into the final JSON structure
     * validate if the JSON result match with the structure
     */
	@Test
	public void obtainFinalJSON() throws Exception  {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		// we call with three days ago real date we want because is the real function "2015-01-01"
		Date callDate = dateFormat.parse("2014-12-29");
		Date fromDate = dateFormat.parse("2015-01-01");
		Date toDate = dateFormat.parse("2015-01-31");
		String baseCurrency = "EUR";
		String quoteCurrency = "DKK";
		String currencyPair = baseCurrency + quoteCurrency;
		String resolution = "daily";
		
		byte[] byteArrayResult = this.currencyWebService.callQuandlCurrencyExchange(callDate, toDate, currencyPair, resolution);
		Assert.assertNotNull(byteArrayResult);

		List<Exchange> exchangeResults = this.currencyExchangeRepository.parseQuandlExchangeResponse(byteArrayResult,fromDate, toDate, true);
		Assert.assertNotNull(exchangeResults);
		Assert.assertFalse(exchangeResults.isEmpty());
		

		//parse the data results into JSON model schema
		CurrencyExchangeResponse exchangeResponseJson = this.currencyExchangeRepository.parseExchangeResponseJson(exchangeResults, fromDate, 
				toDate, baseCurrency, quoteCurrency, currencyPair, resolution);

		//parse to JSON the result
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String jsonResult = ow.writeValueAsString(exchangeResponseJson);
		
		Assert.assertNotNull(exchangeResponseJson);
		Assert.assertThat(jsonResult, 
				com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath("timeseries-response-schema.json"));
	}
}
