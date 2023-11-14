package be.atc.managedBeans;

import be.atc.entities.CategoryEntity;
import be.atc.entities.HallEntity;
import be.atc.entities.HallScheduleEntity;
import be.atc.entities.OpeningHoursEntity;
import be.atc.services.HallScheduleService;
import be.atc.services.OpeningHoursService;
import be.atc.tools.EMF;
import be.atc.tools.NotificationManager;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    public HallScheduleEntity getSelectedTempSchedule() {
        return selectedTempSchedule;
    }

    public void setSelectedTempSchedule(HallScheduleEntity selectedTempSchedule) {
        this.selectedTempSchedule = selectedTempSchedule;
    }

    private HallScheduleEntity selectedTempSchedule;

    private ScheduleModel scheduleModel;

    private HallEntity selectedHall;

    private List<HallScheduleEntity> sortedDefinitiveSchedules;

    private HallScheduleEntity hallSchedule = new HallScheduleEntity();
    ;

    private boolean initialClosedValue;
    private String editedOpeningTime;
    private String editedClosingTime;

    /**
     * The function initializes a schedule model and adds events to it based on a list of hall schedules.
     */
    public void initScheduleModel() {
        scheduleModel = new DefaultScheduleModel();

        for (HallScheduleEntity schedule : schedules) {
            DefaultScheduleEvent event = new DefaultScheduleEvent();
            // Set event properties (e.g., title, start date, end date, etc.)
            scheduleModel.addEvent(event);
        }
    }


    /**
     * The function retrieves the schedule for a specific hall and returns a list of HallScheduleEntity objects.
     *
     * @param hall The "hall" parameter is an instance of the "HallEntity" class, which represents a specific hall.
     * @return The method is returning a List of HallScheduleEntity objects.
     */
    public List<HallScheduleEntity> getScheduleForHall(HallEntity hall) {
        selectedHall = hall;
        log.info("Getting Schedules for hall : " + hall.getName());
        EntityManager em = EMF.getEM();
        try {
            schedules = hallScheduleService.findAllDefinitiveHallScheduleForHallOrNull(hall, em);
            return schedules;
        } finally {
            em.close();
        }
    }

    /**
     * The function retrieves the temporary schedules for a given hall.
     *
     * @param hall The "hall" parameter is an instance of the "HallEntity" class, which represents a specific hall.
     * @return The method is returning a List of HallScheduleEntity objects.
     */
    public List<HallScheduleEntity> getTemporarySchedulesForHall(HallEntity hall) {
        log.info("Getting temporary Schedules for hall : " + hall.getName());
        EntityManager em = EMF.getEM();
        try {
            tempSchedules = hallScheduleService.findAllNotPassedTemporaryHallScheduleForHallOrNull(hall, em);
            return tempSchedules;
        } finally {
            em.close();
        }
    }

    /**
     * The function takes a HallEntity as input, retrieves schedules for that hall, creates a list of 7 empty
     * HallScheduleEntity objects, and updates the list based on the weekday of each schedule.
     *
     * @param hall The parameter "hall" is an instance of the HallEntity class. It represents a specific hall or venue for
     *             which we want to retrieve the sorted schedules.
     */
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

    /**
     * The function takes an integer representing a day number and returns the corresponding weekday name in French.
     *
     * @param dayNumber The parameter "dayNumber" is an integer representing the day of the week. It should be a number
     *                  between 1 and 7, where 1 represents Monday, 2 represents Tuesday, and so on.
     * @return The method is returning the name of the weekday corresponding to the given day number.
     */
    public String getWeekdayName(int dayNumber) {
        switch (dayNumber) {
            case 1:
                return "Lundi";
            case 2:
                return "Mardi";
            case 3:
                return "Mercredi";
            case 4:
                return "Jeudi";
            case 5:
                return "Vendredi";
            case 6:
                return "Samedi";
            case 7:
                return "Dimanche";
            default:
                return "";
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


    /**
     * The function saves definitive hall schedules by checking if the opening time already exists, setting the schedule as
     * non-temporary, checking if the schedule already exists, ending the old schedule if it exists, and creating a new
     * schedule.
     *
     * @param hallSchedule The parameter "hallSchedule" is an object of type HallScheduleEntity.
     */
    public void saveDefinitiveHallSchedules(HallScheduleEntity hallSchedule) {
        EntityManager em = EMF.getEM();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();


            OpeningHoursEntity existingOpeningHours = openingHoursService.findOpeningHoursByOpeningTimeAndClosingTimeOrNull(hallSchedule.getOpeninghoursByOpeningHoursId(), em);

            if (existingOpeningHours != null) {
                // Si l'opening time existe, l'utiliser
                hallSchedule.setOpeninghoursByOpeningHoursId(existingOpeningHours);
            }

            hallSchedule.setTemporary(false);
            // Vérifier si cet HallSchedule existe déjà
            boolean scheduleAlreadyExist = hallScheduleService.exist(hallSchedule, em);

            if (!scheduleAlreadyExist) {
                // Rajouter la date d'aujourd'hui en date de fin de l'ancien horaire,
                hallScheduleService.endOldHallSchedule(hallSchedule, em);
                em.flush();
                // Créer un nouveau horaire
                hallScheduleService.insert(hallSchedule, em);
            }


            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
        } finally {
            em.clear();
            em.close();
            getTemporarySchedulesForHall(selectedHall);
        }
    }

    /**
     * The function saves a temporary hall schedule by setting the selected hall, checking if the opening time already
     * exists, and inserting the hall schedule into the database.
     */
    public void saveTemporaryHallSchedule() {
        EntityManager em = EMF.getEM();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();

            //Setting the selectedHall
            hallSchedule.setHallByHallId(this.selectedHall);
            log.info("hallid:" + this.selectedHall.getId() + " hallName: " + this.selectedHall.getName());
            log.info("hallid:" + hallSchedule.getHallByHallId().getId() + " hallName: " + hallSchedule.getHallByHallId().getName());

            OpeningHoursEntity existingOpeningHours = openingHoursService.findOpeningHoursByOpeningTimeAndClosingTimeOrNull(hallSchedule.getOpeninghoursByOpeningHoursId(), em);

            if (existingOpeningHours != null) {
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

            //setting temporary
            hallSchedule.setTemporary(true);
            hallScheduleService.insert(hallSchedule, em);
            tx.commit();

        } catch (Exception e) {
            log.warn("Error while saving temporary HallSchedule", e);
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
        } finally {
            em.close();
            getTemporarySchedulesForHall(selectedHall);
        }

    }

    public void deleteTemporarySchedules(){

            log.info("Trying to delete temporary schedules " +selectedTempSchedule.getHallByHallId().getName());
            if (selectedTempSchedule != null) {
                EntityManager em = EMF.getEM();
                EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                HallScheduleEntity scheduleToDelete = hallScheduleService.findOneByIdOrNull(selectedTempSchedule.getId(), em);
                if (scheduleToDelete.isTemporary()) {
                    hallScheduleService.delete(scheduleToDelete, em);
                    tx.commit();
                    NotificationManager.addInfoMessageFromBundleRedirect("notification.schedule.successDelete");
                } else {
                    log.error("Definitive schedule can't be deleted.");
                    NotificationManager.addErrorMessage("notification.schedule.failedDelete");
                }
            } catch (Exception e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                log.error("Error while attempting to delete ", e);
                NotificationManager.addErrorMessage("notification.schedule.failedDelete");
            } finally {
                em.clear();
                em.close();
                tempSchedules = getTemporarySchedulesForHall(selectedHall);
            }
        }else {
                log.error("Error while attempting to delete selected schedule is null");
                NotificationManager.addErrorMessage("notification.schedule.failedDelete");
            }
    }

    /**
     * The function checks if the opening and closing times of a hall schedule are set to midnight.
     *
     * @param schedule The parameter "schedule" is an instance of the HallScheduleEntity class.
     * @return The method is returning a boolean value.
     */
    public boolean isClosed(HallScheduleEntity schedule) {

        LocalTime openingTime = schedule.getOpeninghoursByOpeningHoursId().getOpeningTime();
        LocalTime closingTime = schedule.getOpeninghoursByOpeningHoursId().getClosingTime();

        return openingTime.equals(LocalTime.MIDNIGHT) &&
                closingTime.equals(LocalTime.MIDNIGHT);
    }

    /**
     * The function getIndexForWeekDay takes a weekday as input and returns the corresponding index, throwing an exception
     * if the input is invalid.
     *
     * @param weekDay The weekDay parameter is a short data type representing the day of the week. It is expected to be a
     *                value between 1 and 7, where 1 represents Monday and 7 represents Sunday.
     * @return The method is returning the index for the given week day.
     */
    private int getIndexForWeekDay(short weekDay) {
        int index = weekDay - 1;
        if (index < 0 || index >= 7) {
            log.warn("Index invalide pour le jour de la semaine: " + weekDay);
            throw new RuntimeException("Index or weekday Invalid!");
        }
        return index;
    }

    /**
     * The function initializes a new HallScheduleEntity object and sets its opening hours to a new OpeningHoursEntity
     * object, and logs the initialization and the selected hall name.
     */
    public void openNewHallSchedule() {
        this.hallSchedule = new HallScheduleEntity();
        this.hallSchedule.setOpeninghoursByOpeningHoursId(new OpeningHoursEntity());
        log.info("HallSchedule initialized");
        log.info("hallname=" + selectedHall);
    }

    /**
     * The function "openHallSchedule" sets the selectedHall variable, retrieves sorted schedules for the selected hall,
     * and retrieves temporary schedules for the selected hall.
     *
     * @param hall The "hall" parameter is an instance of the HallEntity class.
     */
    public void openHallSchedule(HallEntity hall) {
        selectedHall = hall;
        getSortedSchedules(selectedHall);
        getTemporarySchedulesForHall(selectedHall);
    }

    /**
     * The function getCurrentDate returns the current date as a LocalDate object in Java.
     *
     * @return The method getCurrentDate() returns the current date as a LocalDate object.
     */
    public LocalDate getCurrentDate() {
        return (LocalDate.now());
    }

    /**
     * The function takes a LocalDate object and returns a formatted string representation of the date in the format
     * "dd/MM/yyyy".
     *
     * @param date The parameter "date" is of type LocalDate, which represents a date without a time component.
     * @return The method is returning a formatted string representation of the given LocalDate object.
     */
    public String formatLocalDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    /**
     * The function "onRowCancel" displays an info message stating that the hall edit has been canceled.
     *
     * @param event The event parameter is an object of type RowEditEvent<HallScheduleEntity>. It represents the event that
     *              occurred when a row edit was canceled.
     */
    public void onRowCancel(RowEditEvent<HallScheduleEntity> event) {
        NotificationManager.addInfoMessage("Hall schedule edit Canceled");
    }

    /**
     * The onRowEdit function logs information about a hall schedule, saves the schedule, and retrieves temporary schedules
     * for the hall.
     *
     * @param event The event parameter is an object of type RowEditEvent. It represents the event that occurred when a row
     *              in a table is edited.
     */
    public void onRowEdit(RowEditEvent event) {
        HallScheduleEntity schedule = (HallScheduleEntity) event.getObject();
        log.info("Hall: " + schedule.getHallByHallId().getName() + " " + schedule.getOpeninghoursByOpeningHoursId().getOpeningTime() + " " + schedule.getOpeninghoursByOpeningHoursId().getClosingTime());
        saveDefinitiveHallSchedules(schedule);
        getTemporarySchedulesForHall(schedule.getHallByHallId());
    }

    /**
     * The function initializes the closed status of a hall schedule entity.
     *
     * @param hallSchedule The parameter "hallSchedule" is an instance of the HallScheduleEntity class.
     */
    public void initializeClosedStatus(HallScheduleEntity hallSchedule) {
        initialClosedValue = isClosed(hallSchedule);
    }

    public void updateInitialClosedValue(boolean value) {
        initialClosedValue = value;
    }


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

    public boolean isInitialClosedValue() {
        return initialClosedValue;
    }

    public void setInitialClosedValue(boolean initialClosedValue) {
        this.initialClosedValue = initialClosedValue;
    }
}
