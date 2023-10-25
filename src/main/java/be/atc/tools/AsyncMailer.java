package be.atc.tools;
import be.atc.entities.BookingEntity;
import org.apache.log4j.Logger;


import javax.enterprise.context.ApplicationScoped;
import javax.mail.MessagingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class AsyncMailer {
    private Logger log = Logger.getLogger(AsyncMailer.class);

    //Create a execution pool
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public void sendBookingConfirmationAsync(BookingEntity booking) {
        executorService.submit(() -> {
            try {
                Mailer.sendBookingConfirmation(booking);
                NotificationManager.addInfoMessage("Booking confirmation email has been sent");
            } catch (MessagingException e) {
                log.error("Error while sending confirmation email : " + e.getMessage());
            } catch (Exception ex) {
                log.error("Error while sending confirmation email : " + ex.getMessage());
            }
        });
    }
}
