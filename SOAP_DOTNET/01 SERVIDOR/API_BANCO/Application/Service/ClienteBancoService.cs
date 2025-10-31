using API_BANCO.Application.Interface;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using API_BANCO.Models.Enums;
using API_BANCO.Repositories;
using API_BANCO.Repositories.Interfaces;

namespace API_BANCO.Application.Service;

public class ClienteBancoService : IClienteBancoService
{
    private readonly AppDbContext _context;
    private readonly IClienteBancoRepository _repository;

    public ClienteBancoService(AppDbContext context)
    {
        _context = context;
        _repository = new ClienteBancoRepository(_context);
    }

    public async Task<List<ClienteBanco>> GetAllClientesBanco()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<ClienteBanco?> GetClienteBancoById(int id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<ClienteBanco?> GetClienteBancoByCedula(string cedula)
    {
        return await _repository.GetByCedulaAsync(cedula);
    }

    public async Task<ClienteBanco> CreateClienteBanco(string cedula, string nombreCompleto, int estadoCivil, DateTime fechaNacimiento)
    {
        var clienteBanco = new ClienteBanco
        {
            Cedula = cedula,
            NombreCompleto = nombreCompleto,
            EstadoCivil = (EstadoCivil)estadoCivil,
            FechaNacimiento = fechaNacimiento,
            TieneCreditoActivo = false
        };
        return await _repository.CreateAsync(clienteBanco);
    }

    public async Task<ClienteBanco?> UpdateClienteBanco(int id, string cedula, string nombreCompleto, int estadoCivil, DateTime fechaNacimiento, bool tieneCreditoActivo)
    {
        var clienteBanco = new ClienteBanco
        {
            Id = id,
            Cedula = cedula,
            NombreCompleto = nombreCompleto,
            EstadoCivil = (EstadoCivil)estadoCivil,
            FechaNacimiento = fechaNacimiento,
            TieneCreditoActivo = tieneCreditoActivo
        };
        return await _repository.UpdateAsync(clienteBanco);
    }

    public async Task<bool> DeleteClienteBanco(int id)
    {
        return await _repository.DeleteAsync(id);
    }
}
