package be.atc.services;

import be.atc.entities.BuildingEntity;
import be.atc.entities.CategoryEntity;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class CategoryService extends ServiceImpl<CategoryEntity> {

    private Logger log = org.apache.log4j.Logger.getLogger(CategoryService.class);

    public boolean exist(CategoryEntity c, EntityManager em) {
        log.info("Checking if category already exist.");
        return (findCategoryByNameOrNull(c.getName()) != null);
    }

    private CategoryEntity findCategoryByNameOrNull(String name) {
        log.info("Finding Category with name: " + name);

        EntityManager em = EMF.getEM();
        try{
            return em.createNamedQuery("Category.findOneByName", CategoryEntity.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.info("Query found no category to return");
            return null;
        } finally {
            em.clear();
            em.close();
        }
    }

    public CategoryEntity findOneByIdOrNull(int id, EntityManager em) {
        try{
            return em.createNamedQuery("Category.findOneById", CategoryEntity.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.info("Query found no category to return");
            return null;
        }
    }

    public List<CategoryEntity> findAllOrNull(EntityManager em) {
        try {
            log.info("Finding all categories...");
            TypedQuery<CategoryEntity> query = em.createNamedQuery("Category.findAll", CategoryEntity.class);
            List<CategoryEntity> categoryList = query.getResultList();
            log.info("Selected all categories.");
            return categoryList;
        } catch (Exception e) {
            log.info("Query found no categories to return");
            return null;
        }
    }
}
