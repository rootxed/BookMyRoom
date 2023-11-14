package be.atc.tools;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@Named
@SessionScoped
public class LanguageChanger implements Serializable {
    private Logger log = Logger.getLogger(LanguageChanger.class);
    private Locale locale;

    @PostConstruct
    public void init() {
        locale = new Locale("fr");
        log.debug("Bean initialized.");
        log.debug("Initial locale: " + locale);
//        locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
    }

    public Locale getLocale() {
        return locale;
    }

    public String getLanguage() {
        return locale.getLanguage();
    }

    public void setLanguage(String language) {
        log.debug("local before="+locale);
        locale = new Locale(language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
        log.debug("local="+locale);
    }

}


//@Named
//@SessionScoped
//public class LanguageChanger implements Serializable {
//    private Logger log = Logger.getLogger(LanguageChanger.class);
//    private static final long serialVersionUID = 1L;
//    private Locale locale;
//
//    private Map<String,Object> countries;
//    private List<String> countriesList;
//    @PostConstruct
//    public void init() {
//        countriesList = new ArrayList<>();
//        countriesList.add("fr");
//        countriesList.add("en");
//        locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
//    }
//
//    public List<String> getCountriesList() {
//        return countriesList;
//    }
//
//    public void setCountriesList(List<String> countriesList) {
//        this.countriesList = countriesList;
//    }
//
//    public Map<String, Object> getCountries() {
//        return countries;
//    }
//    public Locale getLocale() {
//        return locale;
//    }
//    public void setLocale(Locale locale) {
//        this.locale = locale;
//    }
//
//    public void changeToEN(){
//        log.log(Level.INFO,"changeToEN");
//        FacesContext.getCurrentInstance()
//                .getViewRoot().setLocale(Locale.ENGLISH);
//        locale = Locale.ENGLISH;
//    }
//    public void changeToFR(){
//        FacesContext.getCurrentInstance()
//                .getViewRoot().setLocale(Locale.FRENCH);
//        locale = Locale.FRENCH;
//    }
//    //value change event listener
//    public void localeChange(ValueChangeEvent e) {
//        locale = new Locale(e.toString());
//
//        if ("fr".equals(locale)) {
//            FacesContext.getCurrentInstance()
//                    .getViewRoot().setLocale(Locale.FRENCH);
//        }
//        if ("en".equals(locale)) {
//            FacesContext.getCurrentInstance()
//                    .getViewRoot().setLocale(Locale.ENGLISH);
//        }
//
//        //Store in session
//        Subject currentUser = SecurityUtils.getSubject();
//        Session session = currentUser.getSession();
//        session.setAttribute("userLocale", locale);
//
//
////        for (Map.Entry<String, Object> entry : countries.entrySet()) {
////
////            if(entry.getValue().toString().equals(locale)) {
////                FacesContext.getCurrentInstance()
////                        .getViewRoot().setLocale((Locale)entry.getValue());
////                //locale = newLocaleValue;
////
//////                Map<String,Object> selectedEntry = (Map<String, Object>) entry;
//////                countries.remove(entry);
//////                countries.putAll(selectedEntry);
////            }
////        }
//    }
//}
