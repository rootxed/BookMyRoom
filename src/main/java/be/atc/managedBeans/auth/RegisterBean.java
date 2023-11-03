package be.atc.managedBeans.auth;

import be.atc.entities.CityEntity;
import be.atc.entities.RoleEntity;
import be.atc.entities.UserEntity;
import be.atc.entities.AddresseEntity;
import be.atc.managedBeans.CitiesBean;
import be.atc.services.CityService;
import be.atc.services.RoleService;
import be.atc.services.UserService;
import be.atc.services.AddresseService;
import be.atc.tools.EMF;
import be.atc.tools.NotificationManager;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.management.Notification;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class RegisterBean implements Serializable {
    private Logger log = org.apache.log4j.Logger.getLogger(RegisterBean.class);

    private UserEntity user;
    private AddresseEntity addresse;

    @Inject
    private UserService userService;

    @Inject
    private AddresseService addresseService;

    @Inject
    private RoleService roleService;

    @Inject
    private CityService cityService;

    @Inject
    private CitiesBean citiesBean;

    private String searchText;
    private List<CityEntity> foundCities;
    private CityEntity selectedCity;


    @PostConstruct
    public void init() {
        user = new UserEntity();
        addresse = new AddresseEntity();
    }

    public String doRegister() {
        log.info("Attempt to persist new registered user: "+ user.getUserName());
        log.info("User properties: userName=" + user.getUserName() + ", email=" + user.getEmail() +", firstName=" + user.getFirstName() + ", lastName=" + user.getLastName());
        EntityManager em = EMF.getEM();
        EntityTransaction transaction = null;

        try {
            if (userService.userExist(user,em)) {
                throw new RuntimeException("Can't persist user already exists");
            }

            transaction = em.getTransaction();
            transaction.begin();

            AddresseEntity addresseToInsert = this.addresse;
            addresseToInsert.setCityByCityId(citiesBean.getSelectedCity());
            log.info("addresseLinne: "+addresseToInsert.getAddressLine() + "Selected City :"+addresseToInsert.getCityByCityId().getName());
            em.persist(addresseToInsert);

            log.info("Adding address: ID=" + addresseToInsert.getId() + ", AddressLine=" + addresseToInsert.getAddressLine());

            UserEntity userToInsert = this.user;
            String encryptedPassword = AuthBean.hashAndSalt(this.user.getPassword());
            userToInsert.setPassword(encryptedPassword);
            RoleEntity roleForNewlyRegisteredUser = roleService.getDefaultRoleForNewUsers();
            userToInsert.setRoleByRoleId(roleForNewlyRegisteredUser);
            userToInsert.setAddresseByAdresseId(addresseToInsert);


            em.persist(userToInsert);

            transaction.commit();

            NotificationManager.addInfoMessageFromBundleRedirect("notification.register.success");
            return "success";
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Failed to insert new user with address", e);
            NotificationManager.addErrorMessageFromBundle("notification.register.failure");
            return "failure";
        } finally {
            em.clear();
            em.close();
        }
    }

    public UserEntity getUser() {
        return user;
    }

    public AddresseEntity getAddresse() {
        return addresse;
    }

    public List<CityEntity> getFoundCities() {
        return foundCities;
    }

    public CityEntity getSelectedCity() {
        return selectedCity;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

}
