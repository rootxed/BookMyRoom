package be.atc.managedBeans;

import be.atc.entities.*;
import be.atc.services.BookingService;
import be.atc.services.HallScheduleService;
import be.atc.services.HallService;
import be.atc.services.UserService;
import be.atc.tools.AsyncMailer;
import be.atc.tools.EMF;
import be.atc.tools.NotificationManager;
import be.atc.tools.TimeSlot;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Named
@ViewScoped
public class BookingBean implements Serializable {

    private Logger log = org.apache.log4j.Logger.getLogger(BookingBean.class);

    private CategoryEntity selectedCategory;

    private CityEntity selectedCity;

    private List<CityEntity> cities;

    private HallEntity selectedHall;

    private List<HallEntity> halls;

    private List<HallScheduleEntity> schedules;

    private HallScheduleEntity schedule;

    private LocalDate selectedDate;

    private BookingEntity bookingEntity;

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public List<TimeSlot> getSelectedSlots() {
        return selectedSlots;
    }

    public void setSelectedSlots(List<TimeSlot> selectedSlots) {
        this.selectedSlots = selectedSlots;
    }

    private List<TimeSlot> timeSlots = new ArrayList<>();
    private List<TimeSlot> selectedSlots;

    @Inject
    private HallService hallService;

    @Inject
    private HallScheduleService hallScheduleService;

    @Inject
    private UserService userService;

    @Inject
    private BookingService bookingService;

    @Inject
    private AsyncMailer asyncMailer;



    public void onCategoryChange() {
        getCitiesByCategory();
    }

    public void onCitySelect(){
        getHallsByCityAndCategory();
    }

    public void testListener(){
        log.info("Listener OK");
    }

    public void onHallSelect(){

    }
     public void onDateSelect(){
         if (selectedHall == null) {
             log.warn("selectedHall is null. Cannot fetch schedule.");
             return;
         }
         getHallScheduleForDate();
         generateTimeSlots(schedule.getOpeninghoursByOpeningHoursId().getOpeningTime(),schedule.getOpeninghoursByOpeningHoursId().getClosingTime());
     }

    public void generateTimeSlots(LocalTime openingTime, LocalTime closingTime) {
        timeSlots.clear();
        LocalTime slotStart = openingTime;

        EntityManager em = EMF.getEM();

        List<BookingEntity> existingBookings = new ArrayList<BookingEntity>();
        try {
            existingBookings = bookingService.findBookingsByDateAndHallOrNull(selectedDate, selectedHall, em);
        } catch (Exception e) {
            log.error("Failed to find booking entity", e);
        }finally {
            em.close();
        }
        if (openingTime.equals(closingTime)) {
            //hall is closed
            log.warn("Opening time and closing time are the same. Cannot generate time slots. Hall is closed.");
            return;

        }else {
            while (!slotStart.isAfter(closingTime.minusHours(1))) {
                LocalTime slotEnd = slotStart.plusHours(1);

                boolean isAvailable = true;

                for (BookingEntity booking : existingBookings) {
                    if ((slotStart.isAfter(booking.getDateTimeIn().toLocalTime()) || slotStart.equals(booking.getDateTimeIn().toLocalTime())) &&
                            (slotEnd.isBefore(booking.getDateTimeOut().toLocalTime()) || slotEnd.equals(booking.getDateTimeOut().toLocalTime()))) {
                        isAvailable = false;
                        break;
                    }
                }

                TimeSlot slot = new TimeSlot(slotStart, slotEnd, isAvailable);
                timeSlots.add(slot);
                slotStart = slotEnd;
            }
        }


    }

    public void onRowSelect(SelectEvent event) {
        TimeSlot slot = (TimeSlot) event.getObject();
        if(slot != null && !selectedSlots.contains(slot)) {
            selectedSlots.add(slot);
        }
    }

    public void onRowUnselect(UnselectEvent event) {
        TimeSlot slot = (TimeSlot) event.getObject();
        selectedSlots.remove(slot);
    }

    public void validateConsecutiveSlots() {
        if (selectedSlots.size() > 1) {
            Collections.sort(selectedSlots, Comparator.comparing(TimeSlot::getStartTime));

            for (int i = 1; i < selectedSlots.size(); i++) {
                if (!selectedSlots.get(i).getStartTime().equals(selectedSlots.get(i - 1).getEndTime())) {
                    selectedSlots.clear();
                    NotificationManager.addErrorMessage("Error, selection must be consecutive.");
                    return;
                }
            }
        }
    }

    public String getSelectedTimeRange() {
        if (selectedSlots.isEmpty()) {
            return "No slots selected";
        }

        Collections.sort(selectedSlots, Comparator.comparing(TimeSlot::getStartTime));
        LocalTime startTime = selectedSlots.get(0).getStartTime();
        LocalTime endTime = selectedSlots.get(selectedSlots.size() - 1).getEndTime();

        return startTime.toString() + " - " + endTime.toString();
    }

     public void getHallScheduleForDate(){
         EntityManager em = EMF.getEM();
         try{
             schedule = hallScheduleService.findCurrentScheduleForHallOnDate(selectedHall, selectedDate, em);
         }catch (Exception e) {
             log.error("Error while getting HallSchdule for hall " +selectedHall ,e );
         }finally {
             em.close();
         }
     }

    public void getHallSchedule(){
        EntityManager em = EMF.getEM();
        try{
            schedules = hallScheduleService.findAllCurrentAndFutureScheduleForHallOrNull(selectedHall, em);
        }catch (Exception e) {
            log.error("Error while getting HallSchdule for hall " +selectedHall );
        }finally {
            em.close();
        }
    }

    public void getBookingsForHallandDate(LocalDate date){

    }

    public String createBooking() {
        log.info("creating new booking");
        // Getting the logged user
        Subject currentUser = null;
        try {
            log.info("Trying to get current user");
            currentUser = SecurityUtils.getSubject();
            log.info("Got the current user: " + currentUser);
            log.info("User Principal: " + currentUser.getPrincipal());
        } catch (Exception e) {
            log.error("An error occurred while getting the current user.", e);
        }

        if (currentUser != null) {
            if (!currentUser.isAuthenticated()) {
                log.warn("User is not logged in. Cannot create a booking.");
                // Handle error in case the user is no more logged in
                // Maybe redirect to login page or show an error message
                return "error";
            }
        }

        EntityManager em = EMF.getEM();
        EntityTransaction tx = null;
        try {
            log.info("trying to create a new booking");
            tx = em.getTransaction();
            tx.begin();

            // Getting the user from DB
            UserEntity loggedInUser = userService.findUserByUsernameOrNull(em, currentUser.getPrincipal().toString());

            if (loggedInUser == null) {
                // Handle error if the logged-in user is not found in the database
                return "error";
            }

            // Create new BookingEntity
            BookingEntity bookingToInsert = new BookingEntity();
            bookingToInsert.setUserByUserId(loggedInUser);


            bookingToInsert.setHallByHallId(selectedHall);
            bookingToInsert.setDateTimeIn(selectedDate.atTime(selectedSlots.get(0).getStartTime()));
            bookingToInsert.setDateTimeOut(selectedDate.atTime(selectedSlots.get(selectedSlots.size() - 1).getEndTime()));
            bookingToInsert.setUserByUserId(loggedInUser);
            bookingToInsert.setTotalPrice(calculateTotalPrice());


            bookingService.insert(bookingToInsert, em);

            tx.commit();

            NotificationManager.addInfoMessage("Booking created successfully");
            // Maybe redirect to a confirmation page or show a success message

            // Store the booking id in the session
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
            session.setAttribute("bookingId", bookingToInsert.getId());

            // Send a confirmation email to the user
            asyncMailer.sendBookingConfirmationAsync(bookingToInsert);


            return "confirmation?faces-redirect=true";

        } catch (Exception e) {
            log.error("An error occurred while creating a new booking", e);
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            // Handle the exception, maybe log it or show an error message
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return "book?faces-redirect=true";
    }

    public double calculateTotalPrice(){
        if (selectedSlots.isEmpty() || selectedHall == null) {
            return 0;
        }
        //As slot is for 1h, we calculate from the size of the selectedSlots
        int hours = selectedSlots.size();

        double totalPrice = selectedHall.getHourlyRate() * hours;
        return totalPrice;

    }

    public void getHallsByCityAndCategory(){
        EntityManager em = EMF.getEM();
        try {
            if (selectedCategory != null && selectedCity != null) {
                log.info("Getting halls that match the category " + selectedCategory + " and city " + selectedCity);
                halls = hallService.findHallsByCityAndCategoryOrNull(selectedCity, selectedCategory, em);
            } else {
                log.info("selectedCategory or selectedCity is Null");
                halls = null;
            }
        } catch (Exception e) {
            log.error("Error while searching for halls by city and category", e);
        } finally {
            em.close();
        }
    }

    public void getCitiesByCategory(){
        EntityManager em = EMF.getEM();
        try {
            if(selectedCategory != null) {
                log.info("Getting cities that have a hall with category " +selectedCategory);
                cities = hallService.findCitiesWithHallsForCategoryOrNull(selectedCategory, em);
            } else {
                log.info("selectedCategory is Null");
                cities = null;
            }
        }catch (Exception e){
            log.error("Error while searching for cities by hall categories");

        }finally {
            em.close();
        }
    }


    public CategoryEntity getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(CategoryEntity selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public List<CityEntity> getCities() {
        return cities;
    }

    public void setCities(List<CityEntity> cities) {
        this.cities = cities;
    }

    public CityEntity getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(CityEntity selectedCity) {
        this.selectedCity = selectedCity;
    }

    public HallEntity getSelectedHall() {
        return selectedHall;
    }

    public void setSelectedHall(HallEntity selectedHall) {
        this.selectedHall = selectedHall;
    }

    public List<HallEntity> getHalls() {
        return halls;
    }

    public void setHalls(List<HallEntity> halls) {
        this.halls = halls;
    }

    public HallScheduleEntity getSchedule() {
        return schedule;
    }

    public void setSchedule(HallScheduleEntity schedule) {
        this.schedule = schedule;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }
}
