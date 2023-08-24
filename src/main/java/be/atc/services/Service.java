package be.atc.services;

import javax.persistence.EntityManager;
import java.util.Collection;

public interface Service<T> {

    boolean exist(T t, EntityManager em);
    T findOneByIdOrNull(int id, EntityManager em);
    Collection<T> findAllOrNull(EntityManager em);

    void insert(T t, EntityManager em);
    void insertAndFlush(T t, EntityManager em);
    void update(T t, EntityManager em);
    void delete(T t, EntityManager em);

}
