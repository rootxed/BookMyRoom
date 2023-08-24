package be.atc.tools;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EMF {
    private static EntityManagerFactory emfInstance =
            Persistence.createEntityManagerFactory("BookMyRoom");

    public static EntityManagerFactory getEmfInstance() {
        return emfInstance;
    }
    public static EntityManager getEM(){
        return emfInstance.createEntityManager();
    }
}
