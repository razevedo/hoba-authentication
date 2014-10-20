/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.services;

import java.io.Serializable;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Fabio Gonçalves
 */
@Entity
@Table(name = "hoba_token")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HobaToken.findAll", query = "SELECT h FROM HobaToken h"),
    @NamedQuery(name = "HobaToken.findByIdToken", query = "SELECT h FROM HobaToken h WHERE h.idToken = :idToken"),
    @NamedQuery(name = "HobaToken.findByToken", query = "SELECT h FROM HobaToken h WHERE h.token = :token"),
    @NamedQuery(name = "HobaToken.findByIdUser", query = "SELECT h FROM HobaToken h WHERE h.idUser = :idUser")})
public class HobaToken implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_token")
    private Integer idToken;
    @Size(max = 200)
    @Column(name = "token")
    private String token;
    @Column(name = "id_user")
    private Integer idUser;
    @JoinColumn(name = "expiration", referencedColumnName = "id_user")
    @ManyToOne
    private HobaUser expiration;

    public HobaToken() {
    }

    public HobaToken(Integer idToken) {
        this.idToken = idToken;
    }

    public Integer getIdToken() {
        return idToken;
    }

    public void setIdToken(Integer idToken) {
        this.idToken = idToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public HobaUser getExpiration() {
        return expiration;
    }

    public void setExpiration(HobaUser expiration) {
        this.expiration = expiration;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idToken != null ? idToken.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HobaToken)) {
            return false;
        }
        HobaToken other = (HobaToken) object;
        if ((this.idToken == null && other.idToken != null) || (this.idToken != null && !this.idToken.equals(other.idToken))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hobba.hobaserver.services.HobaToken[ idToken=" + idToken + " ]";
    }
    
}
