/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.resources;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.eclipse.persistence.annotations.CascadeOnDelete;

/**
 *
 * @author Fabio Gonçalves
 */
@Entity
@Table(name = "hoba_user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HobaUser.findAll", query = "SELECT h FROM HobaUser h"),
    @NamedQuery(name = "HobaUser.findByIdUser", query = "SELECT h FROM HobaUser h WHERE h.idUser = :idUser"),
    @NamedQuery(name = "HobaUser.findByField1", query = "SELECT h FROM HobaUser h WHERE h.field1 = :field1"),
    @NamedQuery(name = "HobaUser.findByField2", query = "SELECT h FROM HobaUser h WHERE h.field2 = :field2"),
    @NamedQuery(name = "HobaUser.findByField3", query = "SELECT h FROM HobaUser h WHERE h.field3 = :field3")})
public class HobaUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_user")
    private Integer idUser;
    @Size(max = 255)
    @Column(name = "field1")
    private String field1;
    @Size(max = 255)
    @Column(name = "field2")
    private String field2;
    @Size(max = 255)
    @Column(name = "field3")
    private String field3;
    @OneToMany(mappedBy = "iduser")
    @CascadeOnDelete
    private Collection<HobaDevices> hobaDevicesCollection;
    @OneToMany(mappedBy = "idUser")
    @CascadeOnDelete
    private Collection<HobaToken> hobaTokenCollection;

    public HobaUser() {
    }

    public HobaUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<HobaDevices> getHobaDevicesCollection() {
        return hobaDevicesCollection;
    }

    public void setHobaDevicesCollection(Collection<HobaDevices> hobaDevicesCollection) {
        this.hobaDevicesCollection = hobaDevicesCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<HobaToken> getHobaTokenCollection() {
        return hobaTokenCollection;
    }

    public void setHobaTokenCollection(Collection<HobaToken> hobaTokenCollection) {
        this.hobaTokenCollection = hobaTokenCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUser != null ? idUser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HobaUser)) {
            return false;
        }
        HobaUser other = (HobaUser) object;
        if ((this.idUser == null && other.idUser != null) || (this.idUser != null && !this.idUser.equals(other.idUser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hobba.hobaserver.resources.HobaUser[ idUser=" + idUser + " ]";
    }
    
}
