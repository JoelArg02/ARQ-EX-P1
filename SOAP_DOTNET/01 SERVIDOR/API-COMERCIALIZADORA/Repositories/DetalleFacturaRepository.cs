using Microsoft.EntityFrameworkCore;
using API_Comercializadora.Configuration;
using API_Comercializadora.Models;
using API_Comercializadora.Repositories.Interfaces;

namespace API_Comercializadora.Repositories;

public class DetalleFacturaRepository : IDetalleFacturaRepository
{
    private readonly AppDbContext _context;

    public DetalleFacturaRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<List<DetalleFactura>> GetByFacturaIdAsync(int facturaId)
    {
        return await _context.DetallesFactura
            .Where(d => d.FacturaId == facturaId)
            .Include(d => d.Electrodomestico)
            .ToListAsync();
    }

    public async Task<DetalleFactura> CreateAsync(DetalleFactura detalle)
    {
        _context.DetallesFactura.Add(detalle);
        await _context.SaveChangesAsync();
        return detalle;
    }

    public async Task<bool> DeleteAsync(int id)
    {
        var detalle = await _context.DetallesFactura.FindAsync(id);
        if (detalle == null) return false;

        _context.DetallesFactura.Remove(detalle);
        await _context.SaveChangesAsync();
        return true;
    }
}
