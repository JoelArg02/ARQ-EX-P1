using API_BANCO.Models.Entities;

namespace API_BANCO.Application.Interface;

public interface ICreditoBancoService
{
    Task<List<CreditoBanco>> GetAllCreditosBanco();
    Task<CreditoBanco?> GetCreditoBancoById(int id);
    Task<List<CreditoBanco>> GetCreditosBancoByClienteBancoId(int clienteBancoId);
    Task<CreditoBanco?> GetCreditoBancoActivoByClienteBancoId(int clienteBancoId);
    Task<CreditoBanco> CreateCreditoBanco(int clienteBancoId, decimal montoAprobado, int numeroCuotas, decimal tasaInteres);
    Task<CreditoBanco?> UpdateCreditoBanco(int id, decimal montoAprobado, int numeroCuotas, decimal tasaInteres, bool activo);
    Task<bool> DeleteCreditoBanco(int id);
}
