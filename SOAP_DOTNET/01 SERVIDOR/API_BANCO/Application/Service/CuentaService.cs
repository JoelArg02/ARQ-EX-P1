using API_BANCO.Application.Interface;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using API_BANCO.Models.Enums;
using API_BANCO.Repositories;
using API_BANCO.Repositories.Interfaces;

namespace API_BANCO.Application.Service;

public class CuentaService : ICuentaService
{
    private readonly AppDbContext _context;
    private readonly ICuentaRepository _repository;

    public CuentaService(AppDbContext context)
    {
        _context = context;
        _repository = new CuentaRepository(_context);
    }

    public async Task<List<Cuenta>> GetAllCuentas()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<Cuenta?> GetCuentaById(int id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<Cuenta?> GetCuentaByNumeroCuenta(string numeroCuenta)
    {
        return await _repository.GetByNumeroCuentaAsync(numeroCuenta);
    }

    public async Task<List<Cuenta>> GetCuentasByClienteBancoId(int clienteBancoId)
    {
        return await _repository.GetByClienteBancoIdAsync(clienteBancoId);
    }

    public async Task<Cuenta> CreateCuenta(int clienteBancoId, string numeroCuenta, decimal saldo, int tipoCuenta)
    {
        // Validar que el cliente no tenga ya una cuenta
        var cuentasExistentes = await _repository.GetByClienteBancoIdAsync(clienteBancoId);
        if (cuentasExistentes.Any())
        {
            throw new InvalidOperationException("El cliente ya tiene una cuenta. Solo se permite una cuenta por cliente.");
        }

        var cuenta = new Cuenta
        {
            ClienteBancoId = clienteBancoId,
            NumeroCuenta = numeroCuenta,
            Saldo = saldo,
            TipoCuenta = (TipoCuenta)tipoCuenta
        };
        return await _repository.CreateAsync(cuenta);
    }

    public async Task<Cuenta?> UpdateCuenta(int id, string numeroCuenta, decimal saldo, int tipoCuenta)
    {
        var cuenta = new Cuenta
        {
            Id = id,
            NumeroCuenta = numeroCuenta,
            Saldo = saldo,
            TipoCuenta = (TipoCuenta)tipoCuenta
        };
        return await _repository.UpdateAsync(cuenta);
    }

    public async Task<bool> DeleteCuenta(int id)
    {
        return await _repository.DeleteAsync(id);
    }
}
