package be.atc.managedBeans;

import be.atc.entities.AddresseEntity;
import be.atc.entities.BuildingEntity;
import be.atc.entities.CityEntity;
import be.atc.services.AddresseService;
import be.atc.services.BuildingService;
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
public class BuildingBean implements Serializable {

    private Logger log = org.apache.log4j.Logger.getLogger(BuildingBean.class);

    @Inject
    private BuildingService buildingService;

    @Inject
    private AddresseService addresseService;

    private List<BuildingEntity> buildings;
    private List<BuildingEntity> filteredBuildings;

    private BuildingEntity building = new BuildingEntity();

    /**
     * The function returns a list of BuildingEntity objects, loading them if necessary.
     *
     * @return The method is returning a List of BuildingEntity objects.
     */
    public List<BuildingEntity> getBuildings() {
        if (buildings == null) {
            buildings = loadBuildings();
        }
        return buildings;
    }

    /**
     * The function loads all buildings from the database and returns them as a list of BuildingEntity objects.
     *
     * @return The method is returning a List of BuildingEntity objects.
     */
    public List<BuildingEntity> loadBuildings() {
        EntityManager em = EMF.getEM();
        List<BuildingEntity> fetchedBuildings;
        try {
            fetchedBuildings = buildingService.findAllOrNull(em);
        } finally {
            em.clear();
            em.close();
        }
        log.info("Fetched all buildings.");
        return fetchedBuildings;
    }

    /**
     * The function searches for buildings by name using a given query and returns a list of matching building entities.
     *
     * @param query The query parameter is a String that represents the name of the building that you want to search for.
     * @return The method is returning a List of BuildingEntity objects.
     */
    public List<BuildingEntity> searchBuildingByName(String query) {
        EntityManager em = EMF.getEM();
        List<BuildingEntity> matchingBuildings = new ArrayList<>();
        try {
            matchingBuildings = buildingService.searchBuildingsByName(query, em);
        } catch (Exception e) {
            log.error("An error occurred while seraching for buildings by name.", e);
        } finally {
            em.clear();
            em.close();
        }
        return matchingBuildings;
    }

    public List<BuildingEntity> getFilteredBuildings() {
        return filteredBuildings;
    }

    public void setFilteredBuildings(List<BuildingEntity> filteredBuildings) {
        this.filteredBuildings = filteredBuildings;
    }

    /**
     * The function "saveBuilding" checks if a building object has an ID, and if it does, it updates the building,
     * otherwise it creates a new building.
     */
    public void saveBuilding() {
        if (building.getId() == 0) {
            createBuilding();
        } else {
            updateBuilding();
        }
    }


    /**
     * This Java function creates a new building, persists it in the database, and handles any exceptions that may occur.
     *
     * @return The method is returning a String value. The possible return values are "success" or "failure".
     */
    public String createBuilding() {
        log.info("Attempting to create a new building...");
        EntityManager em = EMF.getEM();
        EntityTransaction transaction = null;
        try {
            boolean alreadyExist = buildingService.exist(building, em);
            if (alreadyExist) {
                NotificationManager.addErrorMessageFromBundle("notification.building.alreadyExist");
                throw new RuntimeException("Can't create building, this building already exists.");
            }

            transaction = em.getTransaction();
            log.info("Begin transaction to persist a new building");
            transaction.begin();

            AddresseEntity address = building.getAddresseByAdresseId();

            em.persist(address);

            building.setAddresseByAdresseId(address);
            buildingService.insert(building, em);

            transaction.commit();
            log.info("Transaction committed, new building persisted.");
            NotificationManager.addInfoMessageFromBundle("notification.building.successCreated");
            PrimeFaces.current().executeScript("PF('manageBuildingDialog').hide()");
            return "success";
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Failed to create building", e);
            NotificationManager.addErrorMessageFromBundle("notification.building.failedCreated");
            return "failure";
        } finally {
            em.clear();
            em.close();
            buildings = loadBuildings();
            PrimeFaces.current().ajax().update("listForm:dt-buildings", "globalGrowl");
        }
    }

    /**
     * The function attempts to update a building by checking if it already exists, beginning a transaction, updating the
     * address and building, committing the transaction, and displaying success or failure notifications.
     *
     * @return The method is returning a String value. If the building is successfully updated, it returns "success". If
     * there is an exception or error during the update process, it returns "failure".
     */
    public String updateBuilding() {
        log.info("Attempting to update the building...");
        EntityManager em = EMF.getEM();
        EntityTransaction transaction = null;
        try {
            boolean alreadyExist = buildingService.exist(building, em);
            if (alreadyExist) {
                throw new RuntimeException("Can't update building, this building name already exists.");
            }

            transaction = em.getTransaction();
            log.info("Begin transaction to update the building");
            transaction.begin();

            AddresseEntity addressToUpdate = building.getAddresseByAdresseId();
            addresseService.update(addressToUpdate, em);

            buildingService.update(building, em);

            transaction.commit();
            log.info("Transaction committed, building updated.");
            NotificationManager.addInfoMessageFromBundle("notification.building.successUpdate");
            PrimeFaces.current().executeScript("PF('manageBuildingDialog').hide()");
            return "success";
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Failed to update building", e);
            NotificationManager.addErrorMessageFromBundle("notification.building.failedUpdate");
            return "failure";
        } finally {
            em.clear();
            em.close();
            buildings = loadBuildings();
            PrimeFaces.current().ajax().update("listForm:dt-buildings", "globalGrowl");
        }
    }

    /**
     * The function retrieves a list of BuildingEntity objects based on the specified CityEntity object.
     *
     * @param city The "city" parameter is of type CityEntity, which represents a city in the system.
     * @return The method is returning a List of BuildingEntity objects.
     */
    public List<BuildingEntity> getBuildingsByCity(CityEntity city) {
        List<BuildingEntity> buildings = new ArrayList<BuildingEntity>();
        EntityManager em = EMF.getEM();
        try {
            if (city != null) {
                log.info("Getting buildings in " + city);
                buildings = buildingService.findBuildingsByCity(city, em);
            } else {
                log.info("selected city is Null");
            }
        } catch (Exception e) {
            log.error("Error while searching for building by city", e);
        } finally {
            em.close();
        }
        return buildings;
    }

    /**
     * The function "openNew" creates a new BuildingEntity object and sets its AddresseByAdresseId property to a new
     * AddresseEntity object.
     */
    public void openNew() {
        this.building = new BuildingEntity();
        this.building.setAddresseByAdresseId(new AddresseEntity());
    }

    /**
     * The function checks if a building is not being used.
     *
     * @param building The "building" parameter is an instance of the BuildingEntity class.
     * @return The method is returning a boolean value.
     */
    public boolean isBuildingNotUsed(BuildingEntity building) {
        EntityManager em = EMF.getEM();
        try {
            return (!buildingService.isBuildingUsed(building, em));
        } finally {
            em.clear();
            em.close();
        }
    }

    /**
     * The function deletes a building entity from the database if it is not being used, otherwise it logs an error and
     * displays an error message.
     */
    public void deleteBuilding() {
        EntityManager em = EMF.getEM();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            BuildingEntity buildingToDelete = buildingService.findOneByIdOrNull(building.getId(), em);
            if (isBuildingNotUsed(buildingToDelete)) {
                buildingService.delete(buildingToDelete, em);
                tx.commit();
                NotificationManager.addInfoMessageFromBundleRedirect("notification.building.successDelete");
            } else {
                log.error("Building is used and can't be deleted.");
                NotificationManager.addErrorMessage("notification.building.failedDeleteUsed");
            }
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            log.error("Error while attempting to delete ", e);
            NotificationManager.addErrorMessage("notification.building.failedDelete");
        } finally {
            em.clear();
            em.close();
            buildings = loadBuildings();
            PrimeFaces.current().ajax().update("listForm:dt-buildings", "globalGrowl");
        }
    }


    public BuildingEntity getBuilding() {
        return building;
    }

    public void setBuilding(BuildingEntity building) {
        this.building = building;
    }


}
