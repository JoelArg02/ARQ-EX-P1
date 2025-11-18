package ec.edu.pinza.ex_comercializadora_restjava.repositories;

import ec.edu.pinza.ex_comercializadora_restjava.config.DatabaseConnection;
import ec.edu.pinza.ex_comercializadora_restjava.entities.ClienteCom;
import ec.edu.pinza.ex_comercializadora_restjava.entities.DetalleFactura;
import ec.edu.pinza.ex_comercializadora_restjava.entities.Factura;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FacturaRepository {
    
    private final DatabaseConnection dbConnection;
    private final ClienteComRepository clienteRepository;
    private final DetalleFacturaRepository detalleRepository;
    
    public FacturaRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.clienteRepository = new ClienteComRepository();
        this.detalleRepository = new DetalleFacturaRepository();
    }
    
    public Optional<Factura> findById(Integer id) {
        String sql = "SELECT idFactura, idCliente, fecha, total, formaPago, idCreditoBanco " +
                     "FROM Factura WHERE idFactura = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Factura factura = mapResultSetToFactura(rs);
                    // Cargar detalles de la factura
                    factura.setDetalles(detalleRepository.findByFacturaId(factura.getIdFactura()));
                    return Optional.of(factura);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar factura por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public List<Factura> findByClienteId(Integer clienteId) {
        String sql = "SELECT idFactura, idCliente, fecha, total, formaPago, idCreditoBanco " +
                     "FROM Factura WHERE idCliente = ? ORDER BY fecha DESC";
        List<Factura> facturas = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clienteId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                // PASO 1: Cargar datos del ResultSet
                List<FacturaBasica> facturasBasicas = new ArrayList<>();
                while (rs.next()) {
                    FacturaBasica fb = new FacturaBasica();
                    fb.idFactura = rs.getInt("idFactura");
                    fb.idCliente = rs.getInt("idCliente");
                    fb.fecha = rs.getDate("fecha").toLocalDate();
                    fb.total = rs.getBigDecimal("total");
                    fb.formaPago = rs.getString("formaPago");
                    int idCreditoBanco = rs.getInt("idCreditoBanco");
                    if (!rs.wasNull()) {
                        fb.idCreditoBanco = idCreditoBanco;
                    }
                    facturasBasicas.add(fb);
                }
                
                // PASO 2: Cargar clientes y detalles
                for (FacturaBasica fb : facturasBasicas) {
                    try {
                        Factura factura = new Factura();
                        factura.setIdFactura(fb.idFactura);
                        factura.setFecha(fb.fecha);
                        factura.setTotal(fb.total);
                        factura.setFormaPago(fb.formaPago);
                        factura.setIdCreditoBanco(fb.idCreditoBanco);
                        
                        Optional<ClienteCom> clienteOpt = clienteRepository.findById(fb.idCliente);
                        if (clienteOpt.isPresent()) {
                            factura.setCliente(clienteOpt.get());
                            factura.setDetalles(detalleRepository.findByFacturaId(factura.getIdFactura()));
                            facturas.add(factura);
                        } else {
                            System.out.println("⚠️ Factura " + fb.idFactura + " omitida: cliente no encontrado");
                        }
                    } catch (Exception e) {
                        System.err.println("⚠️ Error al mapear factura " + fb.idFactura + ": " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar facturas por cliente: " + e.getMessage());
            e.printStackTrace();
        }
        
        return facturas;
    }
    
    public List<Factura> findAll() {
        String sql = "SELECT idFactura, idCliente, fecha, total, formaPago, idCreditoBanco " +
                     "FROM Factura ORDER BY idFactura DESC";
        List<Factura> facturas = new ArrayList<>();
        
        System.out.println("🔍 FacturaRepository.findAll() - Ejecutando query: " + sql);
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            // PASO 1: Cargar TODOS los datos del ResultSet ANTES de cerrarlo
            List<FacturaBasica> facturasBasicas = new ArrayList<>();
            int count = 0;
            while (rs.next()) {
                count++;
                FacturaBasica fb = new FacturaBasica();
                fb.idFactura = rs.getInt("idFactura");
                fb.idCliente = rs.getInt("idCliente");
                fb.fecha = rs.getDate("fecha").toLocalDate();
                fb.total = rs.getBigDecimal("total");
                fb.formaPago = rs.getString("formaPago");
                int idCreditoBanco = rs.getInt("idCreditoBanco");
                if (!rs.wasNull()) {
                    fb.idCreditoBanco = idCreditoBanco;
                }
                facturasBasicas.add(fb);
                System.out.println("🔍 Cargado factura #" + count + " - ID: " + fb.idFactura);
            }
            
            System.out.println("📊 Total facturas cargadas del ResultSet: " + count);
            
            // PASO 2: Ahora que el ResultSet está cerrado, cargar clientes y detalles
            for (FacturaBasica fb : facturasBasicas) {
                try {
                    Factura factura = new Factura();
                    factura.setIdFactura(fb.idFactura);
                    factura.setFecha(fb.fecha);
                    factura.setTotal(fb.total);
                    factura.setFormaPago(fb.formaPago);
                    factura.setIdCreditoBanco(fb.idCreditoBanco);
                    
                    // Cargar cliente
                    Optional<ClienteCom> clienteOpt = clienteRepository.findById(fb.idCliente);
                    if (clienteOpt.isPresent()) {
                        factura.setCliente(clienteOpt.get());
                        // Cargar detalles
                        factura.setDetalles(detalleRepository.findByFacturaId(factura.getIdFactura()));
                        facturas.add(factura);
                        System.out.println("✅ Factura " + fb.idFactura + " completa (Cliente: " + clienteOpt.get().getNombre() + ")");
                    } else {
                        System.out.println("⚠️ Factura " + fb.idFactura + " omitida: cliente " + fb.idCliente + " no encontrado");
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Error al procesar factura " + fb.idFactura + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("📊 Total facturas agregadas: " + facturas.size());
        } catch (SQLException e) {
            System.err.println("❌ Error SQL al obtener todas las facturas: " + e.getMessage());
            e.printStackTrace();
        }
        
        return facturas;
    }
    
    // Clase auxiliar para almacenar datos básicos de factura
    private static class FacturaBasica {
        Integer idFactura;
        Integer idCliente;
        LocalDate fecha;
        BigDecimal total;
        String formaPago;
        Integer idCreditoBanco;
    }
    
    public Factura save(Factura factura) {
        if (factura.getIdFactura() == null) {
            return insert(factura);
        } else {
            return update(factura);
        }
    }
    
    private Factura insert(Factura factura) {
        String sql = "INSERT INTO Factura (idCliente, fecha, total, formaPago, idCreditoBanco) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Obtener idCliente del objeto Cliente si existe
            Integer idCliente = null;
            if (factura.getCliente() != null && factura.getCliente().getIdCliente() != null) {
                idCliente = factura.getCliente().getIdCliente();
            }
            
            if (idCliente == null) {
                throw new IllegalArgumentException("El idCliente es requerido para crear una factura");
            }
            
            stmt.setInt(1, idCliente);
            stmt.setDate(2, Date.valueOf(factura.getFecha()));
            stmt.setBigDecimal(3, factura.getTotal());
            stmt.setString(4, factura.getFormaPago());
            
            if (factura.getIdCreditoBanco() != null) {
                stmt.setInt(5, factura.getIdCreditoBanco());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    factura.setIdFactura(generatedKeys.getInt(1));
                }
            }
            
            // Guardar detalles de la factura
            if (factura.getDetalles() != null && !factura.getDetalles().isEmpty()) {
                for (DetalleFactura detalle : factura.getDetalles()) {
                    detalle.setFactura(factura);
                    detalleRepository.save(detalle);
                }
            }
            
            return factura;
        } catch (SQLException e) {
            System.err.println("Error al insertar factura: " + e.getMessage());
            throw new RuntimeException("Error al insertar factura", e);
        }
    }
    
    private Factura update(Factura factura) {
        String sql = "UPDATE Factura SET idCliente = ?, fecha = ?, total = ?, " +
                     "formaPago = ?, idCreditoBanco = ? WHERE idFactura = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Obtener idCliente del objeto Cliente si existe
            Integer idCliente = null;
            if (factura.getCliente() != null && factura.getCliente().getIdCliente() != null) {
                idCliente = factura.getCliente().getIdCliente();
            }
            
            if (idCliente == null) {
                throw new IllegalArgumentException("El idCliente es requerido para actualizar una factura");
            }
            
            stmt.setInt(1, idCliente);
            stmt.setDate(2, Date.valueOf(factura.getFecha()));
            stmt.setBigDecimal(3, factura.getTotal());
            stmt.setString(4, factura.getFormaPago());
            
            if (factura.getIdCreditoBanco() != null) {
                stmt.setInt(5, factura.getIdCreditoBanco());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            stmt.setInt(6, factura.getIdFactura());
            
            stmt.executeUpdate();
            
            // Actualizar detalles: eliminar los anteriores y guardar los nuevos
            detalleRepository.deleteByFacturaId(factura.getIdFactura());
            if (factura.getDetalles() != null && !factura.getDetalles().isEmpty()) {
                for (DetalleFactura detalle : factura.getDetalles()) {
                    detalle.setFactura(factura);
                    detalleRepository.save(detalle);
                }
            }
            
            return factura;
        } catch (SQLException e) {
            System.err.println("Error al actualizar factura: " + e.getMessage());
            throw new RuntimeException("Error al actualizar factura", e);
        }
    }
    
    public void delete(Integer id) {
        String sql = "DELETE FROM Factura WHERE idFactura = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar factura: " + e.getMessage());
            throw new RuntimeException("Error al eliminar factura", e);
        }
    }
    
    private Factura mapResultSetToFactura(ResultSet rs) throws SQLException {
        Factura factura = new Factura();
        factura.setIdFactura(rs.getInt("idFactura"));
        factura.setFecha(rs.getDate("fecha").toLocalDate());
        factura.setTotal(rs.getBigDecimal("total"));
        factura.setFormaPago(rs.getString("formaPago"));
        
        int idCreditoBanco = rs.getInt("idCreditoBanco");
        if (!rs.wasNull()) {
            factura.setIdCreditoBanco(idCreditoBanco);
        }
        
        // Cargar el cliente asociado desde el repositorio
        int idCliente = rs.getInt("idCliente");
        Optional<ClienteCom> clienteOpt = clienteRepository.findById(idCliente);
        clienteOpt.ifPresent(factura::setCliente);
        
        return factura;
    }
}
