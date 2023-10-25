package be.atc.tools;

import java.time.LocalTime;
import java.util.Objects;

public class TimeSlot {
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;

    public TimeSlot(LocalTime startTime, LocalTime endTime, boolean available) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean getIsAvailable() {
        return available;
    }

    @Override
    public String toString() {
        return startTime.toString() + "-" + endTime.toString();
    }
}