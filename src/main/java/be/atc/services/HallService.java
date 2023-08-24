package be.atc.services;

import be.atc.entities.BuildingEntity;
import be.atc.entities.CategoryEntity;
import be.atc.entities.HallEntity;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class HallService extends ServiceImpl<HallEntity> {

    private Logger log = org.apache.log4j.Logger.getLogger(HallService.class);

    public boolean exist(HallEntity h, EntityManager em) {
        log.info("Checking if hall already exist.");
        return (findHallByNameAndBuildingOrNull(h.getName(),h.getBuildingByBuildingId()) != null);
    }

    private HallEntity findHallByNameAndBuildingOrNull(String name, BuildingEntity building){
        log.info("Finding Hall with name and building: " + name + " " + building.getName());
        EntityManager em = EMF.getEM();
        try{
            return em.createNamedQuery("Hall.existsWithNameAndBuilding",HallEntity.class)
                    .setParameter(name, building)
                    .getSingleResult();
        }catch (NoResultException e) {
            log.info("Query found no hall to return.");
            return null;
        } finally {
            em.clear();
            em.close();
        }

    }

    public HallEntity findOneByIdOrNull(int id, EntityManager em) {
        return null;
    }

    public List<HallEntity> findAllOrNull(EntityManager em) {
        try {
            log.info("Finding all halls...");
            TypedQuery<HallEntity> query = em.createNamedQuery("Hall.findAll", HallEntity.class);
            List<HallEntity> hallList = query.getResultList();
            log.info("Selected all halls.");
            return hallList;
        } catch (Exception e) {
            log.info("Query found no halls to return");
            return null;
        }
    }
}
