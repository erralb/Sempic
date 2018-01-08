/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Pierre Blarre <Pierre.Blarre@etu.univ-grenoble-alpes.fr>
 */
@Entity
@Table(name = "ALBUM")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Album.findAll", query = "SELECT a FROM Album a")
	, @NamedQuery(name = "Album.findById", query = "SELECT a FROM Album a WHERE a.id = :id")
	, @NamedQuery(name = "Album.findByCreated", query = "SELECT a FROM Album a WHERE a.created = :created")
	, @NamedQuery(name = "Album.findByName", query = "SELECT a FROM Album a WHERE a.name = :name")})
public class Album implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
	private Long id;
	@Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
	private Date created;
	@Size(max = 255)
    @Column(name = "NAME")
	private String name;
	@ManyToMany(mappedBy = "albumCollection")
	private Collection<Picture> pictureCollection;

	public Album() {
	}

	public Album(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public Collection<Picture> getPictureCollection() {
		return pictureCollection;
	}

	public void setPictureCollection(Collection<Picture> pictureCollection) {
		this.pictureCollection = pictureCollection;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Album)) {
			return false;
		}
		Album other = (Album) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "fr.uga.miashs.sempic.model.Album[ id=" + id + " ]";
	}
	
}
