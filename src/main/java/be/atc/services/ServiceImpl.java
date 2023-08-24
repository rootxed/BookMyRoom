package be.atc.services;

import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import java.util.Collection;

public abstract class ServiceImpl<T> implements Service<T> {


    private final static Logger LOG = Logger.getLogger(ServiceImpl.class);

    public abstract boolean exist(T t, EntityManager em);

    public abstract T findOneByIdOrNull(int id, EntityManager em);

    public abstract Collection<T> findAllOrNull(EntityManager em);

    public void insert(T t, EntityManager em) {
        LOG.info("Insert " + t.toString());
        em.persist(t);

    }

    public void insertAndFlush(T t, EntityManager em) {
        LOG.info("Insert and flush " + t.toString());
        em.persist(t);
        em.flush();

    }

    public void update(T t, EntityManager em) {
        LOG.info("Update :" + t.toString());
        em.merge(t);

    }

    public void delete(T t, EntityManager em) {
        LOG.info("Delete :" + t.toString());
        em.remove(t);

    }
}
