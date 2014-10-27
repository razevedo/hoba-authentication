/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.resources;

import java.util.Date;

/**
 *
 * @author Fabio Gonçalves
 */
public class HobaConnections {
    private String deviceType;
    private String ipAddress;
    private Date date;
    private String kid;
    
    public HobaConnections (){
        
    }
    
    public HobaConnections (String deviceType, String ipAddress, Date date, String kid){
        this.deviceType = deviceType;
        this.ipAddress = ipAddress;
        this.date = date;
        this.kid = kid;
    }

    /**
     * @return the deviceType
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * @param deviceType the deviceType to set
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * @return the ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the kid
     */
    public String getKid() {
        return kid;
    }

    /**
     * @param kid the kid to set
     */
    public void setKid(String kid) {
        this.kid = kid;
    }
    
    
}
