package be.atc.managedBeans;

import be.atc.entities.BookingEntity;
import be.atc.services.BookingService;
import be.atc.tools.EMF;
import be.atc.tools.NotificationManager;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Named
@ViewScoped
public class ConfirmationBean implements Serializable {

    private Logger log = org.apache.log4j.Logger.getLogger(ConfirmationBean.class);

    private BookingEntity booking;

    @Inject
    private BookingService bookingService;

    @PostConstruct
    public void init() {
        loadBooking();
    }

    private String loadBooking(){
        // Getting the booking id from the session to avoid booking ID manipulation
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        Integer bookingId = (Integer) session.getAttribute("bookingId");
        log.info("Got the booking id from the session: " + bookingId);

        if(bookingId != null) {
            //Get the booking from the database
            EntityManager em = EMF.getEM();
            try{

                booking = bookingService.findOneByIdOrNull(bookingId,em);
            } catch (Exception e) {
                log.error("Error while getting booking " + e);
                NotificationManager.addErrorMessage("ERROR WHILE GETTING BOOKING");
            }finally {
                em.close();
            }

            if(booking != null) {
                //Checking if the booking was made by the logged in user
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

                if(!currentUser.getPrincipal().equals(booking.getUserByUserId().getUserName())) {
                    //User is not allowed to see the booking
                    booking = null;
                    NotificationManager.addErrorMessage("ERROR YOU DON'T HAVE ACCESS TO THIS BOOKING");
                }
            } else{
                NotificationManager.addErrorMessage("ERROR BOOKING NOT FOUND");
            }

            session.removeAttribute("bookingId");
            return "Success";
        }else{
            return "book?faces-redirect=true";
        }
        //SI L'ID est null, il n'y a pas de booking rediriger vers page de booking ?

    }

    public BookingEntity getBooking() {
        return booking;
    }
}
