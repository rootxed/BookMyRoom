package be.atc.validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.regex.Pattern;

@FacesValidator(value="phoneValidator")
public class PhoneNumberValidator implements Validator {

    private static final String PHONE_PATTERN = "^(\\+)?[0-9\\-\\s/()]*$";
    private static final int MAX_PHONE_LENGTH = 20;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }

        String phoneNumber = value.toString();

        if (!isValidPhoneNumber(phoneNumber)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid phone number format.", null);
            throw new ValidatorException(message);
        }

        if (phoneNumber.length() > MAX_PHONE_LENGTH) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Phone number is too long.", null);
            throw new ValidatorException(message);
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return Pattern.compile(PHONE_PATTERN).matcher(phoneNumber).matches();
    }
}
