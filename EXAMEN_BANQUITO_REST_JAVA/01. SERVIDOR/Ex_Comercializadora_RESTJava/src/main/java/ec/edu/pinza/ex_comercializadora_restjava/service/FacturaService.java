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
     * Procesa pago en efectivo
     */
    private FacturaResponse procesarPagoEfectivo(ClienteCom cliente, List<ItemValidado> items, BigDecimal total) {
        FacturaResponse response = new FacturaResponse();
        
        try {
            // Crear factura
            Factura factura = new Factura();
            factura.setCliente(cliente);
            factura.setFecha(LocalDate.now());
            factura.setTotal(total);
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
            response.setMensaje("Factura creada exitosamente (Pago en efectivo)");
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
            infoCredito.setNumeroCuotas(numeroCuotas);
            infoCredito.setTasaInteres(credito.getTasaInteresAnual());
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
    
    /**
     * Clase interna para items validados
     */
    private static class ItemValidado {
        Producto producto;
        Integer cantidad;
        BigDecimal subtotal;
    }
}
