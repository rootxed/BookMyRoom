package be.atc.converters;

import be.atc.entities.HallEntity;
import be.atc.services.HallService;
import be.atc.tools.EMF;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.util.Objects;

@Named
@RequestScoped
//@FacesConverter(forClass = HallEntity.class)
public class HallConverter implements Converter {

    @Inject
    private HallService hallService;

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        EntityManager em = EMF.getEM();
        try {
            int hallId = Integer.parseInt(value);
            return hallService.findOneByIdOrNull(hallId, em);
        } catch (NumberFormatException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value instanceof HallEntity) {
            return String.valueOf(((HallEntity) value).getId());
        }
        return null;
    }
}
