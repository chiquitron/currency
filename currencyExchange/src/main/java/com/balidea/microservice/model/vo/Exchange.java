package com.balidea.microservice.model.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @author Balidea Consulting & Programming
 * This class represents the object structure that is cached at the cacheCurrencyData
 * indicates the date exchange, the exchange value and if the exchange value is real
 * if the date day represents a weekend day or a holiday it will not be a real value
 */
@SuppressWarnings("serial")
public class Exchange implements Serializable {

	private String dateExcahnge;
	private BigDecimal exchangeValue;
	private String realExchangeValue;
	
	public Exchange(String date, BigDecimal exchange) {
		this.setExchangeValue(exchange);
		this.setDateExcahnge(date);
	}		
		
	public Exchange(String date, BigDecimal exchange, String real) {
		this.setExchangeValue(exchange);
		this.setDateExcahnge(date);
		this.setRealExchangeValue(real);
	}	
	
	public Exchange() {
		super();
	}

	/**
	 * @return the dateExcahnge
	 */
	public String getDateExcahnge() {
		return dateExcahnge;
	}

	/**
	 * @param dateExcahnge the dateExcahnge to set
	 */
	public void setDateExcahnge(String dateExcahnge) {
		this.dateExcahnge = dateExcahnge;
	}

	/**
	 * @return the exchangeValue
	 */
	public BigDecimal getExchangeValue() {
		return exchangeValue;
	}

	/**
	 * @param exchangeValue the exchangeValue to set
	 */
	public void setExchangeValue(BigDecimal exchangeValue) {
		this.exchangeValue = exchangeValue;
	}

	/**
	 * @return the realExchangeValue
	 */
	public String getRealExchangeValue() {
		return realExchangeValue;
	}

	/**
	 * @param realExchangeValue the realExchangeValue to set
	 */
	public void setRealExchangeValue(String realExchangeValue) {
		this.realExchangeValue = realExchangeValue;
	}

}
