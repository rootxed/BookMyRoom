package be.atc.tools;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import be.atc.entities.UserEntity;
import be.atc.managedBeans.BookingHistoryBean;
import be.atc.services.BookingService;
import be.atc.services.UserService;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

@ApplicationScoped
@Named
public class UserSessionUtil {

    private Logger log = org.apache.log4j.Logger.getLogger(UserSessionUtil.class);

    @Inject
    private UserService userService;

    public UserEntity getCurrentUser() {
        Subject subject = SecurityUtils.getSubject();
        String username = (String) subject.getPrincipal();
        UserEntity user = new UserEntity();

        EntityManager em = EMF.getEM();
        try{
            user = userService.findUserByUsernameOrNull(em, username);
        }catch (Exception e)
        {
            log.error("error getting user by username", e);
        }finally {
            if (em != null) {
                em.close();
            }
        }
        return user;
    }
}