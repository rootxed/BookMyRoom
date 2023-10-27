package be.atc.managedBeans;

import be.atc.entities.*;
import be.atc.services.BookingService;
import be.atc.services.HallScheduleService;
import be.atc.services.HallService;
import be.atc.tools.EMF;
import be.atc.tools.NotificationManager;
import be.atc.tools.TimeSlot;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

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

    public List<TimeSlot> getSelectedSlots() {
        return selectedSlots;
    }

    public void setSelectedSlots(List<TimeSlot> selectedSlots) {
        this.selectedSlots = selectedSlots;
    }

    private List<TimeSlot> selectedSlots = new ArrayList<>();


    public void generateTimeSlots(LocalTime openingTime, LocalTime closingTime, List<BookingEntity> existingBookings) {
        timeSlots.clear();
        LocalTime slotStart = openingTime;
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

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
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

}
