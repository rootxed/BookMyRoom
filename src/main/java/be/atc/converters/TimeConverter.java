package be.atc.converters;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Named;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Named
@RequestScoped
public class TimeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            java.util.Date date = sdf.parse(value);
            return new Time(date.getTime());
        } catch (ParseException e) {
            throw new ConverterException("Invalid time format", e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof Time) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format((Time) value);
        } else {
            throw new IllegalArgumentException("La valeur n'est pas de type java.sql.Time");
        }
    }
}