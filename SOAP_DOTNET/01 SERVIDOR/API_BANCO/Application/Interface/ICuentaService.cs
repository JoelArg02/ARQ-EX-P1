using API_BANCO.Models.Entities;

namespace API_BANCO.Application.Interface;

public interface ICuentaService
{
    Task<List<Cuenta>> GetAllCuentas();
    Task<Cuenta?> GetCuentaById(int id);
    Task<Cuenta?> GetCuentaByNumeroCuenta(string numeroCuenta);
    Task<List<Cuenta>> GetCuentasByClienteBancoId(int clienteBancoId);
    Task<Cuenta> CreateCuenta(int clienteBancoId, string numeroCuenta, decimal saldo, int tipoCuenta);
    Task<Cuenta?> UpdateCuenta(int id, string numeroCuenta, decimal saldo, int tipoCuenta);
    Task<bool> DeleteCuenta(int id);
}
