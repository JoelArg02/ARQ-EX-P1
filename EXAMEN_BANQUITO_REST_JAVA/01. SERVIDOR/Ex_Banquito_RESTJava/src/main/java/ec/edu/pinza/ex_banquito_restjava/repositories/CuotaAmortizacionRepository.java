package ec.edu.pinza.ex_banquito_restjava.repositories;

import ec.edu.pinza.ex_banquito_restjava.entities.CuotaAmortizacion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad CuotaAmortizacion
 */
public class CuotaAmortizacionRepository {
    
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("banquitoPU");
    
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public Optional<CuotaAmortizacion> findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            CuotaAmortizacion cuota = em.find(CuotaAmortizacion.class, id);
            return Optional.ofNullable(cuota);
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    public List<CuotaAmortizacion> findByIdCredito(Integer idCredito) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT ca FROM CuotaAmortizacion ca WHERE ca.credito.idCredito = :idCredito " +
                "ORDER BY ca.numeroCuota", CuotaAmortizacion.class)
                .setParameter("idCredito", idCredito)
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<CuotaAmortizacion> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT ca FROM CuotaAmortizacion ca", CuotaAmortizacion.class)
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    public CuotaAmortizacion save(CuotaAmortizacion cuota) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (cuota.getIdCuota() == null) {
                em.persist(cuota);
            } else {
                cuota = em.merge(cuota);
            }
            em.getTransaction().commit();
            return cuota;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void delete(Integer id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            CuotaAmortizacion cuota = em.find(CuotaAmortizacion.class, id);
            if (cuota != null) {
                em.remove(cuota);
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
