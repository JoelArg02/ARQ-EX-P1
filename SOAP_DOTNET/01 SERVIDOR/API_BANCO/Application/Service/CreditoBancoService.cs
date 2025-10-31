using API_BANCO.Application.Interface;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using API_BANCO.Repositories;
using API_BANCO.Repositories.Interfaces;

namespace API_BANCO.Application.Service;

public class CreditoBancoService : ICreditoBancoService
{
    private readonly AppDbContext _context;
    private readonly ICreditoBancoRepository _repository;

    public CreditoBancoService(AppDbContext context)
    {
        _context = context;
        _repository = new CreditoBancoRepository(_context);
    }

    public async Task<List<CreditoBanco>> GetAllCreditosBanco()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<CreditoBanco?> GetCreditoBancoById(int id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<List<CreditoBanco>> GetCreditosBancoByClienteBancoId(int clienteBancoId)
    {
        return await _repository.GetByClienteBancoIdAsync(clienteBancoId);
    }

    public async Task<CreditoBanco?> GetCreditoBancoActivoByClienteBancoId(int clienteBancoId)
    {
        return await _repository.GetActivoByClienteBancoIdAsync(clienteBancoId);
    }

    public async Task<CreditoBanco> CreateCreditoBanco(int clienteBancoId, decimal montoAprobado, int numeroCuotas, decimal tasaInteres)
    {
        var credito = new CreditoBanco
        {
            ClienteBancoId = clienteBancoId,
            MontoAprobado = montoAprobado,
            NumeroCuotas = numeroCuotas,
            TasaInteres = tasaInteres,
            FechaAprobacion = DateTime.UtcNow,
            Activo = true
        };
        return await _repository.CreateAsync(credito);
    }

    public async Task<CreditoBanco?> UpdateCreditoBanco(int id, decimal montoAprobado, int numeroCuotas, decimal tasaInteres, bool activo)
    {
        var credito = new CreditoBanco
        {
            Id = id,
            MontoAprobado = montoAprobado,
            NumeroCuotas = numeroCuotas,
            TasaInteres = tasaInteres,
            Activo = activo
        };
        return await _repository.UpdateAsync(credito);
    }

    public async Task<bool> DeleteCreditoBanco(int id)
    {
        return await _repository.DeleteAsync(id);
    }
}
