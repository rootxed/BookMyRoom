package be.atc.managedBeans;

import be.atc.entities.BookingEntity;
import be.atc.entities.PaymentHistoryEntity;
import be.atc.services.PaymentHistoryService;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.io.Serializable;

@Named
@ViewScoped
public class PayementHistoryBean implements Serializable {

    private Logger log = org.apache.log4j.Logger.getLogger(PayementHistoryBean.class);

    @Inject
    private PaymentHistoryService paymentHistoryService;

    //TODO CREATE A PAYEMENTHistory

    //TODO SEARCH PAYEMENT HISTORY
    public PaymentHistoryEntity getPaymentHistory(BookingEntity booking){

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
        return paymentHistory;
    }

}
