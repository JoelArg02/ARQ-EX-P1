using API_Comercializadora.Models;

namespace API_Comercializadora.Application.Interface;

public interface IFormaPagoService
{
    Task<List<FormaPago>> GetAllFormasPago();
    Task<FormaPago?> GetFormaPagoById(int id);
    Task<FormaPago> CreateFormaPago(string nombre, string? descripcion);
    Task<FormaPago?> UpdateFormaPago(int id, string nombre, string? descripcion);
    Task<bool> DeleteFormaPago(int id);
}
