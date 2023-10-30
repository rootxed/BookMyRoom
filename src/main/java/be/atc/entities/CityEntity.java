package be.atc.entities;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(name = "City.findCitiesByPostalCode",
                query = "SELECT c FROM CityEntity c WHERE c.postalCode  LIKE :postalCode ORDER BY c.postalCode ASC"),
        @NamedQuery(name = "City.findCitiesByName",
                query = "SELECT c FROM CityEntity c WHERE c.name LIKE :cityName ORDER BY c.name ASC"),
        @NamedQuery(name = "City.findCitiesWithHallsByName",
                query = "SELECT DISTINCT c FROM CityEntity c JOIN c.addressesById a JOIN a.buildingsById b WHERE c.name LIKE :cityName ORDER BY c.name ASC")
})
@Entity
@Table(name = "city", schema = "bookmyroom", catalog = "")
public class CityEntity {
    private int id;
    private String name;
    private String postalCode;
    private Collection<AddresseEntity> addressesById;
    private CountryEntity countryByCountryId;

    @Id
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
    @Column(name = "postal_code", nullable = false, length = 10)
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityEntity that = (CityEntity) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(postalCode, that.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, postalCode);
    }

    @OneToMany(mappedBy = "cityByCityId")
    public Collection<AddresseEntity> getAddressesById() {
        return addressesById;
    }

    public void setAddressesById(Collection<AddresseEntity> addressesById) {
        this.addressesById = addressesById;
    }

    @ManyToOne
    @JoinColumn(name = "country_id", referencedColumnName = "id", nullable = false)
    public CountryEntity getCountryByCountryId() {
        return countryByCountryId;
    }

    public void setCountryByCountryId(CountryEntity countryByCountryId) {
        this.countryByCountryId = countryByCountryId;
    }
}
