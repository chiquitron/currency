package com.balidea.microservice.commons;

/**
 * 
 * @author Balidea Consulting & Programming
 */
public class Constants {

	//application properties file
	public static final String PROPERTIES_FILE = "application.properties";	
	public static final String CURRENCY_NAME_PROPERTIE = "currency.name.";
	public static final String CURRENCY_PAIR_PROPERTIE = "currency.pair.";
	public static final String CURRENCY_JSON_NAME = "currency.exchange.name";

	
	//quandl url request
	public static final String QUANDLD_API_URL = "http://www.quandl.com/api/v3/datasets/ECB/";
	
	//quandl CSV response file header
    public static final String [] FILE_HEADER_MAPPING = {"Date","Value"};
    public static final String DATE = "Date";
    public static final String VALUE = "Value";
    
    public static final String DAILY = "daily";
    public static final String WEEKLY = "weekly";
    
    //date formate that use the microservice to compare dates
    public static final String DATE_FORMAT = "yyyy-MM-dd";
   
    //years ago it's storage information in cache
    public static final String YEARS_AGO_STORAGE = "cache.yearsAgo";
    //days ago to obtain Yahoo API results for having information to weekends and holidays
    public static final String DAYS_AGO_STORAGE = "cache.daysAgo";
    
    //cache name where finance data is stored
    public static final String  CACHE_CURRENCY_DATA = "cacheCurrencyData";
    //name from date Cached Data Storage
    public static final String  CACHE_DATE_RESULTS = "cacheDateResults";
    
    //ISO8601 week date format YEAR-WWEAKOFYEAR 2015-W01
    public static final String ISO_DATE_FORMAT = "-W";
    
    //indicates if the value is real or is calculate because it was weekend day or holidays
    public static final String REAL_VALUE_YES = "Y";
    public static final String REAL_VALUE_NO = "N";
    
    //count who indicates if the week is completely
    public static final Integer COMPLET_WEEK = 7; 


}
