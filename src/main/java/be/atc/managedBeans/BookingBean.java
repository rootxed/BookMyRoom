package be.atc.managedBeans;

import be.atc.entities.*;
import be.atc.services.BookingService;
import be.atc.services.HallScheduleService;
import be.atc.services.HallService;
import be.atc.services.UserService;
import be.atc.tools.AsyncMailer;
import be.atc.tools.EMF;
import be.atc.tools.NotificationManager;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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

    private LocalDate todayDate = LocalDate.now();

    public LocalDate getTodayDate() {
        return todayDate;
    }

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

    @Inject
    private TimeSlotsBean timeSlotBean;


    /**
     * The function "onCategoryChange" resets the selected city and hall variables and retrieves cities based on the
     * selected category.
     */
    public void onCategoryChange() {
        selectedCity = null;
        selectedHall = null;
        getCitiesByCategory();
    }

    /**
     * The function "onCitySelect" sets the selectedHall variable to null and then calls the "getHallsByCityAndCategory"
     * function.
     */
    public void onCitySelect() {
        selectedHall = null;
        getHallsByCityAndCategory();
    }


    /**
     * The function checks if a hall and date are selected, and if so, fetches the hall schedule and generates time slots
     * based on the opening and closing times and existing bookings.
     */
    public void onSelection() {
        if (selectedHall != null && selectedDate != null) {

            getHallScheduleForDate();
            List<BookingEntity> existingBookings = new ArrayList<BookingEntity>();
            existingBookings = getExistingBookings();
            timeSlotBean.generateTimeSlots(schedule.getOpeninghoursByOpeningHoursId().getOpeningTime(), schedule.getOpeninghoursByOpeningHoursId().getClosingTime(), existingBookings);
        } else {
            log.info("selectedHall and/or date is null. Cannot fetch schedule.");
        }

    }

    /**
     * The function retrieves existing bookings based on a selected date and hall, and returns an empty list if an
     * exception occurs.
     *
     * @return The method is returning a List of BookingEntity objects.
     */
    public List<BookingEntity> getExistingBookings() {
        EntityManager em = EMF.getEM();
        try {
            return bookingService.findBookingsByDateAndHallOrNull(selectedDate, selectedHall, em);
        } catch (Exception e) {
            log.error("Failed to find booking entity", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    /**
     * The function retrieves the schedule for a specific hall on a given date.
     */
    public void getHallScheduleForDate() {
        EntityManager em = EMF.getEM();
        try {
            schedule = hallScheduleService.findCurrentScheduleForHallOnDate(selectedHall, selectedDate, em);
        } catch (Exception e) {
            log.error("Error while getting HallSchdule for hall " + selectedHall, e);
        } finally {
            em.close();
        }
    }

    /**
     * The function creates a new booking by retrieving the logged-in user, creating a new BookingEntity object, inserting
     * it into the database, storing the booking ID in the session, sending a confirmation email to the user, and returning
     * the appropriate outcome.
     *
     * @return The method is returning a String value. The possible return values are "error",
     * "confirmation?faces-redirect=true", and "book?faces-redirect=true".
     */
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
            bookingToInsert.setDateTimeIn(selectedDate.atTime(timeSlotBean.getSelectedSlots().get(0).getStartTime()));
            bookingToInsert.setDateTimeOut(selectedDate.atTime(timeSlotBean.getSelectedSlots().get(timeSlotBean.getSelectedSlots().size() - 1).getEndTime()));
            bookingToInsert.setUserByUserId(loggedInUser);
            bookingToInsert.setTotalPrice(calculateTotalPrice());


            bookingService.insert(bookingToInsert, em);

            tx.commit();


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
            NotificationManager.addErrorMessageFromBundle("notification.booking.failure");
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return "book?faces-redirect=true";
    }

    /**
     * The function calculates the total price based on the selected time slots and the hourly rate of the selected hall.
     *
     * @return The method is returning a double value, which is the total price calculated based on the selected time slots
     * and the hourly rate of the selected hall.
     */
    public double calculateTotalPrice() {
        if (timeSlotBean.getSelectedSlots().isEmpty() || selectedHall == null) {
            return 0;
        }
        //As slot is for 1h, we calculate from the size of the selectedSlots
        int hours = timeSlotBean.getSelectedSlots().size();

        double totalPrice = selectedHall.getHourlyRate() * hours;
        return totalPrice;

    }

    /**
     * The function retrieves halls that match a selected category and city, or sets the halls variable to null if either
     * the selected category or city is null.
     */
    public void getHallsByCityAndCategory() {
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

    /**
     * The function retrieves cities that have a hall with a specific category.
     */
    public void getCitiesByCategory() {
        EntityManager em = EMF.getEM();
        try {
            if (selectedCategory != null) {
                log.info("Getting cities that have a hall with category " + selectedCategory);
                cities = hallService.findCitiesWithHallsForCategoryOrNull(selectedCategory, em);
            } else {
                log.info("selectedCategory is Null");
                cities = null;
            }
        } catch (Exception e) {
            log.error("Error while searching for cities by hall categories");

        } finally {
            em.close();
        }
    }

    /**
     * The function cancels a booking by using an EntityManager to begin a transaction, calling the cancelBooking method
     * from the bookingService, and committing the transaction.
     *
     * @param booking The "booking" parameter is an instance of the BookingEntity class, which represents a booking made by
     *                a user.
     */
    public void cancelBooking(BookingEntity booking) {

        EntityManager em = EMF.getEM();
        EntityTransaction tx = null;
        try {
            log.info("cancelling booking " + booking.getId());
            tx = em.getTransaction();
            tx.begin();
            bookingService.cancelBooking(booking, em);
            tx.commit();
        } catch (Exception e) {
            log.error("Error while canceling booking.", e);
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
        } finally {
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
