using API_Comercializadora.Models;

namespace API_Comercializadora.Repositories.Interfaces;

public interface IFacturaRepository
{
    Task<List<Factura>> GetAllAsync();
    Task<Factura?> GetByIdAsync(int id);
    Task<Factura> CreateAsync(Factura factura);
    Task<bool> DeleteAsync(int id);
    Task<Factura?> CreateWithDetailsAsync(Factura factura, List<DetalleFactura> detalles);
}
