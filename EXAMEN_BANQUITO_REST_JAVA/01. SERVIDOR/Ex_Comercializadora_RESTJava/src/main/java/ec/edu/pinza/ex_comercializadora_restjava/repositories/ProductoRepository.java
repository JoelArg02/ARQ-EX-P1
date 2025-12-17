package ec.edu.pinza.ex_comercializadora_restjava.repositories;

import ec.edu.pinza.ex_comercializadora_restjava.config.DatabaseConnection;
import ec.edu.pinza.ex_comercializadora_restjava.entities.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar operaciones CRUD de Producto usando JDBC
 * Utiliza el patrón Singleton para la conexión a la base de datos
 */
public class ProductoRepository {
    
    private final DatabaseConnection dbConnection;
    
    public ProductoRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Buscar producto por ID
     */
    public Optional<Producto> findById(Integer id) {
        String sql = "SELECT idProducto, codigo, nombre, precio, stock, imagen FROM Producto WHERE idProducto = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToProducto(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            System.err.println("Error al buscar producto por ID: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Buscar producto por código
     */
    public Optional<Producto> findByCodigo(String codigo) {
        String sql = "SELECT idProducto, codigo, nombre, precio, stock, imagen FROM Producto WHERE codigo = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToProducto(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            System.err.println("Error al buscar producto por código: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Obtener todos los productos
     */
    public List<Producto> findAll() {
        String sql = "SELECT idProducto, codigo, nombre, precio, stock, imagen FROM Producto ORDER BY nombre";
        List<Producto> productos = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los productos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return productos;
    }
    
    /**
     * Guardar o actualizar producto
     */
    public Producto save(Producto producto) {
        // null o 0 significa nuevo producto
        if (producto.getIdProducto() == null || producto.getIdProducto() == 0) {
            return insert(producto);
        } else {
            return update(producto);
        }
    }
    
    /**
     * Insertar nuevo producto
     */
    private Producto insert(Producto producto) {
        String sql = "INSERT INTO Producto (codigo, nombre, precio, stock, imagen) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setBigDecimal(3, producto.getPrecio());
            stmt.setInt(4, producto.getStock());
            stmt.setString(5, producto.getImagen());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    producto.setIdProducto(generatedKeys.getInt(1));
                }
            }
            
            return producto;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            throw new RuntimeException("Error al insertar producto", e);
        }
    }
    
    /**
     * Actualizar producto existente
     * Si el código o imagen no vienen, los obtiene de la BD (para no sobrescribirlos)
     */
    private Producto update(Producto producto) {
        // Obtener datos actuales para preservar campos que no vienen en el request
        Optional<Producto> productoExistente = findById(producto.getIdProducto());
        if (productoExistente.isEmpty()) {
            throw new RuntimeException("Producto no encontrado con ID: " + producto.getIdProducto());
        }
        
        Producto actual = productoExistente.get();
        
        // Preservar código si no viene en el request (el código no debería cambiar)
        if (producto.getCodigo() == null || producto.getCodigo().isEmpty()) {
            producto.setCodigo(actual.getCodigo());
        }
        
        // Preservar imagen si no viene en el request
        if (producto.getImagen() == null) {
            producto.setImagen(actual.getImagen());
        }
        
        String sql = "UPDATE Producto SET nombre = ?, precio = ?, stock = ?, imagen = ? WHERE idProducto = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, producto.getNombre());
            stmt.setBigDecimal(2, producto.getPrecio());
            stmt.setInt(3, producto.getStock());
            stmt.setString(4, producto.getImagen());
            stmt.setInt(5, producto.getIdProducto());
            
            stmt.executeUpdate();
            return producto;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            throw new RuntimeException("Error al actualizar producto", e);
        }
    }
    
    /**
     * Eliminar producto por ID
     */
    public void delete(Integer id) {
        String sql = "DELETE FROM Producto WHERE idProducto = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            throw new RuntimeException("Error al eliminar producto", e);
        }
    }
    
    /**
     * Mapear ResultSet a objeto Producto
     */
    private Producto mapResultSetToProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setIdProducto(rs.getInt("idProducto"));
        producto.setCodigo(rs.getString("codigo"));
        producto.setNombre(rs.getString("nombre"));
        producto.setPrecio(rs.getBigDecimal("precio"));
        producto.setStock(rs.getInt("stock"));
        producto.setImagen(rs.getString("imagen"));
        return producto;
    }
}
