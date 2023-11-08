package be.atc.managedBeans;

import be.atc.entities.BookingEntity;
import be.atc.tools.NotificationManager;
import be.atc.tools.TimeSlot;
import org.apache.log4j.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
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


    /**
     * The function generates time slots based on the opening and closing times, and checks for availability based on
     * existing bookings.
     *
     * @param openingTime      The opening time of the hall. It is of type LocalTime, which represents a time without a date.
     * @param closingTime      The closingTime parameter represents the time when the hall or venue closes. It is of type
     *                         LocalTime, which is a class in Java that represents a time without a date or time zone.
     * @param existingBookings A list of BookingEntity objects representing the existing bookings for a hall or venue. Each
     *                         BookingEntity object contains information about the start and end time of the booking.
     */
    public void generateTimeSlots(LocalTime openingTime, LocalTime closingTime, List<BookingEntity> existingBookings) {
        timeSlots.clear();
        LocalTime slotStart = openingTime;
        if (openingTime.equals(closingTime)) {
            //hall is closed
            log.warn("Opening time and closing time are the same. Cannot generate time slots. Hall is closed.");
            return;

        } else {
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

    /**
     * The getTimeSlots() function returns a list of TimeSlot objects.
     *
     * @return A List of TimeSlot objects is being returned.
     */
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

    /**
     * The function returns the selected time range based on the start and end times of the selected slots.
     *
     * @return The method is returning a string representation of the selected time range.
     */
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
