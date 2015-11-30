package com.balidea.microservice.model.vo;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    "resolution",
    "baseCurrency",
    "quoteCurrency"
})
public class Parameters {

    @JsonProperty("resolution")
    private String resolution;
    @JsonProperty("baseCurrency")
    private String baseCurrency;
    @JsonProperty("quoteCurrency")
    private String quoteCurrency;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The resolution
     */
    @JsonProperty("resolution")
    public String getResolution() {
        return resolution;
    }

    /**
     * 
     * @param resolution
     *     The resolution
     */
    @JsonProperty("resolution")
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Parameters withResolution(String resolution) {
        this.resolution = resolution;
        return this;
    }

    /**
     * 
     * @return
     *     The baseCurrency
     */
    @JsonProperty("baseCurrency")
    public String getBaseCurrency() {
        return baseCurrency;
    }

    /**
     * 
     * @param baseCurrency
     *     The baseCurrency
     */
    @JsonProperty("baseCurrency")
    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Parameters withBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
        return this;
    }
    
    /**
     * 
     * @return
     *     The quoteCurrency
     */
    @JsonProperty("quoteCurrency")
    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    /**
     * 
     * @param quoteCurrency
     *     The quoteCurrency
     */
    @JsonProperty("quoteCurrency")
    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public Parameters withQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
        return this;
    }


    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Parameters withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
