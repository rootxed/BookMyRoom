package be.atc.managedBeans.auth;

import be.atc.entities.UserEntity;
import be.atc.services.UserService;
import be.atc.tools.EMF;

import be.atc.tools.NotificationManager;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginBean implements Serializable {

private Logger log = org.apache.log4j.Logger.getLogger(LoginBean.class);

    private String username;
    private String password;
    @Inject
    private UserService userService;
    @Inject
    AuthBean authBean;

    @PostConstruct
    public void init() {
        this.username ="";
        this.password = "";
    }

    public String doLogin() throws IOException {

        try {
            log.info("Attempting to log in client...");
            UsernamePasswordToken token = new UsernamePasswordToken(getUsername(), getPassword());
            SecurityUtils.getSubject().login(token);

            EntityManager em = EMF.getEM();
            UserEntity foundUserEntity = userService.findUserByUsernameOrNull(em, getUsername());
            if(foundUserEntity == null) {
                log.info("UserEntity " + foundUserEntity.toString() + " not found in data store");
                em.clear();
                em.close();
                return "failure";
            }

            authBean.setCurrentUser(foundUserEntity);
            return "success";

        } catch (UnknownAccountException ex) {
            log.info("Login attempt has failed. Unknown account", ex);
            NotificationManager.addErrorMessageFromBundle("notification.login.unknownAccount");
            return "failure";

        } catch (IncorrectCredentialsException ex) {
            log.info("Login attempt has failed. Incorrect credentials", ex);
            NotificationManager.addErrorMessageFromBundle("notification.login.incorrectCredentials");
            return "failure";

        } catch (Exception ex) {
            log.info("Login attempt has failed.", ex);
            NotificationManager.addErrorMessageFromBundle("notification.login.failed");
            return "failure";
        } finally {

        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
