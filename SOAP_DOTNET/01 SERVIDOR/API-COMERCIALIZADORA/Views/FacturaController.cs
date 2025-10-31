using CoreWCF;
using Microsoft.EntityFrameworkCore;
using API_Comercializadora.Application.Interface;
using API_Comercializadora.Application.Service;
using API_Comercializadora.Configuration;
using API_Comercializadora.Models;

namespace API_Comercializadora.Views;

[ServiceContract]
public interface IFacturaController
{
    [OperationContract]
    Task<List<Factura>> GetAllFacturas();

    [OperationContract]
    Task<Factura?> GetFacturaById(int id);

    [OperationContract]
    Task<Factura?> CreateFactura(Factura factura, List<DetalleFactura> detalles);

    [OperationContract]
    Task<bool> DeleteFactura(int id);
}

public class FacturaController : IFacturaController
{
    private readonly IFacturaService _facturaService;

    public FacturaController(AppDbContext context)
    {
        _facturaService = new FacturaService(context);
    }

    public async Task<List<Factura>> GetAllFacturas()
    {
        return await _facturaService.GetAllFacturas();
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
}
