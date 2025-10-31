using API_BANCO.Models.Entities;

namespace API_BANCO.Repositories.Interfaces;

public interface ICreditoBancoRepository
{
    Task<List<CreditoBanco>> GetAllAsync();
    Task<CreditoBanco?> GetByIdAsync(int id);
    Task<List<CreditoBanco>> GetByClienteBancoIdAsync(int clienteBancoId);
    Task<CreditoBanco?> GetActivoByClienteBancoIdAsync(int clienteBancoId);
    Task<CreditoBanco> CreateAsync(CreditoBanco creditoBanco);
    Task<CreditoBanco?> UpdateAsync(CreditoBanco creditoBanco);
    Task<bool> DeleteAsync(int id);
}
