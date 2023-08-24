package be.atc.managedBeans.test;

import be.atc.entities.CityEntity;
import be.atc.services.CityService;
import org.primefaces.event.SelectEvent;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class CitiesBean implements Serializable {

    @Inject
    private CityService cityService;

    private String searchText;
    private List<CityEntity> foundCities;
    private CityEntity selectedCity;

    public List<CityEntity> searchCitiesByPostalCode(String query) {
        // Utilisez le service CityService pour rechercher les villes par code postal
        return cityService.findCitiesByPostalCodeOrNull(query);
    }

    public void searchCitiesByName() {
        // Utilisez le service CityService pour rechercher les villes par nom
        foundCities = cityService.findCitiesByNameOrNull(searchText);
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

    public List<CityEntity> getFoundCities() {
        return foundCities;
    }

    public CityEntity getSelectedCity() {
        return selectedCity;
    }


    public void setSelectedCity(CityEntity selectedCity) {
        this.selectedCity = selectedCity;
    }
}
