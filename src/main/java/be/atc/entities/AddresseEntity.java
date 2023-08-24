package be.atc.entities;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(name = "Addresse.findAdresseById", query = "SELECT a FROM AddresseEntity a WHERE a.id = :id"),
        @NamedQuery(name = "Addresse.findAddresseByFullInfo", query = "SELECT a FROM AddresseEntity a WHERE a.addressLine = :addressLine AND a.cityByCityId.id = :cityId"),
        @NamedQuery(name = "Addresse.findAll", query = "SELECT a FROM AddresseEntity a"),

})
@Entity
@Table(name = "addresse", schema = "bookmyroom", catalog = "")
public class AddresseEntity {
    private int id;
    private String addressLine;
    private CityEntity cityByCityId;
    private Collection<BuildingEntity> buildingsById;
    private Collection<UserEntity> usersById;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "AddressLine", nullable = false, length = 255)
    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddresseEntity that = (AddresseEntity) o;
        return id == that.id && Objects.equals(addressLine, that.addressLine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, addressLine);
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CityID", referencedColumnName = "ID", nullable = false)
    public CityEntity getCityByCityId() {
        return cityByCityId;
    }

    public void setCityByCityId(CityEntity cityByCityId) {
        this.cityByCityId = cityByCityId;
    }

    @OneToMany(mappedBy = "addresseByAdresseId")
    public Collection<BuildingEntity> getBuildingsById() {
        return buildingsById;
    }

    public void setBuildingsById(Collection<BuildingEntity> buildingsById) {
        this.buildingsById = buildingsById;
    }

    @OneToMany(mappedBy = "addresseByAdresseId")
    public Collection<UserEntity> getUsersById() {
        return usersById;
    }

    public void setUsersById(Collection<UserEntity> usersById) {
        this.usersById = usersById;
    }
}
