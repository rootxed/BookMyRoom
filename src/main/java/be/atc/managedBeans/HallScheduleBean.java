package be.atc.managedBeans;

import be.atc.entities.HallEntity;
import be.atc.entities.HallScheduleEntity;
import be.atc.entities.OpeningHoursEntity;
import be.atc.services.HallScheduleService;
import be.atc.services.OpeningHoursService;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NamedQuery;
import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Named
@ViewScoped
public class HallScheduleBean implements Serializable {

    private Logger log = org.apache.log4j.Logger.getLogger(HallScheduleBean.class);

    @Inject
    private HallScheduleService hallScheduleService;

    @Inject
    private OpeningHoursService openingHoursService;

    private List<HallScheduleEntity> schedules;

    private List<HallScheduleEntity> tempSchedules;

    private ScheduleModel scheduleModel;

    private HallEntity selectedHall;

    private List<HallScheduleEntity> sortedDefinitiveSchedules;

    private HallScheduleEntity hallSchedule;

    @PostConstruct
    public void init() {
        hallSchedule = new HallScheduleEntity();
    }

    public void initScheduleModel() {
        scheduleModel = new DefaultScheduleModel();

        for (HallScheduleEntity schedule : schedules) {
            DefaultScheduleEvent event = new DefaultScheduleEvent();
            // Set event properties (e.g., title, start date, end date, etc.)
            scheduleModel.addEvent(event);
        }
    }


    public List<HallScheduleEntity> getScheduleForHall(HallEntity hall) {
        selectedHall = hall;
        log.info("Getting Schedules for hall : " +hall.getName());
        EntityManager em = EMF.getEM();
        try {
            schedules = hallScheduleService.findAllDefinitiveHallScheduleForHallOrNull(hall, em);
            return schedules;
        } finally {
            em.close();
        }
    }

    public List<HallScheduleEntity> getTemporarySchedulesForHall(HallEntity hall) {
        log.info("Getting temporary Schedules for hall : " +hall.getName());
        EntityManager em = EMF.getEM();
        try {
            tempSchedules = hallScheduleService.findAllNotPassedTemporaryHallScheduleForHallOrNull(hall, em);
            return tempSchedules;
        } finally {
            em.close();
        }
    }

    public void getSortedSchedules(HallEntity hall) {
        log.info("hall: " + hall.getName());
        schedules = getScheduleForHall(hall);

        // Créez une liste de 7 HallScheduleEntity vides.
        List<HallScheduleEntity> fullSchedule = IntStream.rangeClosed(1, 7)
                .mapToObj(i -> {
                    HallScheduleEntity hallSchedule = new HallScheduleEntity();
                    hallSchedule.setWeekDay((short) i);
                    return hallSchedule;
                })
                .collect(Collectors.toList());

        // Parcourez la liste schedules et mettez à jour fullSchedule selon le jour de la semaine.
        for (HallScheduleEntity schedule : schedules) {
            int index = getIndexForWeekDay(schedule.getWeekDay());
            fullSchedule.set(index, schedule);
            log.info("hallschedule: " + fullSchedule.get(index).getWeekDay() + fullSchedule.get(index).getOpeninghoursByOpeningHoursId().getOpeningTime());
        }

        sortedDefinitiveSchedules = fullSchedule;
    }

    public String getWeekdayName(int dayNumber) {
        switch (dayNumber) {
            case 1: return "Lundi";
            case 2: return "Mardi";
            case 3: return "Mercredi";
            case 4: return "Jeudi";
            case 5: return "Vendredi";
            case 6: return "Samedi";
            case 7: return "Dimanche";
            default: return "";
        }
    }


    public HallScheduleEntity getScheduleForDay(int day) {
        if (schedules == null) {
            return new HallScheduleEntity();
        }

        for (HallScheduleEntity schedule : schedules) {
            if (schedule.getWeekDay() == day) {
                return schedule;
            }
        }
        // Si aucun horaire n'est trouvé renvoie vide
        return new HallScheduleEntity();
    }

    public void saveDefinitiveHallSchedules() {
        EntityManager em = EMF.getEM();
        EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();

                for (HallScheduleEntity currentSchedule : schedules) {
                    OpeningHoursEntity existingOpeningHours = openingHoursService.findOpeningHoursByOpeningTimeAndClosingTimeOrNull(currentSchedule.getOpeninghoursByOpeningHoursId(), em);

                    if (existingOpeningHours != null) {
                        // Si l'opening time existe, l'utiliser
                        currentSchedule.setOpeninghoursByOpeningHoursId(existingOpeningHours);
                    }

                    // Vérifier si cet HallSchedule existe déjà
                    boolean scheduleAlreadyExist = hallScheduleService.exist(currentSchedule, em);

                    if (!scheduleAlreadyExist) {
                        // Rajouter la date d'aujourd'hui en date de fin de l'ancien horaire,
                        hallScheduleService.endOldHallSchedule(currentSchedule,em);
                        // Créer un nouveau horaire
                        hallScheduleService.insert(currentSchedule,em);
                    }
                }

                tx.commit();
            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
            } finally {
                em.close();
            }
    }


    public void saveTemporaryHallSchedule(){
        EntityManager em = EMF.getEM();
        EntityTransaction tx = null;

        try{
            tx = em.getTransaction();
            tx.begin();

            //Setting the selectedHall
            hallSchedule.setHallByHallId(this.selectedHall);
            log.info("hallid:"+this.selectedHall.getId() + " hallName: " + this.selectedHall.getName());
            log.info("hallid:"+hallSchedule.getHallByHallId().getId() + " hallName: " + hallSchedule.getHallByHallId().getName());

            OpeningHoursEntity existingOpeningHours = openingHoursService.findOpeningHoursByOpeningTimeAndClosingTimeOrNull(hallSchedule.getOpeninghoursByOpeningHoursId(),em);

            if(existingOpeningHours != null) {
                //Si l'opening time existe, il faut l'utiliser
                hallSchedule.setOpeninghoursByOpeningHoursId(existingOpeningHours);
                //Checking if this HallSchedule alreadyExist
                boolean scheduleAlreadyExist = hallScheduleService.exist(hallSchedule, em);
                if (scheduleAlreadyExist) {
                    log.info("This Schedule already exists !");
                    // Lancer une exception personnalisée ici pour quitter immédiatement le try
                    throw new Exception("This Schedule already exists !");
                }

            }

            hallScheduleService.insert(hallSchedule, em);
            tx.commit();

        }catch (Exception e){
            log.warn("Error while saving temporary HallSchedule",e);
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
        }
        finally {
            em.close();
            getTemporarySchedulesForHall(selectedHall);
        }

    }



    public boolean isClosed(HallScheduleEntity schedule) {

        if (schedule == null || schedule.getOpeninghoursByOpeningHoursId() == null) {
            return true; // Si schedule ou OpeninghoursByOpeningHoursId est null, considérez que l'horaire est fermé
        }

        LocalTime openingTime = schedule.getOpeninghoursByOpeningHoursId().getOpeningTime();
        LocalTime closingTime = schedule.getOpeninghoursByOpeningHoursId().getClosingTime();

        // Si l'openingTime est null, considérez que l'horaire est fermé
        if (openingTime == null || closingTime == null) {
            return true;
        }

        return openingTime.equals(Time.valueOf(LocalTime.MIDNIGHT)) &&
                closingTime.equals(Time.valueOf(LocalTime.MIDNIGHT));
    }

//    public void closeSchedule(HallScheduleEntity hallSchedule) {
//        int index = getIndexForWeekDay(hallSchedule.getWeekDay());
//        sortedDefinitiveSchedules.get(index).getOpeninghoursByOpeningHoursId().setOpeningTime(Time.valueOf(LocalTime.MIDNIGHT));
//        sortedDefinitiveSchedules.get(index).getOpeninghoursByOpeningHoursId().setClosingTime(Time.valueOf(LocalTime.MIDNIGHT));
//    }

    private int getIndexForWeekDay(short weekDay) {
        int index = weekDay - 1;
        if (index < 0 || index >= 7) {
            log.warn("Index invalide pour le jour de la semaine: " + weekDay);
            throw new RuntimeException("Index or weekday Invalid!");
        }
        return index;
    }

    public void openNewHallSchedule(){
        this.hallSchedule = new HallScheduleEntity();
        this.hallSchedule.setOpeninghoursByOpeningHoursId(new OpeningHoursEntity());
        log.info("HallSchedule initialized");
        log.info("hallname="+selectedHall);
    }

    public void openHallSchedule(HallEntity hall){
        selectedHall = hall;
        getSortedSchedules(selectedHall);
        getTemporarySchedulesForHall(selectedHall);
    }

    public LocalDate getCurrentDate(){
        return (LocalDate.now());
    }




//    public void saveOrUpdateSchedules() {
//        EntityManager em = EMF.getEM();
//        try {
//            em.getTransaction().begin();
//
//            for (HallScheduleEntity currentSchedule : schedules) {
//
//                // Vérifier si un OpeningHours avec ces heures existe
//
//                OpeningHoursEntity existingOpeningHours = openingHoursService.findOpeningHoursByOpeningTimeAndClosingTimeOrNull(currentSchedule.getOpeninghoursByOpeningHoursId(), em);
//
//                // Si non, créer et persister le nouvel OpeningHours
//                if (existingOpeningHours == null) {
//                    existingOpeningHours = new OpeningHoursEntity();
//                    existingOpeningHours.setOpeningTime(currentSchedule.getOpeningHoursByOpeningHoursId().getOpeningTime());
//                    existingOpeningHours.setClosingTime(currentSchedule.getOpeningHoursByOpeningHoursId().getClosingTime());
//                    em.persist(existingOpeningHours);
//                }
//
//                // Utilisez l'objet existingOpeningHours pour le currentSchedule
//                currentSchedule.setOpeningHoursByOpeningHoursId(existingOpeningHours);
//
//                HallScheduleEntity existingSchedule = hallScheduleService.findExistingScheduleForDay(currentSchedule.getWeekDay(), em);
//
//                // Si l'horaire n'existe pas du tout, persistez-le simplement
//                if (existingSchedule == null) {
//                    em.persist(currentSchedule);
//                    continue;
//                }
//
//                // Vérifiez si les horaires sont les mêmes
//                boolean isSameSchedule = existingSchedule.getOpeningHoursByOpeningHoursId().equals(currentSchedule.getOpeningHoursByOpeningHoursId());
//
//                if (!isSameSchedule) {
//                    // Mettre à jour la date de fin pour l'horaire existant
//                    existingSchedule.setEndDate(LocalDate.now());
//                    em.merge(existingSchedule);
//
//                    // Créez un nouvel horaire avec une date de début à partir d'aujourd'hui
//                    currentSchedule.setStartDate(LocalDate.now());
//                    em.persist(currentSchedule);
//                }
//            }
//
//            em.getTransaction().commit();
//        } finally {
//            em.close();
//        }
//    }



    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }

    public HallEntity getSelectedHall() {
        return selectedHall;
    }

    public void setSelectedHall(HallEntity selectedHall) {
        this.selectedHall = selectedHall;
    }

    public List<HallScheduleEntity> getSortedDefinitiveSchedules() {
        return sortedDefinitiveSchedules;
    }

    public void setSortedDefinitiveSchedules(List<HallScheduleEntity> sortedDefinitiveSchedules) {
        this.sortedDefinitiveSchedules = sortedDefinitiveSchedules;
    }

    public List<HallScheduleEntity> getTempSchedules() {
        return tempSchedules;
    }

    public void setTempSchedules(List<HallScheduleEntity> tempSchedules) {
        this.tempSchedules = tempSchedules;
    }

    public HallScheduleEntity getHallSchedule() {
        return hallSchedule;
    }

    public void setHallSchedule(HallScheduleEntity hallSchedule) {
        this.hallSchedule = hallSchedule;
    }
}
