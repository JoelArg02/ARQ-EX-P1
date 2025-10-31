using API_BANCO.Models.Entities;

namespace API_BANCO.Repositories.Interfaces;

public interface IAmortizacionCreditoRepository
{
    Task<List<AmortizacionCredito>> GetAllAsync();
    Task<AmortizacionCredito?> GetByIdAsync(int id);
    Task<List<AmortizacionCredito>> GetByCreditoBancoIdAsync(int creditoBancoId);
    Task<AmortizacionCredito> CreateAsync(AmortizacionCredito amortizacion);
    Task<bool> DeleteAsync(int id);
}
