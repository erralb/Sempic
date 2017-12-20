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
import javax.persistence.Query;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
@Stateless
public class SempicUserDao extends AbstractJpaDao<SempicUser,Long> {

    @PersistenceContext(unitName = "SempicPU")
    private EntityManager em;

    public SempicUserDao() {
        super(SempicUser.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SempicUser getByEmail(String email) {
        try {
        return (SempicUser) 
                getEntityManager().createQuery("SELECT u FROM SempicUser u "
                                                + "WHERE u.email=:email")
                .setParameter("email", email)
                .getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }
    
}
