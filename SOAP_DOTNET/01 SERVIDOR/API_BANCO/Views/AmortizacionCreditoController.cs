using CoreWCF;
using Microsoft.EntityFrameworkCore;
using API_BANCO.Application.Interface;
using API_BANCO.Application.Service;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;

namespace API_BANCO.Views;

[ServiceContract]
public interface IAmortizacionCreditoController
{
    [OperationContract]
    Task<List<AmortizacionCredito>> GetAllAmortizaciones();

    [OperationContract]
    Task<AmortizacionCredito?> GetAmortizacionById(int id);

    [OperationContract]
    Task<List<AmortizacionCredito>> GetAmortizacionesByCreditoBancoId(int creditoBancoId);

    [OperationContract]
    Task<AmortizacionCredito> CreateAmortizacion(int creditoBancoId, int numeroCuota, decimal valorCuota, decimal interesPagado, decimal capitalPagado, decimal saldoPendiente);

    [OperationContract]
    Task<bool> DeleteAmortizacion(int id);
}

public class AmortizacionCreditoController : IAmortizacionCreditoController
{
    private readonly IAmortizacionCreditoService _amortizacionService;

    public AmortizacionCreditoController(AppDbContext context)
    {
        _amortizacionService = new AmortizacionCreditoService(context);
    }

    public async Task<List<AmortizacionCredito>> GetAllAmortizaciones()
    {
        return await _amortizacionService.GetAllAmortizaciones();
    }

    public async Task<AmortizacionCredito?> GetAmortizacionById(int id)
    {
        return await _amortizacionService.GetAmortizacionById(id);
    }

    public async Task<List<AmortizacionCredito>> GetAmortizacionesByCreditoBancoId(int creditoBancoId)
    {
        return await _amortizacionService.GetAmortizacionesByCreditoBancoId(creditoBancoId);
    }

    public async Task<AmortizacionCredito> CreateAmortizacion(int creditoBancoId, int numeroCuota, decimal valorCuota, decimal interesPagado, decimal capitalPagado, decimal saldoPendiente)
    {
        return await _amortizacionService.CreateAmortizacion(creditoBancoId, numeroCuota, valorCuota, interesPagado, capitalPagado, saldoPendiente);
    }

    public async Task<bool> DeleteAmortizacion(int id)
    {
        return await _amortizacionService.DeleteAmortizacion(id);
    }
}
