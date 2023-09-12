package be.atc.entities;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;
import java.time.LocalTime;

@NamedQueries({
        @NamedQuery(name = "OpeningHours.findByOpeningTimeAndClosingTime",
                query = "SELECT oh FROM OpeningHoursEntity oh " +
                        "WHERE oh.openingTime = :openingTime " +
                        "AND oh.closingTime = :closingTime")


})

@Entity
@Table(name = "openinghours", schema = "bookmyroom", catalog = "")
public class OpeningHoursEntity {
    private int id;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Collection<HallScheduleEntity> hallschedulesById;

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
    @Column(name = "OpeningTime", nullable = false)
    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    @Basic
    @Column(name = "ClosingTime", nullable = false)
    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpeningHoursEntity that = (OpeningHoursEntity) o;
        return id == that.id && Objects.equals(openingTime, that.openingTime) && Objects.equals(closingTime, that.closingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, openingTime, closingTime);
    }

    @OneToMany(mappedBy = "openinghoursByOpeningHoursId")
    public Collection<HallScheduleEntity> getHallschedulesById() {
        return hallschedulesById;
    }

    public void setHallschedulesById(Collection<HallScheduleEntity> hallschedulesById) {
        this.hallschedulesById = hallschedulesById;
    }
}
