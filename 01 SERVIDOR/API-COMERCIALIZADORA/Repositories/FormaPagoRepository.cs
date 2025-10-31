using Microsoft.EntityFrameworkCore;
using API_Comercializadora.Configuration;
using API_Comercializadora.Models;
using API_Comercializadora.Repositories.Interfaces;

namespace API_Comercializadora.Repositories;

public class FormaPagoRepository : IFormaPagoRepository
{
    private readonly AppDbContext _context;

    public FormaPagoRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<List<FormaPago>> GetAllAsync()
    {
        return await _context.FormasPago.ToListAsync();
    }

    public async Task<FormaPago?> GetByIdAsync(int id)
    {
        return await _context.FormasPago.FindAsync(id);
    }

    public async Task<FormaPago> CreateAsync(FormaPago formaPago)
    {
        _context.FormasPago.Add(formaPago);
        await _context.SaveChangesAsync();
        return formaPago;
    }

    public async Task<FormaPago?> UpdateAsync(FormaPago formaPago)
    {
        var existing = await _context.FormasPago.FindAsync(formaPago.Id);
        if (existing == null) return null;

        existing.Nombre = formaPago.Nombre;
        existing.Descripcion = formaPago.Descripcion;

        await _context.SaveChangesAsync();
        return existing;
    }

    public async Task<bool> DeleteAsync(int id)
    {
        var formaPago = await _context.FormasPago.FindAsync(id);
        if (formaPago == null) return false;

        _context.FormasPago.Remove(formaPago);
        await _context.SaveChangesAsync();
        return true;
    }
}
