using Microsoft.EntityFrameworkCore;
using API_Comercializadora.Application.Interface;
using API_Comercializadora.Configuration;
using API_Comercializadora.Models;
using API_Comercializadora.Repositories;
using API_Comercializadora.Repositories.Interfaces;

namespace API_Comercializadora.Application.Service;

public class FacturaService : IFacturaService
{
    private readonly AppDbContext _context;
    private readonly IFacturaRepository _repository;

    public FacturaService(AppDbContext context)
    {
        _context = context;
        _repository = new FacturaRepository(_context);
    }

    public async Task<List<Factura>> GetAllFacturas()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<Factura?> GetFacturaById(int id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<Factura?> CreateFacturaWithDetails(Factura factura, List<DetalleFactura> detalles)
    {
        return await _repository.CreateWithDetailsAsync(factura, detalles);
    }

    public async Task<bool> DeleteFactura(int id)
    {
        return await _repository.DeleteAsync(id);
    }
}
