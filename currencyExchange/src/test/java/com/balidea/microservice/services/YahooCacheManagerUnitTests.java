package com.balidea.microservice.services;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.balidea.microservice.CurrencyExchangeApplication;
import com.balidea.microservice.commons.Constants;
import com.balidea.microservice.model.vo.DataExchangeResults;
import com.balidea.microservice.model.vo.Exchange;



/**
 * 
 * @author Balidea Consulting & Programming
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { CurrencyExchangeApplication.class })
public class YahooCacheManagerUnitTests {

	@Autowired
	private QuandlCacheManager cacheManager;

    /**
     * Call method that update a list of elements into the cache memory
     */
	@SuppressWarnings("unchecked")
	@Test
	public void updateCacheListElement() throws Exception {
	
		//first we put an element into the cache
		List<Exchange> financialResults = new ArrayList<Exchange>();
		Exchange finance = new Exchange("2015-01-01", new BigDecimal(1000), "Y");
		String key = "test";
		financialResults.add(finance);
		
		Cache cacheFinanceData = this.cacheManager.getCache(Constants.CACHE_CURRENCY_DATA);
		cacheFinanceData.put(new Element(key, financialResults));
		Assert.assertNotNull(cacheFinanceData.get(key));
		List<Exchange> dataCached = (List<Exchange>) cacheFinanceData.get(key).getObjectValue();
		Exchange financeSave = dataCached.get(0);
		Assert.assertEquals(financeSave.getExchangeValue(), new Integer(1000));
		
		Exchange newFinanceResult = new Exchange("2015-01-01", new BigDecimal(2000), "Y");
		List<Exchange> listData = new ArrayList<Exchange>();
		listData.add(newFinanceResult);
		this.cacheManager.updateCacheElement(Constants.CACHE_CURRENCY_DATA, key, listData);
		//check that the data was updated
		Assert.assertNotNull(cacheFinanceData.get(key));
		List<Exchange> dataUpdated = (List<Exchange>) cacheFinanceData.get(key).getObjectValue();
		Exchange financeUpdate = dataUpdated.get(0);
		Assert.assertEquals(financeUpdate.getExchangeValue(), new BigDecimal(2000));
		
	}
	
    /**
     * Call method that update an element into the cache memory that storage the date execution
     */
	@SuppressWarnings("unchecked")
	@Test
	public void updateCacheElement() throws Exception {
	
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		String key = "testData";
		
		
		//first we put an element into the cache
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, - 3);
		
		DataExchangeResults dateFinanceResults= new DataExchangeResults(dateFormat.format(calendar.getTime()), key);
		
		Cache cacheFinanceData = this.cacheManager.getCache(Constants.CACHE_DATE_RESULTS);
		cacheFinanceData.put(new Element(key, dateFinanceResults));
		Assert.assertNotNull(cacheFinanceData.get(key));
		DataExchangeResults dateCache = (DataExchangeResults) cacheFinanceData.get(key).getObjectValue();
		Assert.assertEquals(dateCache.getDateResults(), dateFormat.format(calendar.getTime()));
		
		this.cacheManager.updateCacheElement(Constants.CACHE_DATE_RESULTS, key);
		Assert.assertNotNull(cacheFinanceData.get(key));
		DataExchangeResults dateUpdated = (DataExchangeResults) cacheFinanceData.get(key).getObjectValue();
		Assert.assertEquals(dateUpdated.getDateResults(), dateFormat.format(new Date()));
		
	}
	
    /**
     * Call method that check if there is cache data available 
     */
	@Test
	public void falseIsCacheDataAvailable() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		String key = "falseAvailable";
		Date fromDate = dateFormat.parse("2015-01-02");
		Boolean isAvailable = this.cacheManager.isCacheDataAvailable(Constants.CACHE_CURRENCY_DATA, key, fromDate);
		Assert.assertNotNull(isAvailable);
		Assert.assertFalse(isAvailable);
	}
	
    /**
     * Call method that check if there is cache data available 
     */
	@Test
	public void trueIsCacheDataAvailable() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		String fromDate = "2015-01-01";
		String key = "trueAvailable";
		
		//fist of all it's necessary to cache the data
		DataExchangeResults dateFinanceResults= new DataExchangeResults(fromDate, key);
		
		Cache cacheFinanceData = this.cacheManager.getCache(Constants.CACHE_DATE_RESULTS);
		cacheFinanceData.put(new Element(key, dateFinanceResults));
		
		Boolean isAvailable = this.cacheManager.isCacheDataAvailable(Constants.CACHE_DATE_RESULTS, key,  dateFormat.parse(fromDate));
		Assert.assertNotNull(isAvailable);
		Assert.assertTrue(isAvailable);
	}
	
    /**
     * Call method that check if there is cache data storage and it's outdated
     */
	@Test
	public void falseIsCacheDataOutdated() throws Exception {
		String key = "falseOutdated";
		Boolean isAvailable = this.cacheManager.isCacheDataOutdate(Constants.CACHE_DATE_RESULTS, key);
		Assert.assertNotNull(isAvailable);
		Assert.assertFalse(isAvailable);
	}
	
    /**
     * Call method that check if there is cache data storage and it's outdated
     */
	@Test
	public void trueIsCacheDataOutdated() throws Exception {
		String fromDate = "2015-01-01";
		String key = "trueOutdated";
		
		//fist of all it's necessary to cache the data
		DataExchangeResults dateFinanceResults= new DataExchangeResults(fromDate, key);
		
		Cache cacheFinanceData = this.cacheManager.getCache(Constants.CACHE_DATE_RESULTS);
		cacheFinanceData.put(new Element(key, dateFinanceResults));
		
		Boolean isAvailable = this.cacheManager.isCacheDataOutdate(Constants.CACHE_DATE_RESULTS, key);
		Assert.assertNotNull(isAvailable);
		Assert.assertTrue(isAvailable);
	}
}
