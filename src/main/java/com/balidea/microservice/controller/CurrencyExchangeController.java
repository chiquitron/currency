package com.balidea.microservice.controller;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.balidea.microservice.commons.ApplicationProperties;
import com.balidea.microservice.commons.CommonUtils;
import com.balidea.microservice.commons.Constants;
import com.balidea.microservice.commons.ResourceNotFoundException;
import com.balidea.microservice.model.respository.CurrencyExchangeRepository;
import com.balidea.microservice.model.vo.CurrencyExchangeResponse;
import com.balidea.microservice.model.vo.Exchange;
import com.balidea.microservice.services.QuandlCacheManager;

/**
 * 
 * @author Balidea Consulting & Programming
 */
@RestController()
public class CurrencyExchangeController {

	private static final Logger logger = LoggerFactory
			.getLogger(CurrencyExchangeController.class);
	
	@Autowired
	private HelloWorldService helloWorldService;
	@Autowired
	private QuandlCacheManager cacheManager;
	@Autowired
	private CurrencyExchangeRepository currencyExchangeRepository;
	@Autowired 
	private ApplicationProperties propertiesConfig;


	@RequestMapping("/currencyExchange")
	@ResponseBody
	public Map<String, String> helloWorld() {
		return Collections.singletonMap("message",
				this.helloWorldService.getHelloMessage());
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/currencyExchange/obtainCurrencyExchange", method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST})
	@ResponseBody
	public ResponseEntity<CurrencyExchangeResponse> obtainCurrencyExchange(@RequestParam(value = "fromDate", required=true) @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
			@RequestParam(value = "toDate", required=true) @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate, 
			@RequestParam(value = "baseCurrency", required=true) String baseCurrency, @RequestParam(value = "quoteCurrency", required=true) String quoteCurrency,
			@RequestParam(value = "resolution", required=true) String resolution) throws ResourceNotFoundException {

		logger.info("********************** BEGIN OBTAIN CURRENCY EXCHANGES **************************");
		
		//validate the input parameters format
		Boolean validInputParameters = this.validateInputParameters(fromDate, toDate,baseCurrency, quoteCurrency, resolution);
		
		if (!validInputParameters){
			logger.info("********************** END OBTAIN CURRENCY EXCHANGE **************************");
			throw new ResourceNotFoundException();
		}
		//object JSON response
		CurrencyExchangeResponse currencyExchangeResponseJson = new CurrencyExchangeResponse();

		//calulate the dates intervale between fromDate an toDate
		List<String> dateInterval = CommonUtils.dateInterval(fromDate, toDate);

		//get the correct name of the currency pair from propeties file without uppercase
		String currencyPair = propertiesConfig.getCurrencyPair(baseCurrency.toLowerCase()+quoteCurrency.toLowerCase());
		
		try {

			//if it's something stored at cache memory 
			//it's storage in cacheDataResults the date of the storage results were calculated
			Boolean cacheDataAvailable = this.cacheManager.isCacheDataAvailable(Constants.CACHE_DATE_RESULTS, currencyPair, fromDate);
			Boolean outdatedData = this.cacheManager.isCacheDataOutdate(Constants.CACHE_DATE_RESULTS, currencyPair);

			//if it's not stored information from the interval dates
			//or its outdated information stored
			//it's necessary to call Quandl API to obtain the results 
			if(!cacheDataAvailable || outdatedData){
				logger.info("---> There is not information available into cache memory or its outdated ");
				
				currencyExchangeResponseJson = callQuandlApi(fromDate, toDate, baseCurrency, quoteCurrency, currencyPair,
						resolution,outdatedData, Constants.CACHE_CURRENCY_DATA, dateInterval);
			} else{
				//if it's  stored information from the interval dates
				//it's necessary to extract data from cache memory
				logger.info("---> There is information available into cache memory ");
				
				currencyExchangeResponseJson = extractCacheData(fromDate, toDate, baseCurrency, quoteCurrency,
						currencyPair, resolution, Constants.CACHE_CURRENCY_DATA, dateInterval);
			}
			
		} catch (Exception e) {
			logger.info("********************** END OBTAIN CURRENCY EXCHANGE **************************");
			throw new ResourceNotFoundException();
		}

		logger.info("********************** END OBTAIN CURRENCY EXCHANGE **************************");
		
		return new ResponseEntity<CurrencyExchangeResponse>(currencyExchangeResponseJson, HttpStatus.OK);
	}
	
	/**
	 * Method that validate the input params
	 * @param fromDate 
	 * 			date from to calculate
	 * @param toDate
	 * 			date to to calculate
	 * @param baseCurrency
	 * 			baseCurrency for the call 
	 * @param quoteCurrency
	 * 			quoteCurrency for the call 
	 * @param resolution
	 * 			'daily or weekly
	 * @return Boolean 
	 * 			true or false if all the input params are correct
	 */
	public Boolean validateInputParameters(Date fromDate, Date toDate,
			String baseCurrency, String quoteCurrency, String resolution) {
		
		logger.info("---> Trying to validate input params ");

		Boolean validParameters = true;
		
		//if to date is less than from date there is an error
		if (toDate.compareTo(fromDate) < 0){
			validParameters = false;
			logger.info("---> Failed  validate input params to date: "  + toDate + " is less than "
					+ "from date: " + fromDate + " that's not correct");
		}
		
		//if there's no properties with the baseCurrency name
		if (propertiesConfig.getCurrencyName(baseCurrency.toLowerCase()) == null){
			validParameters = false;
			logger.info("---> Failed  validate input param baseCurrency: " + baseCurrency + " that's not correct");
		}
		
		//if there's no properties with the quoteCurrency name 
		if (propertiesConfig.getCurrencyName(quoteCurrency.toLowerCase()) == null){
			validParameters = false;
			logger.info("---> Failed  validate input param quoteCurrency: " + quoteCurrency + " that's not correct");
		}
		
		//if there's no properties with the pair baseCurrency+quoteCurrency name 
		if (propertiesConfig.getCurrencyPair(baseCurrency.toLowerCase()+quoteCurrency.toLowerCase()) == null){
			validParameters = false;
			logger.info("---> Failed  validate input param ther's no pair: " + baseCurrency.toLowerCase()+quoteCurrency.toLowerCase() + " that's not correct");
		}
		
		//if resolution that came in the input parameter is not daily or weekly it's not correct
		if (!Constants.DAILY.equals(resolution.toLowerCase()) && !Constants.WEEKLY.equals(resolution.toLowerCase())){
			validParameters = false;
			logger.info("---> Failed  validate input param resolution: " + resolution + " that's not correct");
		}
		
		logger.info("---> Result of validating input params valid =  " + validParameters );
		return validParameters;
	}

	/**
	 * Method that call the Quandl API to obtain the Currency Exchange results
	 * @param fromDate 
	 * 			date from to calculate
	 * @param toDate
	 * 			date to to calculate
	 * @param baseCurrency
	 * 			baseCurrency input data
	 * @param quoteCurrency
	 * 			quoteCurrency input data
	 * @param currencyPair
	 * 			currencyPair for the call 
	 * @param resolution
	 * 			daily  or weekly
	 * @param cacheDateResults
	 * 			cacheDateResults information
	 * @param outdatedData
	 * 			boolean that indicates if the storage data its outdatedData
	 * @param cacheCurrencyData
	 * 			cacheCurrencyData finance results cache storage
	 * @param dateInterval
	 * 			list of date to interval the results
	 * @return currencyExchangeResponseJson 
	 * 			object that contains the currency exchange results
	 */
	public CurrencyExchangeResponse callQuandlApi(Date fromDate, Date toDate, String baseCurrency, String quoteCurrency,
			String currencyPair, String resolution,  
			Boolean outdatedData, String cacheCurrencyData,
			List<String> dateInterval) throws Exception {
		
		CurrencyExchangeResponse currencyExchangeResponseJson;
		List<Exchange> currencyExchangeResults = null;
		
		//date and currency pair Cached Data Storage
		Cache cacheDateResults = this.cacheManager.getCache(Constants.CACHE_DATE_RESULTS);
				
		//if it's outdated information stored or it's not stored any information
		//call currencyExchangeService and update the cache data since today to three years ago
		if ((outdatedData && cacheDateResults.getSize() > 0 && cacheDateResults.get(currencyPair) != null) || 
			(cacheDateResults.get(currencyPair) == null)){
			
			logger.info("---> Cache memory it's empty or information its outdated ");
			
			//call the Quandl API to cache the information since three years ago
			currencyExchangeResults =  this.caheYearsAgoData(currencyPair);
			
			//look for the results whose are update and storage right now  
			//extract data from cache memory
			currencyExchangeResponseJson = extractCacheData(fromDate, toDate, baseCurrency, quoteCurrency, currencyPair,
					resolution, Constants.CACHE_CURRENCY_DATA, dateInterval);
			
		} else { 
			//if it's not stored information from the interval dates
			//call currencyExchangeService without update the cache data
			//always the Quandl API is call with resolution daily to store all the information 
			//add three days before the from date its necessary for having information if the form day is weekend or holiday
			logger.info("---> There is not information available into cache memory "
					+ "it's a directly call from Quandl API without cache the data");

			currencyExchangeResults = this.currencyExchangeRepository.currencyExchangeResults(fromDate, toDate, currencyPair, Constants.DAILY, true);
			
			//parse the data results into JSON model schema
			currencyExchangeResponseJson = this.currencyExchangeRepository.parseExchangeResponseJson(currencyExchangeResults, fromDate, 
					toDate, baseCurrency, quoteCurrency, currencyPair, resolution);
		}
		
		return currencyExchangeResponseJson;
	}


	/**
	 * Method that call the Quandl API to obtain the Currency Exchange results
	 * since three years ago since execution day and three days ago since execution day
	 * This method storage that information into cache memory
	 * The call to Quandl API will be daily to obtain all the results 
	 * @param currencyPair
	 * 			currencyPair for the call 
	 * @return List<Finance>
	 * 			object that contains the financial results 
	 */
	public List<Exchange> caheYearsAgoData(String currencyPair) throws Exception {
		
		List<Exchange> currencyExchangeResults;
		Date todayDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(todayDate);
		calendar.add(Calendar.YEAR, - propertiesConfig.getIntValue(Constants.YEARS_AGO_STORAGE));
		//add three days before the from date its necessary for having information if the form day is weekend or holiday
		calendar.add(Calendar.DATE, - propertiesConfig.getIntValue(Constants.DAYS_AGO_STORAGE));
		Date yearsAgo = calendar.getTime();
		
		//call Quandl API and storage Currency Exchange results in cache memory
		//since three year ago until today
		//always the Quandl API is call with resolution daily to store all the information 
		currencyExchangeResults = this.currencyExchangeRepository.cacheQuandlResponse(yearsAgo, todayDate, currencyPair, Constants.DAILY);
		
		return currencyExchangeResults;
	}

	/**
	 * Method that call the Quandl API to obtain the Currency Exchange results
	 * @param fromDate 
	 * 			date from to calculate
	 * @param toDate
	 * 			date to to calculate
	 * @param baseCurrency
	 * 			baseCurrency input data
	 * @param quoteCurrency
	 * 			quoteCurrency input data
	 * @param currencyPair
	 * 			currencyPair for the call 
	 * @param resolution
	 * 			daily or weekly
	 * @param cacheCurrencyData
	 * 			cacheCurrencyData finance results cache storage
	 * @param dateInterval
	 * 			list of date to interval the results
	 * @return CurrencyExchangeResponse 
	 * 			object that contains the Currency Exchange results
	 * @throws Exception 
	 */
	public CurrencyExchangeResponse extractCacheData(Date fromDate, Date toDate, String baseCurrency, String quoteCurrency,
			String currencyPair, String resolution, String cacheCurrencyData,
			List<String> dateInterval) throws Exception {
		
		CurrencyExchangeResponse currencyExchangeResponseJson;

		//there are storaged in the cache memory the results from the country
		List<Exchange> currencyExchangeResults = this.cacheManager.extractCurrencyData(cacheCurrencyData, currencyPair, dateInterval);

		//if there is a problem an it was impossible to extract data from cache 
		//its necessary to call Yahoo API to obtain the information
		if (currencyExchangeResults.isEmpty()){
			logger.info("--> Imposible to extract data from cache it's necessary to make a puntual call to Yahoo API");

			//in the API call it's necessary to obtain 3 years ago to prevent if the from date is a weekend day or a holiday
			currencyExchangeResults = this.currencyExchangeRepository.currencyExchangeResults(fromDate, toDate, currencyPair, Constants.DAILY, true);
			
		}

		//parse the data results into JSON model schema
		currencyExchangeResponseJson = this.currencyExchangeRepository.parseExchangeResponseJson(currencyExchangeResults, fromDate, 
				toDate, baseCurrency, quoteCurrency, currencyPair, resolution);

		return currencyExchangeResponseJson;
	}
	

}
