package be.atc.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
@NamedQueries( value = {
        @NamedQuery(name = "Building.findOneByName", query = "SELECT b FROM BuildingEntity b WHERE LOWER(b.name) = LOWER(:name)"),
        @NamedQuery(name = "Building.findAll", query = "SELECT b FROM BuildingEntity b")
})

@Entity
@Table(name = "building", schema = "bookmyroom", catalog = "")
public class BuildingEntity implements Serializable {
    private int id;
    private String name;
    private AddresseEntity addresseByAdresseId;
    private Collection<HallEntity> hallsById;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildingEntity that = (BuildingEntity) o;
        return id == that.id && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @ManyToOne
    @JoinColumn(name = "AdresseID", referencedColumnName = "ID", nullable = false)
    public AddresseEntity getAddresseByAdresseId() {
        return addresseByAdresseId;
    }

    public void setAddresseByAdresseId(AddresseEntity addresseByAdresseId) {
        this.addresseByAdresseId = addresseByAdresseId;
    }

    @OneToMany(mappedBy = "buildingByBuildingId")
    public Collection<HallEntity> getHallsById() {
        return hallsById;
    }

    public void setHallsById(Collection<HallEntity> hallsById) {
        this.hallsById = hallsById;
    }
}
