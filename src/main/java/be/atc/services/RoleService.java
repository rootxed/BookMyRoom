package be.atc.services;

import be.atc.entities.RoleEntity;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.management.relation.Role;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.List;

@ApplicationScoped
public class RoleService {

    private Logger log = org.apache.log4j.Logger.getLogger(RoleService.class);
    private EntityManager em = EMF.getEM();
    private EntityTransaction transaction = em.getTransaction();

    public boolean roleExist(RoleEntity r){
        return(findRoleByNameOrNull(r.getName())!= null);
    }


    public RoleEntity findRoleByNameOrNull(String roleName) {
        log.info("Finding role by name: " + roleName);

        try {
            return em.createNamedQuery("Role.findRoleByName", RoleEntity.class)
                    .setParameter("name", roleName.trim().toLowerCase())
                    .getSingleResult();
        } catch (NoResultException e) {
            log.info("No role found for the given role name.", e);
            return null;
        }
    }

    public List<RoleEntity> findAllRolesOrNull() {
        log.info("Finding all roles");

        try {
            return em.createNamedQuery("Role.findAllRoles", RoleEntity.class)
                    .getResultList();
        } catch (NoResultException e){
            log.info("No roles found.", e);
            return null;
        }
    }

    public RoleEntity findRoleByIdOrNull(int roleId) {
        log.info("Finding role by ID: " + roleId);

        try {
            return em.find(RoleEntity.class, roleId);
        } catch (NoResultException e) {
            log.info("No role found for the given ID", e);
            return null;
        }
    }

    public RoleEntity getDefaultRoleForNewUsers() {
        log.info("Finding default role for new users");
        return findRoleByNameOrNull("tenant");
    }
}
