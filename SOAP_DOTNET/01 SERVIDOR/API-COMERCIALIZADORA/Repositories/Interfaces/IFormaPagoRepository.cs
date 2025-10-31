using API_Comercializadora.Models;

namespace API_Comercializadora.Repositories.Interfaces;

public interface IFormaPagoRepository
{
    Task<List<FormaPago>> GetAllAsync();
    Task<FormaPago?> GetByIdAsync(int id);
    Task<FormaPago> CreateAsync(FormaPago formaPago);
    Task<FormaPago?> UpdateAsync(FormaPago formaPago);
    Task<bool> DeleteAsync(int id);
}
