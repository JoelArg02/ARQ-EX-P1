using API_BANCO.Application.DTOs.Clientes;
using API_BANCO.Application.DTOs.Creditos;
using API_BANCO.Models.Entities;

namespace API_BANCO.Application.Interface;

public interface IClienteBancoService
{
    Task<List<ClienteBanco>> GetAllClientesBanco();
    Task<ClienteBanco?> GetClienteBancoById(int id);
    Task<ClienteBanco?> GetClienteBancoByCedula(string cedula);
    Task<ClienteBanco> CreateClienteBanco(ClienteBancoCreateDto dto);
    Task<ClienteBanco?> UpdateClienteBanco(int id, string cedula, string nombreCompleto, int estadoCivil, DateTime fechaNacimiento, bool tieneCreditoActivo);
    Task<bool> DeleteClienteBanco(int id);
    Task<bool> VerificarClientePorCedula(string cedula);
    Task<VerificacionClienteResponseDto> VerificarElegibilidadCliente(string cedula);
    Task<CalculoCreditoResponseDto> CalcularMontoMaximoCredito(string cedula);
    Task<EvaluacionCreditoResultadoDto> EvaluarCredito(string cedula, decimal montoRequerido);
    Task<AprobacionCreditoResponseDto> AprobarCredito(AprobacionCreditoRequestDto dto);
    Task<CreditoResumenDto?> ObtenerCreditoPorCedula(string cedula);
    Task<List<AmortizacionCreditoDto>> ObtenerAmortizacionPorCreditoId(int creditoId);

}
