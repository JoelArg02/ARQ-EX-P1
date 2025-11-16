using API_BANCO.Application.DTOs.Movimientos;
using API_BANCO.Application.Interface;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using API_BANCO.Models.Enums;
using API_BANCO.Repositories;
using API_BANCO.Repositories.Interfaces;

namespace API_BANCO.Application.Service;

public class MovimientoService : IMovimientoService
{
    private readonly AppDbContext _context;
    private readonly IMovimientoRepository _repository;

    public MovimientoService(AppDbContext context)
    {
        _context = context;
        _repository = new MovimientoRepository(_context);
    }

    public async Task<List<Movimiento>> GetAllMovimientos()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<Movimiento?> GetMovimientoById(int id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<List<Movimiento>> GetMovimientosByCuentaId(int cuentaId)
    {
        return await _repository.GetByCuentaIdAsync(cuentaId);
    }

    public async Task<Movimiento> CreateMovimiento(int cuentaId, int tipo, decimal monto)
    {
        // Obtener la cuenta
        var cuenta = await _context.Cuentas.FindAsync(cuentaId);
        if (cuenta == null)
        {
            throw new Exception($"Cuenta con ID {cuentaId} no encontrada");
        }

        // Actualizar el saldo según el tipo de movimiento
        if (tipo == 1) // Depósito
        {
            cuenta.Saldo += monto;
        }
        else if (tipo == 2) // Retiro
        {
            if (cuenta.Saldo < monto)
            {
                throw new Exception($"Saldo insuficiente. Saldo actual: {cuenta.Saldo}, Monto a retirar: {monto}");
            }
            cuenta.Saldo -= monto;
        }
        else
        {
            throw new Exception($"Tipo de movimiento inválido: {tipo}. Use 1 para Depósito o 2 para Retiro");
        }

        // Crear el movimiento
        var movimiento = new Movimiento
        {
            CuentaId = cuentaId,
            Tipo = (TipoMovimiento)tipo,
            Monto = monto,
            Fecha = DateTime.UtcNow
        };
        
        // Guardar los cambios
        await _repository.CreateAsync(movimiento);
        await _context.SaveChangesAsync();
        
        return movimiento;
    }

    public async Task<bool> DeleteMovimiento(int id)
    {
        return await _repository.DeleteAsync(id);
    }

    public async Task<List<Movimiento>> GetMovimientosByCedulaAndFechas(MovimientoFiltroDto filtro)
    {
        return await _repository.GetByCedulaAndFechasAsync(filtro.Cedula, filtro.FechaInicio, filtro.FechaFin);
    }
}
