package be.atc.services;

import be.atc.entities.*;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collection;

@ApplicationScoped
public class PaymentHistoryService extends ServiceImpl<PaymentHistoryEntity> {

    private Logger log = org.apache.log4j.Logger.getLogger(PaymentHistoryService.class);

    public PaymentHistoryEntity findOneByBookingOrNull(BookingEntity booking, EntityManager em) {
        log.info("Find payment history by booking");
        try{
            return em.createNamedQuery("PaymentHistory.findOneByBooking", PaymentHistoryEntity.class)
                    .setParameter("booking", booking)
                    .getSingleResult();
        }catch (NoResultException e) {
            log.info("No payment history found for booking");
            return null;
        }
    }

    public boolean exist(PaymentHistoryEntity paymentHistory, EntityManager em) {
        PaymentHistoryEntity p = findOneByBookingOrNull(paymentHistory.getBookingByBookingId(), em);
        if (p != null) {
            return true;
        }else{
            return false;
        }
    }
    public PaymentHistoryEntity findOneByIdOrNull(int id, EntityManager em) {
        return null;
    }

    public Collection<PaymentHistoryEntity> findAllOrNull(EntityManager em) {
        return null;
    }



}
