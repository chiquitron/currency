package com.balidea.microservice.commons;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Balidea Consulting & Programming
 */
@Component
public class ApplicationProperties {

	private static final Logger logger = LoggerFactory
			.getLogger(ApplicationProperties.class);
	
	public Map<String, String> propertiesMap = new HashMap<String, String>();
	

	/**
	 * Method that map all the application properties  
	 */
	@PostConstruct
	public void initializeProperties(){

		Properties props = null;
		try {
			props = PropertiesLoaderUtils.loadAllProperties(Constants.PROPERTIES_FILE);
			for (Object key : props.keySet()){
				propertiesMap.put((String) key, (String)props.get(key));
			}
		} catch (IOException e) {
			logger.info("---> An error have been produced trying to initialize properties: " + e.getMessage());
		}
	}
	
	/**
	 * Method that obtain the value from a property key
	 * @param key 
	 * 			property key 
	 * @return String property value
	 */
	public String getStringValue(String key){
		return propertiesMap.get(key);
	}
	
	/**
	 * Method that obtain the value from a property key
	 * @param key 
	 * 			property key 
	 * @return int property value
	 */
	public int getIntValue(String key){
		return Integer.valueOf(propertiesMap.get(key));
	}

	/**
	 * Method that obtain the value from a property key
	 * with the prefix currency.name.
	 * @param key 
	 * 			property key 
	 * @return String property value
	 */
	public String getCurrencyName(String key){
		return propertiesMap.get(Constants.CURRENCY_NAME_PROPERTIE + key);
	}
	
	/**
	 * Method that obtain the value from a property key
	 * with the prefix currency.pair.
	 * @param key 
	 * 			property key 
	 * @return String property value
	 */
	public String getCurrencyPair(String key){
		return propertiesMap.get(Constants.CURRENCY_PAIR_PROPERTIE + key);
	}
}
