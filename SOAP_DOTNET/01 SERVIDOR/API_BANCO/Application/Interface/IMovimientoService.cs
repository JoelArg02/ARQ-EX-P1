using API_BANCO.Models.Entities;

namespace API_BANCO.Application.Interface;

public interface IMovimientoService
{
    Task<List<Movimiento>> GetAllMovimientos();
    Task<Movimiento?> GetMovimientoById(int id);
    Task<List<Movimiento>> GetMovimientosByCuentaId(int cuentaId);
    Task<Movimiento> CreateMovimiento(int cuentaId, int tipo, decimal monto);
    Task<bool> DeleteMovimiento(int id);
}
