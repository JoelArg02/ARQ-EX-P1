using BancoBanquitoServiceReference;

namespace API_Comercializadora.Application.Interface;

public interface IBancoBanquitoService
{
    Task<bool> VerificarClientePorCedula(string cedula);
    Task<VerificacionClienteResponseDto> VerificarElegibilidadCliente(string cedula);
    Task<CalculoCreditoResponseDto> CalcularMontoMaximoCredito(string cedula);
    Task<AprobacionCreditoResponseDto> AprobarCredito(AprobacionCreditoRequestDto dto);
}
