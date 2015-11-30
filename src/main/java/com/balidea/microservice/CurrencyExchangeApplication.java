package com.balidea.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;


/**
 * 
 * @author Balidea Consulting & Programming
 */
@SpringBootApplication
public class CurrencyExchangeApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CurrencyExchangeApplication.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CurrencyExchangeApplication.class, args);
	}

}