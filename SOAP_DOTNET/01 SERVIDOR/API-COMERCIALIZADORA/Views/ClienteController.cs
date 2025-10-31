using API_Comercializadora.Application.Interface;
using API_Comercializadora.Application.Service;
using API_Comercializadora.Configuration;
using API_Comercializadora.Models;
using CoreWCF;

namespace API_Comercializadora.Views;

[ServiceContract]
public interface IClienteController
{
    [OperationContract]
    Task<List<Cliente>> GetAllClientes();

    [OperationContract]
    Task<Cliente?> GetClienteById(int id);

    [OperationContract]
    Task<Cliente> CreateCliente(
        string cedula,
        string nombreCompleto,
        string? correo,
        string? telefono,
        string? direccion
    );

    [OperationContract]
    Task<Cliente?> UpdateCliente(
        int id,
        string cedula,
        string nombreCompleto,
        string? correo,
        string? telefono,
        string? direccion
    );

    [OperationContract]
    Task<bool> DeleteCliente(int id);
}

public class ClienteController : IClienteController
{
    private readonly IClienteService _clienteService;

    public ClienteController(AppDbContext context)
    {
        _clienteService = new ClienteService(context);
    }

    public async Task<List<Cliente>> GetAllClientes()
    {
        return await _clienteService.GetAllClientes();
    }

    public async Task<Cliente?> GetClienteById(int id)
    {
        return await _clienteService.GetClienteById(id);
    }

    public async Task<Cliente> CreateCliente(
        string cedula,
        string nombreCompleto,
        string? correo,
        string? telefono,
        string? direccion
    )
    {
        return await _clienteService.CreateCliente(
            cedula,
            nombreCompleto,
            correo,
            telefono,
            direccion
        );
    }

    public async Task<Cliente?> UpdateCliente(
        int id,
        string cedula,
        string nombreCompleto,
        string? correo,
        string? telefono,
        string? direccion
    )
    {
        return await _clienteService.UpdateCliente(
            id,
            cedula,
            nombreCompleto,
            correo,
            telefono,
            direccion
        );
    }

    public async Task<bool> DeleteCliente(int id)
    {
        return await _clienteService.DeleteCliente(id);
    }
}
