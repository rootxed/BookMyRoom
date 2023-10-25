package be.atc.tools;

import be.atc.entities.BookingEntity;
import be.atc.entities.BookingEntity;
import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Mailer {
    private static final Logger log = Logger.getLogger(Mailer.class);

    public static void sendBookingConfirmation(BookingEntity booking) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        String mailAccount = "rentschoolsystem@gmail.com";
        String password = "ahwoheliboskhvcv";
        String recipient = booking.getUserByUserId().getEmail();

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailAccount, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mailAccount));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setSubject("Booking Confirmation #" + booking.getId());

        String content = generateMailContent(booking);
        message.setText(content);

        Transport.send(message);
        log.info("Mail sent successfully to : " + recipient);
    }

    private static String generateMailContent(BookingEntity booking) {
        // Generate the content similar to the structure you provided
        String content = "Confirmation #" + booking.getId() + "\n" +
                "Thank you " + booking.getUserByUserId().getFirstName() + " for your booking." + "\n\n" +
                "Details:\n" +
                "Date: " + booking.getDateTimeIn().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                "Schedule: " + booking.getDateTimeIn().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + booking.getDateTimeOut().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n" +
                "Location :\n" +
                booking.getHallByHallId().getName() + "\n" +
                booking.getHallByHallId().getBuildingByBuildingId().getName() + "\n" +
                booking.getHallByHallId().getBuildingByBuildingId().getAddresseByAdresseId().getAddressLine() + "\n" +
                booking.getHallByHallId().getBuildingByBuildingId().getAddresseByAdresseId().getCityByCityId().getPostalCode() +
                " " + booking.getHallByHallId().getBuildingByBuildingId().getAddresseByAdresseId().getCityByCityId().getName() + "\n\n" +
                "Payement due:\n" +
                booking.getTotalPrice() + "€\n" +
                "You can pay at reception via credit card or cash.\n" +
                "It is also possible to pay via bank transfer with the following information:\n\n" +
                "Name: BOOKMYROOM\n" +
                "IBAN: BE0012 3456 7891 2345\n" +
                "BIC: 1234567\n" +
                "COMMUNICATION: " + booking.getDateTimeOut().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + booking.getId();

        return content;
    }
}