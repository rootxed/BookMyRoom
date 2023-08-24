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
        return (findBuildingByNameOrNull(b.getName(), em) != null);
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
        } finally {
            em.clear();
            em.close();
        }

    }

    public BuildingEntity searchBuildingsByName(String name, EntityManager em) throws IllegalArgumentException {
        log.info("Searching buildings starting with : " + name);

        try{

        }
    }
}
