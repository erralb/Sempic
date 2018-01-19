/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.model.datalayer;

import fr.uga.miashs.sempic.model.SempicUser;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Pierre Blarre <Pierre.Blarre@etu.univ-grenoble-alpes.fr>
 */
@Stateless
public class SempicUserFacade extends AbstractFacade<SempicUser> {

    @PersistenceContext(unitName = "NEWSEMPICPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SempicUserFacade() {
        super(SempicUser.class);
    }

    public SempicUser getByEmail(String email) {
        try {
            return (SempicUser) getEntityManager().createQuery("SELECT u FROM SempicUser u "
                    + "WHERE u.email=:email")
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean createAndCheck(SempicUser entity) {
        create(entity);
        return getByEmail(entity.getEmail()) != null;
    }
}
