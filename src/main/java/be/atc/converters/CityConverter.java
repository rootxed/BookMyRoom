package be.atc.converters;

import be.atc.entities.CityEntity;
import be.atc.services.CityService;
import be.atc.tools.EMF;
;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

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
        EntityManager em = EMF.getEM();
        try {
            Integer id = Integer.valueOf(value);
            CityEntity cityEntity = cityService.findCityByIdOrNull(id, em);
            return cityEntity;
        } catch (NumberFormatException e) {
            throw new ConverterException("The value is not a valid City ID: " + value, e);
        } finally {
            em.close();
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
