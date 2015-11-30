package com.balidea.microservice.commons;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.balidea.microservice.CurrencyExchangeApplication;


/**
 * 
 * @author Balidea Consulting & Programming
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { CurrencyExchangeApplication.class })
public class ApplicationPropertiesUnitTests {

	@Autowired
	private ApplicationProperties applicationProperties;
	
    /**
     * Call method who initialize application properties
     */
	@Test
	public void initalizeApplicationProperties() throws Exception {
		this.applicationProperties.initializeProperties();
	}
	
    /**
     * Call method who obtain a String Property with a property name that not exist
     */
	@Test
	public void faildToObtainStringProperty() {
		String stringProperty = this.applicationProperties.getStringValue("property.proof");
		Assert.assertNull(stringProperty);
	}
	
    /**
     * Call method who obtain a String Property with a property name that exist
     */
	@Test
	public void obtainStringProperty() {
		String stringProperty = this.applicationProperties.getStringValue("currency.name.eur");
		Assert.assertNotNull(stringProperty);
		Assert.assertEquals(new String("EUR"), stringProperty);
	}
	
    /**
     * Call method who obtain a int Property with a property name that exist
     */
	@Test
	public void obtainIntProperty() {
		int value = 3;
		int intProperty = this.applicationProperties.getIntValue("cache.yearsAgo");
		Assert.assertNotNull(intProperty);
		Assert.assertEquals(value, intProperty);
	}

}
