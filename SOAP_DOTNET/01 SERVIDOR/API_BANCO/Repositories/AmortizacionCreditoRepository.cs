using Microsoft.EntityFrameworkCore;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using API_BANCO.Repositories.Interfaces;

namespace API_BANCO.Repositories;

public class AmortizacionCreditoRepository : IAmortizacionCreditoRepository
{
    private readonly AppDbContext _context;

    public AmortizacionCreditoRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<List<AmortizacionCredito>> GetAllAsync()
    {
        return await _context.AmortizacionesCredito
            .Include(a => a.CreditoBanco)
            .ToListAsync();
    }

    public async Task<AmortizacionCredito?> GetByIdAsync(int id)
    {
        return await _context.AmortizacionesCredito
            .Include(a => a.CreditoBanco)
            .FirstOrDefaultAsync(a => a.Id == id);
    }

    public async Task<List<AmortizacionCredito>> GetByCreditoBancoIdAsync(int creditoBancoId)
    {
        return await _context.AmortizacionesCredito
            .Where(a => a.CreditoBancoId == creditoBancoId)
            .Include(a => a.CreditoBanco)
            .OrderBy(a => a.NumeroCuota)
            .ToListAsync();
    }

    public async Task<AmortizacionCredito> CreateAsync(AmortizacionCredito amortizacion)
    {
        _context.AmortizacionesCredito.Add(amortizacion);
        await _context.SaveChangesAsync();
        return amortizacion;
    }

    public async Task<bool> DeleteAsync(int id)
    {
        var amortizacion = await _context.AmortizacionesCredito.FindAsync(id);
        if (amortizacion == null) return false;

        _context.AmortizacionesCredito.Remove(amortizacion);
        await _context.SaveChangesAsync();
        return true;
    }
}
