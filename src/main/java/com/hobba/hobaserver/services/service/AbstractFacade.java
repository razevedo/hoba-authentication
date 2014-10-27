/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.services.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

/**
 *
 * @author Fabio Gonçalves
 */
public abstract class AbstractFacade<T> {
    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public T create(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        T t = em.merge(entity);
        em.flush();
        et.commit();
        
        return t;
    }
    
    

    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.remove(em.merge(entity));
        em.flush();
        et.commit();
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }
    
    public T findHKIDbyKID(String kid){
        EntityManager em = getEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root root = cq.from(entityClass);
       cq.where(builder.equal(root.get("kid"), builder.parameter(String.class, "kid")));

        Query query = getEntityManager().createQuery(cq);
        query.setParameter("kid", kid);
        
        if(query.getResultList().size() > 0){
            return (T)query.getResultList().get(0);
        }
        return null;
    }
    
    public T findTokenbyToken(String token){
        EntityManager em = getEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root root = cq.from(entityClass);
       cq.where(builder.equal(root.get("token"), builder.parameter(String.class, "token")));

        Query query = getEntityManager().createQuery(cq);
        query.setParameter("token", token);
        
        if(query.getResultList().size() > 0){
            return (T)query.getResultList().get(0);
        }
        return null;
    }

    public List<T> findAll() {
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

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
}
