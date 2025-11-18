package ec.edu.pinza.ex_banquito_restjava.repositories;

import ec.edu.pinza.ex_banquito_restjava.config.DatabaseConnection;
import ec.edu.pinza.ex_banquito_restjava.entities.Movimiento;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para la entidad Movimiento usando JDBC
 */
public class MovimientoRepository {
    
    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();
    
    public List<Movimiento> findByCedulaAndTipoAndDateRange(String cedula, String tipo, LocalDate fechaDesde, LocalDate fechaHasta) {
        String sql = "SELECT m.COD_MOVIMIENTO, m.NUM_CUENTA, m.TIPO, m.VALOR, m.FECHA " +
                     "FROM MOVIMIENTO m " +
                     "INNER JOIN CUENTA c ON m.NUM_CUENTA = c.NUM_CUENTA " +
                     "WHERE c.CEDULA = ? AND m.TIPO = ? AND m.FECHA BETWEEN ? AND ?";
        List<Movimiento> movimientos = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedula);
            stmt.setString(2, tipo);
            stmt.setDate(3, Date.valueOf(fechaDesde));
            stmt.setDate(4, Date.valueOf(fechaHasta));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Movimiento movimiento = new Movimiento();
                    movimiento.setCodMovimiento(rs.getInt("COD_MOVIMIENTO"));
                    movimiento.setTipo(rs.getString("TIPO"));
                    movimiento.setValor(rs.getBigDecimal("VALOR"));
                    movimiento.setFecha(rs.getDate("FECHA").toLocalDate());
                    movimientos.add(movimiento);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return movimientos;
    }
}
