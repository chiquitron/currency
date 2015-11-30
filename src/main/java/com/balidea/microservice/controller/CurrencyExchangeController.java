package com.balidea.microservice.controller;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.balidea.microservice.commons.ApplicationProperties;

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
	private ApplicationProperties propertiesConfig;


	@RequestMapping("/currencyExchange")
	@ResponseBody
	public Map<String, String> helloWorld() {
		return Collections.singletonMap("message",
				this.helloWorldService.getHelloMessage());
	}

	

}
