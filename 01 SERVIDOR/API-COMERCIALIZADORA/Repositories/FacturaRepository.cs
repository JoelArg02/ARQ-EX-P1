using Microsoft.EntityFrameworkCore;
using API_Comercializadora.Configuration;
using API_Comercializadora.Models;
using API_Comercializadora.Repositories.Interfaces;

namespace API_Comercializadora.Repositories;

public class FacturaRepository : IFacturaRepository
{
    private readonly AppDbContext _context;

    public FacturaRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<List<Factura>> GetAllAsync()
    {
        return await _context.Facturas
            .Include(f => f.Cliente)
            .Include(f => f.FormaPago)
            .Include(f => f.Detalles)
            .ToListAsync();
    }

    public async Task<Factura?> GetByIdAsync(int id)
    {
        return await _context.Facturas
            .Include(f => f.Cliente)
            .Include(f => f.FormaPago)
            .Include(f => f.Detalles)
            .FirstOrDefaultAsync(f => f.Id == id);
    }

    public async Task<Factura> CreateAsync(Factura factura)
    {
        _context.Facturas.Add(factura);
        await _context.SaveChangesAsync();
        return factura;
    }

    public async Task<bool> DeleteAsync(int id)
    {
        var factura = await _context.Facturas.FindAsync(id);
        if (factura == null) return false;

        _context.Facturas.Remove(factura);
        await _context.SaveChangesAsync();
        return true;
    }

    public async Task<Factura?> CreateWithDetailsAsync(Factura factura, List<DetalleFactura> detalles)
    {
        using var transaction = await _context.Database.BeginTransactionAsync();

        try
        {
            _context.Facturas.Add(factura);
            await _context.SaveChangesAsync();

            foreach (var detalle in detalles)
            {
                detalle.FacturaId = factura.Id;
                _context.DetallesFactura.Add(detalle);
            }

            await _context.SaveChangesAsync();

            await transaction.CommitAsync();

            await _context.Entry(factura).Reference(f => f.Cliente).LoadAsync();
            await _context.Entry(factura).Reference(f => f.FormaPago).LoadAsync();
            await _context.Entry(factura).Collection(f => f.Detalles).LoadAsync();

            return factura;
        }
        catch (Exception ex)
        {
            await transaction.RollbackAsync();
            Console.WriteLine($"Error al crear factura: {ex.Message}");
            return null;
        }
    }
}
