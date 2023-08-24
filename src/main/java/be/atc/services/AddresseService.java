package be.atc.services;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;


import be.atc.entities.AddresseEntity;
import be.atc.entities.BuildingEntity;
import be.atc.tools.EMF;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class AddresseService extends ServiceImpl<AddresseEntity>{

    private Logger log = org.apache.log4j.Logger.getLogger(RoleService.class);
    private EntityManager em = EMF.getEM();
    private EntityTransaction transaction = em.getTransaction();


    public AddresseEntity findAddresseByFullInfo(String addressLine, int cityId) {
        try {
            return em.createNamedQuery("Addresse.findAddresseByFullInfo", AddresseEntity.class)
                    .setParameter("addressLine", addressLine)
                    .setParameter("cityId", cityId)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.info("No address found for these info - Address Line: " + addressLine + ", City ID: " + cityId, e);
            return null;
        }
    }


    public boolean exist(AddresseEntity address, EntityManager em) {
        return(findAddresseByFullInfo(address.getAddressLine(), address.getCityByCityId().getId())!= null);
    }

    public AddresseEntity findOneByIdOrNull(int id, EntityManager em) {
        log.info("Select address by id: "+id);
        return em.find(AddresseEntity.class, id);
    }

    public Collection<AddresseEntity> findAllOrNull(EntityManager em) {
        try {
            log.info("Finding all addresses...");
            TypedQuery<AddresseEntity> query = em.createNamedQuery("Addresse.findAll", AddresseEntity.class);
            List<AddresseEntity> addressList = query.getResultList();
            log.info("Selected all addresses.");
            return addressList;
        } catch (Exception e) {
            log.info("Query found no addresses to return");
            return null;
        }
    }
}