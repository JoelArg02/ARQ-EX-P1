using API_Comercializadora.Models;
using API_Comercializadora.Application.DTOs;

namespace API_Comercializadora.Application.Interface;

public interface IFacturaService
{
    Task<List<Factura>> GetAllFacturas();
    Task<Factura?> GetFacturaById(int id);
    Task<Factura?> CreateFacturaWithDetails(Factura factura, List<DetalleFactura> detalles);
    Task<bool> DeleteFactura(int id);
    Task<FacturaResponseDto> CrearFacturaConValidacion(CrearFacturaRequestDto request);
    Task<VerificarElegibilidadResponseDto> VerificarElegibilidadCredito(string cedula);
}
