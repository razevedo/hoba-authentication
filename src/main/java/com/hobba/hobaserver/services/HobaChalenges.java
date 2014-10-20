/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.services;

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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Fabio Gonçalves
 */
@Entity
@Table(name = "hoba_chalenges")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HobaChalenges.findAll", query = "SELECT h FROM HobaChalenges h"),
    @NamedQuery(name = "HobaChalenges.findByIdChalenge", query = "SELECT h FROM HobaChalenges h WHERE h.idChalenge = :idChalenge"),
    @NamedQuery(name = "HobaChalenges.findByChalenge", query = "SELECT h FROM HobaChalenges h WHERE h.chalenge = :chalenge"),
    @NamedQuery(name = "HobaChalenges.findByExpiration", query = "SELECT h FROM HobaChalenges h WHERE h.expiration = :expiration")})
public class HobaChalenges implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_chalenge")
    private Integer idChalenge;
    @Size(max = 200)
    @Column(name = "chalenge")
    private String chalenge;
    @Column(name = "expiration")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiration;
    @JoinColumn(name = "id_keys", referencedColumnName = "id_keys")
    @ManyToOne
    private HobaKeys idKeys;

    public HobaChalenges() {
    }

    public HobaChalenges(Integer idChalenge) {
        this.idChalenge = idChalenge;
    }

    public Integer getIdChalenge() {
        return idChalenge;
    }

    public void setIdChalenge(Integer idChalenge) {
        this.idChalenge = idChalenge;
    }

    public String getChalenge() {
        return chalenge;
    }

    public void setChalenge(String chalenge) {
        this.chalenge = chalenge;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public HobaKeys getIdKeys() {
        return idKeys;
    }

    public void setIdKeys(HobaKeys idKeys) {
        this.idKeys = idKeys;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idChalenge != null ? idChalenge.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HobaChalenges)) {
            return false;
        }
        HobaChalenges other = (HobaChalenges) object;
        if ((this.idChalenge == null && other.idChalenge != null) || (this.idChalenge != null && !this.idChalenge.equals(other.idChalenge))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hobba.hobaserver.services.HobaChalenges[ idChalenge=" + idChalenge + " ]";
    }
    
}
