package be.atc.services;

import be.atc.entities.HallCategoryEntity;
import be.atc.entities.HallEntity;
import be.atc.entities.HallScheduleEntity;
import be.atc.entities.OpeningHoursEntity;
import org.apache.log4j.Logger;
import org.eclipse.persistence.exceptions.EclipseLinkException;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class HallScheduleService extends ServiceImpl<HallScheduleEntity> {

    private Logger log = org.apache.log4j.Logger.getLogger(HallScheduleService.class);

    public boolean exist(HallScheduleEntity hallSchedule, EntityManager em) {
        log.info("Checking if hallSchedule already exist.");
        return (findHallScheduleByHallAndOpeningHoursOrNull(hallSchedule.getHallByHallId(),hallSchedule.getOpeninghoursByOpeningHoursId(),em) != null);
    }

    public HallScheduleEntity findOneByIdOrNull(int id, EntityManager em) {
        return null;
    }

    public List<HallScheduleEntity> findAllOrNull(EntityManager em) {
        try{
            log.info("Finding all HallSchedule...");
            TypedQuery<HallScheduleEntity> query = em.createNamedQuery("HallSchedule.findAll", HallScheduleEntity.class);
            List<HallScheduleEntity> hallScheduleList = query.getResultList();
            log.info("Find all Hall Schedules");
            return hallScheduleList;
        } catch (NoResultException e) {
            log.info("Query found no HallSchedule to return");
            return null;
        }
    }

    public HallScheduleEntity findHallScheduleByHallAndOpeningHoursOrNull (HallEntity hall, OpeningHoursEntity openingHours, EntityManager em) {
        log.info("Finding HallSchedule for hall : " +hall.getName()+ " and opening time: " + openingHours.getOpeningTime() +"-"+openingHours.getClosingTime());

        try{
            return  em.createNamedQuery("HallSchedule.findByHallAndOpeningHours", HallScheduleEntity.class)
                    .setParameter("hall", hall)
                    .setParameter("openingHours", openingHours)
                    .getSingleResult();
        }catch (NoResultException e) {
            log.info("Query found no HallSchedule to return.");
            return null;
        }
    }

    public List<HallScheduleEntity> findAllHallScheduleForHallOrNull (){
        return null;
    }

    public List<HallScheduleEntity> findAllCurrentAndFutureScheduleForHallOrNull(HallEntity hall, EntityManager em){
        TypedQuery <HallScheduleEntity> query = em.createNamedQuery("HallSchedule.findCurrentAndFutureSchedulesForHall", HallScheduleEntity.class)
                .setParameter("hall", hall)
                .setParameter("currentDate", new Date());
        return query.getResultList();
    }

    public List<HallScheduleEntity> findAllDefinitiveHallScheduleForHallOrNull(HallEntity hall, EntityManager em){
        try{
            TypedQuery <HallScheduleEntity> query = em.createNamedQuery("HallSchedule.findDefinitiveScheduleForHall", HallScheduleEntity.class)
                    .setParameter("hall", hall);
            return query.getResultList();
        } catch (NoResultException e)  {
            log.info("Query found no Definitive HallSchedule to return.");
            return null;
        }
    }

    public void endOldHallSchedule (HallScheduleEntity hallSchedule, EntityManager em){
        //Mettre fin à l'ancien horaire
        HallScheduleEntity existingSchedule = findHallScheduleByHallAndOpeningHoursOrNull(hallSchedule.getHallByHallId(), hallSchedule.getOpeninghoursByOpeningHoursId(), em);

        if (existingSchedule == null) {
            log.warn("No existing schedule found to end.");
            return;
        }

        Date currentDate = new Date();


    }

    public void addDefaultHallSchedule(HallEntity hall, EntityManager em){

    }

    public List<HallScheduleEntity> findAllActiveOrFutureTemporaryHallScheduleForHallOrNull(){
        return null;
    }



    public List<HallScheduleEntity> findActualHallScheduleForHallOrNull (){
        return null;
    }
}
