package be.atc.services;

import be.atc.entities.*;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.sql.Time;
import java.time.LocalDate;
import java.sql.Date;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class HallService extends ServiceImpl<HallEntity> {

    @Inject
    private HallScheduleService hallScheduleService;

    @Inject
    private OpeningHoursService openingHoursService;

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
                    .setParameter("name", name)
                    .setParameter("building", building)
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
        log.info("Finding hall By Id " + id);
        try {
            return em.createNamedQuery("Hall.findById", HallEntity.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<HallEntity> findAllOrNull(EntityManager em) {
        try {
            log.info("Finding all halls...");
            TypedQuery<HallEntity> query = em.createNamedQuery("Hall.findAll", HallEntity.class);
            List<HallEntity> hallList = query.getResultList();
            log.info("Selected all halls.");
            return hallList;
        } catch (NoResultException e) {
            log.info("Query found no halls to return");
            return null;
        }
    }

    public List<HallEntity> findByBuilding(BuildingEntity building, EntityManager em) {
        try {
            log.info("Finding halls by building...");
            TypedQuery<HallEntity> query = em.createNamedQuery("Hall.findByBuilding", HallEntity.class)
                    .setParameter("building", building);
            List<HallEntity> hallList = query.getResultList();
            log.info("Selected all halls.");
            return hallList;
        } catch (NoResultException e) {
            log.info("Query found no halls to return");
            return null;
        }
    }

    public void createHallWithDefaultSchedules(HallEntity hall, EntityManager em) {

        Collection<HallScheduleEntity> schedules = new ArrayList<>();

        //Getting the date of the day
        LocalDate todayDate = LocalDate.now();

        //Setting Default OpeningTime And Closing time 00:00 for closed by default
        OpeningHoursEntity newOpeningHour = new OpeningHoursEntity();
        newOpeningHour.setOpeningTime(LocalTime.MIDNIGHT);
        newOpeningHour.setClosingTime(LocalTime.MIDNIGHT);

        OpeningHoursEntity defaultOpeningHour = openingHoursService.findOrCreateOpeningHour(newOpeningHour, em);

        // Starting to persist the Hall
        //insert(hall, em);

        // Create a default schedule for all days of the week with the default opening time and today starting date
        for (short day = 1; day <= 7; day++) {

            HallScheduleEntity schedule = new HallScheduleEntity();
            schedule.setWeekDay(day);
            schedule.setHallByHallId(hall);
            schedule.setOpeninghoursByOpeningHoursId(defaultOpeningHour);
            schedule.setBeginningDate(todayDate);
            schedule.setTemporary(false);
            schedules.add(schedule);
            //hallScheduleService.insert(schedule, em);
            //hall.getHallschedulesById().add(schedule);

        }
        hall.setHallschedulesById(schedules);
        insert(hall, em);
    }

    public List<CityEntity> findCitiesWithHallsForCategoryOrNull(CategoryEntity category, EntityManager em) {
        try {
            return em.createNamedQuery("Hall.findCitiesByHallsCaterogy", CityEntity.class)
                    .setParameter("category", category)
                    .getResultList();
        } catch (NoResultException e) {
            log.info("No cities found for the given hall category: " + category.getName(), e);
            return null;
        }
    }

    public List<HallEntity> findHallsByCityAndCategoryOrNull(CityEntity city, CategoryEntity category, EntityManager em){
        try {
            return em.createNamedQuery("Hall.findByCityAndCategory", HallEntity.class)
                    .setParameter("city", city)
                    .setParameter("category", category)
                    .getResultList();
        } catch (NoResultException e) {
            log.info("No hall found for the given city an categories");
            return null;
        }
    }
}
