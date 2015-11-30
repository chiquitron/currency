package com.balidea.microservice.model.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 * This class represents the JSON result format
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "generateUts",
    "fromDate",
    "toDate",
    "series"
})
public class CurrencyExchangeResponse {

    @JsonProperty("generateUts")
    private Long generateUts;
    @JsonProperty("fromDate")
    private String fromDate;
    @JsonProperty("toDate")
    private String toDate;
    @JsonProperty("series")
    private List<Series> series = new ArrayList<Series>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The generateUts
     */
    @JsonProperty("generateUts")
    public Long getGenerateUts() {
        return generateUts;
    }

    /**
     * 
     * @param generateUts
     *     The generateUts
     */
    @JsonProperty("generateUts")
    public void setGenerateUts(Long generateUts) {
        this.generateUts = generateUts;
    }

    public CurrencyExchangeResponse withGenerateUts(Long generateUts) {
        this.generateUts = generateUts;
        return this;
    }

    /**
     * 
     * @return
     *     The fromDate
     */
    @JsonProperty("fromDate")
    public String getFromDate() {
        return fromDate;
    }

    /**
     * 
     * @param fromDate
     *     The fromDate
     */
    @JsonProperty("fromDate")
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public CurrencyExchangeResponse withFromDate(String fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    /**
     * 
     * @return
     *     The toDate
     */
    @JsonProperty("toDate")
    public String getToDate() {
        return toDate;
    }

    /**
     * 
     * @param toDate
     *     The toDate
     */
    @JsonProperty("toDate")
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public CurrencyExchangeResponse withToDate(String toDate) {
        this.toDate = toDate;
        return this;
    }

    /**
     * 
     * @return
     *     The series
     */
    @JsonProperty("series")
    public List<Series> getSeries() {
        return series;
    }

    /**
     * 
     * @param series
     *     The series
     */
    @JsonProperty("series")
    public void setSeries(List<Series> series) {
        this.series = series;
    }

    public CurrencyExchangeResponse withSeries(List<Series> series) {
        this.series = series;
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

    public CurrencyExchangeResponse withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
