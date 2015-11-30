package com.balidea.microservice.services;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.balidea.microservice.commons.Constants;

/**
 * 
 * @author Balidea Consulting & Programming
 */
@Component
public class CurrencyExchangeServicePost {	
	
	private static final Logger logger = LoggerFactory
			.getLogger(CurrencyExchangeServicePost.class);
	
	
	/**
	 * Method that makes the http call to the Yahoo Api
	 * @param fromDate 
	 * 			date from to calculate
	 * @param toDate
	 * 			date to to calculate
	 * @param currencyPair
	 * 			currencyPair for the call 
	 * @param resolution
	 * 			 daily or weekly
	 * @result  byte[]
	 * 			csv file represented in byte array
	 */
	public byte[] callQuandlCurrencyExchange(Date fromDate, Date toDate, String currencyPair, String resolution) throws Exception {
    	
		logger.info("---> Call to Quandl API with fromDate '" + fromDate + "' toDate '"  + toDate + "'"
				 + " currencyPair '"  + currencyPair + "'" + " resolution '"  + resolution + "'");
		
		//https://www.quandl.com/api/v3/datasets/ECB/EURDKK.csv?start_date=2012-11-01&end_date=2015-08-18&collapse=weekly
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		    
		String currencyPairParam = currencyPair + ".csv?";
		String fromDateParam = "start_date=" + dateFormat.format(fromDate);
		String toDateParam = "end_date=" + dateFormat.format(toDate);
		String resolutionParam = "collapse=" + resolution;
		
		String requestParams = URIUtil.encodeQuery(currencyPairParam +  fromDateParam + "&" + toDateParam + "&" + resolutionParam);
        
		String request = Constants.QUANDLD_API_URL +  requestParams;
		
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(request);

        // Send POST request
        int statusCode = client.executeMethod(method);
        
        byte[] data = null;
        
        if (statusCode != HttpStatus.SC_OK) {
        	logger.info("---> Method call Quandl API failed: " + method.getStatusLine());
        	throw new Exception(" Method call Quandl API failed: " + method.getStatusLine());
        } else {
        	 // Get the response body
        	logger.info("---> Method call Quandl API correct ");
        	
        	InputStream rstream = null;
        	rstream = method.getResponseBodyAsStream();
        	// Process the response from Quandl API
        	data = org.apache.commons.io.IOUtils.toByteArray(rstream);
        }
        
        method.releaseConnection();
        
        return data;
	}
	
}