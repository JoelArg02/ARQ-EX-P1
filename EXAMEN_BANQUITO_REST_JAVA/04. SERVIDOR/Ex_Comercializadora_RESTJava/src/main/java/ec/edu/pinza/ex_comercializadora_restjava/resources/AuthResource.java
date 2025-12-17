package ec.edu.pinza.ex_comercializadora_restjava.resources;

import ec.edu.pinza.ex_comercializadora_restjava.dto.LoginRequestDTO;
import ec.edu.pinza.ex_comercializadora_restjava.dto.LoginResponseDTO;
import ec.edu.pinza.ex_comercializadora_restjava.entities.Usuario;
import ec.edu.pinza.ex_comercializadora_restjava.repositories.UsuarioRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Optional;

/**
 * Recurso REST para autenticación de usuarios
 */
@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    
    /**
     * POST /api/auth/login
     * Autentica un usuario con username y password
     */
    @POST
    @Path("login")
    public Response login(LoginRequestDTO request) {
        System.out.println("=== LOGIN REQUEST ===");
        System.out.println("Username: " + request.getUsername());
        
        // Validar campos requeridos
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(LoginResponseDTO.error("El usuario es requerido"))
                    .build();
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(LoginResponseDTO.error("La contraseña es requerida"))
                    .build();
        }
        
        // Intentar autenticar
        Optional<Usuario> usuarioOpt = usuarioRepository.authenticate(
                request.getUsername().trim(), 
                request.getPassword()
        );
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            System.out.println("Login exitoso para: " + usuario.getUsername() + " - Rol: " + usuario.getRol());
            
            LoginResponseDTO response = LoginResponseDTO.exitoso(
                    usuario.getIdUsuario(),
                    usuario.getUsername(),
                    usuario.getRol(),
                    usuario.getCedula(),
                    usuario.getNombreCliente()
            );
            
            return Response.ok(response).build();
        } else {
            System.out.println("Login fallido para: " + request.getUsername());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(LoginResponseDTO.error("Usuario o contraseña incorrectos"))
                    .build();
        }
    }
    
    /**
     * GET /api/auth/usuarios
     * Obtiene la lista de todos los usuarios (solo para debug/admin)
     */
    @GET
    @Path("usuarios")
    public Response listarUsuarios() {
        return Response.ok(usuarioRepository.findAll()).build();
    }
    
    /**
     * GET /api/auth/test
     * Endpoint de prueba para verificar que el recurso está funcionando
     */
    @GET
    @Path("test")
    public Response test() {
        return Response.ok("{\"status\": \"Auth service OK\"}").build();
    }
}
