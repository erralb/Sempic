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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "PICTURE")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Picture.findAll", query = "SELECT p FROM Picture p")
	, @NamedQuery(name = "Picture.findAllByUser", query = "SELECT a FROM Picture a WHERE a.user = :user")
	, @NamedQuery(name = "Picture.findById", query = "SELECT p FROM Picture p WHERE p.id = :id")
	, @NamedQuery(name = "Picture.findByAdded", query = "SELECT p FROM Picture p WHERE p.added = :added")
	, @NamedQuery(name = "Picture.findByName", query = "SELECT p FROM Picture p WHERE p.name = :name")
})
public class Picture implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@Column(name = "ADDED")
    @Temporal(TemporalType.DATE)
	private Date added;
	@Size(max = 255)
    @Column(name = "NAME")
	private String name;
	
    @Column(name = "FILENAME")
	private String filename;
	
	@JoinColumn(name = "ALBUM_ID", referencedColumnName = "ID")
    @ManyToOne
	private Album album;
	
	@ManyToOne
	private SempicUser user;

	
	@JoinTable(name = "ALBUM_PICTURE", joinColumns = {
    	@JoinColumn(name = "PICTURES_ID", referencedColumnName = "ID")}, inverseJoinColumns = {
    	@JoinColumn(name = "ALBUM_ID", referencedColumnName = "ID")})
    @ManyToMany
	private Collection<Album> albumCollection;

	public Picture() {
	}

	public Picture(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public SempicUser getUser() {
		return user;
	}

	public void setUser(SempicUser user) {
		this.user = user;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public Date getAdded() {
		return added;
	}

	public void setAdded(Date added) {
		this.added = added;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@XmlTransient
	public Collection<Album> getAlbumCollection() {
		return albumCollection;
	}

	public void setAlbumCollection(Collection<Album> albumCollection) {
		this.albumCollection = albumCollection;
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
		if (!(object instanceof Picture)) {
			return false;
		}
		Picture other = (Picture) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "fr.uga.miashs.sempic.model.Picture[ id=" + id + " ]";
	}
	
}
