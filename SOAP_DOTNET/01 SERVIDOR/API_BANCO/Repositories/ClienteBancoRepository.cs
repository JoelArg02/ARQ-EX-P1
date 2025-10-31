using API_BANCO.Application.DTOs.Clientes;
using API_BANCO.Application.DTOs.Creditos;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using API_BANCO.Models.Enums;
using API_BANCO.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace API_BANCO.Repositories;

public class ClienteBancoRepository : IClienteBancoRepository
{
    private readonly AppDbContext _context;

    public ClienteBancoRepository(AppDbContext context)
    {
        _context = context;
    }

    public async Task<List<ClienteBanco>> GetAllAsync()
    {
        return await _context.ClientesBanco.ToListAsync();
    }

    public async Task<ClienteBanco?> GetByIdAsync(int id)
    {
        return await _context.ClientesBanco.FindAsync(id);
    }

    public async Task<ClienteBanco?> GetByCedulaAsync(string cedula)
    {
        return await _context.ClientesBanco
            .FirstOrDefaultAsync(c => c.Cedula == cedula);
    }

    public async Task<ClienteBanco> CreateAsync(ClienteBanco clienteBanco)
    {
        _context.ClientesBanco.Add(clienteBanco);
        await _context.SaveChangesAsync();
        return clienteBanco;
    }

    public async Task<ClienteBanco?> UpdateAsync(ClienteBanco clienteBanco)
    {
        var existing = await _context.ClientesBanco.FindAsync(clienteBanco.Id);
        if (existing == null) return null;

        existing.Cedula = clienteBanco.Cedula;
        existing.NombreCompleto = clienteBanco.NombreCompleto;
        existing.EstadoCivil = clienteBanco.EstadoCivil;
        existing.FechaNacimiento = clienteBanco.FechaNacimiento;
        existing.TieneCreditoActivo = clienteBanco.TieneCreditoActivo;

        await _context.SaveChangesAsync();
        return existing;
    }

    public async Task<bool> DeleteAsync(int id)
    {
        var clienteBanco = await _context.ClientesBanco.FindAsync(id);
        if (clienteBanco == null) return false;

        _context.ClientesBanco.Remove(clienteBanco);
        await _context.SaveChangesAsync();
        return true;
    }

    public async Task<bool> ExistePorCedulaAsync(string cedula)
    {
        return await _context.ClientesBanco
            .AnyAsync(c => c.Cedula == cedula);
    }

    public async Task<VerificacionClienteResponseDto> VerificarElegibilidadAsync(string cedula)
    {
        var response = new VerificacionClienteResponseDto();

        var cliente = await _context.ClientesBanco
            .FirstOrDefaultAsync(c => c.Cedula == cedula);

        if (cliente == null)
        {
            response.Mensaje = "El solicitante no está registrado como cliente del banco.";
            return response;
        }

        response.EsCliente = true;

        var fechaLimite = DateTime.UtcNow.AddMonths(-1);
        response.TieneDepositoUltimoMes = await _context.Movimientos
            .AnyAsync(m => m.Cuenta!.ClienteBancoId == cliente.Id &&
                           m.Tipo == TipoMovimiento.Deposito &&
                           m.Fecha >= fechaLimite);

        var edad = DateTime.UtcNow.Year - cliente.FechaNacimiento.Year;
        if (cliente.FechaNacimiento.Date > DateTime.UtcNow.AddYears(-edad)) edad--;
        response.CumpleEdadEstadoCivil = !(cliente.EstadoCivil == EstadoCivil.Casado && edad < 25);

        response.NoTieneCreditoActivo = !await _context.CreditosBanco
            .AnyAsync(c => c.ClienteBancoId == cliente.Id && c.Activo);

        if (!response.EsElegible)
        {
            var motivos = new List<string>();
            if (!response.TieneDepositoUltimoMes) motivos.Add("No tiene depósitos en el último mes");
            if (!response.CumpleEdadEstadoCivil) motivos.Add("No cumple el requisito de edad mínima (25 años si es casado)");
            if (!response.NoTieneCreditoActivo) motivos.Add("Posee un crédito activo");
            response.Mensaje = string.Join("; ", motivos);
        }
        else
        {
            response.Mensaje = "El cliente cumple con todos los requisitos.";
        }

        return response;
    }

    public async Task<CalculoCreditoResponseDto> CalcularMontoMaximoCreditoAsync(string cedula)
    {
        var response = new CalculoCreditoResponseDto();

        var cliente = await _context.ClientesBanco
            .FirstOrDefaultAsync(c => c.Cedula == cedula);

        if (cliente == null)
        {
            response.Mensaje = "El cliente no existe en el banco.";
            return response;
        }

        var fechaLimite = DateTime.UtcNow.AddMonths(-3);

        var depositos = await _context.Movimientos
            .Where(m => m.Cuenta!.ClienteBancoId == cliente.Id &&
                        m.Tipo == TipoMovimiento.Deposito &&
                        m.Fecha >= fechaLimite)
            .Select(m => m.Monto)
            .ToListAsync();

        var retiros = await _context.Movimientos
            .Where(m => m.Cuenta!.ClienteBancoId == cliente.Id &&
                        m.Tipo == TipoMovimiento.Retiro &&
                        m.Fecha >= fechaLimite)
            .Select(m => m.Monto)
            .ToListAsync();

        var promedioDepositos = depositos.Any() ? depositos.Average() : 0m;
        var promedioRetiros = retiros.Any() ? retiros.Average() : 0m;

        var diferencia = promedioDepositos - promedioRetiros;
        var montoMaximo = diferencia > 0 ? (diferencia * 0.6m) * 9m : 0m;

        response.PromedioDepositos = Math.Round(promedioDepositos, 2);
        response.PromedioRetiros = Math.Round(promedioRetiros, 2);
        response.MontoMaximoCredito = Math.Round(montoMaximo, 2);
        response.Mensaje = montoMaximo > 0
            ? "Monto máximo de crédito calculado correctamente."
            : "El cliente no tiene movimientos suficientes o la diferencia es negativa.";

        return response;
    }
}
