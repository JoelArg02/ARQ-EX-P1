package ec.edu.pinza.ex_banquito_restjava.repositories;

import ec.edu.pinza.ex_banquito_restjava.config.DatabaseConnection;
import ec.edu.pinza.ex_banquito_restjava.entities.CuotaAmortizacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad CuotaAmortizacion usando JDBC
 */
public class CuotaAmortizacionRepository {
    
    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();
    
    public Optional<CuotaAmortizacion> findById(Integer id) {
        String sql = "SELECT ID_CUOTA, ID_CREDITO, NUMERO_CUOTA, VALOR_CUOTA, " +
                     "INTERES_PAGADO, CAPITAL_PAGADO, SALDO_RESTANTE " +
                     "FROM CUOTA_AMORTIZACION WHERE ID_CUOTA = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCuota(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public List<CuotaAmortizacion> findByIdCredito(Integer idCredito) {
        String sql = "SELECT ID_CUOTA, ID_CREDITO, NUMERO_CUOTA, VALOR_CUOTA, " +
                     "INTERES_PAGADO, CAPITAL_PAGADO, SALDO_RESTANTE " +
                     "FROM CUOTA_AMORTIZACION WHERE ID_CREDITO = ? ORDER BY NUMERO_CUOTA";
        List<CuotaAmortizacion> cuotas = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCredito);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cuotas.add(mapResultSetToCuota(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cuotas;
    }
    
    public List<CuotaAmortizacion> findAll() {
        String sql = "SELECT ID_CUOTA, ID_CREDITO, NUMERO_CUOTA, VALOR_CUOTA, " +
                     "INTERES_PAGADO, CAPITAL_PAGADO, SALDO_RESTANTE " +
                     "FROM CUOTA_AMORTIZACION";
        List<CuotaAmortizacion> cuotas = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                cuotas.add(mapResultSetToCuota(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cuotas;
    }
    
    public CuotaAmortizacion save(CuotaAmortizacion cuota) {
        if (cuota.getIdCuota() == null) {
            return insert(cuota);
        } else {
            return update(cuota);
        }
    }
    
    private CuotaAmortizacion insert(CuotaAmortizacion cuota) {
        String sql = "INSERT INTO CUOTA_AMORTIZACION (ID_CREDITO, NUMERO_CUOTA, VALOR_CUOTA, " +
                     "INTERES_PAGADO, CAPITAL_PAGADO, SALDO_RESTANTE) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            Integer idCredito = cuota.getCredito() != null ? cuota.getCredito().getIdCredito() : null;
            stmt.setInt(1, idCredito);
            stmt.setInt(2, cuota.getNumeroCuota());
            stmt.setBigDecimal(3, cuota.getValorCuota());
            stmt.setBigDecimal(4, cuota.getInteresPagado());
            stmt.setBigDecimal(5, cuota.getCapitalPagado());
            stmt.setBigDecimal(6, cuota.getSaldoRestante());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cuota.setIdCuota(generatedKeys.getInt(1));
                }
            }
            
            return cuota;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar cuota de amortización", e);
        }
    }
    
    private CuotaAmortizacion update(CuotaAmortizacion cuota) {
        String sql = "UPDATE CUOTA_AMORTIZACION SET ID_CREDITO = ?, NUMERO_CUOTA = ?, " +
                     "VALOR_CUOTA = ?, INTERES_PAGADO = ?, CAPITAL_PAGADO = ?, " +
                     "SALDO_RESTANTE = ? WHERE ID_CUOTA = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            Integer idCredito = cuota.getCredito() != null ? cuota.getCredito().getIdCredito() : null;
            stmt.setInt(1, idCredito);
            stmt.setInt(2, cuota.getNumeroCuota());
            stmt.setBigDecimal(3, cuota.getValorCuota());
            stmt.setBigDecimal(4, cuota.getInteresPagado());
            stmt.setBigDecimal(5, cuota.getCapitalPagado());
            stmt.setBigDecimal(6, cuota.getSaldoRestante());
            stmt.setInt(7, cuota.getIdCuota());
            
            stmt.executeUpdate();
            return cuota;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar cuota de amortización", e);
        }
    }
    
    public void delete(Integer id) {
        String sql = "DELETE FROM CUOTA_AMORTIZACION WHERE ID_CUOTA = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar cuota de amortización", e);
        }
    }
    
    private CuotaAmortizacion mapResultSetToCuota(ResultSet rs) throws SQLException {
        CuotaAmortizacion cuota = new CuotaAmortizacion();
        cuota.setIdCuota(rs.getInt("ID_CUOTA"));
        cuota.setNumeroCuota(rs.getInt("NUMERO_CUOTA"));
        cuota.setValorCuota(rs.getBigDecimal("VALOR_CUOTA"));
        cuota.setInteresPagado(rs.getBigDecimal("INTERES_PAGADO"));
        cuota.setCapitalPagado(rs.getBigDecimal("CAPITAL_PAGADO"));
        cuota.setSaldoRestante(rs.getBigDecimal("SALDO_RESTANTE"));
        // El ID_CREDITO se carga como parte de la relación Credito si es necesario
        return cuota;
    }
}
