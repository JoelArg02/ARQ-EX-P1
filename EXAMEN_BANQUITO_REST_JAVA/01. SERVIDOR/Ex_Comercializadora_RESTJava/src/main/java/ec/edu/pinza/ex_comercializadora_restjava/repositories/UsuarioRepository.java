package ec.edu.pinza.ex_comercializadora_restjava.repositories;

import ec.edu.pinza.ex_comercializadora_restjava.config.DatabaseConnection;
import ec.edu.pinza.ex_comercializadora_restjava.entities.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar operaciones CRUD de Usuario usando JDBC
 */
public class UsuarioRepository {
    
    private final DatabaseConnection dbConnection;
    
    private static final String SQL_SELECT_BASE = 
        "SELECT u.idUsuario, u.username, u.password, u.idCliente, u.rol, u.activo, " +
        "c.cedula, c.nombre as nombreCliente " +
        "FROM Usuario u " +
        "LEFT JOIN ClienteCom c ON u.idCliente = c.idCliente ";
    
    public UsuarioRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Busca un usuario por su ID
     */
    public Optional<Usuario> findById(Integer id) {
        String sql = SQL_SELECT_BASE + "WHERE u.idUsuario = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    /**
     * Busca un usuario por su username
     */
    public Optional<Usuario> findByUsername(String username) {
        String sql = SQL_SELECT_BASE + "WHERE u.username = ? AND u.activo = TRUE";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por username: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    /**
     * Autentica un usuario verificando username y password
     */
    public Optional<Usuario> authenticate(String username, String password) {
        String sql = SQL_SELECT_BASE + "WHERE u.username = ? AND u.password = ? AND u.activo = TRUE";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapResultSetToUsuario(rs);
                    // No retornar el password en la respuesta
                    usuario.setPassword(null);
                    return Optional.of(usuario);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    /**
     * Obtiene todos los usuarios
     */
    public List<Usuario> findAll() {
        String sql = SQL_SELECT_BASE + "ORDER BY u.rol, u.username";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Usuario usuario = mapResultSetToUsuario(rs);
                usuario.setPassword(null); // No exponer passwords
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        
        return usuarios;
    }
    
    /**
     * Guarda o actualiza un usuario
     */
    public Usuario save(Usuario usuario) {
        if (usuario.getIdUsuario() == null) {
            return insert(usuario);
        } else {
            return update(usuario);
        }
    }
    
    private Usuario insert(Usuario usuario) {
        String sql = "INSERT INTO Usuario (username, password, idCliente, rol, activo) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getPassword());
            if (usuario.getIdCliente() != null) {
                stmt.setInt(3, usuario.getIdCliente());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setString(4, usuario.getRol() != null ? usuario.getRol() : "CLIENTE");
            stmt.setBoolean(5, usuario.getActivo() != null ? usuario.getActivo() : true);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setIdUsuario(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        
        return usuario;
    }
    
    private Usuario update(Usuario usuario) {
        String sql = "UPDATE Usuario SET username = ?, password = ?, idCliente = ?, rol = ?, activo = ? WHERE idUsuario = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getPassword());
            if (usuario.getIdCliente() != null) {
                stmt.setInt(3, usuario.getIdCliente());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setString(4, usuario.getRol());
            stmt.setBoolean(5, usuario.getActivo());
            stmt.setInt(6, usuario.getIdUsuario());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        
        return usuario;
    }
    
    /**
     * Elimina un usuario por su ID
     */
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Usuario WHERE idUsuario = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mapea un ResultSet a un objeto Usuario
     */
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("idUsuario"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPassword(rs.getString("password"));
        
        int idCliente = rs.getInt("idCliente");
        if (!rs.wasNull()) {
            usuario.setIdCliente(idCliente);
        }
        
        usuario.setRol(rs.getString("rol"));
        usuario.setActivo(rs.getBoolean("activo"));
        usuario.setCedula(rs.getString("cedula"));
        usuario.setNombreCliente(rs.getString("nombreCliente"));
        
        return usuario;
    }
}
