package be.atc.managedBeans;

import be.atc.entities.BuildingEntity;
import be.atc.entities.CategoryEntity;
import be.atc.services.CategoryService;
import be.atc.tools.EMF;
import be.atc.tools.NotificationManager;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

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

    public List<CategoryEntity> getFilteredCategories() {
        return filteredCategories;
    }

    public void setFilteredCategories(List<CategoryEntity> filteredCategories) {
        this.filteredCategories = filteredCategories;
    }

    private List<CategoryEntity> filteredCategories;

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    private CategoryEntity category;

    public List<CategoryEntity> loadCategories() {
        EntityManager em = EMF.getEM();
        List<CategoryEntity> fetchedCategories = new ArrayList<>();
        try {
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
        if (category.getId() == 0) {
            createCategory();
        } else {
            updateCategory();
        }
    }

    public String createCategory() {
        log.info("Attempting to create a new category...");
        EntityManager em = EMF.getEM();
        EntityTransaction tx = null;
        try {
            boolean alreadyExist = categoryService.exist(category, em);
            if (alreadyExist) {
                NotificationManager.addErrorMessageFromBundle("notification.category.alreadyExist");
                throw new RuntimeException("Can't create category,this category already exist.");
            }

            tx = em.getTransaction();
            log.info("Begin transaction to persist a new category");
            tx.begin();

            categoryService.insert(category, em);

            tx.commit();
            log.info("Transaction committed, new category persisted.");
            NotificationManager.addInfoMessageFromBundle("notification.category.successCreated");
            PrimeFaces.current().executeScript("PF('manageCategoryDialog').hide()");
            return "succes";
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            log.error("Failed to create building", e);
            NotificationManager.addErrorMessageFromBundle("notification.category.failedCreated");
            return "failure";

        } finally {
            em.clear();
            em.close();
            categories = loadCategories();
            PrimeFaces.current().ajax().update("listForm:dt-categories", "globalGrowl");
        }

    }

    public String updateCategory() {
        log.info("Attempting to update the category...");
        EntityManager em = EMF.getEM();
        EntityTransaction tx = null;
        try {
            boolean alreadyExist = categoryService.exist(category, em);
            if (alreadyExist) {
                throw new RuntimeException("Can't create category,this category already exist.");
            }

            tx = em.getTransaction();
            log.info("Begin transaction to update the category");
            tx.begin();

            categoryService.update(category, em);

            tx.commit();

            log.info("Transaction committed, category updated.");
            NotificationManager.addInfoMessageFromBundle("notification.category.successUpdate");
            PrimeFaces.current().executeScript("PF('manageCategoryDialog').hide()");
            return "success";
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            log.error("Failed to update category", e);
            NotificationManager.addErrorMessageFromBundle("notification.category.failedUpdate");
            return "failure";
        } finally {
            em.clear();
            em.close();
            categories = loadCategories();
            PrimeFaces.current().ajax().update("listForm:dt-categories", "globalGrowl");
        }
    }

    public void deleteCategory(){
        EntityManager em = EMF.getEM();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            CategoryEntity categoryToDelete = categoryService.findOneByIdOrNull(category.getId(), em);
            if (isCategoryNotUsed(categoryToDelete)){
                categoryService.delete(categoryToDelete, em);
                tx.commit();
                NotificationManager.addInfoMessageFromBundleRedirect("notification.category.successDelete");
            }else {
                log.error("Category is used and can't be deleted.");
                NotificationManager.addErrorMessage("notification.category.failedDeleteUsed");
            }
        }catch (Exception e){
            if (tx.isActive()) {
                tx.rollback();
            }
            log.error("Error while attempting to delete ", e);
            NotificationManager.addErrorMessage("notification.category.failedDelete");
        }
        finally {
            em.clear();
            em.close();
            categories = loadCategories();
            PrimeFaces.current().ajax().update("listForm:dt-categories", "globalGrowl");
        }
    }

    public List<CategoryEntity> getCategories() {
        if (categories == null) {
            categories = loadCategories();
        }
        return categories;
    }

    public boolean isCategoryNotUsed(CategoryEntity category) {
        EntityManager em = EMF.getEM();
        try {
            return (!categoryService.isUsed(category,em));
        } finally {
            em.clear();
            em.close();
        }
    }

    public void openNew() {
        this.category = new CategoryEntity();
    }


}