package ec.edu.pinza.ex_comercializadora_restjava.repositories;

import ec.edu.pinza.ex_comercializadora_restjava.entities.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import java.util.Optional;

public class ProductoRepository {
    
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
    
    public Optional<Producto> findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            Producto producto = em.find(Producto.class, id);
            return Optional.ofNullable(producto);
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    public Optional<Producto> findByCodigo(String codigo) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Producto p WHERE p.codigo = :codigo", Producto.class)
                    .setParameter("codigo", codigo)
                    .getResultStream()
                    .findFirst();
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    public List<Producto> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Producto p", Producto.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    public Producto save(Producto producto) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Producto result;
            if (producto.getIdProducto() == null) {
                em.persist(producto);
                result = producto;
            } else {
                result = em.merge(producto);
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
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                em.remove(producto);
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
