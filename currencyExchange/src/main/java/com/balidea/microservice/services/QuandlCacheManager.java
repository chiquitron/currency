package com.balidea.microservice.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.balidea.microservice.commons.ApplicationProperties;
import com.balidea.microservice.commons.Constants;
import com.balidea.microservice.model.vo.DataExchangeResults;
import com.balidea.microservice.model.vo.Exchange;

/**
 * 
 * @author Balidea Consulting & Programming
 */
@Component
public class QuandlCacheManager extends CacheManager{
	
	private static final Logger logger = LoggerFactory
			.getLogger(QuandlCacheManager.class);
	
	@Autowired 
	private ApplicationProperties propertiesConfig;


	/**
	 * Method that update the cache memory and storage new information
	 * this method will be use to storage the Currency Exchange at cache memory
	 * @param cacheName 
	 * 			cache memory name who wants to be updated
	 * @param key
	 * 			cache memory item key who wants to be updated
	 * @param financialResults
	 * 			list of results who want to be storage at the cache memory
	 */
	public void updateCacheElement (String cacheName, String key, List<Exchange> financialResults ){
		
		logger.info("---> Updating information from currencyPair: " + key + " into cache memory ");
		
		Cache cacheCurrencyData = super.getCache(cacheName);
		//remove the old information storage at the cache from the currencyPair indicated in the key parameter
		cacheCurrencyData.remove(key);
		//storage in cache the new information from this currencyPair 
		cacheCurrencyData.put(new Element(key, financialResults));
		
		logger.info("---> Finish updating information from currencyPair: " + key + " into cache memory ");
	}

	/**
	 * Method that update the cache memory and storage new information
	 * this method will be use to storage the date of each currencyPair whose the exchange results was storage
	 * @param cacheName 
	 * 			cache memory name who wants to be updated
	 * @param key
	 * 			cache memory item key who wants to be updated
	 */
	public void updateCacheElement (String cacheName, String key){
		
		logger.info("---> Updating data storage information from currencyPair: " + key + " into cache memory ");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		//store the data when the currency exchange results were stored in the cache memory
		Cache cacheDateResults = super.getCache(cacheName);
		//remove the old information storage at the cache from this currencyPair
		cacheDateResults.remove(key);
		//storage in cache the new information from this currencyPair 
		DataExchangeResults dateFinanceResults = new DataExchangeResults(dateFormat.format(new Date()), key);
		cacheDateResults.put(new Element(key, dateFinanceResults));
		
		logger.info("---> Finish updating data storage information from currencyPair: " + key + " into cache memory ");
	}
	
	/**
	 * Method that extract information from the cache memory
	 * this method will be use to extract from cache memory the currency exchange results from an interval time
	 * @param cacheName 
	 * 			cache memory name who wants to be updated
	 * @param key
	 * 			cache memory item key who wants to be updated
	 * @param List<String> dateInterval
	 * 			list of interval time to extract the results
	 * @return List<Exchange>
	 * 			list of Currency Exchange results extracted from cache memory
	 */
	@SuppressWarnings("unchecked")
	public List<Exchange> extractCurrencyData (String cacheName, String key, List<String> dateInterval ){
		
		logger.info("---> Trying to extract information from cache memory for currencyPair: " + key);
		
		//we have in the cache memory store the results from that data 
		//data stored currency exchange close
		Cache cacheCurrencyData = super.getCache(cacheName);
				
		//currencyPair = cacheKey (example EURDKK)
		//recover the cache data storage from the data interval
		List<String> listAll = new ArrayList<String>();
		listAll.add(key);
		Map<Object, Element> cacheResultsInterval = cacheCurrencyData.getAll(listAll);
		
		//Quandl Currency Exchange list results
		List<Exchange> currencyExchangeResults = new ArrayList<Exchange>();
		
		for (Map.Entry<Object, Element> cacheResultItem : cacheResultsInterval.entrySet()) {
		    Object keyItem = cacheResultItem.getKey();
		    if (cacheResultsInterval.get(keyItem) != null){
			    List<Exchange> cacheExchangeResults = (List<Exchange>) cacheResultsInterval.get(keyItem).getObjectValue();
			 
			    for (Exchange exchangeItem : cacheExchangeResults){
			    	if (dateInterval.contains(exchangeItem.getDateExcahnge())){
			    		currencyExchangeResults.add(exchangeItem);
			    	}
			    }
		    } else {
		    	logger.info("---> It was immposible to extract currencyPair data from cache for currencyPair: " + key);
		    }
		}
		
		logger.info("---> Number of extract elements with information from cache memory for currencyPair: " + currencyExchangeResults.size());
		
		return currencyExchangeResults;
		
	}
	
	/**
	 * Method that indicates if there is storage information from a particular key value
	 * this method will be use to indicate if there is storage information from a particular currencyPair 
	 * taking care the from date this information is need and knowing that in cache memory will be storage
	 * information since three years ago
	 * @param cacheName 
	 * 			cache memory name that wants to be updated
	 * @param key
	 * 			cache memory item key that wants to be updated
	 * @param fromDate 
	 * 			date from to calculate the results
	 * @return Boolean
	 * 			who indicates if there is information available at cache memory
	 */
	public Boolean isCacheDataAvailable (String cacheName, String key, Date fromDate) throws ParseException {
		
		logger.info("---> Seeing if there is information available in the cache memory");
		
		Boolean cacheDataAvailable = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		//date and currencyPair Cached Data Storage
		Cache cacheDateResults = super.getCache(cacheName);

		//if it's something stored at cache memory 
		//it's storage in cacheDataResults the date of the storage results were calculated
		if (cacheDateResults.getSize() > 0 && cacheDateResults.get(key) != null){

			Calendar calendar = Calendar.getInstance();
			
			DataExchangeResults cacheDataExchangeResults = (DataExchangeResults) cacheDateResults.get(key).getObjectValue();
		    
			if (cacheDataExchangeResults != null){
				Date cacheDate = dateFormat.parse(cacheDataExchangeResults.getDateResults());
				calendar.setTime(cacheDate);
				calendar.add(Calendar.YEAR, - propertiesConfig.getIntValue(Constants.YEARS_AGO_STORAGE));
				//add three days before the from date its necessary for having information if the form day is weekend or holiday
				calendar.add(Calendar.DATE, - propertiesConfig.getIntValue(Constants.DAYS_AGO_STORAGE));
				Date yearsAgo = calendar.getTime();
				cacheDataAvailable = (fromDate.compareTo(yearsAgo)>=0);
			} else {
				cacheDataAvailable = false;
			}
		}
		
		logger.info("---> Result if there is information available in the cache memory: " + cacheDataAvailable);
		
		return cacheDataAvailable;
	}
	
	
	/**
	 * Method that indicates if information storage from a particular key value is outdated
	 * @param cacheName 
	 * 			cache memory name that wants to be updated
	 * @param key
	 * 			cache memory item key that wants to be updated
	 * @return Boolean
	 * 			that indicates if the cache memory information storage is outdated
	 */
	public Boolean isCacheDataOutdate (String cacheName, String key) throws ParseException {
		
		logger.info("---> Seeing if the information available in the cache memory is outdated ");
		
		Boolean outdatedData = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		//date and currencyPair Cached Data Storage
		Cache cacheDateResults = super.getCache(cacheName);

		if (cacheDateResults.getSize() > 0 && cacheDateResults.get(key) != null){

			DataExchangeResults cacheDataExchangeResults = (DataExchangeResults) cacheDateResults.get(key).getObjectValue();
		    
			if (cacheDataExchangeResults != null){
				//if date who is storage in the cache is less than the execution date
				//the data will be outdated
				Date todayDate =  dateFormat.parse(dateFormat.format(new Date()));
				Date cacheDate = dateFormat.parse(cacheDataExchangeResults.getDateResults());
				outdatedData = (todayDate.compareTo(cacheDate)>0);
			} 
		}

		logger.info("---> Result information available in the cache memory is outdated: " + outdatedData);
		
		return outdatedData;
	}

}
