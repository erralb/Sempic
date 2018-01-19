/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.model.datalayer;

import fr.uga.miashs.sempic.model.Album;
import fr.uga.miashs.sempic.model.Picture;
import fr.uga.sempic.jsf.beans.AuthManager;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.Part;

/**
 *
 * @author Pierre Blarre <Pierre.Blarre@etu.univ-grenoble-alpes.fr>
 */
@Stateless
public class PictureFacade extends AbstractFacade<Picture> {

    @PersistenceContext(unitName = "NEWSEMPICPU")
    private EntityManager em;

    private Part file;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Inject
    private AuthManager auth;

    public PictureFacade() {
        super(Picture.class);
    }

    public void create(Picture entity) {
        getEntityManager().persist(entity);

    }

    public List<Album> findAllByUser() {
        Query q = em.createNamedQuery("Picture.findAllByUser");
        q.setParameter("user", auth.currentUser());
        return q.getResultList();
    }

    public List<Picture> findAllById(String ids) {
        Query q = em.createQuery("SELECT p FROM Picture p WHERE p.id IN ( " + ids + " )");
        return q.getResultList();
    }

}
