using System.ServiceModel;
using API_Comercializadora.Application.Interface;
using BancoBanquitoServiceReference;

namespace API_Comercializadora.Application.Service;

public class BancoBanquitoService : IBancoBanquitoService
{
    private readonly IClienteBancoController _soapClient;
    private readonly ILogger<BancoBanquitoService> _logger;

    public BancoBanquitoService(IConfiguration configuration, ILogger<BancoBanquitoService> logger)
    {
        _logger = logger;
        
        var soapServiceUrl = configuration["BancoBanquito:ServiceUrl"] ?? "http://localhost:5001/ClienteBancoService.svc";
        
        var binding = new BasicHttpBinding
        {
            MaxReceivedMessageSize = int.MaxValue,
            MaxBufferSize = int.MaxValue,
            OpenTimeout = TimeSpan.FromMinutes(1),
            CloseTimeout = TimeSpan.FromMinutes(1),
            SendTimeout = TimeSpan.FromMinutes(10),
            ReceiveTimeout = TimeSpan.FromMinutes(10)
        };

        var endpoint = new EndpointAddress(soapServiceUrl);
        _soapClient = new ClienteBancoControllerClient(binding, endpoint);
        
        _logger.LogInformation($"Cliente SOAP inicializado con endpoint: {soapServiceUrl}");
    }

    public async Task<bool> VerificarClientePorCedula(string cedula)
    {
        try
        {
            _logger.LogInformation($"Verificando existencia del cliente con cédula: {cedula}");
            var resultado = await _soapClient.VerificarClientePorCedulaAsync(cedula);
            _logger.LogInformation($"Cliente con cédula {cedula} existe: {resultado}");
            return resultado;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"Error al verificar cliente por cédula: {cedula}");
            throw new Exception($"Error al comunicarse con el Banco Banquito: {ex.Message}", ex);
        }
    }

    public async Task<VerificacionClienteResponseDto> VerificarElegibilidadCliente(string cedula)
    {
        try
        {
            _logger.LogInformation($"Verificando elegibilidad del cliente con cédula: {cedula}");
            var resultado = await _soapClient.VerificarElegibilidadClienteAsync(cedula);
            
            _logger.LogInformation($"Elegibilidad cliente {cedula}: {resultado.EsElegible} - {resultado.Mensaje}");
            
            return resultado;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"Error al verificar elegibilidad del cliente: {cedula}");
            throw new Exception($"Error al verificar elegibilidad con el Banco Banquito: {ex.Message}", ex);
        }
    }

    public async Task<CalculoCreditoResponseDto> CalcularMontoMaximoCredito(string cedula)
    {
        try
        {
            _logger.LogInformation($"Calculando monto máximo de crédito para cédula: {cedula}");
            var resultado = await _soapClient.CalcularMontoMaximoCreditoAsync(cedula);
            
            _logger.LogInformation($"Monto máximo calculado para {cedula}: ${resultado.MontoMaximoCredito}");
            
            return resultado;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"Error al calcular monto máximo de crédito: {cedula}");
            throw new Exception($"Error al calcular monto con el Banco Banquito: {ex.Message}", ex);
        }
    }

    public async Task<AprobacionCreditoResponseDto> AprobarCredito(AprobacionCreditoRequestDto dto)
    {
        try
        {
            _logger.LogInformation($"Solicitando aprobación de crédito - Cliente: {dto.CedulaCliente}, Monto: ${dto.MontoSolicitado}, Cuotas: {dto.NumeroCuotas}");
            
            var resultado = await _soapClient.AprobarCreditoAsync(dto);
            
            _logger.LogInformation($"Resultado aprobación crédito para {dto.CedulaCliente}: {resultado.Aprobado} - {resultado.Mensaje}");
            
            return resultado;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"Error al aprobar crédito para cliente: {dto.CedulaCliente}");
            throw new Exception($"Error al aprobar crédito con el Banco Banquito: {ex.Message}", ex);
        }
    }
}
