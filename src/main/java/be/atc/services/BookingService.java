package be.atc.services;

import be.atc.entities.BookingEntity;
import be.atc.entities.HallEntity;
import be.atc.entities.UserEntity;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class BookingService extends  ServiceImpl<BookingEntity> {

    private Logger log = org.apache.log4j.Logger.getLogger(BookingService.class);

    public List<BookingEntity> findBookingsByDateAndHallOrNull(LocalDate date, HallEntity hall, EntityManager em){
        List<BookingEntity> results = em.createNamedQuery("Booking.findBookingsByDateAndHall", BookingEntity.class)
                .setParameter("hall", hall)
                .setParameter("date", date)
                .getResultList();
        if (results.isEmpty()) {
            log.info("No Booking found");
        }

        return results;
    }

    public List<BookingEntity> findBookingsByUser(UserEntity user, EntityManager em){
        List<BookingEntity> results = em.createNamedQuery("Booking.findBookingsByUser", BookingEntity.class)
                .setParameter("user", user)
                .getResultList();
        if (results.isEmpty()) {
            log.info("No Booking found for user " + user.getId());
        }

        return results;
    }

    public boolean exist(BookingEntity bookingEntity, EntityManager em) {
        return false;
    }

    public BookingEntity findOneByIdOrNull(int id, EntityManager em) {
        try{
            return  em.createNamedQuery("Booking.findById", BookingEntity.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }catch (NoResultException e) {
            return null;
        }
    }

    public Collection<BookingEntity> findAllOrNull(EntityManager em) {
        return null;
    }
}
