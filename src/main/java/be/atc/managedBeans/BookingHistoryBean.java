package be.atc.managedBeans;

import be.atc.entities.BookingEntity;
import be.atc.entities.UserEntity;
import be.atc.services.BookingService;
import be.atc.services.HallService;
import be.atc.tools.EMF;
import be.atc.tools.UserSessionUtil;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class BookingHistoryBean implements Serializable {

    private Logger log = org.apache.log4j.Logger.getLogger(BookingHistoryBean.class);

    @Inject
    private BookingService bookingService;

    @Inject
    private UserSessionUtil userSessionUtil;

    private List<BookingEntity> userBookings;

    @PostConstruct
    public void init() {
        userBookings = loadUserBookings();
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

    public List<BookingEntity> getUserBookings() {
        return userBookings;
    }

    public void setUserBookings(List<BookingEntity> userBookings) {
        this.userBookings = userBookings;
    }
}
