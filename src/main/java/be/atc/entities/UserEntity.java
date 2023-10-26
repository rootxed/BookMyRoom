package be.atc.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

@NamedQueries( value = {
        @NamedQuery(name = "User.connexion", query = "SELECT u FROM UserEntity u " +
                "WHERE u.userName = :userName and u.password = :password"),
        @NamedQuery(name = "User.findUserByUsernameOrNull", query = "SELECT u FROM UserEntity u " +
                "WHERE u.userName = :userName"),
        @NamedQuery(name = "User.findByIdOrNull", query = "SELECT u FROM UserEntity u " +
                "WHERE u.id = :id")
})

@Entity
@Table(name = "user", schema = "bookmyroom")


public class UserEntity implements Serializable {
    private int id;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private boolean isBlocked;
    private Collection<BookingEntity> bookingsById;
    private Collection<PaymentHistoryEntity> paymenthistoriesById;
    private RoleEntity roleByRoleId;
    private AddresseEntity addresseByAdresseId;

    // Constructeur de copie
    public UserEntity(UserEntity other) {
        this.id = other.id;
        this.userName = other.userName;
        this.password = other.password;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.phoneNumber = other.phoneNumber;
        this.email = other.email;
        this.isBlocked = other.isBlocked;
        this.bookingsById = other.bookingsById;
        this.paymenthistoriesById = other.paymenthistoriesById;
        this.roleByRoleId = other.roleByRoleId;
        this.addresseByAdresseId = other.addresseByAdresseId;
    }

    public UserEntity() {

    }


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "username", nullable = false, length = 100)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Basic
    @Column(name = "password", nullable = false, length = 255)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "firstname", nullable = false, length = 100)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Basic
    @Column(name = "lastname", nullable = false, length = 100)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Basic
    @Column(name = "phone_number", nullable = false, length = 20)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Basic
    @Column(name = "email", nullable = false, length = 100)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "is_blocked", nullable = false)
    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return id == that.id && isBlocked == that.isBlocked && Objects.equals(userName, that.userName) && Objects.equals(password, that.password) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, password, firstName, lastName, phoneNumber, email, isBlocked);
    }

    @OneToMany(mappedBy = "userByUserId")
    public Collection<BookingEntity> getBookingsById() {
        return bookingsById;
    }

    public void setBookingsById(Collection<BookingEntity> bookingsById) {
        this.bookingsById = bookingsById;
    }

    @OneToMany(mappedBy = "userByReceiverUserId")
    public Collection<PaymentHistoryEntity> getPaymenthistoriesById() {
        return paymenthistoriesById;
    }

    public void setPaymenthistoriesById(Collection<PaymentHistoryEntity> paymenthistoriesById) {
        this.paymenthistoriesById = paymenthistoriesById;
    }

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    public RoleEntity getRoleByRoleId() {
        return roleByRoleId;
    }

    public void setRoleByRoleId(RoleEntity roleByRoleId) {
        this.roleByRoleId = roleByRoleId;
    }

    @ManyToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id", nullable = false)
    public AddresseEntity getAddresseByAdresseId() {
        return addresseByAdresseId;
    }

    public void setAddresseByAdresseId(AddresseEntity addresseByAdresseId) {
        this.addresseByAdresseId = addresseByAdresseId;
    }
}
