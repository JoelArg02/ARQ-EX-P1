using API_BANCO.Application.DTOs.Movimientos;
using API_BANCO.Application.Interface;
using API_BANCO.Application.Service;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using CoreWCF;
using Microsoft.EntityFrameworkCore;
using System.Web.Services.Description;

namespace API_BANCO.Views;

[ServiceContract]
public interface IMovimientoController
{
    [OperationContract]
    Task<List<Movimiento>> GetAllMovimientos();

    [OperationContract]
    Task<Movimiento?> GetMovimientoById(int id);

    [OperationContract]
    Task<List<Movimiento>> GetMovimientosByCuentaId(int cuentaId);

    [OperationContract]
    Task<Movimiento> CreateMovimiento(int cuentaId, int tipo, decimal monto);

    [OperationContract]
    Task<bool> DeleteMovimiento(int id);

    [OperationContract]
    Task<List<Movimiento>> GetMovimientosByCedulaAndFechas(MovimientoFiltroDto filtro);
}

public class MovimientoController : IMovimientoController
{
    private readonly IMovimientoService _movimientoService;

    public MovimientoController(AppDbContext context)
    {
        _movimientoService = new MovimientoService(context);
    }

    public async Task<List<Movimiento>> GetAllMovimientos()
    {
        return await _movimientoService.GetAllMovimientos();
    }

    public async Task<Movimiento?> GetMovimientoById(int id)
    {
        return await _movimientoService.GetMovimientoById(id);
    }

    public async Task<List<Movimiento>> GetMovimientosByCuentaId(int cuentaId)
    {
        return await _movimientoService.GetMovimientosByCuentaId(cuentaId);
    }

    public async Task<Movimiento> CreateMovimiento(int cuentaId, int tipo, decimal monto)
    {
        return await _movimientoService.CreateMovimiento(cuentaId, tipo, monto);
    }

    public async Task<bool> DeleteMovimiento(int id)
    {
        return await _movimientoService.DeleteMovimiento(id);
    }

    public async Task<List<Movimiento>> GetMovimientosByCedulaAndFechas(MovimientoFiltroDto filtro)
    {
        return await _movimientoService.GetMovimientosByCedulaAndFechas(filtro);
    }
}
