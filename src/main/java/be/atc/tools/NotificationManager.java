package be.atc.tools;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Locale;
import java.util.ResourceBundle;

public class NotificationManager {

    public static void addInfoMessage(String str) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "bundle");
        String message = str;//String message = bundle.getString(str);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, ""));
    }

    public static void addErrorMessage(String str) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "bundle");
        String message = str;//String message = bundle.getString(str);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
    }

    public static void addErrorMessage(String str, String clientId) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "bundle");
        String message = str;//String message = bundle.getString(str);
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
    }

    public static void addWarnMessage(String str) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "bundle");
        String message = str;//String message = bundle.getString(str);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, message, ""));
    }

    private static final String BUNDLE_BASE_NAME = "be.atc.messages.messages";

    public static String getLocaleMessageAsString(String message) {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
                BUNDLE_BASE_NAME, locale);
        return resourceBundle.getString(message);
    }

}
