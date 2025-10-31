using CoreWCF;
using Microsoft.EntityFrameworkCore;
using API_Comercializadora.Application.Interface;
using API_Comercializadora.Application.Service;
using API_Comercializadora.Configuration;
using API_Comercializadora.Models;

namespace API_Comercializadora.Views;

[ServiceContract]
public interface IElectrodomesticoController
{
    [OperationContract]
    Task<List<Electrodomestico>> GetAllElectrodomesticos();

    [OperationContract]
    Task<Electrodomestico?> GetElectrodomesticoById(int id);

    [OperationContract]
    Task<Electrodomestico> CreateElectrodomestico(string nombre, string? descripcion, string? marca, decimal precioVenta, bool activo);

    [OperationContract]
    Task<Electrodomestico?> UpdateElectrodomestico(int id, string nombre, string? descripcion, string? marca, decimal precioVenta, bool activo);

    [OperationContract]
    Task<bool> DeleteElectrodomestico(int id);
}

public class ElectrodomesticoController : IElectrodomesticoController
{
    private readonly IElectrodomesticoService _electrodomesticoService;

    public ElectrodomesticoController(AppDbContext context)
    {
        _electrodomesticoService = new ElectrodomesticoService(context);
    }

    public async Task<List<Electrodomestico>> GetAllElectrodomesticos()
    {
        return await _electrodomesticoService.GetAllElectrodomesticos();
    }

    public async Task<Electrodomestico?> GetElectrodomesticoById(int id)
    {
        return await _electrodomesticoService.GetElectrodomesticoById(id);
    }

    public async Task<Electrodomestico> CreateElectrodomestico(string nombre, string? descripcion, string? marca, decimal precioVenta, bool activo)
    {
        return await _electrodomesticoService.CreateElectrodomestico(nombre, descripcion, marca, precioVenta, activo);
    }

    public async Task<Electrodomestico?> UpdateElectrodomestico(int id, string nombre, string? descripcion, string? marca, decimal precioVenta, bool activo)
    {
        return await _electrodomesticoService.UpdateElectrodomestico(id, nombre, descripcion, marca, precioVenta, activo);
    }

    public async Task<bool> DeleteElectrodomestico(int id)
    {
        return await _electrodomesticoService.DeleteElectrodomestico(id);
    }
}
