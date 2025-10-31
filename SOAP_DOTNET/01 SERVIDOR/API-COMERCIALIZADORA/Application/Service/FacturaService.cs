using API_Comercializadora.Application.DTOs;
using API_Comercializadora.Application.Interface;
using API_Comercializadora.Configuration;
using API_Comercializadora.Models;
using API_Comercializadora.Models.Enums;
using API_Comercializadora.Repositories;
using API_Comercializadora.Repositories.Interfaces;
using BancoBanquitoServiceReference;
using Microsoft.EntityFrameworkCore;

namespace API_Comercializadora.Application.Service
{
    public class FacturaService : IFacturaService
    {
        private readonly AppDbContext _context;
        private readonly IFacturaRepository _repository;
        private readonly IBancoBanquitoService _bancoBanquitoService;
        private readonly ILogger<FacturaService> _logger;
        private const decimal DESCUENTO_EFECTIVO = 0.33m;

        public FacturaService(
            AppDbContext context,
            IBancoBanquitoService bancoBanquitoService,
            ILogger<FacturaService> logger
        )
        {
            _context = context;
            _repository = new FacturaRepository(_context);
            _bancoBanquitoService = bancoBanquitoService;
            _logger = logger;
        }

        public async Task<List<Factura>> GetAllFacturas()
        {
            return await _repository.GetAllAsync();
        }

        public async Task<Factura?> GetFacturaById(int id)
        {
            return await _repository.GetByIdAsync(id);
        }

        public async Task<Factura?> CreateFacturaWithDetails(
            Factura factura,
            List<DetalleFactura> detalles
        )
        {
            return await _repository.CreateWithDetailsAsync(factura, detalles);
        }

        public async Task<bool> DeleteFactura(int id)
        {
            return await _repository.DeleteAsync(id);
        }

        public async Task<VerificarElegibilidadResponseDto> VerificarElegibilidadCredito(
            string cedula
        )
        {
            try
            {
                _logger.LogInformation(
                    $"Verificando elegibilidad de crédito para cédula: {cedula}"
                );

                // Verificar si existe el cliente en el banco
                var existeCliente = await _bancoBanquitoService.VerificarClientePorCedula(cedula);

                if (!existeCliente)
                {
                    return new VerificarElegibilidadResponseDto
                    {
                        EsElegible = false,
                        Mensaje =
                            "El cliente no está registrado en el Banco Banquito. No es sujeto de crédito.",
                        MontoMaximoCredito = 0,
                    };
                }

                // Verificar elegibilidad
                var elegibilidad = await _bancoBanquitoService.VerificarElegibilidadCliente(cedula);

                if (!elegibilidad.EsElegible)
                {
                    return new VerificarElegibilidadResponseDto
                    {
                        EsElegible = false,
                        Mensaje = elegibilidad.Mensaje,
                        MontoMaximoCredito = 0,
                    };
                }

                // Calcular monto máximo de crédito
                var calculoCredito = await _bancoBanquitoService.CalcularMontoMaximoCredito(cedula);

                return new VerificarElegibilidadResponseDto
                {
                    EsElegible = true,
                    Mensaje = $"Cliente elegible para crédito. {calculoCredito.Mensaje}",
                    MontoMaximoCredito = calculoCredito.MontoMaximoCredito,
                };
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, $"Error al verificar elegibilidad para cédula: {cedula}");
                throw;
            }
        }

        public async Task<FacturaResponseDto> CrearFacturaConValidacion(
            CrearFacturaRequestDto request
        )
        {
            using var transaction = await _context.Database.BeginTransactionAsync();

            try
            {
                _logger.LogInformation(
                    $"Iniciando creación de factura para cliente ID: {request.ClienteId}"
                );

                // 1. Obtener el cliente
                var cliente = await _context
                    .Set<Cliente>()
                    .FirstOrDefaultAsync(c => c.Id == request.ClienteId);

                if (cliente == null)
                {
                    return new FacturaResponseDto
                    {
                        Exitoso = false,
                        Mensaje = "Cliente no encontrado en el sistema.",
                    };
                }

                // 2. Validar que existan los electrodomésticos y obtener precios
                var electrodomesticosIds = request
                    .Detalles.Select(d => d.ElectrodomesticoId)
                    .ToList();
                var electrodomesticos = await _context
                    .Set<Electrodomestico>()
                    .Where(e => electrodomesticosIds.Contains(e.Id) && e.Activo)
                    .ToListAsync();

                if (electrodomesticos.Count != request.Detalles.Count)
                {
                    return new FacturaResponseDto
                    {
                        Exitoso = false,
                        Mensaje = "Uno o más electrodomésticos no están disponibles.",
                    };
                }

                // 3. Calcular subtotal
                decimal subtotal = 0;
                foreach (var detalle in request.Detalles)
                {
                    var electrodomestico = electrodomesticos.First(e =>
                        e.Id == detalle.ElectrodomesticoId
                    );
                    subtotal += electrodomestico.PrecioVenta * detalle.Cantidad;
                }

                decimal descuento = 0;
                decimal totalFinal = subtotal;
                CreditoInfoDto? creditoInfo = null;

                // 4. Aplicar lógica según forma de pago
                if (request.FormaPago == FormaPagoEnum.Efectivo)
                {
                    // Pago en efectivo: aplicar 33% de descuento
                    descuento = subtotal * DESCUENTO_EFECTIVO;
                    totalFinal = subtotal - descuento;

                    _logger.LogInformation($"Pago en efectivo - Descuento del 33%: ${descuento}");
                }
                else if (request.FormaPago == FormaPagoEnum.CreditoDirecto)
                {
                    // Pago con crédito: verificar con el banco usando la cédula del cliente
                    _logger.LogInformation(
                        $"Pago con crédito - Verificando elegibilidad en Banco Banquito para cédula: {cliente.Cedula}"
                    );

                    // Verificar elegibilidad
                    var elegibilidad = await VerificarElegibilidadCredito(cliente.Cedula);

                    if (!elegibilidad.EsElegible)
                    {
                        await transaction.RollbackAsync();
                        return new FacturaResponseDto
                        {
                            Exitoso = false,
                            Mensaje = $"Crédito no aprobado: {elegibilidad.Mensaje}",
                        };
                    }

                    // Verificar que el monto no exceda el máximo
                    if (totalFinal > elegibilidad.MontoMaximoCredito)
                    {
                        await transaction.RollbackAsync();
                        return new FacturaResponseDto
                        {
                            Exitoso = false,
                            Mensaje =
                                $"El monto de la compra (${totalFinal}) excede el monto máximo de crédito aprobado (${elegibilidad.MontoMaximoCredito})",
                        };
                    }

                    // Validar número de cuotas
                    if (!request.NumeroCuotas.HasValue || request.NumeroCuotas.Value < 1)
                    {
                        return new FacturaResponseDto
                        {
                            Exitoso = false,
                            Mensaje = "Debe especificar el número de cuotas para el crédito.",
                        };
                    }

                    // Aprobar crédito en el banco
                    var aprobacionRequest = new AprobacionCreditoRequestDto
                    {
                        CedulaCliente = cliente.Cedula,
                        MontoSolicitado = totalFinal,
                        NumeroCuotas = request.NumeroCuotas.Value,
                    };

                    var aprobacion = await _bancoBanquitoService.AprobarCredito(aprobacionRequest);

                    if (!aprobacion.Aprobado)
                    {
                        await transaction.RollbackAsync();
                        return new FacturaResponseDto
                        {
                            Exitoso = false,
                            Mensaje = $"Crédito rechazado por el banco: {aprobacion.Mensaje}",
                        };
                    }

                    creditoInfo = new CreditoInfoDto
                    {
                        CreditoAprobado = true,
                        MontoCredito = totalFinal,
                        NumeroCuotas = aprobacion.NumeroCuotas,
                        ValorCuota = aprobacion.ValorCuota,
                        MensajeBanco = aprobacion.Mensaje,
                    };

                    _logger.LogInformation(
                        $"Crédito aprobado - Cuotas: {aprobacion.NumeroCuotas}, Valor cuota: ${aprobacion.ValorCuota}"
                    );
                }

                // 5. Crear factura
                var numeroFactura = GenerarNumeroFactura();
                var factura = new Factura
                {
                    NumeroFactura = numeroFactura,
                    FechaEmision = DateTime.UtcNow,
                    ClienteId = cliente.Id,
                    FormaPago = request.FormaPago,
                    Subtotal = subtotal,
                    Descuento = descuento,
                    TotalFinal = totalFinal,
                };

                _context.Set<Factura>().Add(factura);
                await _context.SaveChangesAsync();

                // 6. Crear detalles de factura
                foreach (var detalle in request.Detalles)
                {
                    var electrodomestico = electrodomesticos.First(e =>
                        e.Id == detalle.ElectrodomesticoId
                    );
                    var detalleFactura = new DetalleFactura
                    {
                        FacturaId = factura.Id,
                        ElectrodomesticoId = detalle.ElectrodomesticoId,
                        Cantidad = detalle.Cantidad,
                        PrecioUnitario = electrodomestico.PrecioVenta,
                        Subtotal = electrodomestico.PrecioVenta * detalle.Cantidad,
                    };
                    _context.Set<DetalleFactura>().Add(detalleFactura);
                }

                await _context.SaveChangesAsync();
                await transaction.CommitAsync();

                _logger.LogInformation($"Factura creada exitosamente: {numeroFactura}");

                return new FacturaResponseDto
                {
                    Exitoso = true,
                    Mensaje = "Factura creada exitosamente.",
                    FacturaId = factura.Id,
                    NumeroFactura = factura.NumeroFactura,
                    Subtotal = subtotal,
                    Descuento = descuento,
                    TotalFinal = totalFinal,
                    FormaPago = request.FormaPago.ToString(),
                    InformacionCredito = creditoInfo,
                };
            }
            catch (Exception ex)
            {
                await transaction.RollbackAsync();
                _logger.LogError(ex, "Error al crear factura");
                throw new Exception($"Error al procesar la factura: {ex.Message}", ex);
            }
        }

        private string GenerarNumeroFactura()
        {
            var timestamp = DateTime.UtcNow.ToString("yyyyMMddHHmmss");
            var random = new Random().Next(1000, 9999);
            return $"FAC-{timestamp}-{random}";
        }
    }
}
