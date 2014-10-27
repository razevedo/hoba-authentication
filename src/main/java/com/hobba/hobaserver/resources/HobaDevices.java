/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.resources;

import java.io.Serializable;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author Fabio Gonçalves
 */
@Entity
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Table(name = "hoba_devices")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HobaDevices.findAll", query = "SELECT h FROM HobaDevices h"),
    @NamedQuery(name = "HobaDevices.findByIdDevices", query = "SELECT h FROM HobaDevices h WHERE h.idDevices = :idDevices"),
    @NamedQuery(name = "HobaDevices.findByDid", query = "SELECT h FROM HobaDevices h WHERE h.did = :did"),
    @NamedQuery(name = "HobaDevices.findByIpAddress", query = "SELECT h FROM HobaDevices h WHERE h.ipAddress = :ipAddress"),
    @NamedQuery(name = "HobaDevices.findByLastDate", query = "SELECT h FROM HobaDevices h WHERE h.lastDate = :lastDate"),
    @NamedQuery(name = "HobaDevices.findByDidtype", query = "SELECT h FROM HobaDevices h WHERE h.didtype = :didtype")})
public class HobaDevices implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_devices")
    private Integer idDevices;
    @Size(max = 20)
    @Column(name = "did")
    private String did;
    @Size(max = 25)
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "last_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastDate;
    @Size(max = 200)
    @Column(name = "didtype")
    private String didtype;
    @JoinColumn(name = "iduser", referencedColumnName = "id_user")
    @ManyToOne
    private HobaUser iduser;
    @OneToMany(mappedBy = "idDevices")
    private Collection<HobaKeys> hobaKeysCollection;

    public HobaDevices() {
    }

    public HobaDevices(Integer idDevices) {
        this.idDevices = idDevices;
    }

    public Integer getIdDevices() {
        return idDevices;
    }

    public void setIdDevices(Integer idDevices) {
        this.idDevices = idDevices;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    public String getDidtype() {
        return didtype;
    }

    public void setDidtype(String didtype) {
        this.didtype = didtype;
    }

    public HobaUser getIduser() {
        return iduser;
    }

    public void setIduser(HobaUser iduser) {
        this.iduser = iduser;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<HobaKeys> getHobaKeysCollection() {
        return hobaKeysCollection;
    }

    public void setHobaKeysCollection(Collection<HobaKeys> hobaKeysCollection) {
        this.hobaKeysCollection = hobaKeysCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDevices != null ? idDevices.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HobaDevices)) {
            return false;
        }
        HobaDevices other = (HobaDevices) object;
        if ((this.idDevices == null && other.idDevices != null) || (this.idDevices != null && !this.idDevices.equals(other.idDevices))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hobba.hobaserver.services.HobaDevices[ idDevices=" + idDevices + " ]";
    }
    
}
