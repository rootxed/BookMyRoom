package be.atc.managedBeans;

import be.atc.entities.*;
import be.atc.services.BookingService;
import be.atc.services.BuildingService;
import be.atc.services.HallService;
import be.atc.tools.EMF;
import be.atc.tools.UserSessionUtil;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class BookingHistoryBean implements Serializable {

    //TODO getter pour peupler la dt pour eviter loop

    private Logger log = org.apache.log4j.Logger.getLogger(BookingHistoryBean.class);

    @Inject
    private BookingService bookingService;

    @Inject
    private UserSessionUtil userSessionUtil;

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

    private LocalDate selectedDate = LocalDate.now();

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

    public List<BookingEntity> loadUserBookings() {

        log.info("Loading user bookings");
        // getting current user
        UserEntity currentUser = userSessionUtil.getCurrentUser();
        List<BookingEntity> userBookings = new ArrayList<BookingEntity>();
        EntityManager em = EMF.getEM();
        try {
            userBookings = bookingService.findBookingsByUser(currentUser, em);
        }
        catch (Exception e) {
            log.error("error getting userBookings ",e);
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
        return userBookings;

    }

    public List<BookingEntity> loadBookings() {
        List<BookingEntity> bookings = new ArrayList<BookingEntity>();
        EntityManager em = EMF.getEM();
        try{
            if (selectedHall != null && selectedDate != null){
                bookings = bookingService.findByHallAndDate(selectedHall, selectedDate, em);
            }else if (selectedBuilding != null && selectedDate != null){
                bookings = bookingService.findByBuildingAndDate(selectedBuilding, selectedDate, em);
            }
        }catch (Exception e){
            log.error("Error while loading bookings ",e);
        }finally {
            em.close();
        }
        return bookings;
    }

    public String getBookingStatus(BookingEntity booking) {
        LocalDateTime now = LocalDateTime.now();
        if(booking.getIsCanceled()){
            return "CANCELED";
        }
        else if (booking.getDateTimeOut().isAfter(now)) {
            return "UPCOMING";
        }
        else {
            return "PASSED";
        }
    }

    public List<BookingEntity> getBookingsByHallAndDate(HallEntity hall, LocalDate date) {
        List<BookingEntity> bookings = new ArrayList<BookingEntity>();
        EntityManager em = EMF.getEM();
        try{
            bookings = bookingService.findByHallAndDate(hall, date, em);
        } catch (Exception e){
            log.error("Error getting bookings", e);
        }finally {
            em.close();
        }
        return bookings;
    }

    public List<BookingEntity> getBookingsByBuildingAndDate(BuildingEntity building, LocalDate date) {
        List<BookingEntity> bookings = new ArrayList<BookingEntity>();
        EntityManager em = EMF.getEM();
        try{
            bookings = bookingService.findByBuildingAndDate(building, date, em);
        } catch (Exception e){
            log.error("Error getting bookings", e);
        }finally {
            em.close();
        }
        return bookings;
    }

}
