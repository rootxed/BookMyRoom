package be.atc.services;

import be.atc.entities.UserEntity;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collection;
import java.util.Locale;

@ApplicationScoped
public class UserService extends ServiceImpl<UserEntity> {
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


    public boolean exist(UserEntity userEntity, EntityManager em) {
        return false;
    }

    public UserEntity findOneByIdOrNull(int id, EntityManager em) {
        log.info("Finding user by Id: " + id);

        try {
            return em.createNamedQuery("User.findByIdOrNull", UserEntity.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.info("The query found no user to return");
            return null;
        }
    }

    public Collection<UserEntity> findAllOrNull(EntityManager em) {
        return null;
    }
}
