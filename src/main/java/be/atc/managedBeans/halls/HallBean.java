package be.atc.managedBeans.halls;

import be.atc.entities.CategoryEntity;
import be.atc.entities.HallCategoryEntity;
import be.atc.entities.HallEntity;
import be.atc.managedBeans.categories.CategoryBean;
import be.atc.services.HallCategoryService;
import be.atc.services.HallService;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class HallBean implements Serializable {

    private Logger log = org.apache.log4j.Logger.getLogger(HallBean.class);

    @Inject
    private HallService hallService;

    @Inject
    private HallCategoryService hallCategoryService;

    private List<HallEntity> halls;

    private HallEntity hall;

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
    }

    private List<CategoryEntity> categories;

    private List<HallCategoryEntity> hallCategories;

    @PostConstruct
    public void init() {
        halls = getHalls();
        hall = new HallEntity();
    }

    public List<HallEntity> getHalls() {
        EntityManager em = EMF.getEM();
        List<HallEntity> fetchedHalls = new ArrayList<>();
        try {
            fetchedHalls = hallService.findAllOrNull(em);
        } catch (Exception e) {
            log.error("An error occurred while fetching services.");
        } finally {
            em.clear();
            em.close();
        }
        return fetchedHalls;
    }

    public void loadHallCategories(HallEntity selectedHall) {
        EntityManager em = EMF.getEM();
        try {
            hallCategories = hallCategoryService.findAllCategoriesByHallOrNull(selectedHall, em);
        } catch (Exception e) {
            log.error("An error occurred while fetching categories for hall.", e);
        } finally {
            em.clear();
            em.close();
        }
    }


    public String createHall() {
        log.info("Attempting to create a new hall...");
        EntityManager em = EMF.getEM();
        EntityTransaction tx = null;
        try {
            boolean alreadyExist = hallService.exist(hall, em);
            if (alreadyExist) {
                throw new RuntimeException("Can't create hall,this hall already exist.");
            }

            tx = em.getTransaction();
            log.info("Begin transaction to persist a new hall");
            tx.begin();

            hallService.insert(hall, em);

            //Insert the associated categories
            for(CategoryEntity category : categories) {
                HallCategoryEntity hallCategory = new HallCategoryEntity();
                hallCategory.setHallByHallId(hall);
                hallCategory.setCategoryByCategoryId(category);
                hallCategoryService.insert(hallCategory, em);
            }

            tx.commit();
            log.info("Transaction committed, new hall persisted");
            return "succes";

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            log.error("Failed to create hall", e);
            return "failure";
        } finally {
            em.clear();
            em.close();
            halls = getHalls();
        }

    }

    public String updateHall() {
        log.info("Attempting to update a hall...");
        EntityManager em = EMF.getEM();
        EntityTransaction tx = null;
        try {

            tx = em.getTransaction();
            log.info("Begin transaction to update a hall");
            tx.begin();

            hallService.update(hall, em);

            // Get existing hall-category associations for this hall
            List<HallCategoryEntity> existingAssociations = hallCategoryService.findAllCategoriesByHallOrNull(hall, em);

            // Update or delete existing associations based on the selected categories
            for (HallCategoryEntity existingAssociation : existingAssociations) {
                CategoryEntity associatedCategory = existingAssociation.getCategoryByCategoryId();
                if (categories.contains(associatedCategory)) {
                    categories.remove(associatedCategory); // Remove from categories list to prevent duplicates
                } else {
                    hallCategoryService.delete(existingAssociation, em);
                }
            }

            // Insert new associations for the remaining selected categories
            for (CategoryEntity category : categories) {
                HallCategoryEntity hallCategory = new HallCategoryEntity();
                hallCategory.setHallByHallId(hall);
                hallCategory.setCategoryByCategoryId(category);
                hallCategoryService.insert(hallCategory, em);
            }

            tx.commit();
            log.info("Transaction committed, hall updated");
            return "succes";

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            log.error("Failed to update hall", e);
            return "failure";
        } finally {
            em.clear();
            em.close();
            halls = getHalls();
        }
    }

    public void openNew() {
        this.hall = new HallEntity();
        this.categories = new ArrayList<CategoryEntity>();
    }

    public HallEntity getHall() {
        return hall;
    }

    public void setHall(HallEntity hall) {
        this.hall = hall;
    }

}
