package ec.edu.pinza.ex_banquito_restjava.repositories;

import ec.edu.pinza.ex_banquito_restjava.config.DatabaseConnection;
import ec.edu.pinza.ex_banquito_restjava.entities.Cliente;
import ec.edu.pinza.ex_banquito_restjava.entities.Credito;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Credito usando JDBC
 */
public class CreditoRepository {
    
    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();
    private final ClienteRepository clienteRepository = new ClienteRepository();
    
    public Optional<Credito> findById(Integer id) {
        String sql = "SELECT ID_CREDITO, CEDULA, MONTO_APROBADO, MONTO_SOLICITADO, " +
                     "PLAZO_MESES, TASA_INTERES_ANUAL, MONTO_MAXIMO_AUTORIZADO, " +
                     "FECHA_OTORGAMIENTO, ESTADO FROM CREDITO WHERE ID_CREDITO = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCredito(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public List<Credito> findByCedula(String cedula) {
        String sql = "SELECT ID_CREDITO, CEDULA, MONTO_APROBADO, MONTO_SOLICITADO, " +
                     "PLAZO_MESES, TASA_INTERES_ANUAL, MONTO_MAXIMO_AUTORIZADO, " +
                     "FECHA_OTORGAMIENTO, ESTADO FROM CREDITO WHERE CEDULA = ?";
        List<Credito> creditos = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedula);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    creditos.add(mapResultSetToCredito(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return creditos;
    }
    
    public List<Credito> findActivosByCedula(String cedula) {
        String sql = "SELECT ID_CREDITO, CEDULA, MONTO_APROBADO, MONTO_SOLICITADO, " +
                     "PLAZO_MESES, TASA_INTERES_ANUAL, MONTO_MAXIMO_AUTORIZADO, " +
                     "FECHA_OTORGAMIENTO, ESTADO FROM CREDITO " +
                     "WHERE CEDULA = ? AND ESTADO <> 'CANCELADO'";
        List<Credito> creditos = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedula);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    creditos.add(mapResultSetToCredito(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return creditos;
    }
    
    public boolean tieneCreditosActivos(String cedula) {
        String sql = "SELECT COUNT(*) as total FROM CREDITO " +
                     "WHERE CEDULA = ? AND ESTADO IN ('APROBADO', 'VIGENTE')";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedula);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public List<Credito> findAll() {
        String sql = "SELECT ID_CREDITO, CEDULA, MONTO_APROBADO, MONTO_SOLICITADO, " +
                     "PLAZO_MESES, TASA_INTERES_ANUAL, MONTO_MAXIMO_AUTORIZADO, " +
                     "FECHA_OTORGAMIENTO, ESTADO FROM CREDITO";
        List<Credito> creditos = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                creditos.add(mapResultSetToCredito(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return creditos;
    }
    
    public Credito save(Credito credito) {
        if (credito.getIdCredito() == null) {
            return insert(credito);
        } else {
            return update(credito);
        }
    }
    
    private Credito insert(Credito credito) {
        String sql = "INSERT INTO CREDITO (CEDULA, MONTO_APROBADO, MONTO_SOLICITADO, " +
                     "PLAZO_MESES, TASA_INTERES_ANUAL, MONTO_MAXIMO_AUTORIZADO, " +
                     "FECHA_OTORGAMIENTO, ESTADO) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            String cedula = credito.getCliente() != null ? credito.getCliente().getCedula() : null;
            stmt.setString(1, cedula);
            stmt.setBigDecimal(2, credito.getMontoAprobado());
            stmt.setBigDecimal(3, credito.getMontoSolicitado());
            stmt.setInt(4, credito.getPlazoMeses());
            stmt.setBigDecimal(5, credito.getTasaInteresAnual());
            stmt.setBigDecimal(6, credito.getMontoMaximoAutorizado());
            stmt.setDate(7, Date.valueOf(credito.getFechaOtorgamiento()));
            stmt.setString(8, credito.getEstado());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    credito.setIdCredito(generatedKeys.getInt(1));
                }
            }
            
            return credito;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar crédito", e);
        }
    }
    
    private Credito update(Credito credito) {
        String sql = "UPDATE CREDITO SET CEDULA = ?, MONTO_APROBADO = ?, MONTO_SOLICITADO = ?, " +
                     "PLAZO_MESES = ?, TASA_INTERES_ANUAL = ?, MONTO_MAXIMO_AUTORIZADO = ?, " +
                     "FECHA_OTORGAMIENTO = ?, ESTADO = ? WHERE ID_CREDITO = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String cedula = credito.getCliente() != null ? credito.getCliente().getCedula() : null;
            stmt.setString(1, cedula);
            stmt.setBigDecimal(2, credito.getMontoAprobado());
            stmt.setBigDecimal(3, credito.getMontoSolicitado());
            stmt.setInt(4, credito.getPlazoMeses());
            stmt.setBigDecimal(5, credito.getTasaInteresAnual());
            stmt.setBigDecimal(6, credito.getMontoMaximoAutorizado());
            stmt.setDate(7, Date.valueOf(credito.getFechaOtorgamiento()));
            stmt.setString(8, credito.getEstado());
            stmt.setInt(9, credito.getIdCredito());
            
            stmt.executeUpdate();
            return credito;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar crédito", e);
        }
    }
    
    public void delete(Integer id) {
        String sql = "DELETE FROM CREDITO WHERE ID_CREDITO = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar crédito", e);
        }
    }
    
    private Credito mapResultSetToCredito(ResultSet rs) throws SQLException {
        Credito credito = new Credito();
        credito.setIdCredito(rs.getInt("ID_CREDITO"));
        credito.setMontoAprobado(rs.getBigDecimal("MONTO_APROBADO"));
        credito.setMontoSolicitado(rs.getBigDecimal("MONTO_SOLICITADO"));
        credito.setPlazoMeses(rs.getInt("PLAZO_MESES"));
        credito.setTasaInteresAnual(rs.getBigDecimal("TASA_INTERES_ANUAL"));
        credito.setMontoMaximoAutorizado(rs.getBigDecimal("MONTO_MAXIMO_AUTORIZADO"));
        credito.setFechaOtorgamiento(rs.getDate("FECHA_OTORGAMIENTO").toLocalDate());
        credito.setEstado(rs.getString("ESTADO"));
        
        // Cargar el cliente asociado usando la cédula
        String cedula = rs.getString("CEDULA");
        if (cedula != null && !cedula.isEmpty()) {
            Optional<Cliente> clienteOpt = clienteRepository.findByCedula(cedula);
            clienteOpt.ifPresent(credito::setCliente);
        }
        
        return credito;
    }
}
