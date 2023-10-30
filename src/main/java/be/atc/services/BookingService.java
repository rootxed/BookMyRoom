package be.atc.services;

import be.atc.entities.BookingEntity;
import be.atc.entities.BuildingEntity;
import be.atc.entities.HallEntity;
import be.atc.entities.UserEntity;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.ArrayList;
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

    public void cancelBooking(BookingEntity booking, EntityManager em) {
            findOneByIdOrNull(booking.getId(), em);
            if(booking != null){
                booking.setIsCanceled(true);
                update(booking, em);
            }
    }

    public List<BookingEntity> findByBuildingAndDate(BuildingEntity building, LocalDate date, EntityManager em){
        log.info("Getting bookings for building " + building.getName() + " for " + date);
        List<BookingEntity> bookings = new ArrayList<BookingEntity>();
        try{
            bookings = em.createNamedQuery("Booking.findByBuildingAndDate", BookingEntity.class)
                    .setParameter("building", building)
                    .setParameter("date", date)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error While getting Bookings by building and date", e);
        }
        return bookings;
    }

    public List<BookingEntity> findByHallAndDate(HallEntity hall, LocalDate date, EntityManager em){
        log.info("Getting bookings for hall " + hall.getName() + " for " + date);
        List<BookingEntity> bookings = new ArrayList<BookingEntity>();
        try{
            bookings = em.createNamedQuery("Booking.findByHallAndDate", BookingEntity.class)
                    .setParameter("hall", hall)
                    .setParameter("date", date)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error While getting Bookings by hall and date", e);
        }
        return bookings;
    }

    public Collection<BookingEntity> findAllOrNull(EntityManager em) {
        return null;
    }
}
