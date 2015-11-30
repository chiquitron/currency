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
 * This class represents a component from the JSON result format
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "name",
    "parameters",
    "samples"
})
public class Series {

    @JsonProperty("parameters")
    private Parameters parameters;
    @JsonProperty("name")
    private String name;
    @JsonProperty("samples")
    private List<Sample> samples = new ArrayList<Sample>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The parameters
     */
    @JsonProperty("parameters")
    public Parameters getParameters() {
        return parameters;
    }

    /**
     * 
     * @param parameters
     *     The parameters
     */
    @JsonProperty("parameters")
    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public Series withParameters(Parameters parameters) {
        this.parameters = parameters;
        return this;
    }

    /**
     * 
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Series withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 
     * @return
     *     The samples
     */
    @JsonProperty("samples")
    public List<Sample> getSamples() {
        return samples;
    }

    /**
     * 
     * @param samples
     *     The samples
     */
    @JsonProperty("samples")
    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    public Series withSamples(List<Sample> samples) {
        this.samples = samples;
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

    public Series withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
