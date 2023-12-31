package be.atc.entities;

import be.atc.enums.PaymentType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(name = "PaymentHistory.findOneByBooking",
                query = "SELECT p FROM PaymentHistoryEntity p WHERE p.bookingByBookingId = :booking")
})

@Entity
@Table(name = "payment_history", schema = "bookmyroom", catalog = "")
public class PaymentHistoryEntity {
    private int id;
    private PaymentType paymentType;
    private Timestamp timeStamp;
    private UserEntity userByReceiverUserId;
    private BookingEntity bookingByBookingId;

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
    @Column(name = "payment_type", nullable = false)
    @Enumerated(EnumType.STRING)
    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    @Basic
    @Column(name = "time_stamp", nullable = false)
    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentHistoryEntity that = (PaymentHistoryEntity) o;
        return id == that.id && Objects.equals(paymentType, that.paymentType) && Objects.equals(timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paymentType, timeStamp);
    }

    @ManyToOne
    @JoinColumn(name = "receiver_user_id", referencedColumnName = "id", nullable = false)
    public UserEntity getUserByReceiverUserId() {
        return userByReceiverUserId;
    }

    public void setUserByReceiverUserId(UserEntity userByReceiverUserId) {
        this.userByReceiverUserId = userByReceiverUserId;
    }

    @ManyToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "id", nullable = false)
    public BookingEntity getBookingByBookingId() {
        return bookingByBookingId;
    }

    public void setBookingByBookingId(BookingEntity bookingByBookingId) {
        this.bookingByBookingId = bookingByBookingId;
    }
}
