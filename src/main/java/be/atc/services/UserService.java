package be.atc.services;

import be.atc.entities.UserEntity;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Locale;

@ApplicationScoped
public class UserService {
    private Logger log = org.apache.log4j.Logger.getLogger(UserService.class);

    public boolean userExist(UserEntity u, EntityManager em) {

        return (findUserByUsernameOrNull(em, u.getUserName()) != null);

    }

    public UserEntity findUserByUsernameOrNull(EntityManager em, String userName) throws IllegalArgumentException {
        log.info("Finding user for username: " + userName);

        try {
            return em.createNamedQuery("User.findUserByUsernameOrNull", UserEntity.class)
                    .setParameter("userName", userName.trim().toLowerCase())
                    .getSingleResult();
        } catch (NoResultException e) {
            log.info("The query found no user to return");
            return null;
        }
    }
}
