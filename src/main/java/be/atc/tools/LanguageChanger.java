package be.atc.tools;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
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
    private static final long serialVersionUID = 1L;
    private String locale;

    private Map<String,Object> countries;
    private List<String> countriesList;
    @PostConstruct
    public void init() {
        countriesList = new ArrayList<>();
        countriesList.add("fr");
        countriesList.add("en");
    }

    public List<String> getCountriesList() {
        return countriesList;
    }

    public void setCountriesList(List<String> countriesList) {
        this.countriesList = countriesList;
    }

    public Map<String, Object> getCountries() {
        return countries;
    }
    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void changeToEN(){
        log.log(Level.INFO,"changeToEN");
        FacesContext.getCurrentInstance()
                .getViewRoot().setLocale(Locale.ENGLISH);
    }
    public void changeToFR(){
        FacesContext.getCurrentInstance()
                .getViewRoot().setLocale(Locale.FRENCH);

    }
    //value change event listener
    public void localeChange(ValueChangeEvent e) {
        locale = e.getNewValue().toString();

        if ("fr".equals(locale)) {
            FacesContext.getCurrentInstance()
                    .getViewRoot().setLocale(Locale.FRENCH);
        }
        if ("en".equals(locale)) {
            FacesContext.getCurrentInstance()
                    .getViewRoot().setLocale(Locale.ENGLISH);
        }

//        for (Map.Entry<String, Object> entry : countries.entrySet()) {
//
//            if(entry.getValue().toString().equals(locale)) {
//                FacesContext.getCurrentInstance()
//                        .getViewRoot().setLocale((Locale)entry.getValue());
//                //locale = newLocaleValue;
//
////                Map<String,Object> selectedEntry = (Map<String, Object>) entry;
////                countries.remove(entry);
////                countries.putAll(selectedEntry);
//            }
//        }
    }
}
