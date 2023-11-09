package be.atc.validators;

import be.atc.services.CategoryService;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.regex.Pattern;

@FacesValidator(value="categoryNameValidator")
public class CategoryNameValidator implements Validator {

    private Logger log = org.apache.log4j.Logger.getLogger(CategoryNameValidator.class);

    //CDI seems to not work in FacesValidator
    //Found related bugs in jakarta.faces 2.3.x
//    @Inject
//    CategoryService categoryService;

    private static final String NAME_PATTERN = "^[A-Za-z0-9 ._-]*$";
    private static final int MAX_NAME_LENGTH = 100;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }

        String currentName = (String) component.getAttributes().get("currentName");
        String categoryName = value.toString();

        if (currentName != null && currentName.equals(categoryName)) {
            return;
        }

        if (!isValidName(categoryName)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid category name.", " Only alphanumeric characters, '.', '-', and '_' are allowed.");
            throw new ValidatorException(message);
        }

        if (categoryName.length() > MAX_NAME_LENGTH) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Category name is too long.", null);
            throw new ValidatorException(message);
        }

        if (categoryNameAlreadyExists(categoryName)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "This category name already exists.", null);
            throw new ValidatorException(message);
        }
    }

    private boolean isValidName(String name) {
        return Pattern.compile(NAME_PATTERN).matcher(name).matches();
    }

    private boolean categoryNameAlreadyExists(String name) {
        CategoryService categoryService = new CategoryService();
        EntityManager em = EMF.getEM();
        try {
            return categoryService.nameExist(name, em);
        } catch (Exception e) {
            log.error("Error while verifying category name already exists", e);
            return false; // In case of an error, consider the category name doesn't exist.
        } finally {
            em.close();
        }
    }
}
