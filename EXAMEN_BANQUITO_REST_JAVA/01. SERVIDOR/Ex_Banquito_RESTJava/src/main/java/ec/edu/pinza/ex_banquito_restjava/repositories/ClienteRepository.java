package ec.edu.pinza.ex_banquito_restjava.repositories;

import ec.edu.pinza.ex_banquito_restjava.entities.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Cliente
 */
public class ClienteRepository {
    
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("banquitoPU");
    
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public Optional<Cliente> findByCedula(String cedula) {
        EntityManager em = getEntityManager();
        try {
            Cliente cliente = em.find(Cliente.class, cedula);
            return Optional.ofNullable(cliente);
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    public List<Cliente> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Cliente c", Cliente.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    public Cliente save(Cliente cliente) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (cliente.getCedula() == null || findByCedula(cliente.getCedula()).isEmpty()) {
                em.persist(cliente);
            } else {
                cliente = em.merge(cliente);
            }
            em.getTransaction().commit();
            return cliente;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void delete(String cedula) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Cliente cliente = em.find(Cliente.class, cedula);
            if (cliente != null) {
                em.remove(cliente);
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
