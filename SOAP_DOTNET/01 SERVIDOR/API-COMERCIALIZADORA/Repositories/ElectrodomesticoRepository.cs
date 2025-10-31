using Microsoft.EntityFrameworkCore;
using API_Comercializadora.Configuration;
using API_Comercializadora.Models;
using API_Comercializadora.Repositories.Interfaces;

namespace API_Comercializadora.Repositories;

public class ElectrodomesticoRepository : IElectrodomesticoRepository
{
    private readonly AppDbContext _context;

    public ElectrodomesticoRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<List<Electrodomestico>> GetAllAsync()
    {
        return await _context.Electrodomesticos.ToListAsync();
    }

    public async Task<Electrodomestico?> GetByIdAsync(int id)
    {
        return await _context.Electrodomesticos.FindAsync(id);
    }

    public async Task<Electrodomestico> CreateAsync(Electrodomestico electrodomestico)
    {
        _context.Electrodomesticos.Add(electrodomestico);
        await _context.SaveChangesAsync();
        return electrodomestico;
    }

    public async Task<Electrodomestico?> UpdateAsync(Electrodomestico electrodomestico)
    {
        var existing = await _context.Electrodomesticos.FindAsync(electrodomestico.Id);
        if (existing == null) return null;

        existing.Nombre = electrodomestico.Nombre;
        existing.Descripcion = electrodomestico.Descripcion;
        existing.Marca = electrodomestico.Marca;
        existing.PrecioVenta = electrodomestico.PrecioVenta;
        existing.Activo = electrodomestico.Activo;

        await _context.SaveChangesAsync();
        return existing;
    }

    public async Task<bool> DeleteAsync(int id)
    {
        var electrodomestico = await _context.Electrodomesticos.FindAsync(id);
        if (electrodomestico == null) return false;

        _context.Electrodomesticos.Remove(electrodomestico);
        await _context.SaveChangesAsync();
        return true;
    }
}
