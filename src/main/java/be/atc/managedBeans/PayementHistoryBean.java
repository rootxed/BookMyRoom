package be.atc.managedBeans;

import be.atc.entities.BookingEntity;
import be.atc.entities.PaymentHistoryEntity;
import be.atc.entities.UserEntity;
import be.atc.services.BookingService;
import be.atc.services.PaymentHistoryService;
import be.atc.services.UserService;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Named
@ViewScoped
public class PayementHistoryBean implements Serializable {

    private Logger log = org.apache.log4j.Logger.getLogger(PayementHistoryBean.class);

    public PaymentHistoryEntity getPaymentHistory() {
        return paymentHistory;
    }

    public void setPaymentHistory(PaymentHistoryEntity paymentHistory) {
        this.paymentHistory = paymentHistory;
    }

    private PaymentHistoryEntity paymentHistory = new PaymentHistoryEntity();

    private PaymentHistoryEntity selectedPaymentHistory;

    private BookingEntity selectedBooking;

    public BookingEntity getSelectedBooking() {
        return selectedBooking;
    }

    public void setSelectedBooking(BookingEntity selectedBooking) {
        this.selectedBooking = selectedBooking;
    }

    @Inject
    private PaymentHistoryService paymentHistoryService;

    @Inject
    private UserService userService;

    /**
     * The function creates a payment history record for a booking, associating it with the logged-in user.
     *
     * @return The method is returning a String value. The possible return values are "error", "success", or "failure".
     */
    public String createPaymentHistory(){
        log.info("Attempting to create a payment history...");
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

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
                log.warn("User is not logged in. Cannot create a paymentHistory.");
                // Handle error in case the user is no more logged in
                // Maybe redirect to login page or show an error message
                return "error";
            }
        }

        EntityManager em = EMF.getEM();
        EntityTransaction tx = null;

        try{
            boolean alreadyExist = paymentHistoryService.exist(paymentHistory, em);
            if(alreadyExist){
                throw new RuntimeException("Can't create a payment history, a payment history already exist for this booking");
            }

            // Getting the user from DB
            UserEntity loggedInUser = userService.findUserByUsernameOrNull(em, currentUser.getPrincipal().toString());

            if (loggedInUser == null) {
                // Handle error if the logged-in user is not found in the database
                return "error";
            }

            tx = em.getTransaction();
            log.info("Begin transaction to persist a new payment history");
            tx.begin();

            paymentHistory.setTimeStamp(timestamp);
            paymentHistory.setUserByReceiverUserId(loggedInUser);
            paymentHistory.setBookingByBookingId(selectedBooking);
            paymentHistoryService.insert(paymentHistory,em);

            tx.commit();
            log.info("Transaction committed, a new payment history is available for this booking");
            return "success";
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            log.error("Failed to create payment history", e);
            return "failure";
        } finally {
            em.clear();
            em.close();
        }
    }

    /**
     * The function retrieves the payment history status for a given booking entity.
     *
     * @param booking The "booking" parameter is an instance of the "BookingEntity" class, which represents a booking made
     * by a user.
     * @return The method is returning a PaymentHistoryEntity object.
     */
    public PaymentHistoryEntity getPaymentHistoryStatus(BookingEntity booking){
        selectedPaymentHistory = null;
        EntityManager em = EMF.getEM();
        PaymentHistoryEntity paymentHistory = new PaymentHistoryEntity();
        try {
            paymentHistory = paymentHistoryService.findOneByBookingOrNull(booking, em);
        } catch (Exception e) {
            log.error("An error occurred finding PaymentHistory.");
        } finally {
            em.clear();
            em.close();
        }
        this.selectedPaymentHistory = paymentHistory;
        return paymentHistory;
    }

    public PaymentHistoryEntity getSelectedPaymentHistory() {
        return selectedPaymentHistory;
    }

    public void setSelectedPaymentHistory(PaymentHistoryEntity selectedPaymentHistory) {
        this.selectedPaymentHistory = selectedPaymentHistory;
    }
}
