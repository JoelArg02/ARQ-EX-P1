using Microsoft.EntityFrameworkCore;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using API_BANCO.Repositories.Interfaces;

namespace API_BANCO.Repositories;

public class MovimientoRepository : IMovimientoRepository
{
    private readonly AppDbContext _context;

    public MovimientoRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<List<Movimiento>> GetAllAsync()
    {
        return await _context.Movimientos
            .Include(m => m.Cuenta)
            .ToListAsync();
    }

    public async Task<Movimiento?> GetByIdAsync(int id)
    {
        return await _context.Movimientos
            .Include(m => m.Cuenta)
            .FirstOrDefaultAsync(m => m.Id == id);
    }

    public async Task<List<Movimiento>> GetByCuentaIdAsync(int cuentaId)
    {
        return await _context.Movimientos
            .Where(m => m.CuentaId == cuentaId)
            .Include(m => m.Cuenta)
            .OrderByDescending(m => m.Fecha)
            .ToListAsync();
    }

    public async Task<Movimiento> CreateAsync(Movimiento movimiento)
    {
        _context.Movimientos.Add(movimiento);
        await _context.SaveChangesAsync();
        return movimiento;
    }

    public async Task<bool> DeleteAsync(int id)
    {
        var movimiento = await _context.Movimientos.FindAsync(id);
        if (movimiento == null) return false;

        _context.Movimientos.Remove(movimiento);
        await _context.SaveChangesAsync();
        return true;
    }

    public async Task<List<Movimiento>> GetByCedulaAndFechasAsync(string cedula, DateTime? fechaInicio = null, DateTime? fechaFin = null)
    {
        var query = _context.Movimientos
            .Include(m => m.Cuenta)
            .ThenInclude(c => c.ClienteBanco)
            .Where(m => m.Cuenta.ClienteBanco.Cedula == cedula)
            .AsQueryable();

        if (fechaInicio.HasValue)
            query = query.Where(m => m.Fecha >= fechaInicio.Value);

        if (fechaFin.HasValue)
            query = query.Where(m => m.Fecha <= fechaFin.Value);

        return await query
            .OrderByDescending(m => m.Fecha)
            .ToListAsync();
    }

}
