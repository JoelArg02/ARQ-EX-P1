package ec.edu.pinza.ex_comercializadora_restjava.repositories;

import ec.edu.pinza.ex_comercializadora_restjava.config.DatabaseConnection;
import ec.edu.pinza.ex_comercializadora_restjava.entities.ClienteCom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar operaciones CRUD de ClienteCom usando JDBC
 */
public class ClienteComRepository {
    
    private final DatabaseConnection dbConnection;
    
    public ClienteComRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public Optional<ClienteCom> findById(Integer id) {
        String sql = "SELECT idCliente, cedula, nombre, direccion, telefono FROM ClienteCom WHERE idCliente = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCliente(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public Optional<ClienteCom> findByCedula(String cedula) {
        String sql = "SELECT idCliente, cedula, nombre, direccion, telefono FROM ClienteCom WHERE cedula = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedula);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCliente(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por cédula: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public List<ClienteCom> findAll() {
        String sql = "SELECT idCliente, cedula, nombre, direccion, telefono FROM ClienteCom";
        List<ClienteCom> clientes = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los clientes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return clientes;
    }
    
    public ClienteCom save(ClienteCom cliente) {
        if (cliente.getIdCliente() == null) {
            return insert(cliente);
        } else {
            return update(cliente);
        }
    }
    
    private ClienteCom insert(ClienteCom cliente) {
        String sql = "INSERT INTO ClienteCom (cedula, nombre, direccion, telefono) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, cliente.getCedula());
            stmt.setString(2, cliente.getNombre());
            stmt.setString(3, cliente.getDireccion());
            stmt.setString(4, cliente.getTelefono());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setIdCliente(generatedKeys.getInt(1));
                }
            }
            
            return cliente;
        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
            throw new RuntimeException("Error al insertar cliente", e);
        }
    }
    
    private ClienteCom update(ClienteCom cliente) {
        String sql = "UPDATE ClienteCom SET cedula = ?, nombre = ?, direccion = ?, telefono = ? WHERE idCliente = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cliente.getCedula());
            stmt.setString(2, cliente.getNombre());
            stmt.setString(3, cliente.getDireccion());
            stmt.setString(4, cliente.getTelefono());
            stmt.setInt(5, cliente.getIdCliente());
            
            stmt.executeUpdate();
            return cliente;
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            throw new RuntimeException("Error al actualizar cliente", e);
        }
    }
    
    public void delete(Integer id) {
        String sql = "DELETE FROM ClienteCom WHERE idCliente = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            throw new RuntimeException("Error al eliminar cliente", e);
        }
    }
    
    private ClienteCom mapResultSetToCliente(ResultSet rs) throws SQLException {
        ClienteCom cliente = new ClienteCom();
        cliente.setIdCliente(rs.getInt("idCliente"));
        cliente.setCedula(rs.getString("cedula"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setDireccion(rs.getString("direccion"));
        cliente.setTelefono(rs.getString("telefono"));
        return cliente;
    }
}
