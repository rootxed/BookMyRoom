package be.atc.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(name = "Hall.existsWithNameAndBuilding",
                query = "SELECT h FROM HallEntity h WHERE h.name = :name AND h.buildingByBuildingId = :building"),
        @NamedQuery(name = "Hall.findAll",
                query = "SELECT h FROM HallEntity h ORDER BY h.id DESC"),
        @NamedQuery( name = "HallEntity.countByBuilding",
                query = "SELECT COUNT (h) FROM HallEntity h where h.buildingByBuildingId = :building"),
        @NamedQuery( name = "Hall.findCitiesByHallsCaterogy",
                query = "SELECT DISTINCT h.buildingByBuildingId.addresseByAdresseId.cityByCityId FROM HallEntity h JOIN h.hallcategoriesById hc " +
                        "WHERE hc.categoryByCategoryId = :category"),
        @NamedQuery(
                name = "Hall.findByCityAndCategory",
                query = "SELECT h FROM HallEntity h " +
                        "JOIN h.hallcategoriesById hc " +
                        "WHERE hc.categoryByCategoryId = :category " +
                        "AND h.buildingByBuildingId.addresseByAdresseId.cityByCityId = :city"),
        @NamedQuery(name = "Hall.findById", query = "SELECT h FROM HallEntity h WHERE h.id = :id"),
        @NamedQuery(name = "Hall.findByBuilding", query = "SELECT h FROM HallEntity h WHERE h.buildingByBuildingId = :building")

})

@Entity
@Table(name = "hall", schema = "bookmyroom", catalog = "")
public class HallEntity implements Serializable {
    private int id;
    private String name;
    private double hourlyRate;
    private Collection<BookingEntity> bookingsById;
    private BuildingEntity buildingByBuildingId;
    private Collection<HallCategoryEntity> hallcategoriesById;
    private Collection<HallScheduleEntity> hallschedulesById;

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
    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "hourly_rate", nullable = false, precision = 2)
    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
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
    @JoinColumn(name = "building_id", referencedColumnName = "id", nullable = false)
    public BuildingEntity getBuildingByBuildingId() {
        return buildingByBuildingId;
    }

    public void setBuildingByBuildingId(BuildingEntity buildingByBuildingId) {
        this.buildingByBuildingId = buildingByBuildingId;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "hallByHallId", cascade = CascadeType.ALL)
    public Collection<HallCategoryEntity> getHallcategoriesById() {
        return hallcategoriesById;
    }

    public void setHallcategoriesById(Collection<HallCategoryEntity> hallcategoriesById) {
        this.hallcategoriesById = hallcategoriesById;
    }

    @OneToMany(mappedBy = "hallByHallId", cascade = CascadeType.PERSIST)
    public Collection<HallScheduleEntity> getHallschedulesById() {
        return hallschedulesById;
    }

    public void setHallschedulesById(Collection<HallScheduleEntity> hallschedulesById) {
        this.hallschedulesById = hallschedulesById;
    }
}
