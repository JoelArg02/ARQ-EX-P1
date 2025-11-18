package ec.edu.pinza.ex_banquito_restjava.repositories;

import ec.edu.pinza.ex_banquito_restjava.config.DatabaseConnection;
import ec.edu.pinza.ex_banquito_restjava.entities.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Cliente usando JDBC
 */
public class ClienteRepository {
    
    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();
    
    public Optional<Cliente> findByCedula(String cedula) {
        String sql = "SELECT CEDULA, NOMBRE, FECHA_NACIMIENTO, ESTADO_CIVIL FROM CLIENTE WHERE CEDULA = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedula);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = mapResultSetToCliente(rs);
                    return Optional.of(cliente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public List<Cliente> findAll() {
        String sql = "SELECT CEDULA, NOMBRE, FECHA_NACIMIENTO, ESTADO_CIVIL FROM CLIENTE";
        List<Cliente> clientes = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return clientes;
    }
    
    public Cliente save(Cliente cliente) {
        if (findByCedula(cliente.getCedula()).isEmpty()) {
            return insert(cliente);
        } else {
            return update(cliente);
        }
    }
    
    private Cliente insert(Cliente cliente) {
        String sql = "INSERT INTO CLIENTE (CEDULA, NOMBRE, FECHA_NACIMIENTO, ESTADO_CIVIL) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cliente.getCedula());
            stmt.setString(2, cliente.getNombre());
            stmt.setDate(3, Date.valueOf(cliente.getFechaNacimiento()));
            stmt.setString(4, cliente.getEstadoCivil());
            
            stmt.executeUpdate();
            return cliente;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar cliente", e);
        }
    }
    
    private Cliente update(Cliente cliente) {
        String sql = "UPDATE CLIENTE SET NOMBRE = ?, FECHA_NACIMIENTO = ?, ESTADO_CIVIL = ? WHERE CEDULA = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cliente.getNombre());
            stmt.setDate(2, Date.valueOf(cliente.getFechaNacimiento()));
            stmt.setString(3, cliente.getEstadoCivil());
            stmt.setString(4, cliente.getCedula());
            
            stmt.executeUpdate();
            return cliente;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar cliente", e);
        }
    }
    
    public void delete(String cedula) {
        String sql = "DELETE FROM CLIENTE WHERE CEDULA = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedula);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar cliente", e);
        }
    }
    
    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setCedula(rs.getString("CEDULA"));
        cliente.setNombre(rs.getString("NOMBRE"));
        cliente.setFechaNacimiento(rs.getDate("FECHA_NACIMIENTO").toLocalDate());
        cliente.setEstadoCivil(rs.getString("ESTADO_CIVIL"));
        return cliente;
    }
}
