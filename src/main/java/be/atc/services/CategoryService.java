package be.atc.services;

import be.atc.entities.BuildingEntity;
import be.atc.entities.CategoryEntity;
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
        CategoryEntity foundCategory = findCategoryByNameOrNull(c.getName(),em);
        if(foundCategory == null){
            return false;
        }else{
            if  (foundCategory.getId() == c.getId()){
                return false;
            }
            else{
                return true;
            }
        }
    }

    public boolean nameExist(String name, EntityManager em) {
        log.info("Checking if category name already exist.");
        return (findCategoryByNameOrNull(name, em) != null);
    }


    private CategoryEntity findCategoryByNameOrNull(String name, EntityManager em) {
        log.info("Finding Category with name: " + name);


        try{
            return em.createNamedQuery("Category.findOneByName", CategoryEntity.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.info("Query found no category to return");
            return null;
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

    public boolean isUsed(CategoryEntity category, EntityManager em) {
        log.info("Searching if category is used");

        TypedQuery<Long> query = em.createNamedQuery("HallCategory.countByCategory", Long.class)
                .setParameter("category", category);

        long count = query.getSingleResult();

        return count > 0;
    }
}
