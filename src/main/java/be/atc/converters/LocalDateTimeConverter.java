package be.atc.converters;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Named
@RequestScoped
public class LocalDateTimeConverter implements Converter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter NUMERIC_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        //String to localDateTime
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof LocalDateTime) {
            LocalDateTime dateTime = (LocalDateTime) value;

            String type = (String) component.getAttributes().get("type");

            if ("date".equals(type)) {
                return DATE_FORMATTER.format(dateTime);
            } else if ("time".equals(type)) {
                return TIME_FORMATTER.format(dateTime);
            } else if ("numeric".equals(type)) {
                return NUMERIC_FORMATTER.format(dateTime);
            } else {
                return dateTime.toString();
            }
        } else {
            throw new IllegalArgumentException("Object is not of type LocalDateTime.");
        }
    }
}
