using Microsoft.EntityFrameworkCore;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using API_BANCO.Repositories.Interfaces;

namespace API_BANCO.Repositories;

public class ClienteBancoRepository : IClienteBancoRepository
{
    private readonly AppDbContext _context;

    public ClienteBancoRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<List<ClienteBanco>> GetAllAsync()
    {
        return await _context.ClientesBanco.ToListAsync();
    }

    public async Task<ClienteBanco?> GetByIdAsync(int id)
    {
        return await _context.ClientesBanco.FindAsync(id);
    }

    public async Task<ClienteBanco?> GetByCedulaAsync(string cedula)
    {
        return await _context.ClientesBanco
            .FirstOrDefaultAsync(c => c.Cedula == cedula);
    }

    public async Task<ClienteBanco> CreateAsync(ClienteBanco clienteBanco)
    {
        _context.ClientesBanco.Add(clienteBanco);
        await _context.SaveChangesAsync();
        return clienteBanco;
    }

    public async Task<ClienteBanco?> UpdateAsync(ClienteBanco clienteBanco)
    {
        var existing = await _context.ClientesBanco.FindAsync(clienteBanco.Id);
        if (existing == null) return null;

        existing.Cedula = clienteBanco.Cedula;
        existing.NombreCompleto = clienteBanco.NombreCompleto;
        existing.EstadoCivil = clienteBanco.EstadoCivil;
        existing.FechaNacimiento = clienteBanco.FechaNacimiento;
        existing.TieneCreditoActivo = clienteBanco.TieneCreditoActivo;

        await _context.SaveChangesAsync();
        return existing;
    }

    public async Task<bool> DeleteAsync(int id)
    {
        var clienteBanco = await _context.ClientesBanco.FindAsync(id);
        if (clienteBanco == null) return false;

        _context.ClientesBanco.Remove(clienteBanco);
        await _context.SaveChangesAsync();
        return true;
    }
}
