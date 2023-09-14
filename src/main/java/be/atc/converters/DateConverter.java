package be.atc.converters;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Named
@RequestScoped
public class DateConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // Adapter le format à votre cas
            java.util.Date utilDate = sdf.parse(value);
            return utilDate;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Conversion from String to java.util.Date failed", e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof java.util.Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Adapter le format à votre cas
            return sdf.format((java.util.Date) value);
        } else {
            throw new IllegalArgumentException("Conversion from java.util.Date to String failed");
        }
    }
}