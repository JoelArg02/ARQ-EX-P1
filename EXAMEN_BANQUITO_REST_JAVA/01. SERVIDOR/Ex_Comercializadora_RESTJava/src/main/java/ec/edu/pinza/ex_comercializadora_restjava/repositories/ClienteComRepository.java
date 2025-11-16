package ec.edu.pinza.ex_comercializadora_restjava.repositories;

import ec.edu.pinza.ex_comercializadora_restjava.entities.ClienteCom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import java.util.Optional;

public class ClienteComRepository {
    
    private static EntityManagerFactory emf;
    
    private static synchronized EntityManagerFactory getEMF() {
        if (emf == null || !emf.isOpen()) {
            System.out.println("=== Inicializando EntityManagerFactory para ClienteComRepository ===");
            emf = Persistence.createEntityManagerFactory("comercializadoraPU");
            System.out.println("=== EntityManagerFactory creado exitosamente ===");
        }
        return emf;
    }
    
    private EntityManager getEntityManager() {
        return getEMF().createEntityManager();
    }
    
    public Optional<ClienteCom> findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            ClienteCom cliente = em.find(ClienteCom.class, id);
            return Optional.ofNullable(cliente);
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    public Optional<ClienteCom> findByCedula(String cedula) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT c FROM ClienteCom c WHERE c.cedula = :cedula", ClienteCom.class)
                    .setParameter("cedula", cedula)
                    .getResultStream()
                    .findFirst();
        } catch (Exception e) {
            System.err.println("ERROR en findByCedula: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    public List<ClienteCom> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT c FROM ClienteCom c", ClienteCom.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    public ClienteCom save(ClienteCom cliente) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            ClienteCom result;
            if (cliente.getIdCliente() == null) {
                em.persist(cliente);
                result = cliente;
            } else {
                result = em.merge(cliente);
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
            ClienteCom cliente = em.find(ClienteCom.class, id);
            if (cliente != null) {
                em.remove(cliente);
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
