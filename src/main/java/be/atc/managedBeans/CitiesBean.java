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

    /**
     * The function searches for cities based on a given postal code using the CityService.
     *
     * @param query The "query" parameter is a string representing the postal code that is being searched for.
     * @return The method is returning a List of CityEntity objects.
     */
    public List<CityEntity> searchCitiesByPostalCode(String query) {
        // Utilisez le service CityService pour rechercher les villes par code postal
        List<CityEntity> cities = new ArrayList<CityEntity>();
        EntityManager em = EMF.getEM();
        try {
            cities = cityService.findCitiesByPostalCodeOrNull(query, em);
        } catch (Exception e) {
            log.error("Error while searching cities", e);
        } finally {
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
        } catch (Exception e) {
            log.error("Error while searching cities", e);
        } finally {
            em.close();
        }
        return cities;
    }

    /**
     * The function searches for cities with halls by name using the CityService.
     *
     * @param query The "query" parameter is a string that represents the name of the city or hall that you want to search
     *              for.
     * @return The method is returning a List of CityEntity objects.
     */
    public List<CityEntity> searchCitiesWithHallsByName(String query) {
        // Utilisez le service CityService pour rechercher les villes par nom
        List<CityEntity> cities = new ArrayList<CityEntity>();
        EntityManager em = EMF.getEM();
        try {
            cities = cityService.findCitiesWithHallsByNameOrNull(query, em);
        } catch (Exception e) {
            log.error("Error while searching cities with halls", e);
        } finally {
            em.close();
        }
        return cities;
    }

    /**
     * The selectCity function assigns the selectedCity variable to the object passed in the SelectEvent event.
     *
     * @param event The event parameter is an object that represents the event that triggered the method. It contains
     *              information about the event, such as the source of the event and any additional data associated with it. In this
     *              case, the event is a SelectEvent, which is typically fired when a user selects an item from a
     */
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
