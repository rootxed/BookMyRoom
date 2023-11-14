package be.atc.managedBeans;

import be.atc.entities.*;
import be.atc.services.BookingService;
import be.atc.tools.EMF;
import be.atc.tools.UserSessionUtil;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Named
@ViewScoped
public class BookingHistoryBean implements Serializable {

    //TODO getter pour peupler la dt pour eviter loop

    private Logger log = org.apache.log4j.Logger.getLogger(BookingHistoryBean.class);

    @Inject
    private BookingService bookingService;

    @Inject
    private UserSessionUtil userSessionUtil;

    public BookingHistoryBean() {
    }

    public CityEntity getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(CityEntity selectedCity) {
        this.selectedCity = selectedCity;
    }

    public BuildingEntity getSelectedBuilding() {
        return selectedBuilding;
    }

    public void setSelectedBuilding(BuildingEntity selectedBuilding) {
        this.selectedBuilding = selectedBuilding;
    }

    public HallEntity getSelectedHall() {
        return selectedHall;
    }

    public void setSelectedHall(HallEntity selectedHall) {
        this.selectedHall = selectedHall;
    }


    private CityEntity selectedCity;

    private BuildingEntity selectedBuilding;

    private HallEntity selectedHall;

    public BookingEntity getSelectedBooking() {
        return selectedBooking;
    }

    public void setSelectedBooking(BookingEntity selectedBooking) {
        this.selectedBooking = selectedBooking;
    }

    private BookingEntity selectedBooking;

    private List<BookingEntity> userBookings;

    public List<BookingEntity> getFilteredBookings() {
        return filteredBookings;
    }

    public void setFilteredBookings(List<BookingEntity> filteredBookings) {
        this.filteredBookings = filteredBookings;
    }

    private List<BookingEntity> filteredBookings;

    private LocalDate selectedDate = LocalDate.now();

    /**
     * The `init()` function is annotated with `@PostConstruct` and initializes the `selectedDate` variable with the
     * current date.
     */
    @PostConstruct
    public void init() {
        selectedDate = LocalDate.now();
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    public List<BookingEntity> getBookings() {
        bookings = loadBookings();
        return bookings;
    }

    /**
     * The function sets the list of bookings for a specific entity.
     *
     * @param bookings The "bookings" parameter is a List of BookingEntity objects.
     */
    public void setBookings(List<BookingEntity> bookings) {
        this.bookings = bookings;
    }

    private List<BookingEntity> bookings;

    public List<BookingEntity> getUserBookings() {
        if (userBookings == null) {
            userBookings = loadUserBookings();
        }
        return userBookings;
    }

    public void onDateSelect(SelectEvent<LocalDate> event) {
        selectedDate = event.getObject();
    }

    /**
     * The function "loadUserBookings" retrieves a list of booking entities associated with the current user.
     *
     * @return The method is returning a List of BookingEntity objects.
     */
    public List<BookingEntity> loadUserBookings() {

        log.info("Loading user bookings");
        // getting current user
        UserEntity currentUser = userSessionUtil.getCurrentUser();
        List<BookingEntity> userBookings = new ArrayList<BookingEntity>();
        EntityManager em = EMF.getEM();
        try {
            userBookings = bookingService.findBookingsByUser(currentUser, em);
        } catch (Exception e) {
            log.error("error getting userBookings ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return userBookings;

    }

    /**
     * The function loads bookings based on selected hall and date or selected building and date.
     *
     * @return The method is returning a List of BookingEntity objects.
     */
    public List<BookingEntity> loadBookings() {
        List<BookingEntity> bookings = new ArrayList<BookingEntity>();
        EntityManager em = EMF.getEM();
        try {
            if (selectedHall != null && selectedDate != null) {
                bookings = bookingService.findByHallAndDate(selectedHall, selectedDate, em);
            } else if (selectedBuilding != null && selectedDate != null) {
                bookings = bookingService.findByBuildingAndDate(selectedBuilding, selectedDate, em);
            }
        } catch (Exception e) {
            log.error("Error while loading bookings ", e);
        } finally {
            em.close();
        }
        return bookings;
    }

    /**
     * The function returns the booking status based on the current time and the booking entity's properties.
     *
     * @param booking The "booking" parameter is an instance of the BookingEntity class, which represents a booking made by
     *                a user. It contains information such as the booking's cancellation status, date and time of check-out, and other
     *                relevant details.
     * @return The method is returning a String value, which represents the status of a booking.
     */
    public String getBookingStatus(BookingEntity booking) {
        LocalDateTime now = LocalDateTime.now();
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "bundle");
        String status;
        if (booking.getIsCanceled()) {
            status = "CANCELED";
        } else if (booking.getDateTimeOut().isAfter(now)) {
            status = "UPCOMING";
        } else {
            status = "PASSED";
        }
        return (status);
    }

    /**
     * The function returns a style class based on the status of a booking entity.
     *
     * @param booking The parameter "booking" is an instance of the BookingEntity class, which represents a booking made by
     *                a user.
     * @return The method is returning a String value. The specific String value that is being returned depends on the
     * conditions in the if-else statements. It could be "canceled", "upcoming", or "passed".
     */
    public String getBookingStatusStyleClass(BookingEntity booking) {
        if (booking.getIsCanceled()) {
            return "canceled";
        } else if (booking.getDateTimeOut().isAfter(LocalDateTime.now())) {
            return "upcoming";
        } else {
            return "passed";
        }
    }

    public List<BookingEntity> getBookingsByHallAndDate(HallEntity hall, LocalDate date) {
        List<BookingEntity> bookings = new ArrayList<BookingEntity>();
        EntityManager em = EMF.getEM();
        try {
            bookings = bookingService.findByHallAndDate(hall, date, em);
        } catch (Exception e) {
            log.error("Error getting bookings", e);
        } finally {
            em.close();
        }
        return bookings;
    }

    public List<BookingEntity> getBookingsByBuildingAndDate(BuildingEntity building, LocalDate date) {
        List<BookingEntity> bookings = new ArrayList<BookingEntity>();
        EntityManager em = EMF.getEM();
        try {
            bookings = bookingService.findByBuildingAndDate(building, date, em);
        } catch (Exception e) {
            log.error("Error getting bookings", e);
        } finally {
            em.close();
        }
        return bookings;
    }

}
