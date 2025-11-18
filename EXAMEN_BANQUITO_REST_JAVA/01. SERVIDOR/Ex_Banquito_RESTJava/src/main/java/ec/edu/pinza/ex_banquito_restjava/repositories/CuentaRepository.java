package ec.edu.pinza.ex_banquito_restjava.repositories;

import ec.edu.pinza.ex_banquito_restjava.config.DatabaseConnection;
import ec.edu.pinza.ex_banquito_restjava.entities.Cuenta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Cuenta usando JDBC
 */
public class CuentaRepository {
    
    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();
    
    public Optional<Cuenta> findByNumCuenta(String numCuenta) {
        String sql = "SELECT NUM_CUENTA, CEDULA, SALDO FROM CUENTA WHERE NUM_CUENTA = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numCuenta);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCuenta(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public List<Cuenta> findByCedula(String cedula) {
        String sql = "SELECT NUM_CUENTA, CEDULA, SALDO FROM CUENTA WHERE CEDULA = ?";
        List<Cuenta> cuentas = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedula);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cuentas.add(mapResultSetToCuenta(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cuentas;
    }
    
    public List<Cuenta> findAll() {
        String sql = "SELECT NUM_CUENTA, CEDULA, SALDO FROM CUENTA";
        List<Cuenta> cuentas = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                cuentas.add(mapResultSetToCuenta(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cuentas;
    }
    
    public Cuenta save(Cuenta cuenta) {
        String cedula = cuenta.getCliente() != null ? cuenta.getCliente().getCedula() : null;
        if (cuenta.getNumCuenta() == null || findByNumCuenta(cuenta.getNumCuenta()).isEmpty()) {
            return insert(cuenta, cedula);
        } else {
            return update(cuenta, cedula);
        }
    }
    
    private Cuenta insert(Cuenta cuenta, String cedula) {
        String sql = "INSERT INTO CUENTA (NUM_CUENTA, CEDULA, SALDO) VALUES (?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cuenta.getNumCuenta());
            stmt.setString(2, cedula);
            stmt.setBigDecimal(3, cuenta.getSaldo());
            
            stmt.executeUpdate();
            return cuenta;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar cuenta", e);
        }
    }
    
    private Cuenta update(Cuenta cuenta, String cedula) {
        String sql = "UPDATE CUENTA SET CEDULA = ?, SALDO = ? WHERE NUM_CUENTA = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedula);
            stmt.setBigDecimal(2, cuenta.getSaldo());
            stmt.setString(3, cuenta.getNumCuenta());
            
            stmt.executeUpdate();
            return cuenta;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar cuenta", e);
        }
    }
    
    public void delete(String numCuenta) {
        String sql = "DELETE FROM CUENTA WHERE NUM_CUENTA = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numCuenta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar cuenta", e);
        }
    }
    
    private Cuenta mapResultSetToCuenta(ResultSet rs) throws SQLException {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumCuenta(rs.getString("NUM_CUENTA"));
        cuenta.setSaldo(rs.getBigDecimal("SALDO"));
        // La CEDULA se carga como parte de la relación Cliente si es necesario
        return cuenta;
    }
}
