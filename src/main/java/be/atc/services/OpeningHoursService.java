package be.atc.services;


import be.atc.entities.BuildingEntity;
import be.atc.entities.HallEntity;
import be.atc.entities.OpeningHoursEntity;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collection;

@ApplicationScoped
public class OpeningHoursService extends ServiceImpl<OpeningHoursEntity> {

    private Logger log = org.apache.log4j.Logger.getLogger(HallService.class);

    public boolean exist(OpeningHoursEntity openingHoursEntity, EntityManager em) {
        return false;
    }

    public OpeningHoursEntity findOpeningHoursByOpeningTimeAndClosingTimeOrNull(OpeningHoursEntity openingHours, EntityManager em){
        log.info("Finding openingHours by OpeningTime and ClosingTime");
        try{
            return em.createNamedQuery("OpeningHours.findByOpeningTimeAndClosingTime",OpeningHoursEntity.class)
                    .setParameter("openingTime", openingHours.getOpeningTime())
                    .setParameter("closingTime", openingHours.getClosingTime())
                    .getSingleResult();
        }catch (NoResultException e) {
            log.info("Query found no OpeningHours to return.");
            return null;
        }

    }

    public OpeningHoursEntity findOrCreateOpeningHour(OpeningHoursEntity openingHours , EntityManager em) {
        // Essayez de trouver l'heure d'ouverture existante
        OpeningHoursEntity existingOpeningHour = findOpeningHoursByOpeningTimeAndClosingTimeOrNull(openingHours, em);

        if (existingOpeningHour == null) {
            existingOpeningHour = openingHours;
            insert(openingHours, em);
        }

        return existingOpeningHour;
    }



    public OpeningHoursEntity findOneByIdOrNull(int id, EntityManager em) {
        return null;
    }


    public Collection<OpeningHoursEntity> findAllOrNull(EntityManager em) {
        return null;
    }
}
