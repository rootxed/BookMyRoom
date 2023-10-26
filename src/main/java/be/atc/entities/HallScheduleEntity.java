package be.atc.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(name = "HallSchedule.findAll",
                query = "SELECT hs FROM HallScheduleEntity hs"),
        @NamedQuery(name = "HallSchedule.findByHallAndOpeningHours",
                query = "SELECT hs FROM HallScheduleEntity hs " +
                        "WHERE hs.hallByHallId = :hall " +
                        "AND hs.openinghoursByOpeningHoursId = :openingHours"),
        @NamedQuery(name = "HallSchedule.findCurrentAndFutureSchedulesForHall",
                query = "SELECT hs FROM HallScheduleEntity hs " +
                        "WHERE hs.hallByHallId = :hall " +
                        "AND (hs.endingDate IS NULL OR hs.endingDate >= :currentDate)"),
        @NamedQuery(name = "HallSchedule.findDefinitiveScheduleForHall",
                query = "SELECT hs FROM HallScheduleEntity hs " +
                        "WHERE hs.hallByHallId = :hall " +
                        "AND hs.temporary = :isTemporary "+
                        "AND (hs.endingDate IS NULL) "),
        @NamedQuery(name = "HallSchedule.findNotPassedTempScheduleForHall",
                query = "SELECT hs FROM HallScheduleEntity hs " +
                        "WHERE hs.hallByHallId = :hall " +
                        "AND (hs.endingDate >= :currentDate)"),
        @NamedQuery(name = "HallSchedule.findByHallSchedule",
                query = "SELECT hs FROM HallScheduleEntity hs " +
                        "WHERE hs.weekDay = :weekDay "+
                        "AND hs.weekDay = :weekDay "+
                        "AND hs.beginningDate = :beginningDate "+
                        "AND hs.endingDate = :endingDate "+
                        "AND hs.hallByHallId = :hall "+
                        "AND hs.openinghoursByOpeningHoursId = :openingHours "+
                        "AND hs.temporary = :isTemporary"),
        @NamedQuery(name = "HallSchedule.findActualDefinitiveHallSchedule",
                query = "SELECT hs FROM HallScheduleEntity hs " +
                        "WHERE hs.weekDay = :weekDay "+
                        "AND hs.hallByHallId = :hall "+
                        "AND hs.temporary = :isTemporary " +
                        "AND hs.endingDate = null"),
        @NamedQuery(name = "HallSchedule.findAllDefinitiveHallSchedulesAfterDate",
                query = "SELECT hs FROM HallScheduleEntity hs " +
                        "WHERE hs.weekDay = :weekDay "+
                        "AND hs.hallByHallId = :hall "+
                        "AND hs.temporary = :isTemporary " +
                        "AND hs.beginningDate >= :newBeginningDate"),
        @NamedQuery(name = "HallSchedule.findAllDefinitiveHallSchedulesToEdit",
                query = "SELECT hs FROM HallScheduleEntity hs " +
                        "WHERE hs.weekDay = :weekDay "+
                        "AND hs.hallByHallId = :hall "+
                        "AND hs.temporary = :isTemporary " +
                        "AND (hs.beginningDate >= :newBeginningDate OR hs.endingDate IS NULL)"),
        @NamedQuery(name = "HallSchedule.findAllCurrentAndFutureDefinitiveSchedules",
                query = "SELECT hs FROM HallScheduleEntity hs " +
                        "WHERE hs.weekDay = :weekDay "+
                        "AND hs.hallByHallId = :hall "+
                        "AND hs.temporary = :isTemporary " +
                        "AND (hs.endingDate >= :todayDate OR hs.endingDate IS NULL)"),
        @NamedQuery(
                name = "HallSchedule.findTemporaryForHallAndDate",
                query = "SELECT hs FROM HallScheduleEntity hs WHERE hs.hallByHallId = :hall AND hs.weekDay = :weekDay AND hs.beginningDate <= :date AND hs.endingDate >= :date AND hs.temporary = true"
        ),
        @NamedQuery(
                name = "HallSchedule.findRegularForHallAndDate",
                query = "SELECT hs FROM HallScheduleEntity hs WHERE hs.hallByHallId = :hall AND hs.weekDay = :weekDay AND hs.beginningDate <= :date AND (hs.endingDate IS NULL OR hs.endingDate >= :date)  "
        )






})

@Entity
@Table(name = "hallschedule", schema = "bookmyroom", catalog = "")
public class HallScheduleEntity {
    private int id;
    private short weekDay;
    private LocalDate beginningDate;
    private LocalDate endingDate;
    private HallEntity hallByHallId;
    private OpeningHoursEntity openinghoursByOpeningHoursId;
    private boolean isTemporary;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "week_day", nullable = false)
    public short getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(short weekDay) {
        this.weekDay = weekDay;
    }

    @Basic
    @Column(name = "beginning_date", nullable = false)
    public LocalDate getBeginningDate() {
        return beginningDate;
    }

    public void setBeginningDate(LocalDate beginningDate) {
        this.beginningDate = beginningDate;
    }

    @Basic
    @Column(name = "ending_date", nullable = true)
    public LocalDate getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDate endingDate) {
        this.endingDate = endingDate;
    }

    @Basic
    @Column(name = "is_temporary", nullable = false)
    public boolean isTemporary() {
        return isTemporary;
    }

    public void setTemporary(boolean temporary) {
        isTemporary = temporary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HallScheduleEntity that = (HallScheduleEntity) o;
        return id == that.id && isTemporary == that.isTemporary && weekDay == that.weekDay && Objects.equals(beginningDate, that.beginningDate) && Objects.equals(endingDate, that.endingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, weekDay, beginningDate, endingDate, isTemporary);
    }

    @ManyToOne
    @JoinColumn(name = "hall_id", referencedColumnName = "id", nullable = false)
    public HallEntity getHallByHallId() {
        return hallByHallId;
    }

    public void setHallByHallId(HallEntity hallByHallId) {
        this.hallByHallId = hallByHallId;
    }

    @ManyToOne (cascade = CascadeType.PERSIST)
    @JoinColumn(name = "opening_hours_id", referencedColumnName = "id", nullable = false)
    public OpeningHoursEntity getOpeninghoursByOpeningHoursId() {
        return openinghoursByOpeningHoursId;
    }

    public void setOpeninghoursByOpeningHoursId(OpeningHoursEntity openinghoursByOpeningHoursId) {
        this.openinghoursByOpeningHoursId = openinghoursByOpeningHoursId;
    }
}
