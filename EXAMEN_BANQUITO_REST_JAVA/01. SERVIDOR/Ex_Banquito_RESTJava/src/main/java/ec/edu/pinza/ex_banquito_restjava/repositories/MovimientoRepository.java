package ec.edu.pinza.ex_banquito_restjava.repositories;

import ec.edu.pinza.ex_banquito_restjava.entities.Movimiento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Movimiento
 */
public class MovimientoRepository {
    
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("banquitoPU");
    
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public Optional<Movimiento> findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            Movimiento movimiento = em.find(Movimiento.class, id);
            return Optional.ofNullable(movimiento);
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    public List<Movimiento> findByNumCuenta(String numCuenta) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT m FROM Movimiento m WHERE m.cuenta.numCuenta = :numCuenta ORDER BY m.fecha DESC", 
                Movimiento.class)
                .setParameter("numCuenta", numCuenta)
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Movimiento> findByCedulaAndTipoAndDateRange(String cedula, String tipo, LocalDate fechaDesde, LocalDate fechaHasta) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT m FROM Movimiento m WHERE m.cuenta.cliente.cedula = :cedula " +
                "AND m.tipo = :tipo AND m.fecha BETWEEN :fechaDesde AND :fechaHasta", 
                Movimiento.class)
                .setParameter("cedula", cedula)
                .setParameter("tipo", tipo)
                .setParameter("fechaDesde", fechaDesde)
                .setParameter("fechaHasta", fechaHasta)
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Movimiento> findByCedulaAndDateRange(String cedula, LocalDate fechaDesde, LocalDate fechaHasta) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT m FROM Movimiento m WHERE m.cuenta.cliente.cedula = :cedula " +
                "AND m.fecha BETWEEN :fechaDesde AND :fechaHasta", 
                Movimiento.class)
                .setParameter("cedula", cedula)
                .setParameter("fechaDesde", fechaDesde)
                .setParameter("fechaHasta", fechaHasta)
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Movimiento> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT m FROM Movimiento m", Movimiento.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    public Movimiento save(Movimiento movimiento) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (movimiento.getCodMovimiento() == null) {
                em.persist(movimiento);
            } else {
                movimiento = em.merge(movimiento);
            }
            em.getTransaction().commit();
            return movimiento;
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
            Movimiento movimiento = em.find(Movimiento.class, id);
            if (movimiento != null) {
                em.remove(movimiento);
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
