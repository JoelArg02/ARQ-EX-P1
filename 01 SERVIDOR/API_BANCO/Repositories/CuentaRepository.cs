using Microsoft.EntityFrameworkCore;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using API_BANCO.Repositories.Interfaces;

namespace API_BANCO.Repositories;

public class CuentaRepository : ICuentaRepository
{
    private readonly AppDbContext _context;

    public CuentaRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<List<Cuenta>> GetAllAsync()
    {
        return await _context.Cuentas
            .Include(c => c.ClienteBanco)
            .ToListAsync();
    }

    public async Task<Cuenta?> GetByIdAsync(int id)
    {
        return await _context.Cuentas
            .Include(c => c.ClienteBanco)
            .FirstOrDefaultAsync(c => c.Id == id);
    }

    public async Task<Cuenta?> GetByNumeroCuentaAsync(string numeroCuenta)
    {
        return await _context.Cuentas
            .Include(c => c.ClienteBanco)
            .FirstOrDefaultAsync(c => c.NumeroCuenta == numeroCuenta);
    }

    public async Task<List<Cuenta>> GetByClienteBancoIdAsync(int clienteBancoId)
    {
        return await _context.Cuentas
            .Where(c => c.ClienteBancoId == clienteBancoId)
            .Include(c => c.ClienteBanco)
            .ToListAsync();
    }

    public async Task<Cuenta> CreateAsync(Cuenta cuenta)
    {
        _context.Cuentas.Add(cuenta);
        await _context.SaveChangesAsync();
        return cuenta;
    }

    public async Task<Cuenta?> UpdateAsync(Cuenta cuenta)
    {
        var existing = await _context.Cuentas.FindAsync(cuenta.Id);
        if (existing == null) return null;

        existing.NumeroCuenta = cuenta.NumeroCuenta;
        existing.Saldo = cuenta.Saldo;
        existing.TipoCuenta = cuenta.TipoCuenta;
        existing.ClienteBancoId = cuenta.ClienteBancoId;

        await _context.SaveChangesAsync();
        return existing;
    }

    public async Task<bool> DeleteAsync(int id)
    {
        var cuenta = await _context.Cuentas.FindAsync(id);
        if (cuenta == null) return false;

        _context.Cuentas.Remove(cuenta);
        await _context.SaveChangesAsync();
        return true;
    }
}
