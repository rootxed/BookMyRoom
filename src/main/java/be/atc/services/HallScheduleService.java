package be.atc.services;

import be.atc.entities.HallEntity;
import be.atc.entities.HallScheduleEntity;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class HallScheduleService extends ServiceImpl<HallScheduleEntity> {

    private Logger log = org.apache.log4j.Logger.getLogger(HallScheduleService.class);

    public boolean exist(HallScheduleEntity hallSchedule, EntityManager em) {
        log.info("Checking if hallSchedule already exist.");
        return (findHallScheduleByHallScheduleOrNull(hallSchedule, em) != null);
    }

    public HallScheduleEntity findOneByIdOrNull(int id, EntityManager em) {
        try {
            return em.createNamedQuery("HallSchedule.findById", HallScheduleEntity.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.info("Query found no HallSchedule to return.");
            return null;
        }
    }

    public List<HallScheduleEntity> findAllOrNull(EntityManager em) {
        try {
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

    public HallScheduleEntity findHallScheduleByHallScheduleOrNull(HallScheduleEntity hallSchedule, EntityManager em) {
        log.info("Finding HallSchedule by hall schedule");

        try {
            return em.createNamedQuery("HallSchedule.findByHallSchedule", HallScheduleEntity.class)
                    .setParameter("weekDay", hallSchedule.getWeekDay())
                    .setParameter("beginningDate", hallSchedule.getBeginningDate())
                    .setParameter("endingDate", hallSchedule.getEndingDate())
                    .setParameter("hall", hallSchedule.getHallByHallId())
                    .setParameter("openingHours", hallSchedule.getOpeninghoursByOpeningHoursId())
                    .setParameter("isTemporary", hallSchedule.isTemporary())
                    .getSingleResult();
        } catch (NoResultException e) {
            log.info("Query found no HallSchedule to return.");
            return null;
        }
    }

    public List<HallScheduleEntity> findAllHallScheduleForHallOrNull() {
        return null;
    }

    public List<HallScheduleEntity> findAllCurrentAndFutureScheduleForHallOrNull(HallEntity hall, EntityManager em) {
        TypedQuery<HallScheduleEntity> query = em.createNamedQuery("HallSchedule.findCurrentAndFutureSchedulesForHall", HallScheduleEntity.class)
                .setParameter("hall", hall)
                .setParameter("currentDate", new Date());
        return query.getResultList();
    }

    public HallScheduleEntity findCurrentScheduleForHallOnDate(HallEntity hall, LocalDate date, EntityManager em) {
        short dayOfWeek = (short) date.getDayOfWeek().getValue();
        log.info("Finding Current schedule for day " + dayOfWeek + " hall " + hall.getId() + " and date " + date);

        try {
            // Recherchez d'abord les horaires temporaires pour cette date
            TypedQuery<HallScheduleEntity> tempQuery = em.createNamedQuery("HallSchedule.findTemporaryForHallAndDate", HallScheduleEntity.class);
            tempQuery.setParameter("hall", hall);
            tempQuery.setParameter("date", date);
            tempQuery.setParameter("weekDay", dayOfWeek);
            HallScheduleEntity temporarySchedule = tempQuery.getSingleResult();

            if (temporarySchedule != null) {
                log.info("Found a temporary schedule");
                return temporarySchedule;  // utilisez l'horaire temporaire trouvé
            }
        } catch (NoResultException e) {
            log.info("No Temporary Schedule found");
        }

        try {
            // Sinon, utilisez l'horaire régulier pour le jour de la semaine
            TypedQuery<HallScheduleEntity> regularQuery = em.createNamedQuery("HallSchedule.findRegularForHallAndDate", HallScheduleEntity.class);
            regularQuery.setParameter("hall", hall);
            regularQuery.setParameter("weekDay", dayOfWeek);
            regularQuery.setParameter("date", date);
            HallScheduleEntity regularSchedule = regularQuery.getSingleResult();

            if (regularSchedule != null) {
                log.info("Found a regular schedule");
                return regularSchedule;
            }
        } catch (NoResultException e) {
            log.info("No Definitive Schedule found");
        }

        return null;

    }

    public List<HallScheduleEntity> findAllDefinitiveHallScheduleForHallOrNull(HallEntity hall, EntityManager em) {
        try {
            TypedQuery<HallScheduleEntity> query = em.createNamedQuery("HallSchedule.findDefinitiveScheduleForHall", HallScheduleEntity.class)
                    .setParameter("hall", hall)
                    .setParameter("isTemporary", false);
            return query.getResultList();
        } catch (NoResultException e) {
            log.info("Query found no Definitive HallSchedule to return.");
            return null;
        }
    }

    public List<HallScheduleEntity> findAllNotPassedTemporaryHallScheduleForHallOrNull(HallEntity hall, EntityManager em) {
        LocalDate currentDate = LocalDate.now();
        try {
            TypedQuery<HallScheduleEntity> query = em.createNamedQuery("HallSchedule.findNotPassedTempScheduleForHall", HallScheduleEntity.class)
                    .setParameter("hall", hall)
                    .setParameter("currentDate", currentDate);
            ;
            return query.getResultList();
        } catch (NoResultException e) {
            log.info("Query found no Definitive HallSchedule to return.");
            return null;
        }

    }

    public void endOldHallSchedule(HallScheduleEntity hallSchedule, EntityManager em) {

        LocalDate today = LocalDate.now();

        //Find all hall definitive hallschedule for this hall and weekday with beginigdate after hallschedule beginingdate or endingdate =null
        TypedQuery<HallScheduleEntity> allSchedulesQuery = em.createNamedQuery("HallSchedule.findAllCurrentAndFutureDefinitiveSchedules", HallScheduleEntity.class)
                .setParameter("hall", hallSchedule.getHallByHallId())
                .setParameter("weekDay", hallSchedule.getWeekDay())
                .setParameter("isTemporary", false)
                .setParameter("todayDate", today);

        List<HallScheduleEntity> schedulesToEdit = allSchedulesQuery.getResultList();

        for (HallScheduleEntity schedule : schedulesToEdit) {

            if (schedule.getBeginningDate().isAfter(hallSchedule.getBeginningDate()) ||
                    schedule.getBeginningDate().isEqual(hallSchedule.getBeginningDate())) {
                // Delete the schedule if the beginning date is after or equal to the new beginning date."
                delete(schedule, em);

            } else if (schedule.getEndingDate() == null) {
                // update avec nouvelle date de début si null
                // Update the end date if it is null.

                schedule.setEndingDate(hallSchedule.getBeginningDate().minusDays(1));
                update(schedule, em);
            } else {
                // update avec la date de début new en date de fin
                if ((schedule.getEndingDate().isAfter(hallSchedule.getBeginningDate()) || schedule.getEndingDate().isEqual(hallSchedule.getBeginningDate()))
                        && (schedule.getBeginningDate().isBefore(hallSchedule.getBeginningDate()) || schedule.getBeginningDate().isEqual(hallSchedule.getBeginningDate()))) {
                    schedule.setEndingDate(hallSchedule.getBeginningDate().minusDays(1));
                    update(schedule, em);
                }
            }

        }

    }

}
