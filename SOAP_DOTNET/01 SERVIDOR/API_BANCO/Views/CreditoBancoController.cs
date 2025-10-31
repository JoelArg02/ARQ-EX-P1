using CoreWCF;
using Microsoft.EntityFrameworkCore;
using API_BANCO.Application.Interface;
using API_BANCO.Application.Service;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;

namespace API_BANCO.Views;

[ServiceContract]
public interface ICreditoBancoController
{
    [OperationContract]
    Task<List<CreditoBanco>> GetAllCreditosBanco();

    [OperationContract]
    Task<CreditoBanco?> GetCreditoBancoById(int id);

    [OperationContract]
    Task<List<CreditoBanco>> GetCreditosBancoByClienteBancoId(int clienteBancoId);

    [OperationContract]
    Task<CreditoBanco?> GetCreditoBancoActivoByClienteBancoId(int clienteBancoId);

    [OperationContract]
    Task<CreditoBanco> CreateCreditoBanco(int clienteBancoId, decimal montoAprobado, int numeroCuotas, decimal tasaInteres);

    [OperationContract]
    Task<CreditoBanco?> UpdateCreditoBanco(int id, decimal montoAprobado, int numeroCuotas, decimal tasaInteres, bool activo);

    [OperationContract]
    Task<bool> DeleteCreditoBanco(int id);
}

public class CreditoBancoController : ICreditoBancoController
{
    private readonly ICreditoBancoService _creditoBancoService;

    public CreditoBancoController(AppDbContext context)
    {
        _creditoBancoService = new CreditoBancoService(context);
    }

    public async Task<List<CreditoBanco>> GetAllCreditosBanco()
    {
        return await _creditoBancoService.GetAllCreditosBanco();
    }

    public async Task<CreditoBanco?> GetCreditoBancoById(int id)
    {
        return await _creditoBancoService.GetCreditoBancoById(id);
    }

    public async Task<List<CreditoBanco>> GetCreditosBancoByClienteBancoId(int clienteBancoId)
    {
        return await _creditoBancoService.GetCreditosBancoByClienteBancoId(clienteBancoId);
    }

    public async Task<CreditoBanco?> GetCreditoBancoActivoByClienteBancoId(int clienteBancoId)
    {
        return await _creditoBancoService.GetCreditoBancoActivoByClienteBancoId(clienteBancoId);
    }

    public async Task<CreditoBanco> CreateCreditoBanco(int clienteBancoId, decimal montoAprobado, int numeroCuotas, decimal tasaInteres)
    {
        return await _creditoBancoService.CreateCreditoBanco(clienteBancoId, montoAprobado, numeroCuotas, tasaInteres);
    }

    public async Task<CreditoBanco?> UpdateCreditoBanco(int id, decimal montoAprobado, int numeroCuotas, decimal tasaInteres, bool activo)
    {
        return await _creditoBancoService.UpdateCreditoBanco(id, montoAprobado, numeroCuotas, tasaInteres, activo);
    }

    public async Task<bool> DeleteCreditoBanco(int id)
    {
        return await _creditoBancoService.DeleteCreditoBanco(id);
    }
}
