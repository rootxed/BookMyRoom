package be.atc.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "booking", schema = "bookmyroom", catalog = "")
public class BookingEntity {
    private int id;
    private Timestamp dateTimeIn;
    private Timestamp dateTimeOut;
    private BigDecimal totalPrice;
    private byte isCanceled;
    private HallEntity hallByHallId;
    private UserEntity userByUserId;
    private Collection<PaymentHistoryEntity> paymenthistoriesById;

    @Id
    @Column(name = "ID", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "DateTimeIn", nullable = false)
    public Timestamp getDateTimeIn() {
        return dateTimeIn;
    }

    public void setDateTimeIn(Timestamp dateTimeIn) {
        this.dateTimeIn = dateTimeIn;
    }

    @Basic
    @Column(name = "DateTimeOut", nullable = false)
    public Timestamp getDateTimeOut() {
        return dateTimeOut;
    }

    public void setDateTimeOut(Timestamp dateTimeOut) {
        this.dateTimeOut = dateTimeOut;
    }

    @Basic
    @Column(name = "TotalPrice", nullable = false, precision = 2)
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Basic
    @Column(name = "IsCanceled", nullable = false)
    public byte getIsCanceled() {
        return isCanceled;
    }

    public void setIsCanceled(byte isCanceled) {
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
    @JoinColumn(name = "HallID", referencedColumnName = "ID", nullable = false)
    public HallEntity getHallByHallId() {
        return hallByHallId;
    }

    public void setHallByHallId(HallEntity hallByHallId) {
        this.hallByHallId = hallByHallId;
    }

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "ID", nullable = false)
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
