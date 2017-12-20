/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.model.datalayer;

import java.util.List;
import javax.persistence.EntityManager;

/**
 * Base class for Data Access Objects.
 * A subclass of this has to be created for each class for which you want the 
 * instances to be persisted with JPA. Within each subclass you can create
 * specific methods for each query you want to execute on the type.
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
public abstract class AbstractJpaDao<T,K>  {

    private Class<T> entityClass;

    public AbstractJpaDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    /**
     * Creates the entity into the database
     * @param entity 
     */
    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    /**
     * Update the entity in the database. It is also used to reattach the entity
     * into the peristence cache.
     * @param entity 
     */
    public void update(T entity) {
        getEntityManager().merge(entity);
    }

    /**
     * Delete the entity from the database
     * @param entity 
     */
    public void delete(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    /**
     * Retreive the entity given its identifier (the key)
     * @param id
     * @return 
     */
    public T read(K id) {
        return getEntityManager().find(entityClass, id);
    }

    /**
     * Retrieves all the entities of the type
     * @return 
     */
    public List<T> readAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    /**
     * Gives the number og entity of the type that are in the database
     * @return 
     */
    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }    
}
