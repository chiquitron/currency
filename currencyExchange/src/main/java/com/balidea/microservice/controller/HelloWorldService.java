package com.balidea.microservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.balidea.microservice.commons.ApplicationProperties;

@Component
public class HelloWorldService {

	@Autowired
	private ApplicationProperties configuration;

	private static final Logger logger = LoggerFactory
			.getLogger(HelloWorldService.class);

	
	public String getHelloMessage() {
		logger.info("---> Hello Word Currency Exchange ");
		return " Welcome to Currency Exchange Microservice Voo"; 
	}

}
