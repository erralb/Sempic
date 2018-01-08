/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.model;

import static fr.uga.miashs.sempic.model.Picture_.filename;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.Part;

/**
 *
 * @author Pierre Blarre <Pierre.Blarre@etu.univ-grenoble-alpes.fr>
 */
@Stateless
public class PictureFacade extends AbstractFacade<Picture> {

	@PersistenceContext(unitName = "SempicPU")
	private EntityManager em;
	
	private Part file;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public PictureFacade() {
		super(Picture.class);
	}
	
	public void create(Picture entity) {
		getEntityManager().persist(entity);
		
		try (InputStream input = file.getInputStream()) {
//			Files.copy(input, new File(uploads, filename).toPath());
		}
		catch (IOException e) {
			// Show faces message?
		}
	}
	
}
