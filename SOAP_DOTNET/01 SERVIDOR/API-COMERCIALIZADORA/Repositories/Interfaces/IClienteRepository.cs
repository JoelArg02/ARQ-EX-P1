using API_Comercializadora.Models;

namespace API_Comercializadora.Repositories.Interfaces
{

    public interface IClienteRepository
    {
        Task<List<Cliente>> GetAllAsync();
        Task<Cliente?> GetByIdAsync(int id);
        Task<Cliente> CreateAsync(Cliente cliente);
        Task<Cliente?> UpdateAsync(Cliente cliente);
        Task<bool> DeleteAsync(int id);
    }
}