using API_BANCO.Models.Entities;

namespace API_BANCO.Repositories.Interfaces;

public interface IMovimientoRepository
{
    Task<List<Movimiento>> GetAllAsync();
    Task<Movimiento?> GetByIdAsync(int id);
    Task<List<Movimiento>> GetByCuentaIdAsync(int cuentaId);
    Task<Movimiento> CreateAsync(Movimiento movimiento);
    Task<bool> DeleteAsync(int id);
    Task<List<Movimiento>> GetByCedulaAndFechasAsync(string cedula, DateTime? fechaInicio = null, DateTime? fechaFin = null);

}
