using CoreWCF;
using Microsoft.EntityFrameworkCore;
using API_Comercializadora.Application.Interface;
using API_Comercializadora.Application.Service;
using API_Comercializadora.Configuration;
using API_Comercializadora.Models;
using API_Comercializadora.Models.Enums;
using API_Comercializadora.Application.DTOs;

namespace API_Comercializadora.Views;

[ServiceContract]
public interface IFacturaController
{
    [OperationContract]
    Task<List<FacturaListDto>> GetAllFacturas();

    [OperationContract]
    Task<Factura?> GetFacturaById(int id);

    [OperationContract]
    Task<Factura?> CreateFactura(Factura factura, List<DetalleFactura> detalles);

    [OperationContract]
    Task<bool> DeleteFactura(int id);

    [OperationContract]
    Task<FacturaResponseDto> CrearFacturaConValidacion(CrearFacturaRequestDto request);

    [OperationContract]
    Task<VerificarElegibilidadResponseDto> VerificarElegibilidadCredito(string cedula);
}

public class FacturaController : IFacturaController
{
    private readonly IFacturaService _facturaService;
    private readonly IBancoBanquitoService _bancoBanquitoService;
    private readonly ILogger<FacturaController> _logger;

    public FacturaController(
        AppDbContext context, 
        IConfiguration configuration,
        ILogger<FacturaController> logger)
    {
        _logger = logger;
        var facturaLogger = LoggerFactory.Create(builder => builder.AddConsole()).CreateLogger<FacturaService>();
        _bancoBanquitoService = new BancoBanquitoService(configuration, 
            LoggerFactory.Create(builder => builder.AddConsole()).CreateLogger<BancoBanquitoService>());
        _facturaService = new FacturaService(context, _bancoBanquitoService, facturaLogger);
    }

    public async Task<List<FacturaListDto>> GetAllFacturas()
    {
        try
        {
            var facturas = await _facturaService.GetAllFacturas();
            
            return facturas.Select(f => new FacturaListDto
            {
                Id = f.Id,
                ClienteId = f.ClienteId,
                NombreCliente = f.Cliente?.NombreCompleto ?? "Cliente Desconocido",
                CedulaCliente = f.Cliente?.Cedula ?? "",
                FechaEmision = f.FechaEmision,
                Subtotal = f.Subtotal,
                Iva = f.Subtotal * 0.15m, // Calcular IVA como 15% del subtotal
                Total = f.TotalFinal,
                FormaPago = f.FormaPago == FormaPagoEnum.Efectivo ? "Efectivo" : "Crédito",
                CantidadProductos = f.Detalles?.Count ?? 0
            }).ToList();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error al obtener todas las facturas");
            return new List<FacturaListDto>();
        }
    }

    public async Task<Factura?> GetFacturaById(int id)
    {
        return await _facturaService.GetFacturaById(id);
    }

    public async Task<Factura?> CreateFactura(Factura factura, List<DetalleFactura> detalles)
    {
        return await _facturaService.CreateFacturaWithDetails(factura, detalles);
    }

    public async Task<bool> DeleteFactura(int id)
    {
        return await _facturaService.DeleteFactura(id);
    }

    public async Task<FacturaResponseDto> CrearFacturaConValidacion(CrearFacturaRequestDto request)
    {
        try
        {
            
            return await _facturaService.CrearFacturaConValidacion(request);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error en FacturaController.CrearFacturaConValidacion");
            return new FacturaResponseDto
            {
                Exitoso = false,
                Mensaje = $"Error al procesar la factura: {ex.Message}"
            };
        }
    }

    public async Task<VerificarElegibilidadResponseDto> VerificarElegibilidadCredito(string cedula)
    {
        try
        {
            _logger.LogInformation($"Verificando elegibilidad de crédito para cédula: {cedula}");
            return await _facturaService.VerificarElegibilidadCredito(cedula);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error en FacturaController.VerificarElegibilidadCredito");
            return new VerificarElegibilidadResponseDto
            {
                EsElegible = false,
                Mensaje = $"Error al verificar elegibilidad: {ex.Message}",
                MontoMaximoCredito = 0
            };
        }
    }
}
