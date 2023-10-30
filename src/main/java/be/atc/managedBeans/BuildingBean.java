package be.atc.managedBeans;

import be.atc.entities.AddresseEntity;
import be.atc.entities.BuildingEntity;
import be.atc.entities.CityEntity;
import be.atc.entities.HallEntity;
import be.atc.services.AddresseService;
import be.atc.services.BuildingService;
import be.atc.tools.EMF;
import be.atc.tools.NotificationManager;
import org.apache.log4j.Logger;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.primefaces.event.SelectEvent;

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
public class BuildingBean implements Serializable {

    private Logger log = org.apache.log4j.Logger.getLogger(BuildingBean.class);

    @Inject
    private BuildingService buildingService;

    @Inject
    private AddresseService addresseService;

    private List<BuildingEntity> buildings;
    private List<BuildingEntity> filteredBuildings;

    private BuildingEntity building = new BuildingEntity();

    @Inject
    private CitiesBean citiesBean;

    public List<BuildingEntity> getBuildings() {
        if (buildings == null) {
            buildings = loadBuildings();
        }
        return buildings;
    }

    public List<BuildingEntity> loadBuildings(){
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

    public List<BuildingEntity> searchBuildingByName(String query){
        EntityManager em = EMF.getEM();
        List<BuildingEntity> matchingBuildings = new ArrayList<>();
        try {
            matchingBuildings = buildingService.searchBuildingsByName(query,em);
        } catch (Exception e ) {
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

    public void saveBuilding() {
        if(building.getId() == 0){
            createBuilding();
        }else {
            updateBuilding();
        }
    }


    public String createBuilding() {
        log.info("Attempting to create a new building...");
        EntityManager em = EMF.getEM();
        EntityTransaction transaction = null;
        try {
            boolean alreadyExist = buildingService.exist(building, em);
            if (alreadyExist){
                throw new RuntimeException("Can't create building, this building already exists.");
            }

            transaction = em.getTransaction();
            log.info("Begin transaction to persist a new building");
            transaction.begin();

            AddresseEntity address =building.getAddresseByAdresseId();

            em.persist(address);

            building.setAddresseByAdresseId(address);
            buildingService.insert(building, em);

            transaction.commit();
            log.info("Transaction committed, new building persisted.");
            return "success";
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Failed to create building", e);
            return "failure";
        } finally {
            em.clear();
            em.close();
            buildings = getBuildings();
        }
    }

    public String updateBuilding() {
        log.info("Attempting to update the building...");
        EntityManager em = EMF.getEM();
        EntityTransaction transaction = null;
        try {
            boolean alreadyExist = buildingService.exist(building, em);
            if (alreadyExist){
                throw new RuntimeException("Can't update building, this building name already exists.");
            }

            transaction = em.getTransaction();
            log.info("Begin transaction to update the building");
            transaction.begin();

            AddresseEntity addressToUpdate = building.getAddresseByAdresseId();
            addresseService.update(addressToUpdate,em);

            buildingService.update(building, em);

            transaction.commit();
            log.info("Transaction committed, building updated.");
            return "success";
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Failed to update building", e);
            return "failure";
        } finally {
            em.clear();
            em.close();
            buildings = getBuildings();
        }
    }

    public List<BuildingEntity> getBuildingsByCity(CityEntity city){
        List<BuildingEntity> buildings = new ArrayList<BuildingEntity>();
        EntityManager em = EMF.getEM();
        try {
            if(city != null) {
                log.info("Getting buildings in " + city);
                buildings = buildingService.findBuildingsByCity(city, em);
            } else {
                log.info("selected city is Null");
            }
        }catch (Exception e){
            log.error("Error while searching for building by city", e);
        }finally {
            em.close();
        }
        return buildings;
    }

    public void openNew() {
        this.building = new BuildingEntity();
        this.building.setAddresseByAdresseId(new AddresseEntity());
    }

    public boolean isBuildingNotUsed(BuildingEntity building){
        EntityManager em = EMF.getEM();
        try {
            return (!buildingService.isBuildingUsed(building,em));
        } finally {
            em.clear();
            em.close();
        }
    }

    public void deleteBuilding(BuildingEntity building){
        EntityManager em = EMF.getEM();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            BuildingEntity buildingToDelete = buildingService.findOneByIdOrNull(building.getId(), em);
            if (isBuildingNotUsed(buildingToDelete)){
                buildingService.delete(buildingToDelete,em);
                tx.commit();
                NotificationManager.addInfoMessage("Hall '" + buildingToDelete.getName() +"' deleted.");
            }else {
                log.error("Building is used and can't be deleted.");
                NotificationManager.addErrorMessage("Error, building is already used and can't be deleted.");
            }
        }catch (Exception e){
            if (tx.isActive()) {
                tx.rollback();
            }
            log.error("Error while attempting to delete ", e);
            NotificationManager.addErrorMessage("Error while deleting.");
        }
        finally {
            em.clear();
            em.close();
        }
    }


    public BuildingEntity getBuilding() {
        return building;
    }

    public void setBuilding(BuildingEntity building) {
        this.building = building;
    }



}
