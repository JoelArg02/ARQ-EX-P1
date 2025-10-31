using API_Comercializadora.Models;

namespace API_Comercializadora.Application.Interface;

public interface IElectrodomesticoService
{
    Task<List<Electrodomestico>> GetAllElectrodomesticos();
    Task<Electrodomestico?> GetElectrodomesticoById(int id);
    Task<Electrodomestico> CreateElectrodomestico(string nombre, string? descripcion, string? marca, decimal precioVenta, bool activo);
    Task<Electrodomestico?> UpdateElectrodomestico(int id, string nombre, string? descripcion, string? marca, decimal precioVenta, bool activo);
    Task<bool> DeleteElectrodomestico(int id);
}
