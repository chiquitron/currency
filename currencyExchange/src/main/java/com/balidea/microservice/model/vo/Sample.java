package com.balidea.microservice.model.vo;

import java.math.BigDecimal;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
 * @author Balidea Consulting & Programming
 * This class represents a component from the JSON result format
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "date",
    "value"
})
public class Sample {

    @JsonProperty("date")
    private String date;
    @JsonProperty("value")
    private BigDecimal value;

    /**
     * 
     * @return
     *     The date
     */
    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    /**
     * 
     * @param date
     *     The date
     */
    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    public Sample withDate(String date) {
        this.date = date;
        return this;
    }

    /**
     * 
     * @return
     *     The value
     */
    @JsonProperty("value")
    public BigDecimal getValue() {
        return value;
    }

    /**
     * 
     * @param value
     *     The value
     */
    @JsonProperty("value")
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Sample withValue(BigDecimal value) {
        this.value = value;
        return this;
    }

}
