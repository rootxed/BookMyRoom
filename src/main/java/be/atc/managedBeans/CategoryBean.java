package be.atc.managedBeans;

import be.atc.entities.BookingEntity;
import be.atc.entities.CategoryEntity;
import be.atc.services.CategoryService;
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
public class CategoryBean implements Serializable {

    private Logger log = org.apache.log4j.Logger.getLogger(CategoryBean.class);

    @Inject
    private CategoryService categoryService;

    private List<CategoryEntity> categories;

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    private CategoryEntity category;

    public List<CategoryEntity> loadCategories(){
        EntityManager em = EMF.getEM();
        List<CategoryEntity> fetchedCategories = new ArrayList<>();
        try{
            fetchedCategories = categoryService.findAllOrNull(em);
        } catch (Exception e) {
            log.error("An error occurred while fetching services.");
        } finally {
            em.clear();
            em.close();
        }
        log.info("Fetched all categories");
        return fetchedCategories;
    }

    public void saveCategory() {
        if(category.getId() == 0){
            createCategory();
        }else {
            updateCategory();
        }
    }

    public String createCategory() {
        log.info("Attempting to create a new category...");
        EntityManager em = EMF.getEM();
        EntityTransaction tx = null;
        try{
            boolean alreadyExist = categoryService.exist(category, em);
            if(alreadyExist) {
                throw new RuntimeException("Can't create category,this category already exist.");
            }

            tx = em.getTransaction();
            log.info("Begin transaction to persist a new category");
            tx.begin();

            categoryService.insert(category, em);

            tx.commit();
            log.info("Transaction committed, new category persisted.");
            return "succes";


        }catch (Exception e) {
            if(tx != null && tx.isActive()) {
                tx.rollback();
            }
            log.error("Failed to create building", e);
            return "failure";

        } finally {
           em.clear();
           em.close();
           categories = loadCategories();
        }

    }

    public String updateCategory(){
        log.info("Attempting to update the category...");
        EntityManager em = EMF.getEM();
        EntityTransaction tx = null;
        try {
            boolean alreadyExist = categoryService.exist(category, em);
            if(alreadyExist) {
                throw new RuntimeException("Can't create category,this category already exist.");
            }

            tx = em.getTransaction();
            log.info("Begin transaction to update the category");
            tx.begin();

            categoryService.update(category, em);

            tx.commit();

            log.info("Transaction committed, category updated.");
            return "success";
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            log.error("Failed to update category", e);
            return "failure";
        } finally {
            em.clear();
            em.close();
            categories = loadCategories();
        }
    }

    public List<CategoryEntity> getCategories() {
        if (categories == null) {
            categories = loadCategories();
        }
        return categories;
    }

    public void openNew() {
        this.category = new CategoryEntity();
    }


}