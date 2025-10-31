using API_Comercializadora.Models;

namespace API_Comercializadora.Repositories.Interfaces;

public interface IDetalleFacturaRepository
{
    Task<List<DetalleFactura>> GetByFacturaIdAsync(int facturaId);
    Task<DetalleFactura> CreateAsync(DetalleFactura detalle);
    Task<bool> DeleteAsync(int id);
}
