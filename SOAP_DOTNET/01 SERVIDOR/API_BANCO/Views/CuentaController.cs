using CoreWCF;
using Microsoft.EntityFrameworkCore;
using API_BANCO.Application.Interface;
using API_BANCO.Application.Service;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;

namespace API_BANCO.Views;

[ServiceContract]
public interface ICuentaController
{
    [OperationContract]
    Task<List<Cuenta>> GetAllCuentas();

    [OperationContract]
    Task<Cuenta?> GetCuentaById(int id);

    [OperationContract]
    Task<Cuenta?> GetCuentaByNumeroCuenta(string numeroCuenta);

    [OperationContract]
    Task<List<Cuenta>> GetCuentasByClienteBancoId(int clienteBancoId);

    [OperationContract]
    Task<Cuenta> CreateCuenta(int clienteBancoId, string numeroCuenta, decimal saldo, int tipoCuenta);

    [OperationContract]
    Task<Cuenta?> UpdateCuenta(int id, string numeroCuenta, decimal saldo, int tipoCuenta);

    [OperationContract]
    Task<bool> DeleteCuenta(int id);
}

public class CuentaController : ICuentaController
{
    private readonly ICuentaService _cuentaService;

    public CuentaController(AppDbContext context)
    {
        _cuentaService = new CuentaService(context);
    }

    public async Task<List<Cuenta>> GetAllCuentas()
    {
        return await _cuentaService.GetAllCuentas();
    }

    public async Task<Cuenta?> GetCuentaById(int id)
    {
        return await _cuentaService.GetCuentaById(id);
    }

    public async Task<Cuenta?> GetCuentaByNumeroCuenta(string numeroCuenta)
    {
        return await _cuentaService.GetCuentaByNumeroCuenta(numeroCuenta);
    }

    public async Task<List<Cuenta>> GetCuentasByClienteBancoId(int clienteBancoId)
    {
        return await _cuentaService.GetCuentasByClienteBancoId(clienteBancoId);
    }

    public async Task<Cuenta> CreateCuenta(int clienteBancoId, string numeroCuenta, decimal saldo, int tipoCuenta)
    {
        return await _cuentaService.CreateCuenta(clienteBancoId, numeroCuenta, saldo, tipoCuenta);
    }

    public async Task<Cuenta?> UpdateCuenta(int id, string numeroCuenta, decimal saldo, int tipoCuenta)
    {
        return await _cuentaService.UpdateCuenta(id, numeroCuenta, saldo, tipoCuenta);
    }

    public async Task<bool> DeleteCuenta(int id)
    {
        return await _cuentaService.DeleteCuenta(id);
    }
}
