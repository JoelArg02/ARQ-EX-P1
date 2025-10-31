using API_BANCO.Models.Entities;

namespace API_BANCO.Application.Interface;

public interface IAmortizacionCreditoService
{
    Task<List<AmortizacionCredito>> GetAllAmortizaciones();
    Task<AmortizacionCredito?> GetAmortizacionById(int id);
    Task<List<AmortizacionCredito>> GetAmortizacionesByCreditoBancoId(int creditoBancoId);
    Task<AmortizacionCredito> CreateAmortizacion(int creditoBancoId, int numeroCuota, decimal valorCuota, decimal interesPagado, decimal capitalPagado, decimal saldoPendiente);
    Task<bool> DeleteAmortizacion(int id);
}
