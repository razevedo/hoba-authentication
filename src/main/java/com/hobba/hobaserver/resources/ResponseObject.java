/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.resources;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author Fabio Gonçalves
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ResponseObject implements Serializable{
    private List<HobaDevices> list;

    /**
     * @return the list
     */
    public List<HobaDevices> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<HobaDevices> list) {
        this.list = list;
    }
    
    
}
