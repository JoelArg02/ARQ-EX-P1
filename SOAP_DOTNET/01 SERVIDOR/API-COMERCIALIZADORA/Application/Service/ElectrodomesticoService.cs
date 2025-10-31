using Microsoft.EntityFrameworkCore;
using API_Comercializadora.Application.Interface;
using API_Comercializadora.Configuration;
using API_Comercializadora.Models;
using API_Comercializadora.Repositories;
using API_Comercializadora.Repositories.Interfaces;

namespace API_Comercializadora.Application.Service;

public class ElectrodomesticoService : IElectrodomesticoService
{
    private readonly AppDbContext _context;
    private readonly IElectrodomesticoRepository _repository;

    public ElectrodomesticoService(AppDbContext context)
    {
        _context = context;
        _repository = new ElectrodomesticoRepository(_context);
    }

    public async Task<List<Electrodomestico>> GetAllElectrodomesticos()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<Electrodomestico?> GetElectrodomesticoById(int id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<Electrodomestico> CreateElectrodomestico(string nombre, string? descripcion, string? marca, decimal precioVenta, bool activo)
    {
        var electrodomestico = new Electrodomestico
        {
            Nombre = nombre,
            Descripcion = descripcion,
            Marca = marca,
            PrecioVenta = precioVenta,
            Activo = activo
        };

        return await _repository.CreateAsync(electrodomestico);
    }

    public async Task<Electrodomestico?> UpdateElectrodomestico(int id, string nombre, string? descripcion, string? marca, decimal precioVenta, bool activo)
    {
        var electrodomestico = new Electrodomestico
        {
            Id = id,
            Nombre = nombre,
            Descripcion = descripcion,
            Marca = marca,
            PrecioVenta = precioVenta,
            Activo = activo
        };

        return await _repository.UpdateAsync(electrodomestico);
    }

    public async Task<bool> DeleteElectrodomestico(int id)
    {
        return await _repository.DeleteAsync(id);
    }
}
