using API_Comercializadora.Models;

namespace API_Comercializadora.Application.Interface;

public interface IClienteService
{
    Task<List<Cliente>> GetAllClientes();
    Task<Cliente?> GetClienteById(int id);
    Task<Cliente> CreateCliente(
        string cedula,
        string nombreCompleto,
        string? correo,
        string? telefono,
        string? direccion
    );
    Task<Cliente?> UpdateCliente(
        int id,
        string cedula,
        string nombreCompleto,
        string? correo,
        string? telefono,
        string? direccion
    );
    Task<bool> DeleteCliente(int id);
}
