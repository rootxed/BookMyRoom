package be.atc.services;

import be.atc.entities.CategoryEntity;
import be.atc.entities.CityEntity;
import be.atc.entities.RoleEntity;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.List;

@ApplicationScoped
public class CityService {

    private Logger log = org.apache.log4j.Logger.getLogger(CityService.class);

    public CityEntity findCityByIdOrNull(int cityId, EntityManager em) {
        log.info("Finding city by ID: " + cityId);

        try {
            return em.find(CityEntity.class, cityId);
        } catch (NoResultException e) {
            log.info("No city found for the given ID", e);
            return null;
        }
    }

    public List<CityEntity> findCitiesByPostalCodeOrNull(String postalCode, EntityManager em) {
        try {
            return em.createNamedQuery("City.findCitiesByPostalCode", CityEntity.class)
                    .setParameter("postalCode", postalCode +"%")
                    .setMaxResults(10)
                    .getResultList();
        } catch (NoResultException e) {
            log.info("No cities found for the given postal code: " + postalCode, e);
            return null;
        }
    }

    public List<CityEntity> findCitiesByNameOrNull(String cityName, EntityManager em) {
        try {
            return em.createNamedQuery("City.findCitiesByName", CityEntity.class)
                    .setParameter("cityName", cityName + "%")
                    .setMaxResults(10)
                    .getResultList();
        } catch (NoResultException e) {
            log.info("No cities found for the given city name: " + cityName, e);
            return null;
        }
    }

}
