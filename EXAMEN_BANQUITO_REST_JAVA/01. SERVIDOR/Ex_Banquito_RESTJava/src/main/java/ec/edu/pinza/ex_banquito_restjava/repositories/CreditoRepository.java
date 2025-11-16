package ec.edu.pinza.ex_banquito_restjava.repositories;

import ec.edu.pinza.ex_banquito_restjava.entities.Credito;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Credito
 */
public class CreditoRepository {
    
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("banquitoPU");
    
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public Optional<Credito> findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            Credito credito = em.find(Credito.class, id);
            return Optional.ofNullable(credito);
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    public List<Credito> findByCedula(String cedula) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT c FROM Credito c WHERE c.cliente.cedula = :cedula", Credito.class)
                .setParameter("cedula", cedula)
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Credito> findActivosByCedula(String cedula) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT c FROM Credito c WHERE c.cliente.cedula = :cedula AND c.estado <> 'CANCELADO'", 
                Credito.class)
                .setParameter("cedula", cedula)
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    public boolean tieneCreditsActivos(String cedula) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(c) FROM Credito c WHERE c.cliente.cedula = :cedula " +
                "AND c.estado IN ('APROBADO', 'VIGENTE')", Long.class)
                .setParameter("cedula", cedula)
                .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
    
    public List<Credito> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Credito c", Credito.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    public Credito save(Credito credito) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (credito.getIdCredito() == null) {
                em.persist(credito);
            } else {
                credito = em.merge(credito);
            }
            em.getTransaction().commit();
            return credito;
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
            Credito credito = em.find(Credito.class, id);
            if (credito != null) {
                em.remove(credito);
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
