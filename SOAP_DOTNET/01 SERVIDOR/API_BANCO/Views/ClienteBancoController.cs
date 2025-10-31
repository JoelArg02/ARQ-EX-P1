using API_BANCO.Application.DTOs.Clientes;
using API_BANCO.Application.DTOs.Creditos;
using API_BANCO.Application.Interface;
using API_BANCO.Application.Service;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using CoreWCF;
using Microsoft.EntityFrameworkCore;

namespace API_BANCO.Views;

[ServiceContract]
public interface IClienteBancoController
{
    [OperationContract]
    Task<List<ClienteBanco>> GetAllClientesBanco();

    [OperationContract]
    Task<ClienteBanco?> GetClienteBancoById(int id);

    [OperationContract]
    Task<ClienteBanco?> GetClienteBancoByCedula(string cedula);

    [OperationContract]
    Task<ClienteBanco> CreateClienteBanco(ClienteBancoCreateDto dto);

    [OperationContract]
    Task<ClienteBanco?> UpdateClienteBanco(int id, string cedula, string nombreCompleto, int estadoCivil, DateTime fechaNacimiento, bool tieneCreditoActivo);

    [OperationContract]
    Task<bool> DeleteClienteBanco(int id);

    [OperationContract]
    Task<bool> VerificarClientePorCedula(string cedula);
    [OperationContract]
    Task<VerificacionClienteResponseDto> VerificarElegibilidadCliente(string cedula);
    [OperationContract]
    Task<CalculoCreditoResponseDto> CalcularMontoMaximoCredito(string cedula);

}

public class ClienteBancoController : IClienteBancoController
{
    private readonly IClienteBancoService _clienteBancoService;

    public ClienteBancoController(AppDbContext context)
    {
        _clienteBancoService = new ClienteBancoService(context);
    }

    public async Task<List<ClienteBanco>> GetAllClientesBanco()
    {
        return await _clienteBancoService.GetAllClientesBanco();
    }

    public async Task<ClienteBanco?> GetClienteBancoById(int id)
    {
        return await _clienteBancoService.GetClienteBancoById(id);
    }

    public async Task<ClienteBanco?> GetClienteBancoByCedula(string cedula)
    {
        return await _clienteBancoService.GetClienteBancoByCedula(cedula);
    }

    public async Task<ClienteBanco> CreateClienteBanco(ClienteBancoCreateDto dto)
    {
        return await _clienteBancoService.CreateClienteBanco(dto);
    }

    public async Task<ClienteBanco?> UpdateClienteBanco(int id, string cedula, string nombreCompleto, int estadoCivil, DateTime fechaNacimiento, bool tieneCreditoActivo)
    {
        return await _clienteBancoService.UpdateClienteBanco(id, cedula, nombreCompleto, estadoCivil, fechaNacimiento, tieneCreditoActivo);
    }

    public async Task<bool> DeleteClienteBanco(int id)
    {
        return await _clienteBancoService.DeleteClienteBanco(id);
    }

    public async Task<bool> VerificarClientePorCedula(string cedula)
    {
        return await _clienteBancoService.VerificarClientePorCedula(cedula);
    }

    public async Task<VerificacionClienteResponseDto> VerificarElegibilidadCliente(string cedula)
    {
        return await _clienteBancoService.VerificarElegibilidadCliente(cedula);
    }
    public async Task<CalculoCreditoResponseDto> CalcularMontoMaximoCredito(string cedula)
    {
        return await _clienteBancoService.CalcularMontoMaximoCredito(cedula);
    }


}
