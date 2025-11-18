package ec.edu.pinza.ex_comercializadora_restjava.repositories;

import ec.edu.pinza.ex_comercializadora_restjava.config.DatabaseConnection;
import ec.edu.pinza.ex_comercializadora_restjava.entities.DetalleFactura;
import ec.edu.pinza.ex_comercializadora_restjava.entities.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleFacturaRepository {
    
    private final DatabaseConnection dbConnection;
    private final ProductoRepository productoRepository;
    
    public DetalleFacturaRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.productoRepository = new ProductoRepository();
    }
    
    /**
     * Busca todos los detalles de una factura
     */
    public List<DetalleFactura> findByFacturaId(Integer idFactura) {
        String sql = "SELECT idDetalle, idFactura, idProducto, cantidad, precioUnitario, subtotal " +
                     "FROM DetalleFactura WHERE idFactura = ?";
        List<DetalleFactura> detalles = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idFactura);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetalleFactura detalle = mapResultSetToDetalle(rs);
                    if (detalle != null) {
                        detalles.add(detalle);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar detalles de factura: " + e.getMessage());
            e.printStackTrace();
        }
        
        return detalles;
    }
    
    /**
     * Guarda un detalle de factura
     */
    public DetalleFactura save(DetalleFactura detalle) {
        String sql = "INSERT INTO DetalleFactura (idFactura, idProducto, cantidad, precioUnitario, subtotal) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, detalle.getFactura().getIdFactura());
            stmt.setInt(2, detalle.getProducto().getIdProducto());
            stmt.setInt(3, detalle.getCantidad());
            stmt.setBigDecimal(4, detalle.getPrecioUnitario());
            stmt.setBigDecimal(5, detalle.getSubtotal());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    detalle.setIdDetalle(generatedKeys.getInt(1));
                }
            }
            
            return detalle;
        } catch (SQLException e) {
            System.err.println("Error al guardar detalle de factura: " + e.getMessage());
            throw new RuntimeException("Error al guardar detalle de factura", e);
        }
    }
    
    /**
     * Elimina todos los detalles de una factura
     */
    public void deleteByFacturaId(Integer idFactura) {
        String sql = "DELETE FROM DetalleFactura WHERE idFactura = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idFactura);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar detalles de factura: " + e.getMessage());
            throw new RuntimeException("Error al eliminar detalles de factura", e);
        }
    }
    
    private DetalleFactura mapResultSetToDetalle(ResultSet rs) throws SQLException {
        DetalleFactura detalle = new DetalleFactura();
        detalle.setIdDetalle(rs.getInt("idDetalle"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioUnitario(rs.getBigDecimal("precioUnitario"));
        detalle.setSubtotal(rs.getBigDecimal("subtotal"));
        
        // Cargar el producto asociado
        int idProducto = rs.getInt("idProducto");
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto != null) {
            detalle.setProducto(producto);
        } else {
            System.err.println("⚠️ Producto " + idProducto + " no encontrado para detalle " + rs.getInt("idDetalle"));
        }
        
        return detalle;
    }
}
