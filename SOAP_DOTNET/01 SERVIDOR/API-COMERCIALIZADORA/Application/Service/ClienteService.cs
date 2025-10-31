using API_Comercializadora.Application.Interface;
using API_Comercializadora.Configuration;
using API_Comercializadora.Models;
using API_Comercializadora.Repositories;
using API_Comercializadora.Repositories.Interfaces;

namespace API_Comercializadora.Application.Service;

public class ClienteService : IClienteService
{
    private readonly AppDbContext _context;
    private readonly IClienteRepository _repository;

    public ClienteService(AppDbContext context)
    {
        _context = context;
        _repository = new ClienteRepository(_context);
    }

    public async Task<List<Cliente>> GetAllClientes()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<Cliente?> GetClienteById(int id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<Cliente> CreateCliente(
        string cedula,
        string nombreCompleto,
        string? correo,
        string? telefono,
        string? direccion
    )
    {
        var cliente = new Cliente
        {
            Cedula = cedula,
            NombreCompleto = nombreCompleto,
            Correo = correo,
            Telefono = telefono,
            Direccion = direccion,
        };

        return await _repository.CreateAsync(cliente);
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
        var cliente = new Cliente
        {
            Id = id,
            Cedula = cedula,
            NombreCompleto = nombreCompleto,
            Correo = correo,
            Telefono = telefono,
            Direccion = direccion,
        };

        return await _repository.UpdateAsync(cliente);
    }

    public async Task<bool> DeleteCliente(int id)
    {
        return await _repository.DeleteAsync(id);
    }
}
