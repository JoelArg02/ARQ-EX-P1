using API_BANCO.Models.Entities;

namespace API_BANCO.Repositories.Interfaces;

public interface IClienteBancoRepository
{
    Task<List<ClienteBanco>> GetAllAsync();
    Task<ClienteBanco?> GetByIdAsync(int id);
    Task<ClienteBanco?> GetByCedulaAsync(string cedula);
    Task<ClienteBanco> CreateAsync(ClienteBanco clienteBanco);
    Task<ClienteBanco?> UpdateAsync(ClienteBanco clienteBanco);
    Task<bool> DeleteAsync(int id);
}
