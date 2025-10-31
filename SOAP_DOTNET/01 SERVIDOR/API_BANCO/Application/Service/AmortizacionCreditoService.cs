using API_BANCO.Application.Interface;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using API_BANCO.Repositories;
using API_BANCO.Repositories.Interfaces;

namespace API_BANCO.Application.Service;

public class AmortizacionCreditoService : IAmortizacionCreditoService
{
    private readonly AppDbContext _context;
    private readonly IAmortizacionCreditoRepository _repository;

    public AmortizacionCreditoService(AppDbContext context)
    {
        _context = context;
        _repository = new AmortizacionCreditoRepository(_context);
    }

    public async Task<List<AmortizacionCredito>> GetAllAmortizaciones()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<AmortizacionCredito?> GetAmortizacionById(int id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<List<AmortizacionCredito>> GetAmortizacionesByCreditoBancoId(int creditoBancoId)
    {
        return await _repository.GetByCreditoBancoIdAsync(creditoBancoId);
    }

    public async Task<AmortizacionCredito> CreateAmortizacion(int creditoBancoId, int numeroCuota, decimal valorCuota, decimal interesPagado, decimal capitalPagado, decimal saldoPendiente)
    {
        var amortizacion = new AmortizacionCredito
        {
            CreditoBancoId = creditoBancoId,
            NumeroCuota = numeroCuota,
            ValorCuota = valorCuota,
            InteresPagado = interesPagado,
            CapitalPagado = capitalPagado,
            SaldoPendiente = saldoPendiente,
            FechaPago = DateTime.UtcNow
        };
        return await _repository.CreateAsync(amortizacion);
    }

    public async Task<bool> DeleteAmortizacion(int id)
    {
        return await _repository.DeleteAsync(id);
    }
}
