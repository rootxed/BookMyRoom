package be.atc.managedBeans;

import be.atc.entities.BuildingEntity;
import be.atc.entities.CategoryEntity;
import be.atc.entities.HallCategoryEntity;
import be.atc.entities.HallEntity;
import be.atc.services.HallCategoryService;
import be.atc.services.HallScheduleService;
import be.atc.services.HallService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class HallBean implements Serializable {

    private Logger log = org.apache.log4j.Logger.getLogger(HallBean.class);

    @Inject
    private HallService hallService;

    @Inject
    private HallCategoryService hallCategoryService;

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

    /**
     * The function loads a list of HallEntity objects from the database using an EntityManager.
     *
     * @return The method is returning a List of HallEntity objects.
     */
    public List<HallEntity> loadHalls() {
        EntityManager em = EMF.getEM();
        List<HallEntity> fetchedHalls = new ArrayList<>();
        try {
            fetchedHalls = hallService.findAllOrNull(em);
        } catch (Exception e) {
            log.error("An error occurred while fetching Halls.");
        } finally {
            em.clear();
            em.close();
        }
        return fetchedHalls;
    }

    /**
     * The function retrieves a list of HallEntity objects based on the provided BuildingEntity object.
     *
     * @param building The "building" parameter is of type BuildingEntity. It represents the building for which we want to
     * retrieve the halls.
     * @return The method is returning a List of HallEntity objects.
     */
    public List<HallEntity> getHallsByBuilding(BuildingEntity building) {
        List<HallEntity> halls = new ArrayList<HallEntity>();
        EntityManager em = EMF.getEM();
        try {
            if (building != null) {
                log.info("Getting halls in building " + building.getName());
                halls = hallService.findByBuilding(building, em);
            } else {
                log.info("Building is Null");
            }
        } catch (Exception e) {
            log.error("Error while searching for halls by building", e);
        } finally {
            em.close();
        }
        return halls;
    }

    /**
     * The save function checks if the hall is new or existing and calls the appropriate method to create or update the
     * hall.
     */
    public void save() {
        //Checking if the save is for an existing Hall or a new one
        if (hall.getId() == 0) {
            createHall();
        } else {
            updateHall();
        }
    }


    /**
     * This function creates a new hall and persists it in the database, handling any exceptions that may occur.
     *
     * @return The method is returning a String value. If the hall creation is successful, it returns "success". If there
     * is an exception or error during the creation process, it returns "failure".
     */
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

    /**
     * The function updates a hall and handles any exceptions that may occur.
     *
     * @return The method is returning a String value. If the hall is successfully updated, it returns "success". If there
     * is an exception or failure in updating the hall, it returns "failure".
     */
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

    /**
     * The function inserts hall-category associations into a list and returns the list.
     *
     * @return The method is returning a List of HallCategoryEntity objects.
     */
    private List<HallCategoryEntity> insertHallCategories() {
        // Prepare and insert hall-categories association
        List<HallCategoryEntity> listHC = new ArrayList<>();
        for (CategoryEntity category : categories) {
            HallCategoryEntity hallCategory = new HallCategoryEntity();
            hallCategory.setHallByHallId(hall);
            hallCategory.setCategoryByCategoryId(category);
            listHC.add(hallCategory);
        }
        return listHC;
    }


    /**
     * The function updates the hall-category associations by creating new associations, removing associations that are no
     * longer selected, and returning the updated associations.
     *
     * @param em The parameter "em" is an instance of the EntityManager class. EntityManager is part of the Java
     * Persistence API (JPA) and is used to interact with the database. It provides methods for querying, persisting, and
     * managing entities in the database. In this code snippet, the EntityManager is used to
     * @return The method is returning a List of HallCategoryEntity objects.
     */
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

    /**
     * The function "openNew" initializes a new HallEntity object and creates an empty ArrayList of CategoryEntity objects.
     */
    public void openNew() {
        this.hall = new HallEntity();
        this.categories = new ArrayList<CategoryEntity>();
    }

    /**
     * The function "openEdit" sets the "hall" variable to the selected hall and populates the "categories" list with the
     * categories associated with the selected hall.
     *
     * @param selectedHall The selectedHall parameter is an instance of the HallEntity class. It represents a specific hall
     * object that has been selected for editing.
     */
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

    /**
     * The function returns a list of HallEntity objects, loading them if necessary.
     *
     * @return The method is returning a List of HallEntity objects.
     */
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
