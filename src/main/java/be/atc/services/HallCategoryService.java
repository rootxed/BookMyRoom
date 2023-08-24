package be.atc.services;

import be.atc.entities.BuildingEntity;
import be.atc.entities.CategoryEntity;
import be.atc.entities.HallCategoryEntity;
import be.atc.entities.HallEntity;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class HallCategoryService extends ServiceImpl<HallCategoryEntity>{

    private Logger log = org.apache.log4j.Logger.getLogger(UserService.class);

    public boolean exist(HallCategoryEntity hc, EntityManager em) {
        log.info("Checking if building already exist.");
        return (findHallCategoryByHallAndCategory(hc,em) != null);
    }

    public HallCategoryEntity findHallCategoryByHallAndCategory(HallCategoryEntity hallCategory,  EntityManager em) {
        log.info("Finding HallCategory with Hall and Category");
        try {
            return em.createNamedQuery("HallCategory.findByHallAndCategory", HallCategoryEntity.class)
                    .setParameter("hall", hallCategory.getHallByHallId())
                    .setParameter("category", hallCategory.getCategoryByCategoryId())
                    .getSingleResult();
        } catch (NoResultException e) {
            log.info("Query found no HallCategory to return");
            return null;
        }
    }

    public List<HallCategoryEntity> findAllCategoriesByHallOrNull(HallEntity hall, EntityManager em) {
        log.info("Finding all categories for hall: " + hall.getName());
        try {
            TypedQuery<HallCategoryEntity> query = em.createNamedQuery("HallCategory.findByHall", HallCategoryEntity.class)
                    .setParameter("hall", hall);
            List<HallCategoryEntity> hallCategoryList = query.getResultList();
            log.info("Found " + hallCategoryList.size() + " categories for hall id "+ hall.getId());
            return hallCategoryList;
        } catch (NoResultException e) {
            log.info("Query found no categories for the hall");
            return null;
        }
    }

    public HallCategoryEntity findOneByIdOrNull(int id, EntityManager em) {
        return null;
    }

    public Collection<HallCategoryEntity> findAllOrNull(EntityManager em) {
        return null;
    }
}
