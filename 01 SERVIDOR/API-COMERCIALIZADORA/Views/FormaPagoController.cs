using API_Comercializadora.Application.Interface;
using API_Comercializadora.Application.Service;
using API_Comercializadora.Configuration;
using API_Comercializadora.Models;
using CoreWCF;
using Microsoft.EntityFrameworkCore;

namespace API_Comercializadora.Views;

[ServiceContract]
public interface IFormaPagoController
{
    [OperationContract]
    Task<List<FormaPago>> GetAllFormasPago();

    [OperationContract]
    Task<FormaPago?> GetFormaPagoById(int id);

    [OperationContract]
    Task<FormaPago> CreateFormaPago(string nombre, string? descripcion);

    [OperationContract]
    Task<FormaPago?> UpdateFormaPago(int id, string nombre, string? descripcion);

    [OperationContract]
    Task<bool> DeleteFormaPago(int id);
}

public class FormaPagoController : IFormaPagoController
{
    private readonly IFormaPagoService _formaPagoService;

    public FormaPagoController(AppDbContext context)
    {
        _formaPagoService = new FormaPagoService(context);
    }

    public async Task<List<FormaPago>> GetAllFormasPago()
    {
        return await _formaPagoService.GetAllFormasPago();
    }

    public async Task<FormaPago?> GetFormaPagoById(int id)
    {
        return await _formaPagoService.GetFormaPagoById(id);
    }

    public async Task<FormaPago> CreateFormaPago(string nombre, string? descripcion)
    {
        return await _formaPagoService.CreateFormaPago(nombre, descripcion);
    }

    public async Task<FormaPago?> UpdateFormaPago(int id, string nombre, string? descripcion)
    {
        return await _formaPagoService.UpdateFormaPago(id, nombre, descripcion);
    }

    public async Task<bool> DeleteFormaPago(int id)
    {
        return await _formaPagoService.DeleteFormaPago(id);
    }
}
