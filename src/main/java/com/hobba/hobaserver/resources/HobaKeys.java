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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "hoba_keys")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HobaKeys.findAll", query = "SELECT h FROM HobaKeys h"),
    @NamedQuery(name = "HobaKeys.findByIdKeys", query = "SELECT h FROM HobaKeys h WHERE h.idKeys = :idKeys"),
    @NamedQuery(name = "HobaKeys.findByKid", query = "SELECT h FROM HobaKeys h WHERE h.kid = :kid"),
    @NamedQuery(name = "HobaKeys.findByKidtype", query = "SELECT h FROM HobaKeys h WHERE h.kidtype = :kidtype"),
    @NamedQuery(name = "HobaKeys.findByPub", query = "SELECT h FROM HobaKeys h WHERE h.pub = :pub")})
public class HobaKeys implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_keys")
    private Integer idKeys;
    @Size(max = 255)
    @Column(name = "kid")
    private String kid;
    @Size(max = 255)
    @Column(name = "kidtype")
    private String kidtype;
    @Size(max = 500)
    @Column(name = "pub")
    private String pub;
    @JoinColumn(name = "id_devices", referencedColumnName = "id_devices")
    @ManyToOne
    private HobaDevices idDevices;
    @OneToMany(mappedBy = "idKeys")
    @CascadeOnDelete
    private Collection<HobaChallenges> hobaChallengesCollection;

    public HobaKeys() {
    }

    public HobaKeys(Integer idKeys) {
        this.idKeys = idKeys;
    }

    public Integer getIdKeys() {
        return idKeys;
    }

    public void setIdKeys(Integer idKeys) {
        this.idKeys = idKeys;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getKidtype() {
        return kidtype;
    }

    public void setKidtype(String kidtype) {
        this.kidtype = kidtype;
    }

    public String getPub() {
        return pub;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }

    public HobaDevices getIdDevices() {
        return idDevices;
    }

    public void setIdDevices(HobaDevices idDevices) {
        this.idDevices = idDevices;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<HobaChallenges> getHobaChallengesCollection() {
        return hobaChallengesCollection;
    }

    public void setHobaChallengesCollection(Collection<HobaChallenges> hobaChallengesCollection) {
        this.hobaChallengesCollection = hobaChallengesCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idKeys != null ? idKeys.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HobaKeys)) {
            return false;
        }
        HobaKeys other = (HobaKeys) object;
        if ((this.idKeys == null && other.idKeys != null) || (this.idKeys != null && !this.idKeys.equals(other.idKeys))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hobba.hobaserver.resources.HobaKeys[ idKeys=" + idKeys + " ]";
    }
    
}
