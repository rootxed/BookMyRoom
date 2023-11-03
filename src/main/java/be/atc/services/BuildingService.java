package be.atc.services;

import be.atc.entities.BuildingEntity;
import be.atc.entities.CityEntity;
import be.atc.entities.UserEntity;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class BuildingService extends  ServiceImpl<BuildingEntity>{

    private Logger log = org.apache.log4j.Logger.getLogger(UserService.class);


    public boolean exist(BuildingEntity b, EntityManager em) {
        log.info("Checking if building already exist.");
        BuildingEntity foundBuilding = findBuildingByNameOrNull(b.getName(), em);
        if (foundBuilding == null){
            return false;
        }else{
            //IF entity is the same as the updated entity
            if (foundBuilding.getId() == b.getId()){
                return false;
            }
            else{
                return true;
            }
        }
    }

    public boolean nameAlreadyExist(String name, EntityManager em) {
        log.info("Checking if building Name already exist.");
        return (findBuildingByNameOrNull(name, em) != null);
    }

    public BuildingEntity findOneByIdOrNull(int id, EntityManager em) {
        log.info("Select building by id: "+id);
        return em.find(BuildingEntity.class, id);
    }

    public List<BuildingEntity> findAllOrNull(EntityManager em) {
        log.info("Finding all buildings...");
        try {
            TypedQuery<BuildingEntity> query = em.createNamedQuery("Building.findAll", BuildingEntity.class);
            List<BuildingEntity> buildingList = query.getResultList();
            log.info("Selected all buildings.");
            return buildingList;
        } catch (Exception e) {
            log.info("Query found no buildings to return");
            return null;
        }
    }

    public BuildingEntity findBuildingByNameOrNull(String name, EntityManager em) throws IllegalArgumentException{
        log.info("Finding Building with name: " + name);

        try{
            return em.createNamedQuery("Building.findOneByName",BuildingEntity.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.info("Query found no building to return");
            return null;
        }

    }

    public List<BuildingEntity> searchBuildingsByName(String name, EntityManager em) throws IllegalArgumentException {
        log.info("Searching buildings starting with : " + name);
        try {
            return em.createNamedQuery("Building.searchByName", BuildingEntity.class)
                    .setParameter("name", name + "%")
                    .setMaxResults(10)
                    .getResultList();
        } catch (NoResultException e) {
            log.info("No buildings found for building for the name: " + name, e);
            return null;
        }
    }

    public boolean isBuildingUsed(BuildingEntity building, EntityManager em) {
        log.info("Searching if building is used");

        TypedQuery<Long> query = em.createNamedQuery("HallEntity.countByBuilding", Long.class)
                .setParameter("building", building);

        long count = query.getSingleResult();

        return count > 0;
    }

    public List<BuildingEntity> findBuildingsByCity(CityEntity city, EntityManager em){
        log.info("Finding buildings by city");
        return em.createNamedQuery("Building.findByCity", BuildingEntity.class)
                .setParameter("city", city)
                .getResultList();
    }
}
