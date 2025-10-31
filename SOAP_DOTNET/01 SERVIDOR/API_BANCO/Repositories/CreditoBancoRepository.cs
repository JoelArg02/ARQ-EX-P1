using Microsoft.EntityFrameworkCore;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using API_BANCO.Repositories.Interfaces;

namespace API_BANCO.Repositories;

public class CreditoBancoRepository : ICreditoBancoRepository
{
    private readonly AppDbContext _context;

    public CreditoBancoRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<List<CreditoBanco>> GetAllAsync()
    {
        return await _context.CreditosBanco
            .Include(c => c.ClienteBanco)
            .Include(c => c.Amortizaciones)
            .ToListAsync();
    }

    public async Task<CreditoBanco?> GetByIdAsync(int id)
    {
        return await _context.CreditosBanco
            .Include(c => c.ClienteBanco)
            .Include(c => c.Amortizaciones)
            .FirstOrDefaultAsync(c => c.Id == id);
    }

    public async Task<List<CreditoBanco>> GetByClienteBancoIdAsync(int clienteBancoId)
    {
        return await _context.CreditosBanco
            .Where(c => c.ClienteBancoId == clienteBancoId)
            .Include(c => c.ClienteBanco)
            .Include(c => c.Amortizaciones)
            .ToListAsync();
    }

    public async Task<CreditoBanco?> GetActivoByClienteBancoIdAsync(int clienteBancoId)
    {
        return await _context.CreditosBanco
            .Where(c => c.ClienteBancoId == clienteBancoId && c.Activo)
            .Include(c => c.ClienteBanco)
            .Include(c => c.Amortizaciones)
            .FirstOrDefaultAsync();
    }

    public async Task<CreditoBanco> CreateAsync(CreditoBanco creditoBanco)
    {
        _context.CreditosBanco.Add(creditoBanco);
        await _context.SaveChangesAsync();
        return creditoBanco;
    }

    public async Task<CreditoBanco?> UpdateAsync(CreditoBanco creditoBanco)
    {
        var existing = await _context.CreditosBanco.FindAsync(creditoBanco.Id);
        if (existing == null) return null;

        existing.MontoAprobado = creditoBanco.MontoAprobado;
        existing.NumeroCuotas = creditoBanco.NumeroCuotas;
        existing.TasaInteres = creditoBanco.TasaInteres;
        existing.Activo = creditoBanco.Activo;

        await _context.SaveChangesAsync();
        return existing;
    }

    public async Task<bool> DeleteAsync(int id)
    {
        var credito = await _context.CreditosBanco.FindAsync(id);
        if (credito == null) return false;

        _context.CreditosBanco.Remove(credito);
        await _context.SaveChangesAsync();
        return true;
    }
}
