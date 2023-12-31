package be.atc.managedBeans;

import be.atc.entities.UserEntity;
import be.atc.services.UserService;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class UserBean implements Serializable {

    private Logger log = org.apache.log4j.Logger.getLogger(UserBean.class);

    @Inject
    private UserService userService;

    private List<UserEntity> users;
    private List<UserEntity> filteredUsers;


    /**
     * The function loads a list of UserEntity objects from the database using an EntityManager.
     *
     * @return The method is returning a List of UserEntity objects.
     */
    private List<UserEntity> loadUsers() {
        EntityManager em = EMF.getEM();
        try {
            return (List<UserEntity>) userService.findAllOrNull(em);
        } catch (Exception e) {
            log.error("Error while loading users", e);
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * The function returns a list of user entities, loading them from a data source if necessary.
     *
     * @return The method is returning a List of UserEntity objects.
     */
    public List<UserEntity> getUsers() {
        if(users==null){
            users=loadUsers();
        }
        return users;
    }

    public List<UserEntity> getFilteredUsers() {
        return filteredUsers;
    }

    public void setFilteredUsers(List<UserEntity> filteredUsers) {
        this.filteredUsers = filteredUsers;
    }
}