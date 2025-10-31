using Microsoft.EntityFrameworkCore;
using API_Comercializadora.Application.Interface;
using API_Comercializadora.Configuration;
using API_Comercializadora.Models;
using API_Comercializadora.Repositories;
using API_Comercializadora.Repositories.Interfaces;

namespace API_Comercializadora.Application.Service;

public class FormaPagoService : IFormaPagoService
{
    private readonly AppDbContext _context;
    private readonly IFormaPagoRepository _repository;

    public FormaPagoService(AppDbContext context)
    {
        _context = context;
        _repository = new FormaPagoRepository(_context);
    }

    public async Task<List<FormaPago>> GetAllFormasPago()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<FormaPago?> GetFormaPagoById(int id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<FormaPago> CreateFormaPago(string nombre, string? descripcion)
    {
        var formaPago = new FormaPago
        {
            Nombre = nombre,
            Descripcion = descripcion
        };
        return await _repository.CreateAsync(formaPago);
    }

    public async Task<FormaPago?> UpdateFormaPago(int id, string nombre, string? descripcion)
    {
        var formaPago = new FormaPago
        {
            Id = id,
            Nombre = nombre,
            Descripcion = descripcion
        };
        return await _repository.UpdateAsync(formaPago);
    }

    public async Task<bool> DeleteFormaPago(int id)
    {
        return await _repository.DeleteAsync(id);
    }
}
