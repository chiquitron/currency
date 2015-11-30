
package com.balidea.microservice.model.respository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.balidea.microservice.commons.ApplicationProperties;
import com.balidea.microservice.commons.CommonUtils;
import com.balidea.microservice.commons.Constants;
import com.balidea.microservice.model.vo.CurrencyExchangeResponse;
import com.balidea.microservice.model.vo.Exchange;
import com.balidea.microservice.model.vo.Parameters;
import com.balidea.microservice.model.vo.Sample;
import com.balidea.microservice.model.vo.Series;
import com.balidea.microservice.services.CurrencyExchangeServicePost;
import com.balidea.microservice.services.QuandlCacheManager;


/**
 * 
 * @author Balidea Consulting & Programming
 */
@Component
public class CurrencyExchangeRepository {

	private static final Logger logger = LoggerFactory
			.getLogger(CurrencyExchangeRepository.class);

	@Autowired
	private CurrencyExchangeServicePost currencyExchangeService;
	@Autowired
	private QuandlCacheManager cacheManager;
	@Autowired 
	private ApplicationProperties propertiesConfig;
	
	/**
	 * Method that call theQuandl API to obtain the Currency Exchange and save into the cache memory the results
	 * @param fromDate 
	 * 			date from to calculate
	 * @param toDate
	 * 			date to to calculate
	 * @param currencyPair
	 * 			currencyPair for the call 
	 * @param resolution
	 * 			daily or weekly
	 * @param cacheFinanceData
	 * 			cacheFinanceData finance results cache storage
	 * @param dateInterval
	 * 			list of date to interval the results
	 * @return List<Exchange>
	 * 			list that contains the Currency Exchange results
	 */
	public List<Exchange> cacheQuandlResponse(Date fromDate, Date toDate, String currencyPair, String resolution) throws Exception {
		
		logger.info("---> Caching FinanceResults with fromDate '" + fromDate + "' toDate '"  + toDate + "'"
				 + " currencyPair '"  + currencyPair + "'" + " resolution '"  + resolution + "'");

		List<Exchange> currencyExchangeResults = null;
		try {
			currencyExchangeResults = this.currencyExchangeResults(fromDate, toDate, currencyPair, resolution, false);
			
			//remove the old Currency Exchange results data
			//and store the new Currency Exchange results data
			this.cacheManager.updateCacheElement(Constants.CACHE_CURRENCY_DATA, currencyPair, currencyExchangeResults);
			
			//remove the old information storage at the cache from this currencyPair
			//store the data when the Currency Exchange results were stored in the cache memory
			this.cacheManager.updateCacheElement(Constants.CACHE_DATE_RESULTS, currencyPair);
			
		}  catch (Exception e) {
			logger.info("---> An error has occurred Caching Currency Exchange results so cache wasn't updated '" + e.getMessage());
		}
		
		logger.info("---> Finish caching  Currency Exchange results with fromDate '" + fromDate + "' and toDate"  
				+ toDate + "' number of storage information elements: " + currencyExchangeResults.size());
		
		return currencyExchangeResults;
		
	}

	/**
	 * Method that call the Quandl API and parse the API results into the correct format
	 * @param fromDate 
	 * 			date from to calculate
	 * @param toDate
	 * 			date to to calculate
	 * @param currencyPair
	 * 			currencyPair for the call 
	 * @param resolution
	 * 			daily or weekly
	 * @param Boolean directlyCall 
	 * 			indicates if its a directly call because there are not information in cache
	 * @return List<Exchange>
	 * 			list that contains the Currency Exchange results
	 */
	public List<Exchange> currencyExchangeResults(Date fromDate, Date toDate, String currencyPair, String resolution, Boolean directlyCall ) throws Exception {
		
		logger.info("---> FinanceResults  call to Quandl API to obtain information with fromDate '" + fromDate + "' toDate '"  + toDate + "'"
				 + " currencyPair '"  + currencyPair + "'" + " resolution '"  + resolution + "'");
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fromDate);
		
		//if it's a directly call we call Quandl API since 3 days ago
		if (directlyCall){
			calendar.add(Calendar.DATE, - propertiesConfig.getIntValue(Constants.DAYS_AGO_STORAGE));
		} 
		
		byte[] quandlCsvResult = currencyExchangeService.callQuandlCurrencyExchange(calendar.getTime(), toDate, currencyPair, resolution);
		List<Exchange> currencyExchangeResults = this.parseQuandlExchangeResponse(quandlCsvResult,fromDate, toDate, directlyCall);
		return currencyExchangeResults;
	}
	
	/**
	 * Method that read the csv API results file and parse it into the correct format
	 * @param quandlCsv
	 * 			api csv results represented in byte
	 * @param fromDate 
	 * 			date from to calculate
	 * @param toDate
	 * 			date to to calculate
	 * @param Boolean directlyCall 
	 * 			indicates if its a directly call because there are not information in cache
	 * @return List<Exchange>
	 * 			list that contains the Currency Exchange results
	 */
	public List<Exchange> parseQuandlExchangeResponse (byte [] quandlCsv, Date fromDate, Date toDate, Boolean directlyCall ) throws Exception {
		
		logger.info("---> Saving Quandl Currency Exchange Parsing Response CSV '");
		
		File tempFile = File.createTempFile("currencyExchangeCsv", null, null);
		FileOutputStream fos = new FileOutputStream(tempFile);
		fos.write(quandlCsv);
		fos.close();

		CSVParser csvFileParser = null;
		//initialize FileReader object
		FileReader fileReader =  new FileReader(tempFile);
		
		//Create the CSVFormat object with the header mapping
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(Constants.FILE_HEADER_MAPPING).withQuote(null).withRecordSeparator('\n');

	    //Create a new list of financial close 
	    List<Exchange> currencyExchangeRecords = new ArrayList<Exchange>();
        List<Exchange> exchangeRecords = new ArrayList<Exchange>();
        
		try {

		    //initialize CSVParser object
		    csvFileParser = new CSVParser(fileReader, csvFileFormat);
		    
		    //Get a list of CSV file records
		    List<CSVRecord> csvRecords = csvFileParser.getRecords(); 

		    //Read the CSV file records starting from the second record to skip the header
		    //save the csv results into a list to order the results by date and can map weekends and holidays
		    for (int i = 1; i < csvRecords.size(); i++) {
		        CSVRecord record = csvRecords.get(i);
		        //Create a new exchangeValue object and fill his data
		        BigDecimal bigDecimalValue = new BigDecimal(record.get(Constants.VALUE));
		        Exchange exchangeValue = new Exchange(record.get(Constants.DATE) , bigDecimalValue);
		        exchangeRecords.add(exchangeValue);  
		    }
		    
		    //process all the records and map weekends and holidays
		    currencyExchangeRecords = this.processRecords(exchangeRecords, fromDate, toDate, directlyCall);
		    
		} catch (Exception e) {
			logger.info("---> Error while Saving Quandl Currency Exchange Parsing Response CSV '" + e.getMessage());
		} finally {
		    try {
		        csvFileParser.close();
		    } catch (IOException e) {
		    	logger.info("---> Error while closing fileReader/csvFileParser '" + e.getMessage());
		    }
		}
		
		logger.info("---> Finish Quandl Currency Exchange Parsing Response CSV '");
		
		return currencyExchangeRecords;
	}

	/**
	 * Method that process all the results and mapped weekends and holidays
	 * @param exchangeRecords
	 * 			api results list
	 * @param fromDate 
	 * 			date from to calculate
	 * @param toDate
	 * 			date to to calculate
	 * @param Boolean directlyCall 
	 * 			indicates if its a directly call because there are not information in cache
	 * @return List<Finance>
	 * 			list that contains the financial results 
	 * @throws ParseException 
	 */
	private List<Exchange> processRecords(List<Exchange> exchangeRecords, Date fromDate, Date toDate, Boolean directlyCall) throws ParseException {
		
		logger.info("---> Processing Quandl API response records for mapping weekends and holidays");
		
		//calulate the dates intervale between fromDate an toDate
		List<String> dateInterval = CommonUtils.dateInterval(fromDate, toDate);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		//last date ago we have data
		Calendar calendarLast = Calendar.getInstance();
		calendarLast.setTime(new Date());
		calendarLast.add(Calendar.YEAR, - propertiesConfig.getIntValue(Constants.YEARS_AGO_STORAGE));
		//add three days before the from date its necessary for having information if the form day is weekend or holiday
		calendarLast.add(Calendar.DATE, - propertiesConfig.getIntValue(Constants.DAYS_AGO_STORAGE));
		
		//if it's a directly call we have information three days ago 
		//so it's not necessary to map this three days ago to the result list
		//add three days
		Calendar calendarDirectly = Calendar.getInstance();
		calendarDirectly.setTime(fromDate);
		calendarDirectly.add(Calendar.DATE, - propertiesConfig.getIntValue(Constants.DAYS_AGO_STORAGE));
		
		Map<String,BigDecimal> mapExcahngeRecords = new HashMap<String,BigDecimal>();
		for (Exchange exchangeItem : exchangeRecords){
			mapExcahngeRecords.put(exchangeItem.getDateExcahnge(), exchangeItem.getExchangeValue());
		}
		
		Map<String, BigDecimal> orderMapExchangeRecords = new TreeMap<String, BigDecimal>(mapExcahngeRecords);
		
		List<Exchange> exchangeRecordsProcessed = new ArrayList<Exchange>();
		
		for (String dateItem : dateInterval){
			//if the Quandl API return exchange value from this date
			if (orderMapExchangeRecords.containsKey(dateItem)){
				if (!directlyCall){
					Exchange exchange = new Exchange(dateItem, orderMapExchangeRecords.get(dateItem), Constants.REAL_VALUE_YES);
					exchangeRecordsProcessed.add(exchange);
				} else if (calendarDirectly.getTime().compareTo(dateFormat.parse(dateItem)) <= 0){
					Exchange exchange = new Exchange(dateItem, orderMapExchangeRecords.get(dateItem), Constants.REAL_VALUE_YES);
					exchangeRecordsProcessed.add(exchange);
				}
				
			} else {
				//if the Quandl API doesn't return exchange value from this date
				//it's because is weekend or holiday
				//it's necessary to map this date exchange value with the most last exchange value we have  
				String previousDate = dateItem;

				while (!orderMapExchangeRecords.containsKey(previousDate)){
					//if it's a directly call we have information three days ago so we can lock for that day information
					if (directlyCall && (calendarDirectly.getTime().compareTo(dateFormat.parse(previousDate)) >= 0)){
						previousDate = calendarDirectly.getTime().toString();
						break;
					}
					if (!directlyCall && calendarLast.getTime().compareTo(dateFormat.parse(previousDate)) >= 0){
						break;
					}

					previousDate = CommonUtils.obtainPreviousWorkingDay(previousDate);
				}

				if (orderMapExchangeRecords.get(previousDate) != null){
					if (!directlyCall){
						Exchange finance = new Exchange(dateItem, orderMapExchangeRecords.get(previousDate), Constants.REAL_VALUE_NO);
						exchangeRecordsProcessed.add(finance);
					} else if (calendarDirectly.getTime().compareTo(dateFormat.parse(dateItem)) <= 0){
						Exchange finance = new Exchange(dateItem, orderMapExchangeRecords.get(previousDate), Constants.REAL_VALUE_NO);
						exchangeRecordsProcessed.add(finance);
					}
				} else {
					logger.info("---> There's no information from date: "  + dateItem + " so this date must not be in the result list");
				}
			}
			
		}
		
		return exchangeRecordsProcessed;
	}

	/**
	 * Method that generate the JSON result from the rest service
	 * @param currencyExchangeResults
	 * 			result list
	 * @param fromDate 
	 * 			date from to calculate
	 * @param toDate
	 * 			date to to calculate
	 * @param currencyPair
	 * 			currencyPair for the call 
	 * @param resolution
	 * 			daily or weekly
	 * @return CurrencyExchangeResponse
	 * 			JSON result
	 */
	public CurrencyExchangeResponse parseExchangeResponseJson(List<Exchange> currencyExchangeResults, Date fromDate, Date toDate, 
			String baseCurrency, String quoteCurrency, String currencyPair, String resolution ) {
	
		logger.info("---> Processing information parsing into JSON response structure ");
		
		CurrencyExchangeResponse  excahngeResponse = new CurrencyExchangeResponse();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		//mapping general JSON properties
		//time in seconds
		excahngeResponse.setGenerateUts( (new Date().getTime())/1000);
		excahngeResponse.setFromDate(dateFormat.format(fromDate));
		excahngeResponse.setToDate(dateFormat.format(toDate));
		
		//mapping Series parameters in JSON 
		Parameters parameters = new Parameters();
		
		parameters.setResolution(resolution.toLowerCase());
		//TODO VOO ver si es asi o por separado
		parameters.setBaseCurrency(baseCurrency);
		parameters.setQuoteCurrency(quoteCurrency);
		
		Series serieResponse = new Series();

		serieResponse.setParameters(parameters);
		
		//TODO VOO ver que nombre 
		// the name from the properties file
		serieResponse.setName(propertiesConfig.getStringValue(Constants.CURRENCY_JSON_NAME));

		List<Sample> sampleList = new ArrayList<Sample>();
		
		//mapping ​​close values to sample json data
		if (Constants.DAILY.equals(resolution.toLowerCase())) {
			for (Exchange excahngeValue : currencyExchangeResults){
				Sample sampleValue = new Sample();
				sampleValue.setDate(excahngeValue.getDateExcahnge());
				if (excahngeValue.getExchangeValue() != null){
					sampleValue.setValue(excahngeValue.getExchangeValue());
				}
				sampleList.add(sampleValue);
			}
		} else if (Constants.WEEKLY.equals(resolution.toLowerCase())) {
			mapWeeklyDatas(currencyExchangeResults, sampleList);
		}

		//mapping sample list with closing date
		serieResponse.setSamples(sampleList);
		
		excahngeResponse.getSeries().add(serieResponse);
		
		logger.info("---> Finish parsing JSON response structure ");
		
		return excahngeResponse;
	 	
	}

	/**
	 * Method that map the sample list results if the resolution is weekly
	 * if the resolution is weekly the rest service have to calculate a weekly average 
	 * with the exchange date values 
	 * @param currencyExchangeResults
	 * 			result list
	 * @param sampleList 
	 * 			sample list with the average results
	 */
	private void mapWeeklyDatas(List<Exchange> currencyExchangeResults, List<Sample> sampleList) {
		
		logger.info("---> Mapping weekly datas calculating the exchange vale average ");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		//variable who indicates the number of the week in the year
		Integer weekNumber = -1;
		//Variable with the summatory of all exchange values 
		BigDecimal exchangeValues = new BigDecimal(0);
		//variable who have the count of days for each week 
		//it's necessary because if the week is'nt complete it seems 7 days this week will be ignored
		Integer weekDayCount = 0;
		//variable who have the count of the real values for each week 
		//necessary to calculate the average 
		Integer weekRealValuesCount = 0;
		
		Exchange excahngeItem = null;
		
		for (ListIterator<Exchange> iter = currencyExchangeResults.listIterator(); iter.hasNext(); ) {

			//it's the first buckle iteration 
			if (weekNumber.equals(-1)){
				 excahngeItem = iter.next();
			} 
			
			Date dateExchange;
			try {
				dateExchange = dateFormat.parse(excahngeItem.getDateExcahnge());
				DateTime dateIso = new DateTime(dateExchange);
				DateTime dateWeek = new DateTime(dateExchange);
				
				if (!weekNumber.equals(-1)){
					weekNumber = dateIso.getWeekOfWeekyear();
				}
				
				while (weekNumber.equals(dateIso.getWeekOfWeekyear()) || weekNumber.equals(-1)){

					weekNumber = dateIso.getWeekOfWeekyear();
					//if it's a real value we will use to get the average 
					//in other case it will be ignored
					if (Constants.REAL_VALUE_YES.equals(excahngeItem.getRealExchangeValue())){
						exchangeValues = exchangeValues.add(excahngeItem.getExchangeValue());
						weekRealValuesCount += 1;
					}
					
					weekDayCount += 1;
					
					if (!iter.hasNext()){
						break;
					}
					
					excahngeItem = iter.next();
					dateExchange = dateFormat.parse(excahngeItem.getDateExcahnge());
					dateIso = new DateTime(dateExchange);

				}
				
				//it's an incomplete week the information from this week will not be send at results list
				if (weekDayCount.equals(Constants.COMPLET_WEEK)){
					//it's a completely week so the results will be send at the results list
					Sample sampleValue = new Sample();
					sampleValue.setDate(dateWeek.getWeekyear() + Constants.ISO_DATE_FORMAT + 
							StringUtils.leftPad(String.valueOf(dateWeek.getWeekOfWeekyear()), 2, "0") );
					BigDecimal average = exchangeValues.divide(new BigDecimal(weekRealValuesCount.intValue()));
					sampleValue.setValue(average);
					sampleList.add(sampleValue);
				}
				//restar the values of the calculated data
				exchangeValues = new BigDecimal(0);
				weekDayCount = 0;
				weekRealValuesCount = 0;

			} catch (ParseException e) {
				logger.info("---> An erorr have been procuded where mapping weekly datas: " + e.getMessage());
			}

		}

	}
	
}
