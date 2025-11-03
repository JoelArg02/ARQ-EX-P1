using Microsoft.EntityFrameworkCore;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using API_BANCO.Repositories.Interfaces;

namespace API_BANCO.Repositories;

public class CuentaRepository : ICuentaRepository
{
    private readonly AppDbContext _context;

    public CuentaRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<List<Cuenta>> GetAllAsync()
    {
        var cuentas = await _context.Cuentas
            .Include(c => c.ClienteBanco)
            .ToListAsync();

        // Calcular el saldo real de cada cuenta sumando los movimientos
        foreach (var cuenta in cuentas)
        {
            cuenta.Saldo = await CalcularSaldoRealAsync(cuenta.Id);
        }

        return cuentas;
    }

    public async Task<Cuenta?> GetByIdAsync(int id)
    {
        var cuenta = await _context.Cuentas
            .Include(c => c.ClienteBanco)
            .FirstOrDefaultAsync(c => c.Id == id);

        if (cuenta != null)
        {
            cuenta.Saldo = await CalcularSaldoRealAsync(cuenta.Id);
        }

        return cuenta;
    }

    public async Task<Cuenta?> GetByNumeroCuentaAsync(string numeroCuenta)
    {
        var cuenta = await _context.Cuentas
            .Include(c => c.ClienteBanco)
            .FirstOrDefaultAsync(c => c.NumeroCuenta == numeroCuenta);

        if (cuenta != null)
        {
            cuenta.Saldo = await CalcularSaldoRealAsync(cuenta.Id);
        }

        return cuenta;
    }

    public async Task<List<Cuenta>> GetByClienteBancoIdAsync(int clienteBancoId)
    {
        var cuentas = await _context.Cuentas
            .Where(c => c.ClienteBancoId == clienteBancoId)
            .Include(c => c.ClienteBanco)
            .ToListAsync();

        // Calcular el saldo real de cada cuenta sumando los movimientos
        foreach (var cuenta in cuentas)
        {
            cuenta.Saldo = await CalcularSaldoRealAsync(cuenta.Id);
        }

        return cuentas;
    }

    private async Task<decimal> CalcularSaldoRealAsync(int cuentaId)
    {
        var movimientos = await _context.Movimientos
            .Where(m => m.CuentaId == cuentaId)
            .ToListAsync();

        decimal saldo = 0;
        foreach (var mov in movimientos)
        {
            if (mov.Tipo == Models.Enums.TipoMovimiento.Deposito)
                saldo += mov.Monto;
            else if (mov.Tipo == Models.Enums.TipoMovimiento.Retiro)
                saldo -= mov.Monto;
        }

        return saldo;
    }

    public async Task<Cuenta> CreateAsync(Cuenta cuenta)
    {
        _context.Cuentas.Add(cuenta);
        await _context.SaveChangesAsync();
        return cuenta;
    }

    public async Task<Cuenta?> UpdateAsync(Cuenta cuenta)
    {
        var existing = await _context.Cuentas.FindAsync(cuenta.Id);
        if (existing == null) return null;

        existing.NumeroCuenta = cuenta.NumeroCuenta;
        existing.Saldo = cuenta.Saldo;
        existing.TipoCuenta = cuenta.TipoCuenta;
        existing.ClienteBancoId = cuenta.ClienteBancoId;

        await _context.SaveChangesAsync();
        return existing;
    }

    public async Task<bool> DeleteAsync(int id)
    {
        var cuenta = await _context.Cuentas.FindAsync(id);
        if (cuenta == null) return false;

        _context.Cuentas.Remove(cuenta);
        await _context.SaveChangesAsync();
        return true;
    }
}
