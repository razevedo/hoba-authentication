/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.resources;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fabio Gonçalves
 */
public class HobaEndpoints {
    private String endpoint;
    private String url;
    private String requestMethod;
    private List<String> parameters;
    private String parametersType;
    private String header;
    
    public HobaEndpoints(){
        this.parameters = new ArrayList<String>();
    }
    
    public HobaEndpoints(String endpoint, String url, String requestMethod, String parametersType, String header){
        this.endpoint = endpoint;
        this.url = url;
        this.requestMethod = requestMethod;
        this.parameters = new ArrayList<String>();
        this.parametersType = parametersType;
        this.header = header;
    }
    
    

    /**
     * @return the endpoint
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the requestMethod
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * @param requestMethod the requestMethod to set
     */
    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    /**
     * @return the parameters
     */
    public List<String> getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
    
    public void addParameter(String parameter){
        this.parameters.add(parameter);
    }

    /**
     * @return the parametersType
     */
    public String getParametersType() {
        return parametersType;
    }

    /**
     * @param parametersType the parametersType to set
     */
    public void setParametersType(String parametersType) {
        this.parametersType = parametersType;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }
    
}
