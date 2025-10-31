using API_Comercializadora.Models;

namespace API_Comercializadora.Repositories.Interfaces;

public interface IElectrodomesticoRepository
{
    Task<List<Electrodomestico>> GetAllAsync();
    Task<Electrodomestico?> GetByIdAsync(int id);
    Task<Electrodomestico> CreateAsync(Electrodomestico electrodomestico);
    Task<Electrodomestico?> UpdateAsync(Electrodomestico electrodomestico);
    Task<bool> DeleteAsync(int id);
}
