package com.balidea.microservice.model.vo;

import java.io.Serializable;

/**
 * 
 * @author Balidea Consulting & Programming
 * This class represents the object structure that is cached at the cacheDateResults
 * indicates the date from each currencyPair results that have been stored
 */
@SuppressWarnings("serial")
public class DataExchangeResults implements Serializable {

	private String dateResults;
	private String currencyPairResults;

	
	public DataExchangeResults(String date, String currencyPairResults) {
		this.setCurrencyPairResults(currencyPairResults);
		this.setDateResults(date);
	}		
		
	public DataExchangeResults() {
		super();
	}

	/**
	 * @return the dateResults
	 */
	public String getDateResults() {
		return dateResults;
	}
	/**
	 * @param dateResults the dateResults to set
	 */
	public void setDateResults(String dateResults) {
		this.dateResults = dateResults;
	}

	/**
	 * @return the currencyPairResults
	 */
	public String getCurrencyPairResults() {
		return currencyPairResults;
	}

	/**
	 * @param currencyPairResults the currencyPairResults to set
	 */
	public void setCurrencyPairResults(String currencyPairResults) {
		this.currencyPairResults = currencyPairResults;
	}


}
