package ec.edu.pinza.ex_comercializadora_restjava.service;

import ec.edu.pinza.ex_comercializadora_restjava.client.BanquitoCreditoClient;
import ec.edu.pinza.ex_comercializadora_restjava.dto.CrearFacturaRequest;
import ec.edu.pinza.ex_comercializadora_restjava.dto.FacturaResponse;
import ec.edu.pinza.ex_comercializadora_restjava.dto.MontoMaximoResponseDTO;
import ec.edu.pinza.ex_comercializadora_restjava.dto.OtorgarCreditoResponseDTO;
import ec.edu.pinza.ex_comercializadora_restjava.dto.SujetoCreditoResponseDTO;
import ec.edu.pinza.ex_comercializadora_restjava.entities.ClienteCom;
import ec.edu.pinza.ex_comercializadora_restjava.entities.DetalleFactura;
import ec.edu.pinza.ex_comercializadora_restjava.entities.Factura;
import ec.edu.pinza.ex_comercializadora_restjava.entities.Producto;
import ec.edu.pinza.ex_comercializadora_restjava.repositories.ClienteComRepository;
import ec.edu.pinza.ex_comercializadora_restjava.repositories.FacturaRepository;
import ec.edu.pinza.ex_comercializadora_restjava.repositories.ProductoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de facturación que maneja pagos en efectivo y créditos
 */
public class FacturaService {
    
    private final ClienteComRepository clienteRepo;
    private final ProductoRepository productoRepo;
    private final FacturaRepository facturaRepo;
    private final BanquitoCreditoClient banquitoClient;
    
    public FacturaService() {
        this.clienteRepo = new ClienteComRepository();
        this.productoRepo = new ProductoRepository();
        this.facturaRepo = new FacturaRepository();
        this.banquitoClient = new BanquitoCreditoClient();
    }
    
    /**
     * Crea una factura (efectivo o crédito)
     * @param request Solicitud de facturación
     * @return Respuesta con resultado
     */
    public FacturaResponse crearFactura(CrearFacturaRequest request) {
        FacturaResponse response = new FacturaResponse();
        
        try {
            // 1. Validar cliente
            ClienteCom cliente = clienteRepo.findByCedula(request.getCedulaCliente()).orElse(null);
            if (cliente == null) {
                response.setExitoso(false);
                response.setMensaje("Cliente no encontrado con cédula: " + request.getCedulaCliente());
                return response;
            }
            
            // 2. Validar items y calcular total
            if (request.getItems() == null || request.getItems().isEmpty()) {
                response.setExitoso(false);
                response.setMensaje("La factura debe tener al menos un item");
                return response;
            }
            
            List<ItemValidado> itemsValidados = new ArrayList<>();
            BigDecimal totalFactura = BigDecimal.ZERO;
            
            for (CrearFacturaRequest.ItemFactura item : request.getItems()) {
                Producto producto = productoRepo.findById(item.getIdProducto()).orElse(null);
                if (producto == null) {
                    response.setExitoso(false);
                    response.setMensaje("Producto no encontrado: " + item.getIdProducto());
                    return response;
                }
                
                if (producto.getStock() < item.getCantidad()) {
                    response.setExitoso(false);
                    response.setMensaje("Stock insuficiente para: " + producto.getNombre() + 
                                      " (disponible: " + producto.getStock() + ", solicitado: " + item.getCantidad() + ")");
                    return response;
                }
                
                BigDecimal subtotal = producto.getPrecio().multiply(new BigDecimal(item.getCantidad()));
                totalFactura = totalFactura.add(subtotal);
                
                ItemValidado validado = new ItemValidado();
                validado.producto = producto;
                validado.cantidad = item.getCantidad();
                validado.subtotal = subtotal;
                itemsValidados.add(validado);
            }
            
            // 3. Procesar según forma de pago
            if ("EFECTIVO".equals(request.getFormaPago())) {
                return procesarPagoEfectivo(cliente, itemsValidados, totalFactura);
            } else if ("CREDITO_DIRECTO".equals(request.getFormaPago())) {
                if (request.getNumeroCuotas() == null || request.getNumeroCuotas() <= 0) {
                    response.setExitoso(false);
                    response.setMensaje("Debe especificar el número de cuotas para pago a crédito");
                    return response;
                }
                return procesarPagoCredito(cliente, itemsValidados, totalFactura, request.getNumeroCuotas());
            } else {
                response.setExitoso(false);
                response.setMensaje("Forma de pago inválida. Use 'EFECTIVO' o 'CREDITO_DIRECTO'");
                return response;
            }
            
        } catch (Exception e) {
            response.setExitoso(false);
            response.setMensaje("Error al procesar factura: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * Procesa pago en efectivo con descuento del 33%
     */
    private FacturaResponse procesarPagoEfectivo(ClienteCom cliente, List<ItemValidado> items, BigDecimal total) {
        FacturaResponse response = new FacturaResponse();
        
        try {
            // Aplicar descuento del 33% para pagos en efectivo
            BigDecimal descuento = total.multiply(new BigDecimal("0.33"));
            BigDecimal totalConDescuento = total.subtract(descuento);
            
            // Crear factura
            Factura factura = new Factura();
            factura.setCliente(cliente);
            factura.setFecha(LocalDate.now());
            factura.setTotal(totalConDescuento);
            factura.setFormaPago("EFECTIVO");
            factura.setIdCreditoBanco(null);
            
            // Crear detalles
            List<DetalleFactura> detalles = new ArrayList<>();
            for (ItemValidado item : items) {
                DetalleFactura detalle = new DetalleFactura();
                detalle.setFactura(factura);
                detalle.setProducto(item.producto);
                detalle.setCantidad(item.cantidad);
                detalle.setPrecioUnitario(item.producto.getPrecio());
                detalle.setSubtotal(item.subtotal);
                detalles.add(detalle);
                
                // Actualizar stock
                item.producto.setStock(item.producto.getStock() - item.cantidad);
                productoRepo.save(item.producto);
            }
            
            factura.setDetalles(detalles);
            facturaRepo.save(factura);
            
            // Construir respuesta
            response.setExitoso(true);
            response.setMensaje("Factura creada exitosamente (Pago en efectivo - Descuento del 33% aplicado)");
            response.setIdFactura(factura.getIdFactura());
            response.setCedulaCliente(cliente.getCedula());
            response.setNombreCliente(cliente.getNombre());
            response.setFecha(factura.getFecha());
            response.setTotal(factura.getTotal());
            response.setFormaPago("EFECTIVO");
            response.setDetalles(construirDetallesDTO(detalles));
            
            return response;
            
        } catch (Exception e) {
            response.setExitoso(false);
            response.setMensaje("Error al guardar factura: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * Procesa pago a crédito (integrando con BanQuito)
     */
    private FacturaResponse procesarPagoCredito(ClienteCom cliente, List<ItemValidado> items, 
                                                BigDecimal total, int numeroCuotas) {
        FacturaResponse response = new FacturaResponse();
        
        try {
            // 1. Validar sujeto de crédito
            SujetoCreditoResponseDTO validacion = banquitoClient.esSujetoCredito(cliente.getCedula());
            if (!validacion.isEsSujetoCredito()) {
                response.setExitoso(false);
                response.setMensaje("Cliente no es sujeto de crédito: " + validacion.getMotivo());
                return response;
            }
            
            // 2. Validar monto máximo
            MontoMaximoResponseDTO montoMax = banquitoClient.obtenerMontoMaximo(cliente.getCedula());
            if (montoMax.getMontoMaximo().compareTo(total) < 0) {
                response.setExitoso(false);
                response.setMensaje("Monto solicitado ($" + total + ") supera el monto máximo disponible ($" + 
                                  montoMax.getMontoMaximo() + ")");
                return response;
            }
            
            // 3. Solicitar crédito al banco
            OtorgarCreditoResponseDTO credito = banquitoClient.otorgarCredito(
                cliente.getCedula(), 
                total, 
                numeroCuotas
            );
            
            if (!credito.isAprobado()) {
                response.setExitoso(false);
                response.setMensaje("Crédito no aprobado: " + credito.getMensaje());
                return response;
            }
            
            // 4. Crear factura con referencia al crédito
            Factura factura = new Factura();
            factura.setCliente(cliente);
            factura.setFecha(LocalDate.now());
            factura.setTotal(total);
            factura.setFormaPago("CREDITO_DIRECTO");
            factura.setIdCreditoBanco(credito.getIdCredito());
            
            // Crear detalles
            List<DetalleFactura> detalles = new ArrayList<>();
            for (ItemValidado item : items) {
                DetalleFactura detalle = new DetalleFactura();
                detalle.setFactura(factura);
                detalle.setProducto(item.producto);
                detalle.setCantidad(item.cantidad);
                detalle.setPrecioUnitario(item.producto.getPrecio());
                detalle.setSubtotal(item.subtotal);
                detalles.add(detalle);
                
                // Actualizar stock
                item.producto.setStock(item.producto.getStock() - item.cantidad);
                productoRepo.save(item.producto);
            }
            
            factura.setDetalles(detalles);
            facturaRepo.save(factura);
            
            // 5. Construir respuesta
            response.setExitoso(true);
            response.setMensaje("Factura creada exitosamente (Pago a crédito)");
            response.setIdFactura(factura.getIdFactura());
            response.setCedulaCliente(cliente.getCedula());
            response.setNombreCliente(cliente.getNombre());
            response.setFecha(factura.getFecha());
            response.setTotal(factura.getTotal());
            response.setFormaPago("CREDITO_DIRECTO");
            response.setIdCreditoBanco(credito.getIdCredito());
            response.setDetalles(construirDetallesDTO(detalles));
            
            // Info del crédito
            FacturaResponse.InfoCredito infoCredito = new FacturaResponse.InfoCredito();
            infoCredito.setIdCredito(credito.getIdCredito());
            infoCredito.setMontoCredito(credito.getMontoAprobado());
            infoCredito.setNumeroCuotas(numeroCuotas);
            infoCredito.setValorCuota(credito.getTablaAmortizacion() != null && !credito.getTablaAmortizacion().isEmpty() 
                    ? credito.getTablaAmortizacion().get(0).getValorCuota() 
                    : null);
            infoCredito.setTasaInteres(credito.getTasaInteresAnual());
            infoCredito.setEstado("ACTIVO");
            infoCredito.setTablaAmortizacion(credito.getTablaAmortizacion());
            response.setInfoCredito(infoCredito);
            
            return response;
            
        } catch (Exception e) {
            response.setExitoso(false);
            response.setMensaje("Error al procesar crédito: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * Obtiene tabla de amortización de una factura a crédito
     */
    public List<OtorgarCreditoResponseDTO.CuotaDTO> obtenerTablaAmortizacion(Integer idFactura) {
        Factura factura = facturaRepo.findById(idFactura).orElse(null);
        if (factura == null) {
            return null;
        }
        
        if (!"CREDITO_DIRECTO".equals(factura.getFormaPago()) || factura.getIdCreditoBanco() == null) {
            return null;
        }
        
        return banquitoClient.obtenerTablaAmortizacion(factura.getIdCreditoBanco());
    }
    
    /**
     * Construye DTOs de detalles
     */
    private List<FacturaResponse.DetalleFacturaDTO> construirDetallesDTO(List<DetalleFactura> detalles) {
        List<FacturaResponse.DetalleFacturaDTO> dtos = new ArrayList<>();
        for (DetalleFactura detalle : detalles) {
            FacturaResponse.DetalleFacturaDTO dto = new FacturaResponse.DetalleFacturaDTO();
            dto.setCodigoProducto(detalle.getProducto().getCodigo());
            dto.setNombreProducto(detalle.getProducto().getNombre());
            dto.setCantidad(detalle.getCantidad());
            dto.setPrecioUnitario(detalle.getPrecioUnitario());
            dto.setSubtotal(detalle.getSubtotal());
            dtos.add(dto);
        }
        return dtos;
    }

    public List<FacturaResponse> obtenerTodasLasFacturas() {
        List<FacturaResponse> respuestas = new ArrayList<>();
        
        try {
            List<Factura> facturas = facturaRepo.findAll();
            System.out.println("✅ TOTAL FACTURAS ENCONTRADAS EN BD: " + facturas.size());
            
            for (Factura factura : facturas) {
                System.out.println("📋 Procesando factura ID: " + factura.getIdFactura());
                ClienteCom cliente = factura.getCliente();
                
                // Validar que el cliente exista antes de usarlo
                if (cliente == null) {
                    System.err.println("⚠️ Factura " + factura.getIdFactura() + " omitida: cliente no encontrado");
                    continue;
                }
                
                FacturaResponse response = new FacturaResponse();
                response.setIdFactura(factura.getIdFactura());
                response.setCedulaCliente(cliente.getCedula());
                response.setNombreCliente(cliente.getNombre());
                response.setFecha(factura.getFecha());
                response.setTotal(factura.getTotal());
                response.setFormaPago(factura.getFormaPago());
                response.setIdCreditoBanco(factura.getIdCreditoBanco());
                response.setDetalles(construirDetallesDTO(factura.getDetalles()));
                
                // Si es crédito, agregar info
                if ("CREDITO_DIRECTO".equals(factura.getFormaPago()) && factura.getIdCreditoBanco() != null) {
                    try {
                        OtorgarCreditoResponseDTO creditoInfo = banquitoClient.obtenerInformacionCredito(factura.getIdCreditoBanco());
                        
                        if (creditoInfo != null && creditoInfo.getTablaAmortizacion() != null && !creditoInfo.getTablaAmortizacion().isEmpty()) {
                            FacturaResponse.InfoCredito infoCredito = new FacturaResponse.InfoCredito();
                            infoCredito.setIdCredito(factura.getIdCreditoBanco());
                            infoCredito.setMontoCredito(factura.getTotal());
                            infoCredito.setNumeroCuotas(creditoInfo.getTablaAmortizacion().size());
                            infoCredito.setValorCuota(creditoInfo.getTablaAmortizacion().get(0).getValorCuota());
                            infoCredito.setTasaInteres(creditoInfo.getTasaInteresAnual());
                            infoCredito.setEstado(creditoInfo.getEstado() != null ? creditoInfo.getEstado() : "APROBADO");
                            infoCredito.setTablaAmortizacion(creditoInfo.getTablaAmortizacion());
                            response.setInfoCredito(infoCredito);
                        }
                    } catch (Exception e) {
                        System.err.println("Error al obtener info de crédito: " + e.getMessage());
                    }
                }
                
                respuestas.add(response);
                System.out.println("✅ Factura " + factura.getIdFactura() + " agregada a respuesta");
            }
            
            System.out.println("📊 TOTAL FACTURAS EN RESPUESTA: " + respuestas.size());
        } catch (Exception e) {
            System.err.println("❌ Error al obtener todas las facturas: " + e.getMessage());
            e.printStackTrace();
        }
        
        return respuestas;
    }
    
    /**
     * Obtiene todas las facturas de un cliente por su cédula
     */
    public List<FacturaResponse> obtenerFacturasPorCliente(String cedula) {
        List<FacturaResponse> respuestas = new ArrayList<>();
        
        try {
            ClienteCom cliente = clienteRepo.findByCedula(cedula).orElse(null);
            if (cliente == null) {
                return respuestas;
            }
            
            List<Factura> facturas = facturaRepo.findByClienteId(cliente.getIdCliente());
            
            for (Factura factura : facturas) {
                // Validar que el cliente de la factura exista (doble validación)
                ClienteCom clienteFactura = factura.getCliente();
                if (clienteFactura == null) {
                    System.err.println("⚠️ Factura " + factura.getIdFactura() + " omitida: cliente no encontrado en factura");
                    continue;
                }
                
                FacturaResponse response = new FacturaResponse();
                response.setIdFactura(factura.getIdFactura());
                response.setCedulaCliente(cliente.getCedula());
                response.setNombreCliente(cliente.getNombre());
                response.setFecha(factura.getFecha());
                response.setTotal(factura.getTotal());
                response.setFormaPago(factura.getFormaPago());
                response.setIdCreditoBanco(factura.getIdCreditoBanco());
                response.setDetalles(construirDetallesDTO(factura.getDetalles()));
                
                // Si es crédito, agregar info
                if ("CREDITO_DIRECTO".equals(factura.getFormaPago()) && factura.getIdCreditoBanco() != null) {
                    try {
                        OtorgarCreditoResponseDTO creditoInfo = banquitoClient.obtenerInformacionCredito(factura.getIdCreditoBanco());
                        
                        if (creditoInfo != null && creditoInfo.getTablaAmortizacion() != null && !creditoInfo.getTablaAmortizacion().isEmpty()) {
                            FacturaResponse.InfoCredito infoCredito = new FacturaResponse.InfoCredito();
                            infoCredito.setIdCredito(factura.getIdCreditoBanco());
                            infoCredito.setMontoCredito(factura.getTotal());
                            infoCredito.setNumeroCuotas(creditoInfo.getTablaAmortizacion().size());
                            infoCredito.setValorCuota(creditoInfo.getTablaAmortizacion().get(0).getValorCuota());
                            infoCredito.setTasaInteres(creditoInfo.getTasaInteresAnual());
                            infoCredito.setEstado(creditoInfo.getEstado() != null ? creditoInfo.getEstado() : "APROBADO");
                            infoCredito.setTablaAmortizacion(creditoInfo.getTablaAmortizacion());
                            response.setInfoCredito(infoCredito);
                        }
                    } catch (Exception e) {
                        // Si falla obtener el crédito, continuar sin esa info
                        System.err.println("Error al obtener info de crédito: " + e.getMessage());
                    }
                }
                
                respuestas.add(response);
            }
            
        } catch (Exception e) {
            System.err.println("Error al obtener facturas del cliente: " + e.getMessage());
        }
        
        return respuestas;
    }
    
    /**
     * Obtiene una factura específica por su ID
     */
    public FacturaResponse obtenerFacturaPorId(Integer idFactura) {
        try {
            Factura factura = facturaRepo.findById(idFactura).orElse(null);
            if (factura == null) {
                return null;
            }
            
            ClienteCom cliente = factura.getCliente();
            
            // Validar que el cliente exista
            if (cliente == null) {
                System.err.println("⚠️ Factura " + idFactura + " tiene cliente nulo, no se puede construir respuesta completa");
                return null;
            }
            
            FacturaResponse response = new FacturaResponse();
            response.setIdFactura(factura.getIdFactura());
            response.setCedulaCliente(cliente.getCedula());
            response.setNombreCliente(cliente.getNombre());
            response.setFecha(factura.getFecha());
            response.setTotal(factura.getTotal());
            response.setFormaPago(factura.getFormaPago());
            response.setIdCreditoBanco(factura.getIdCreditoBanco());
            response.setDetalles(construirDetallesDTO(factura.getDetalles()));
            
            // Si es crédito, agregar info completa
            if ("CREDITO_DIRECTO".equals(factura.getFormaPago()) && factura.getIdCreditoBanco() != null) {
                System.out.println("DEBUG: Factura es crédito, ID crédito: " + factura.getIdCreditoBanco());
                try {
                    OtorgarCreditoResponseDTO creditoInfo = banquitoClient.obtenerInformacionCredito(factura.getIdCreditoBanco());
                    
                    System.out.println("DEBUG: Info crédito obtenida, estado: " + (creditoInfo != null ? creditoInfo.getEstado() : "null"));
                    
                    if (creditoInfo != null && creditoInfo.getTablaAmortizacion() != null && !creditoInfo.getTablaAmortizacion().isEmpty()) {
                        FacturaResponse.InfoCredito infoCredito = new FacturaResponse.InfoCredito();
                        infoCredito.setIdCredito(factura.getIdCreditoBanco());
                        infoCredito.setMontoCredito(factura.getTotal());
                        infoCredito.setNumeroCuotas(creditoInfo.getTablaAmortizacion().size());
                        infoCredito.setValorCuota(creditoInfo.getTablaAmortizacion().get(0).getValorCuota());
                        infoCredito.setTasaInteres(creditoInfo.getTasaInteresAnual());
                        infoCredito.setEstado(creditoInfo.getEstado() != null ? creditoInfo.getEstado() : "APROBADO");
                        infoCredito.setTablaAmortizacion(creditoInfo.getTablaAmortizacion());
                        response.setInfoCredito(infoCredito);
                        System.out.println("DEBUG: InfoCredito configurado correctamente con estado: " + infoCredito.getEstado());
                    } else {
                        System.err.println("DEBUG: Información de crédito vacía o nula");
                    }
                } catch (Exception e) {
                    System.err.println("Error al obtener info de crédito: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("DEBUG: Factura NO es crédito o no tiene ID crédito. FormaPago: " + factura.getFormaPago() + ", IDCreditoBanco: " + factura.getIdCreditoBanco());
            }
            
            return response;
            
        } catch (Exception e) {
            System.err.println("Error al obtener factura por ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Clase interna para items validados
     */
    private static class ItemValidado {
        Producto producto;
        Integer cantidad;
        BigDecimal subtotal;
    }
}
