package be.atc.validators;

import be.atc.managedBeans.BuildingBean;
import be.atc.services.BuildingService;
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

@FacesValidator(value="buildingNameValidator")
public class BuildingNameValidator implements Validator {

    private Logger log = org.apache.log4j.Logger.getLogger(BuildingNameValidator.class);

    //CDI seems to not work in FacesValidator
    //Found related bugs in jakarta.faces 2.3.x
//    @Inject
//    BuildingService buildingService;

    private static final String NAME_PATTERN = "^[A-Za-z0-9._@#!-]*$";
    private static final int MAX_NAME_LENGTH = 100;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }

        String currentName = (String) component.getAttributes().get("currentName");
        String buildingName = value.toString();

        if (currentName != null && currentName.equals(buildingName)) {
            // Name didn't changed. No need to check.
            return;
        }

        if (!isValidName(buildingName)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid building name.", "Only alphanumeric characters, '.', '-', '_', '!', '#','@' are allowed.");
            throw new ValidatorException(message);
        }

        if (buildingName.length() > MAX_NAME_LENGTH) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Building name is too long.", null);
            throw new ValidatorException(message);
        }

        if (buildingNameAlreadyExists(buildingName)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "This building name already exists.", null);
            throw new ValidatorException(message);
        }
    }

    private boolean isValidName(String name) {
        return Pattern.compile(NAME_PATTERN).matcher(name).matches();
    }

    private boolean buildingNameAlreadyExists(String name) {
        BuildingService buildingService = new BuildingService();
        EntityManager em = EMF.getEM();
        try {
            return buildingService.nameAlreadyExist(name, em);
        } catch (Exception e) {
            log.error("Error while verifying building name already exists", e);
            return false; // En cas d'erreur, considérez que le nom de bâtiment n'existe pas.
        } finally {
            em.close();
        }
    }
}