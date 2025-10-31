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
            response.EsCliente = false;
            response.EsElegible = false;
            response.Mensaje = "El solicitante no está registrado como cliente del banco.";
            return response;
        }

        response.EsCliente = true;

        var fechaLimite = DateTime.UtcNow.AddMonths(-1);

        response.TieneDepositoUltimoMes = await _context.Movimientos
            .AnyAsync(m =>
                m.Cuenta!.ClienteBancoId == cliente.Id &&
                m.Tipo == TipoMovimiento.Deposito &&
                m.Fecha >= fechaLimite);

        var edad = DateTime.UtcNow.Year - cliente.FechaNacimiento.Year;
        if (cliente.FechaNacimiento.Date > DateTime.UtcNow.AddYears(-edad))
            edad--;

        response.CumpleEdadEstadoCivil = !(cliente.EstadoCivil == EstadoCivil.Casado && edad < 25);

        response.NoTieneCreditoActivo = !await _context.CreditosBanco
            .AnyAsync(c => c.ClienteBancoId == cliente.Id && c.Activo);

        response.EsElegible = response.EsCliente
            && response.TieneDepositoUltimoMes
            && response.CumpleEdadEstadoCivil
            && response.NoTieneCreditoActivo;

        if (!response.EsElegible)
        {
            var motivos = new List<string>();
            if (!response.TieneDepositoUltimoMes)
                motivos.Add("No tiene depósitos en el último mes");
            if (!response.CumpleEdadEstadoCivil)
                motivos.Add("No cumple el requisito de edad mínima (25 años si es casado)");
            if (!response.NoTieneCreditoActivo)
                motivos.Add("Posee un crédito activo");
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

    public async Task<EvaluacionCreditoResultadoDto> EvaluarCreditoAsync(string cedula, decimal montoRequerido)
    {
        var cliente = await _context.ClientesBanco.FirstOrDefaultAsync(c => c.Cedula == cedula);
        if (cliente == null)
            return new EvaluacionCreditoResultadoDto { Detalle = "El cliente no existe." };

        var fechaLimite = DateTime.UtcNow.AddMonths(-3);

        var depositos = await _context.Movimientos
            .Where(m => m.Cuenta!.ClienteBancoId == cliente.Id &&
                        m.Tipo == TipoMovimiento.Deposito &&
                        m.Fecha >= fechaLimite)
            .ToListAsync();

        var retiros = await _context.Movimientos
            .Where(m => m.Cuenta!.ClienteBancoId == cliente.Id &&
                        m.Tipo == TipoMovimiento.Retiro &&
                        m.Fecha >= fechaLimite)
            .ToListAsync();

        var promedioDepositos = depositos.Any() ? depositos.Average(d => d.Monto) : 0;
        var promedioRetiros = retiros.Any() ? retiros.Average(r => r.Monto) : 0;

        var limiteMaximo = ((promedioDepositos - promedioRetiros) * 0.6m) * 9;

        return new EvaluacionCreditoResultadoDto
        {
            PromedioDepositos3Meses = promedioDepositos,
            PromedioRetiros3Meses = promedioRetiros,
            LimiteMaximoCredito = limiteMaximo,
            Aprobado = montoRequerido <= limiteMaximo,
            Detalle = montoRequerido <= limiteMaximo
                ? "Crédito aprobado."
                : "El monto solicitado supera el límite máximo permitido."
        };
    }

    public async Task<AprobacionCreditoResponseDto> AprobarCreditoAsync(string cedula, decimal montoSolicitado, int numeroCuotas)
    {
        var response = new AprobacionCreditoResponseDto();

        if (numeroCuotas < 3 || numeroCuotas > 24)
        {
            response.Mensaje = "El número de cuotas debe estar entre 3 y 24 meses.";
            return response;
        }

        var cliente = await _context.ClientesBanco.FirstOrDefaultAsync(c => c.Cedula == cedula);
        if (cliente == null)
        {
            response.Mensaje = "El cliente no existe.";
            return response;
        }

        if (cliente.TieneCreditoActivo)
        {
            response.Mensaje = "El cliente ya tiene un crédito activo.";
            return response;
        }

        var evaluacion = await EvaluarCreditoAsync(cedula, montoSolicitado);
        if (!evaluacion.Aprobado)
        {
            response.Mensaje = "El crédito no cumple con las condiciones para ser aprobado.";
            return response;
        }

        var tasaAnual = 0.16m;
        var tasaPeriodo = tasaAnual / 12m;

        var cuota = montoSolicitado * (tasaPeriodo / (1 - (decimal)Math.Pow((double)(1 + tasaPeriodo), -numeroCuotas)));

        var credito = new CreditoBanco
        {
            ClienteBancoId = cliente.Id,
            MontoAprobado = montoSolicitado,
            TasaInteres = tasaAnual,
            NumeroCuotas = numeroCuotas,
            FechaAprobacion = DateTime.UtcNow,
            Activo = true
        };

        _context.CreditosBanco.Add(credito);
        cliente.TieneCreditoActivo = true;
        await _context.SaveChangesAsync();

        var saldoPendiente = montoSolicitado;
        for (int i = 1; i <= numeroCuotas; i++)
        {
            var interes = saldoPendiente * tasaPeriodo;
            var capital = cuota - interes;
            saldoPendiente -= capital;

            var amortizacion = new AmortizacionCredito
            {
                CreditoBancoId = credito.Id,
                NumeroCuota = i,
                ValorCuota = Math.Round(cuota, 2),
                InteresPagado = Math.Round(interes, 2),
                CapitalPagado = Math.Round(capital, 2),
                SaldoPendiente = Math.Round(Math.Max(saldoPendiente, 0), 2)
            };

            _context.AmortizacionesCredito.Add(amortizacion);

            response.TablaAmortizacion.Add(new AmortizacionCreditoDto
            {
                NumeroCuota = i,
                ValorCuota = Math.Round(cuota, 2),
                InteresPagado = Math.Round(interes, 2),
                CapitalPagado = Math.Round(capital, 2),
                SaldoPendiente = Math.Round(Math.Max(saldoPendiente, 0), 2)
            });
        }

        await _context.SaveChangesAsync();

        response.Aprobado = true;
        response.ValorCuota = Math.Round(cuota, 2);
        response.NumeroCuotas = numeroCuotas;
        response.Mensaje = "Crédito aprobado y tabla de amortización generada correctamente.";

        return response;
    }

    public async Task<CreditoResumenDto?> ObtenerCreditoPorCedulaAsync(string cedula)
    {
        var cliente = await _context.ClientesBanco.FirstOrDefaultAsync(c => c.Cedula == cedula);
        if (cliente == null)
            return null;

        var credito = await _context.CreditosBanco
            .FirstOrDefaultAsync(c => c.ClienteBancoId == cliente.Id && c.Activo);

        if (credito == null)
            return null;

        var ultimoPago = await _context.AmortizacionesCredito
            .Where(a => a.CreditoBancoId == credito.Id)
            .OrderByDescending(a => a.NumeroCuota)
            .FirstOrDefaultAsync();

        var saldoPendiente = ultimoPago?.SaldoPendiente ?? credito.MontoAprobado;
        var fechaFinalizacion = credito.FechaAprobacion.AddMonths(credito.NumeroCuotas);

        return new CreditoResumenDto
        {
            CreditoId = credito.Id,
            CedulaCliente = cliente.Cedula,
            NombreCliente = cliente.NombreCompleto,
            MontoAprobado = credito.MontoAprobado,
            SaldoPendiente = saldoPendiente,
            FechaAprobacion = credito.FechaAprobacion,
            FechaFinalizacion = fechaFinalizacion
        };
    }

    public async Task<List<AmortizacionCreditoDto>> ObtenerAmortizacionPorCreditoIdAsync(int creditoId)
    {
        return await _context.AmortizacionesCredito
            .Where(a => a.CreditoBancoId == creditoId)
            .OrderBy(a => a.NumeroCuota)
            .Select(a => new AmortizacionCreditoDto
            {
                NumeroCuota = a.NumeroCuota,
                ValorCuota = a.ValorCuota,
                InteresPagado = a.InteresPagado,
                CapitalPagado = a.CapitalPagado,
                SaldoPendiente = a.SaldoPendiente
            })
            .ToListAsync();
    }

}
