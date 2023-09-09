package be.atc.entities;

import javax.persistence.*;
import java.sql.Date;
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
                        "AND (hs.endingDate IS NULL)")


})

@Entity
@Table(name = "hallschedule", schema = "bookmyroom", catalog = "")
public class HallScheduleEntity {
    private int id;
    private short weekDay;
    private Date beginningDate;
    private Date endingDate;
    private HallEntity hallByHallId;
    private OpeningHoursEntity openinghoursByOpeningHoursId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "WeekDay", nullable = false)
    public short getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(short weekDay) {
        this.weekDay = weekDay;
    }

    @Basic
    @Column(name = "BeginningDate", nullable = false)
    public Date getBeginningDate() {
        return beginningDate;
    }

    public void setBeginningDate(Date beginningDate) {
        this.beginningDate = beginningDate;
    }

    @Basic
    @Column(name = "EndingDate", nullable = true)
    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HallScheduleEntity that = (HallScheduleEntity) o;
        return id == that.id && weekDay == that.weekDay && Objects.equals(beginningDate, that.beginningDate) && Objects.equals(endingDate, that.endingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, weekDay, beginningDate, endingDate);
    }

    @ManyToOne
    @JoinColumn(name = "HallID", referencedColumnName = "ID", nullable = false)
    public HallEntity getHallByHallId() {
        return hallByHallId;
    }

    public void setHallByHallId(HallEntity hallByHallId) {
        this.hallByHallId = hallByHallId;
    }

    @ManyToOne
    @JoinColumn(name = "OpeningHoursID", referencedColumnName = "ID", nullable = false)
    public OpeningHoursEntity getOpeninghoursByOpeningHoursId() {
        return openinghoursByOpeningHoursId;
    }

    public void setOpeninghoursByOpeningHoursId(OpeningHoursEntity openinghoursByOpeningHoursId) {
        this.openinghoursByOpeningHoursId = openinghoursByOpeningHoursId;
    }
}
