package ec.edu.pinza.ex_comercializadora_restjava.repositories;

import ec.edu.pinza.ex_comercializadora_restjava.entities.Factura;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import java.util.Optional;

public class FacturaRepository {
    
    private static EntityManagerFactory emf;
    
    private static synchronized EntityManagerFactory getEMF() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory("comercializadoraPU");
        }
        return emf;
    }
    
    private EntityManager getEntityManager() {
        return getEMF().createEntityManager();
    }
    
    public Optional<Factura> findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            Factura factura = em.find(Factura.class, id);
            return Optional.ofNullable(factura);
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    public List<Factura> findByClienteId(Integer clienteId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT f FROM Factura f WHERE f.cliente.idCliente = :clienteId ORDER BY f.fecha DESC", Factura.class)
                    .setParameter("clienteId", clienteId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Factura> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT f FROM Factura f ORDER BY f.idFactura DESC", Factura.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    public Factura save(Factura factura) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Factura result;
            if (factura.getIdFactura() == null) {
                em.persist(factura);
                result = factura;
            } else {
                result = em.merge(factura);
            }
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void delete(Integer id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Factura factura = em.find(Factura.class, id);
            if (factura != null) {
                em.remove(factura);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
