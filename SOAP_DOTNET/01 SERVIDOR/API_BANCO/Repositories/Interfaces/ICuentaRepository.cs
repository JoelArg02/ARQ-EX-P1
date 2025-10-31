using API_BANCO.Models.Entities;

namespace API_BANCO.Repositories.Interfaces;

public interface ICuentaRepository
{
    Task<List<Cuenta>> GetAllAsync();
    Task<Cuenta?> GetByIdAsync(int id);
    Task<Cuenta?> GetByNumeroCuentaAsync(string numeroCuenta);
    Task<List<Cuenta>> GetByClienteBancoIdAsync(int clienteBancoId);
    Task<Cuenta> CreateAsync(Cuenta cuenta);
    Task<Cuenta?> UpdateAsync(Cuenta cuenta);
    Task<bool> DeleteAsync(int id);
}
