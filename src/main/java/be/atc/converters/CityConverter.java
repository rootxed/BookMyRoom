package be.atc.converters;

import be.atc.entities.CityEntity;
import be.atc.services.CityService;
;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class CityConverter implements Converter {

    @Inject
    private CityService cityService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            Integer id = Integer.valueOf(value);
            CityEntity cityEntity = cityService.findCityByIdOrNull(id);
            return cityEntity;
        } catch (NumberFormatException e) {
            throw new ConverterException("The value is not a valid City ID: " + value, e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof CityEntity) {
            Integer id = ((CityEntity) value).getId();
            return (id != null) ? String.valueOf(id) : null;
        } else {
            throw new ConverterException("The value is not a valid City instance: " + value);
        }
    }
}
