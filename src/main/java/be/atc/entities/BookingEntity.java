package be.atc.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@NamedQueries( value = {
        @NamedQuery(name = "Booking.findBookingsByDateAndHall",
                query = "SELECT b " +
                        "FROM BookingEntity b " +
                        "WHERE b.hallByHallId = :hall " +
                        "AND b.isCanceled = FALSE " +
                        "AND FUNCTION('YEAR', b.dateTimeIn) = FUNCTION('YEAR', :date) " +
                        "AND FUNCTION('MONTH', b.dateTimeIn) = FUNCTION('MONTH', :date) " +
                        "AND FUNCTION('DAY', b.dateTimeIn) = FUNCTION('DAY', :date)"),
        @NamedQuery(name = "Booking.findById",
                query = "SELECT b " +
                        "FROM BookingEntity b " +
                        "WHERE b.id = :id" ),
        @NamedQuery(name = "Booking.findBookingsByUser",
                query = "SELECT b " +
                        "FROM BookingEntity b " +
                        "WHERE b.userByUserId = :user" ),
        @NamedQuery(name = "Booking.findByBuildingAndDate",
                query = "SELECT b " +
                        "FROM BookingEntity b " +
                        "WHERE b.hallByHallId.buildingByBuildingId = :building " +
                        "AND FUNCTION('YEAR', b.dateTimeIn) = FUNCTION('YEAR', :date) " +
                        "AND FUNCTION('MONTH', b.dateTimeIn) = FUNCTION('MONTH', :date) " +
                        "AND FUNCTION('DAY', b.dateTimeIn) = FUNCTION('DAY', :date)"),
        @NamedQuery(name = "Booking.findByHallAndDate",
                query = "SELECT b " +
                        "FROM BookingEntity b " +
                        "WHERE b.hallByHallId = :hall " +
                        "AND FUNCTION('YEAR', b.dateTimeIn) = FUNCTION('YEAR', :date) " +
                        "AND FUNCTION('MONTH', b.dateTimeIn) = FUNCTION('MONTH', :date) " +
                        "AND FUNCTION('DAY', b.dateTimeIn) = FUNCTION('DAY', :date)")

})

@Entity
@Table(name = "booking", schema = "bookmyroom", catalog = "")
public class BookingEntity {
    private int id;
    private LocalDateTime dateTimeIn;
    private LocalDateTime dateTimeOut;
    private double totalPrice;
    private boolean isCanceled;
    private HallEntity hallByHallId;
    private UserEntity userByUserId;
    private Collection<PaymentHistoryEntity> paymenthistoriesById;

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
    @Column(name = "date_time_in", nullable = false)
    public LocalDateTime getDateTimeIn() {
        return dateTimeIn;
    }

    public void setDateTimeIn(LocalDateTime dateTimeIn) {
        this.dateTimeIn = dateTimeIn;
    }

    @Basic
    @Column(name = "date_time_out", nullable = false)
    public LocalDateTime getDateTimeOut() {
        return dateTimeOut;
    }

    public void setDateTimeOut(LocalDateTime dateTimeOut) {
        this.dateTimeOut = dateTimeOut;
    }

    @Basic
    @Column(name = "total_price", nullable = false, precision = 2)
    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Basic
    @Column(name = "is_canceled", nullable = false)
    public boolean getIsCanceled() {
        return isCanceled;
    }

    public void setIsCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingEntity that = (BookingEntity) o;
        return id == that.id && isCanceled == that.isCanceled && Objects.equals(dateTimeIn, that.dateTimeIn) && Objects.equals(dateTimeOut, that.dateTimeOut) && Objects.equals(totalPrice, that.totalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateTimeIn, dateTimeOut, totalPrice, isCanceled);
    }

    @ManyToOne
    @JoinColumn(name = "hall_id", referencedColumnName = "id", nullable = false)
    public HallEntity getHallByHallId() {
        return hallByHallId;
    }

    public void setHallByHallId(HallEntity hallByHallId) {
        this.hallByHallId = hallByHallId;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    public UserEntity getUserByUserId() {
        return userByUserId;
    }

    public void setUserByUserId(UserEntity userByUserId) {
        this.userByUserId = userByUserId;
    }

    @OneToMany(mappedBy = "bookingByBookingId")
    public Collection<PaymentHistoryEntity> getPaymenthistoriesById() {
        return paymenthistoriesById;
    }

    public void setPaymenthistoriesById(Collection<PaymentHistoryEntity> paymenthistoriesById) {
        this.paymenthistoriesById = paymenthistoriesById;
    }
}
