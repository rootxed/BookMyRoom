package be.atc.converters;

import be.atc.entities.CategoryEntity;
import be.atc.services.CategoryService;
import be.atc.tools.EMF;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Named
@RequestScoped
public class CategoryConverter implements Converter {

    @Inject
    private CategoryService categoryService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        EntityManager em = EMF.getEM();
        try {
            Integer id = Integer.valueOf(value);
            CategoryEntity categoryEntity = categoryService.findOneByIdOrNull(id,em);
            return categoryEntity;
        } catch (NumberFormatException e) {
            throw new ConverterException("The value is not a valid Category ID: " + value, e);
        } catch (NoResultException e) {
            throw new ConverterException("No category found with ID: " + value, e);
        } catch (Exception e) {
            throw new ConverterException("Unexpected error when converting Category ID: " + value, e);
        }finally {
            em.close();
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof CategoryEntity) {
            Integer id = ((CategoryEntity) value).getId();
            return (id != null) ? String.valueOf(id) : null;
        } else {
            throw new ConverterException("The value is not a valid Category instance: " + value);
        }
    }
}