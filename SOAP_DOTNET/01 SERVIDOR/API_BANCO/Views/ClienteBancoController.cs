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

    [OperationContract]
    Task<EvaluacionCreditoResultadoDto> EvaluarCredito(EvaluacionCreditoRequestDto dto);

    [OperationContract]
    Task<AprobacionCreditoResponseDto> AprobarCredito(AprobacionCreditoRequestDto dto);
    [OperationContract]
    Task<CreditoResumenDto?> ObtenerCreditoPorCedula(string cedula);

    [OperationContract]
    Task<List<AmortizacionCreditoDto>> ObtenerAmortizacionPorCreditoId(int creditoId);

    [OperationContract]
    Task<List<AmortizacionCreditoDto>> GetAmortizacionesByCredito(int creditoId);

    [OperationContract]
    Task<List<CreditoBanco>> GetAllCreditosBanco();

    [OperationContract]
    Task<List<CreditoBanco>> GetCreditosByClienteId(int clienteId);
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
        try
        {
            Console.WriteLine($"[DEBUG] CreateClienteBanco recibido - Cedula: {dto.Cedula}, Nombre: {dto.NombreCompleto}, EstadoCivil: {dto.EstadoCivil}, FechaNacimiento: {dto.FechaNacimiento}");
            var resultado = await _clienteBancoService.CreateClienteBanco(dto);
            Console.WriteLine($"[DEBUG] Cliente creado exitosamente - ID: {resultado.Id}");
            return resultado;
        }
        catch (Exception ex)
        {
            Console.WriteLine($"[ERROR] CreateClienteBanco falló: {ex.Message}");
            Console.WriteLine($"[ERROR] StackTrace: {ex.StackTrace}");
            throw;
        }
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

    public async Task<EvaluacionCreditoResultadoDto> EvaluarCredito(EvaluacionCreditoRequestDto dto)
    {
        return await _clienteBancoService.EvaluarCredito(dto.CedulaCliente, dto.MontoRequerido);
    }
    public async Task<AprobacionCreditoResponseDto> AprobarCredito(AprobacionCreditoRequestDto dto)
    {
        return await _clienteBancoService.AprobarCredito(dto);
    }

    public async Task<CreditoResumenDto?> ObtenerCreditoPorCedula(string cedula)
    {
        return await _clienteBancoService.ObtenerCreditoPorCedula(cedula);
    }

    public async Task<List<AmortizacionCreditoDto>> ObtenerAmortizacionPorCreditoId(int creditoId)
    {
        return await _clienteBancoService.ObtenerAmortizacionPorCreditoId(creditoId);
    }

    public async Task<List<AmortizacionCreditoDto>> GetAmortizacionesByCredito(int creditoId)
    {
        return await _clienteBancoService.GetAmortizacionesByCredito(creditoId);
    }

    public async Task<List<CreditoBanco>> GetAllCreditosBanco()
    {
        return await _clienteBancoService.GetAllCreditosBanco();
    }

    public async Task<List<CreditoBanco>> GetCreditosByClienteId(int clienteId)
    {
        return await _clienteBancoService.GetCreditosByClienteId(clienteId);
    }
}
