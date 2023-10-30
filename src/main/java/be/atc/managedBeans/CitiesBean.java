package be.atc.managedBeans;

import be.atc.entities.CityEntity;
import be.atc.services.CityService;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class CitiesBean implements Serializable {

    private Logger log = org.apache.log4j.Logger.getLogger(CitiesBean.class);

    @Inject
    private CityService cityService;

    private String searchText;
    private CityEntity selectedCity;

    public List<CityEntity> searchCitiesByPostalCode(String query) {
        // Utilisez le service CityService pour rechercher les villes par code postal
        List<CityEntity> cities = new ArrayList<CityEntity>();
        EntityManager em = EMF.getEM();
        try {
            cities = cityService.findCitiesByPostalCodeOrNull(query, em);
        }catch (Exception e) {
            log.error("Error while searching cities", e);
        }finally {
            em.close();
        }
        return cities;
    }

    public List<CityEntity> searchCitiesByName(String query) {
        // Utilisez le service CityService pour rechercher les villes par nom
        List<CityEntity> cities = new ArrayList<CityEntity>();
        EntityManager em = EMF.getEM();
        try {
            cities = cityService.findCitiesByNameOrNull(query, em);
        }catch (Exception e) {
            log.error("Error while searching cities", e);
        }finally {
            em.close();
        }
        return cities;
    }

    public List<CityEntity> searchCitiesWithHallsByName(String query) {
        // Utilisez le service CityService pour rechercher les villes par nom
        List<CityEntity> cities = new ArrayList<CityEntity>();
        EntityManager em = EMF.getEM();
        try {
            cities = cityService.findCitiesWithHallsByNameOrNull(query, em);
        }catch (Exception e) {
            log.error("Error while searching cities with halls", e);
        }finally {
            em.close();
        }
        return cities;
    }

    public void selectCity(SelectEvent event) {
        selectedCity = (CityEntity) event.getObject();
    }

    public void handleEmptyAutoComplete() {
        selectedCity = null; // Clear the selected city
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public CityEntity getSelectedCity() {
        return selectedCity;
    }


    public void setSelectedCity(CityEntity selectedCity) {
        this.selectedCity = selectedCity;
    }
}
