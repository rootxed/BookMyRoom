package be.atc.converters;

import be.atc.entities.BuildingEntity;
import be.atc.services.BuildingService;
import be.atc.tools.EMF;

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
public class BuildingConverter implements Converter {

    @Inject
    private BuildingService buildingService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        EntityManager em = EMF.getEM();
        try {
            Integer id = Integer.valueOf(value);
            BuildingEntity buildingEntity = buildingService.findOneByIdOrNull(id, em);
            return buildingEntity;
        } catch (NumberFormatException e) {
            throw new ConverterException("The value is not a valid Building ID: " + value, e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof BuildingEntity) {
            Integer id = ((BuildingEntity) value).getId();
            return (id != null) ? String.valueOf(id) : null;
        } else {
            throw new ConverterException("The value is not a valid Building instance: " + value);
        }
    }
}