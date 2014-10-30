/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.resources;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Fabio Gonçalves
 */
@Entity
@Table(name = "hoba_session")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HobaSession.findAll", query = "SELECT h FROM HobaSession h"),
    @NamedQuery(name = "HobaSession.findByIdSession", query = "SELECT h FROM HobaSession h WHERE h.idSession = :idSession"),
    @NamedQuery(name = "HobaSession.findBySessionIni", query = "SELECT h FROM HobaSession h WHERE h.sessionIni = :sessionIni"),
    @NamedQuery(name = "HobaSession.findByIsvalid", query = "SELECT h FROM HobaSession h WHERE h.isvalid = :isvalid")})
public class HobaSession implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_session")
    private Integer idSession;
    @Column(name = "session_ini")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sessionIni;
    @Column(name = "isvalid")
    private Boolean isvalid;
    @JoinColumn(name = "id_devices", referencedColumnName = "id_devices")
    @ManyToOne
    private HobaDevices idDevices;

    public HobaSession() {
    }

    public HobaSession(Integer idSession) {
        this.idSession = idSession;
    }

    public Integer getIdSession() {
        return idSession;
    }

    public void setIdSession(Integer idSession) {
        this.idSession = idSession;
    }

    public Date getSessionIni() {
        return sessionIni;
    }

    public void setSessionIni(Date sessionIni) {
        this.sessionIni = sessionIni;
    }

    public Boolean getIsvalid() {
        return isvalid;
    }

    public void setIsvalid(Boolean isvalid) {
        this.isvalid = isvalid;
    }

    public HobaDevices getIdDevices() {
        return idDevices;
    }

    public void setIdDevices(HobaDevices idDevices) {
        this.idDevices = idDevices;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idSession != null ? idSession.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HobaSession)) {
            return false;
        }
        HobaSession other = (HobaSession) object;
        if ((this.idSession == null && other.idSession != null) || (this.idSession != null && !this.idSession.equals(other.idSession))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hobba.hobaserver.resources.HobaSession[ idSession=" + idSession + " ]";
    }
    
}
