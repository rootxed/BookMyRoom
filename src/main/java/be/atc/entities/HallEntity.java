package be.atc.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(name = "Hall.existsWithNameAndBuilding",
                query = "SELECT h FROM HallEntity h WHERE h.name = :name AND h.buildingByBuildingId = :building"),
        @NamedQuery(name = "Hall.findAll", query = "SELECT h FROM HallEntity h")
})

@Entity
@Table(name = "hall", schema = "bookmyroom", catalog = "")
public class HallEntity implements Serializable {
    private int id;
    private String name;
    private BigDecimal hourlyRate;
    private Collection<BookingEntity> bookingsById;
    private BuildingEntity buildingByBuildingId;
    private Collection<HallCategoryEntity> hallcategoriesById;
    private Collection<HallScheduleEntity> hallschedulesById;

    @Id
    @Column(name = "ID", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "HourlyRate", nullable = false, precision = 2)
    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HallEntity that = (HallEntity) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(hourlyRate, that.hourlyRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, hourlyRate);
    }

    @OneToMany(mappedBy = "hallByHallId")
    public Collection<BookingEntity> getBookingsById() {
        return bookingsById;
    }

    public void setBookingsById(Collection<BookingEntity> bookingsById) {
        this.bookingsById = bookingsById;
    }

    @ManyToOne
    @JoinColumn(name = "BuildingID", referencedColumnName = "ID", nullable = false)
    public BuildingEntity getBuildingByBuildingId() {
        return buildingByBuildingId;
    }

    public void setBuildingByBuildingId(BuildingEntity buildingByBuildingId) {
        this.buildingByBuildingId = buildingByBuildingId;
    }

    @OneToMany(mappedBy = "hallByHallId")
    public Collection<HallCategoryEntity> getHallcategoriesById() {
        return hallcategoriesById;
    }

    public void setHallcategoriesById(Collection<HallCategoryEntity> hallcategoriesById) {
        this.hallcategoriesById = hallcategoriesById;
    }

    @OneToMany(mappedBy = "hallByHallId")
    public Collection<HallScheduleEntity> getHallschedulesById() {
        return hallschedulesById;
    }

    public void setHallschedulesById(Collection<HallScheduleEntity> hallschedulesById) {
        this.hallschedulesById = hallschedulesById;
    }
}
