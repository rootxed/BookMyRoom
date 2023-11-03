package be.atc.validators;

import be.atc.services.UserService;
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

@FacesValidator(value="usernameValidator")
public class UsernameValidator implements Validator {

    private Logger log = org.apache.log4j.Logger.getLogger(UsernameValidator.class);

    //CDI seems to not work in FacesValidator
    //Found related bugs in jakarta.faces 2.3.x
//    @Inject
//    private UserService userService;

    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_.-]{4,30}$";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }

        String username = value.toString();

        if (!isValidUsername(username)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid username format.", "Must be between 4 and 30 characters. Only alphanumeric characters, '.', '-', and '_' are allowed.");
            throw new ValidatorException(message);
        }

        if (usernameAlreadyExists(username)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Username already exists in the database.", null);
            throw new ValidatorException(message);
        }
    }

    private boolean isValidUsername(String username) {
        return Pattern.compile(USERNAME_PATTERN).matcher(username).matches();
    }

    private boolean usernameAlreadyExists(String username) {
        UserService userService = new UserService();
        EntityManager em = EMF.getEM();
        try {
            return userService.userNameExist(username, em);
        } catch (Exception e) {
            log.error("Error while verifying username already exists", e);
            return false; // En cas d'erreur, considérez que le nom d'utilisateur n'existe pas.
        } finally {
            em.close();

        }
    }
}