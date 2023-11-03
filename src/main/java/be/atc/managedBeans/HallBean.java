package be.atc.managedBeans;

import be.atc.entities.*;
import be.atc.services.HallCategoryService;
import be.atc.services.HallScheduleService;
import be.atc.services.HallService;
import be.atc.tools.EMF;
import be.atc.tools.NotificationManager;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class HallBean implements Serializable {

    private Logger log = org.apache.log4j.Logger.getLogger(HallBean.class);

    @Inject
    private HallService hallService;

    @Inject
    private HallCategoryService hallCategoryService;

    @Inject
    private HallScheduleService hallScheduleService;

    private List<HallEntity> halls;

    private List<HallEntity> filteredHalls;

    private HallEntity hall = new HallEntity();

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
    }

    private List<CategoryEntity> categories;

    private List<HallCategoryEntity> hallCategories;

    public List<HallEntity> loadHalls() {
        EntityManager em = EMF.getEM();
        List<HallEntity> fetchedHalls = new ArrayList<>();
        try {
            fetchedHalls = hallService.findAllOrNull(em);

            // Rafraîchir manuellement les associations hallcategoriesbyID pour chaque hall
//            for (HallEntity hall : fetchedHalls) {
//                em.refresh(hall); // Rafraîchir l'entité pour actualiser les associations
//            }
        } catch (Exception e) {
            log.error("An error occurred while fetching Halls.");
        } finally {
            em.clear();
            em.close();
        }
        return fetchedHalls;
    }

    public List<CategoryEntity> getSelectedCategories() {
        List<CategoryEntity> selectedCategories = new ArrayList<>();
        for (HallCategoryEntity hallCategory : hall.getHallcategoriesById()) {
            selectedCategories.add(hallCategory.getCategoryByCategoryId());
        }
        return selectedCategories;
    }

    public List<HallEntity> getHallsByBuilding(BuildingEntity building){
        List<HallEntity> halls = new ArrayList<HallEntity>();
        EntityManager em = EMF.getEM();
        try {
            if(building != null) {
                log.info("Getting halls in building " + building.getName());
                halls = hallService.findByBuilding(building, em);
            } else {
                log.info("Building is Null");
            }
        }catch (Exception e){
            log.error("Error while searching for halls by building", e);
        }finally {
            em.close();
        }
        return halls;
    }

    public void save() {
        //Checking if the save is for an existing Hall or a new one
        if(hall.getId() == 0){
            createHall();
        }else {
            updateHall();
        }
    }


    public String createHall() {
        log.info("Attempting to create a new hall...");
        EntityManager em = EMF.getEM();
        EntityTransaction tx = null;
        try {
            boolean alreadyExist = hallService.exist(hall, em);
            if (alreadyExist) {
                throw new RuntimeException("Can't create hall, this hall already exists.");
            }

            tx = em.getTransaction();
            log.info("Begin transaction to persist a new hall");
            tx.begin();

            hall.setHallcategoriesById(insertHallCategories());
            //Create a hall with the default schedule
            hallService.createHallWithDefaultSchedules(hall, em);

            tx.commit();
            log.info("Transaction committed, new hall persisted");
            NotificationManager.addInfoMessageFromBundle("notification.hall.successCreated");
            PrimeFaces.current().executeScript("PF('manageHallDialog').hide()");
            return "success";

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            log.error("Failed to create hall", e);
            NotificationManager.addErrorMessageFromBundle("notification.hall.failedCreated");
            return "failure";
        } finally {
            em.clear();
            em.close();
            halls = loadHalls();
            PrimeFaces.current().ajax().update("listForm:dt-halls", "globalGrowl");
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


            hall.setHallcategoriesById(updateHallCategories(em));
            hallService.update(hall, em);

            tx.commit();
            log.info("Transaction committed, hall updated");
            NotificationManager.addInfoMessageFromBundle("notification.hall.successUpdate");
            PrimeFaces.current().executeScript("PF('manageHallDialog').hide()");
            return "success";

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            log.error("Failed to update hall", e);
            NotificationManager.addErrorMessageFromBundle("notification.hall.failedUpdate");
            return "failure";
        } finally {
            em.close();
            halls = loadHalls();
            PrimeFaces.current().ajax().update("listForm:dt-halls", "globalGrowl");
        }
    }

    private List<HallCategoryEntity> insertHallCategories() {
        // Prepare and insert hall-categories association
        List<HallCategoryEntity> listHC = new ArrayList<>();
        for (CategoryEntity category : categories) {
            HallCategoryEntity hallCategory = new HallCategoryEntity();
            hallCategory.setHallByHallId(hall);
            hallCategory.setCategoryByCategoryId(category);
            //hallCategoryService.insert(hallCategory, em);
            listHC.add(hallCategory);
        }
        return listHC;
    }


    private List<HallCategoryEntity> updateHallCategories(EntityManager em) {
        // Get existing hall-category associations for this hall
        List<HallCategoryEntity> existingAssociations = hallCategoryService.findAllCategoriesByHallOrNull(hall, em);

        // Create a map of existing associations for faster lookups
        Map<CategoryEntity, HallCategoryEntity> existingAssociationsMap = new HashMap<>();
        for (HallCategoryEntity existingAssociation : existingAssociations) {
            existingAssociationsMap.put(existingAssociation.getCategoryByCategoryId(), existingAssociation);
        }

        // Prepare and insert hall-categories association
        List<HallCategoryEntity> updatedAssociations = new ArrayList<>();
        for (CategoryEntity category : categories) {
            HallCategoryEntity hallCategory = existingAssociationsMap.get(category);

            if (hallCategory == null) {
                // Create a new association if it doesn't exist
                hallCategory = new HallCategoryEntity();
                hallCategory.setHallByHallId(hall);
                hallCategory.setCategoryByCategoryId(category);
            }

            updatedAssociations.add(hallCategory);
            existingAssociationsMap.remove(category); // Remove from the map
        }

        // Delete any remaining associations that are no longer selected
        for (HallCategoryEntity associationToRemove : existingAssociationsMap.values()) {
            hallCategoryService.delete(associationToRemove, em);
        }

        return updatedAssociations;
    }


//    private void updateHallCategories(EntityManager em) {
//        // Get existing hall-category associations for this hall
//        List<HallCategoryEntity> existingAssociations = hallCategoryService.findAllCategoriesByHallOrNull(hall, em);
//
//        // Compare existing associations with selected categories
//        for (HallCategoryEntity existingAssociation : existingAssociations) {
//            CategoryEntity associatedCategory = existingAssociation.getCategoryByCategoryId();
//            if (categories.contains(associatedCategory)) {
//                categories.remove(associatedCategory); // Remove from categories list to prevent duplicates
//            } else {
//                hallCategoryService.delete(existingAssociation, em);
//            }
//        }
//
//        // Insert new associations for the remaining selected categories
//        for (CategoryEntity category : categories) {
//            HallCategoryEntity hallCategory = new HallCategoryEntity();
//            hallCategory.setHallByHallId(hall);
//            hallCategory.setCategoryByCategoryId(category);
//            hallCategoryService.insert(hallCategory, em);
//        }
//    }

    private void deleteExistingHallCategories(EntityManager em) {
        for (HallCategoryEntity hallCategory : hall.getHallcategoriesById()) {
            hallCategoryService.delete(hallCategory, em);
        }
    }

    public void openNew() {
        this.hall = new HallEntity();
        this.categories = new ArrayList<CategoryEntity>();
    }

    public void openEdit(HallEntity selectedHall) {
        hall = selectedHall;
        categories = new ArrayList<>(hall.getHallcategoriesById().stream()
                .map(HallCategoryEntity::getCategoryByCategoryId)
                .collect(Collectors.toList()));
    }


    public HallEntity getHall() {
        return hall;
    }

    public void setHall(HallEntity hall) {
        this.hall = hall;
    }

    public List<HallEntity> getHalls() {
        if (halls == null) {
            halls = loadHalls();
        }
        return halls;
    }

    public List<HallEntity> getFilteredHalls() {
        return filteredHalls;
    }

    public void setFilteredHalls(List<HallEntity> filteredHalls) {
        this.filteredHalls = filteredHalls;
    }
}
