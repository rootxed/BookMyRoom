package be.atc.managedBeans;

import be.atc.entities.*;
import be.atc.services.BookingService;
import be.atc.services.HallScheduleService;
import be.atc.services.HallService;
import be.atc.tools.EMF;
import be.atc.tools.NotificationManager;
import be.atc.tools.TimeSlot;
import org.apache.log4j.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Named
@ViewScoped
public class TimeSlotsBean implements Serializable {

    private Logger log = org.apache.log4j.Logger.getLogger(TimeSlotsBean.class);

    private List<TimeSlot> timeSlots = new ArrayList<>();
    private List<TimeSlot> selectedSlots = new ArrayList<>();


    @Inject
    private HallService hallService;

    @Inject
    private HallScheduleService hallScheduleService;

    @Inject
    private BookingService bookingService;

    private LocalDate selectedDate;
    private HallEntity selectedHall;
    private List<BookingEntity> existingBookings;
    private HallScheduleEntity schedule;
    private CategoryEntity selectedCategory;
    private CityEntity selectedCity;

    public void generateTimeSlots(LocalTime openingTime, LocalTime closingTime) {
        timeSlots.clear();
        LocalTime slotStart = openingTime;

        EntityManager em = EMF.getEM();

        List<BookingEntity> existingBookings = new ArrayList<BookingEntity>();
        try {
            existingBookings = bookingService.findBookingsByDateAndHallOrNull(selectedDate, selectedHall, em);
        } catch (Exception e) {
            log.error("Failed to find booking entity", e);
        }finally {
            em.close();
        }
        if (openingTime.equals(closingTime)) {
            //hall is closed
            log.warn("Opening time and closing time are the same. Cannot generate time slots. Hall is closed.");
            return;

        }else {
            while (!slotStart.isAfter(closingTime.minusHours(1))) {
                LocalTime slotEnd = slotStart.plusHours(1);

                boolean isAvailable = true;

                for (BookingEntity booking : existingBookings) {
                    if ((slotStart.isAfter(booking.getDateTimeIn().toLocalTime()) || slotStart.equals(booking.getDateTimeIn().toLocalTime())) &&
                            (slotEnd.isBefore(booking.getDateTimeOut().toLocalTime()) || slotEnd.equals(booking.getDateTimeOut().toLocalTime()))) {
                        isAvailable = false;
                        break;
                    }
                }

                TimeSlot slot = new TimeSlot(slotStart, slotEnd, isAvailable);
                timeSlots.add(slot);
                slotStart = slotEnd;
            }
        }


    }

    public void validateConsecutiveSlots() {
        if (selectedSlots.size() > 1) {
            Collections.sort(selectedSlots, Comparator.comparing(TimeSlot::getStartTime));

            for (int i = 1; i < selectedSlots.size(); i++) {
                if (!selectedSlots.get(i).getStartTime().equals(selectedSlots.get(i - 1).getEndTime())) {
                    selectedSlots.clear();
                    NotificationManager.addErrorMessage("Error, selection must be consecutive.");
                    return;
                }
            }
        }
    }

    public String getSelectedTimeRange() {
        if (selectedSlots.isEmpty()) {
            return "No slots selected";
        }

        Collections.sort(selectedSlots, Comparator.comparing(TimeSlot::getStartTime));
        LocalTime startTime = selectedSlots.get(0).getStartTime();
        LocalTime endTime = selectedSlots.get(selectedSlots.size() - 1).getEndTime();

        return startTime.toString() + " - " + endTime.toString();
    }

    public void getHallScheduleForDate(){
        EntityManager em = EMF.getEM();
        try{
            schedule = hallScheduleService.findCurrentScheduleForHallOnDate(selectedHall, selectedDate, em);
        }catch (Exception e) {
            log.error("Error while getting HallSchdule for hall " +selectedHall ,e );
        }finally {
            em.close();
        }
    }







}
