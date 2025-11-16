package ec.edu.pinza.ex_banquito_restjava.repositories;

import ec.edu.pinza.ex_banquito_restjava.entities.Cuenta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Cuenta
 */
public class CuentaRepository {
    
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("banquitoPU");
    
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public Optional<Cuenta> findByNumCuenta(String numCuenta) {
        EntityManager em = getEntityManager();
        try {
            Cuenta cuenta = em.find(Cuenta.class, numCuenta);
            return Optional.ofNullable(cuenta);
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    public List<Cuenta> findByCedula(String cedula) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT c FROM Cuenta c WHERE c.cliente.cedula = :cedula", Cuenta.class)
                .setParameter("cedula", cedula)
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Cuenta> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Cuenta c", Cuenta.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    public Cuenta save(Cuenta cuenta) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (cuenta.getNumCuenta() == null || findByNumCuenta(cuenta.getNumCuenta()).isEmpty()) {
                em.persist(cuenta);
            } else {
                cuenta = em.merge(cuenta);
            }
            em.getTransaction().commit();
            return cuenta;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void delete(String numCuenta) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Cuenta cuenta = em.find(Cuenta.class, numCuenta);
            if (cuenta != null) {
                em.remove(cuenta);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
