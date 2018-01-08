/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.model;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Pierre Blarre <Pierre.Blarre@etu.univ-grenoble-alpes.fr>
 */
@Stateless
public class AlbumFacade extends AbstractFacade<Album> {

	@PersistenceContext(unitName = "SempicPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AlbumFacade() {
		super(Album.class);
	}
	
}
