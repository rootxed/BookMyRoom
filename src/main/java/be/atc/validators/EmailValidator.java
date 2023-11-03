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

@FacesValidator(value="emailValidator")
public class EmailValidator implements Validator {

    private Logger log = org.apache.log4j.Logger.getLogger(EmailValidator.class);

    //CDI seems to not work in FacesValidator
    //Found related bugs in jakarta.faces 2.3.x
//    @Inject
//    UserService userService;

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final int MAX_EMAIL_LENGTH = 100;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }

        String email = value.toString();

        if (!isValidEmail(email)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid email address format.", null);
            throw new ValidatorException(message);
        }

        if (email.length() > MAX_EMAIL_LENGTH) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email is too long.", null);
            throw new ValidatorException(message);
        }

        if (emailAlreadyExists(email)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email already exists in the database.", null);
            throw new ValidatorException(message);
        }
    }

    private boolean isValidEmail(String email) {
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }

    private boolean emailAlreadyExists(String email) {
        UserService userService = new UserService();
        EntityManager em = EMF.getEM();
        try{
            return userService.emailExist(email, em);
        } catch(Exception e){
            log.error("Error while verifying email already exists", e);
            return false; // En cas d'erreur, considérez que l'e-mail n'existe pas.
        }finally {
            em.close();
        }
    }
}




