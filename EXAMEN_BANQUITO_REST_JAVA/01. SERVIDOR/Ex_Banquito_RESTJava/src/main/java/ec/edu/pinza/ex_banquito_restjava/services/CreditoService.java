package ec.edu.pinza.ex_banquito_restjava.services;

import ec.edu.pinza.ex_banquito_restjava.entities.Cliente;
import ec.edu.pinza.ex_banquito_restjava.entities.Credito;
import ec.edu.pinza.ex_banquito_restjava.entities.CuotaAmortizacion;
import ec.edu.pinza.ex_banquito_restjava.entities.Movimiento;
import ec.edu.pinza.ex_banquito_restjava.repositories.ClienteRepository;
import ec.edu.pinza.ex_banquito_restjava.repositories.CreditoRepository;
import ec.edu.pinza.ex_banquito_restjava.repositories.CuotaAmortizacionRepository;
import ec.edu.pinza.ex_banquito_restjava.repositories.MovimientoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para manejar la lógica de negocio de créditos
 */
public class CreditoService {
    
    private final ClienteRepository clienteRepository = new ClienteRepository();
    private final CreditoRepository creditoRepository = new CreditoRepository();
    private final MovimientoRepository movimientoRepository = new MovimientoRepository();
    private final CuotaAmortizacionRepository cuotaRepository = new CuotaAmortizacionRepository();
    
    private static final BigDecimal TASA_INTERES_ANUAL = new BigDecimal("16.00");
    private static final BigDecimal PORCENTAJE_BASE = new BigDecimal("0.60");
    private static final int MULTIPLICADOR = 9;
    
    /**
     * Valida si un cliente es sujeto de crédito
     */
    public ValidationResult validarSujetoCredito(String cedula) {
        // 1. Verificar si es cliente del banco
        Optional<Cliente> clienteOpt = clienteRepository.findByCedula(cedula);
        if (clienteOpt.isEmpty()) {
            return new ValidationResult(false, "El cliente no está registrado en el banco, no es cliente");
        }
        
        Cliente cliente = clienteOpt.get();
        
        // 2. Verificar que tenga al menos una transacción de DEPÓSITO en el último mes
        LocalDate hoy = LocalDate.now();
        LocalDate hace1Mes = hoy.minusMonths(1);
        
        List<Movimiento> depositos = movimientoRepository.findByCedulaAndTipoAndDateRange(
            cedula, "DEP", hace1Mes, hoy);
        
        if (depositos.isEmpty()) {
            return new ValidationResult(false, "El cliente no tiene depósitos en el último mes");
        }
        
        // 3. Verificar edad si está casado
        if ("C".equalsIgnoreCase(cliente.getEstadoCivil())) {
            int edad = Period.between(cliente.getFechaNacimiento(), hoy).getYears();
            if (edad < 25) {
                return new ValidationResult(false, "Cliente casado menor de 25 años no califica");
            }
        }
        
        // 4. Verificar que NO tenga créditos activos
        boolean tieneCreditsActivos = creditoRepository.tieneCreditsActivos(cedula);
        if (tieneCreditsActivos) {
            return new ValidationResult(false, "El cliente ya tiene un crédito activo");
        }
        
        return new ValidationResult(true, "Cliente calificado para crédito");
    }
    
    /**
     * Calcula el monto máximo de crédito autorizado
     */
    public BigDecimal calcularMontoMaximo(String cedula) {
        LocalDate hoy = LocalDate.now();
        LocalDate hace3Meses = hoy.minusMonths(3);
        
        // Obtener depósitos de los últimos 3 meses
        List<Movimiento> depositos = movimientoRepository.findByCedulaAndTipoAndDateRange(
            cedula, "DEP", hace3Meses, hoy);
        
        // Obtener retiros de los últimos 3 meses
        List<Movimiento> retiros = movimientoRepository.findByCedulaAndTipoAndDateRange(
            cedula, "RET", hace3Meses, hoy);
        
        // Calcular promedios
        BigDecimal promedioDepositos = calcularPromedio(depositos);
        BigDecimal promedioRetiros = calcularPromedio(retiros);
        
        // Calcular monto máximo: ((Promedio_Depósitos – Promedio_Retiros) * 60%) * 9
        BigDecimal diferencia = promedioDepositos.subtract(promedioRetiros);
        BigDecimal base = diferencia.multiply(PORCENTAJE_BASE);
        BigDecimal montoMaximo = base.multiply(new BigDecimal(MULTIPLICADOR));
        
        return montoMaximo.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Otorga un crédito y genera la tabla de amortización
     */
    public CreditoResult otorgarCredito(String cedula, BigDecimal precioElectrodomestico, int numeroCuotas) {
        try {
            // Validar que sea sujeto de crédito
            ValidationResult validacion = validarSujetoCredito(cedula);
            if (!validacion.isValido()) {
                return new CreditoResult(false, validacion.getMensaje(), null, null);
            }
            
            // Validar número de cuotas (mínimo 3, máximo 24)
            if (numeroCuotas < 3 || numeroCuotas > 24) {
                return new CreditoResult(false, "El plazo debe estar entre 3 y 24 meses", null, null);
            }
            
            // Calcular monto máximo autorizado
            BigDecimal montoMaximo = calcularMontoMaximo(cedula);
            
            // Verificar que el precio no exceda el monto máximo
            if (precioElectrodomestico.compareTo(montoMaximo) > 0) {
                return new CreditoResult(false, 
                    "El monto solicitado excede el límite autorizado de $" + montoMaximo, 
                    null, null);
            }
            
            // Crear el crédito
            Cliente cliente = clienteRepository.findByCedula(cedula).get();
            Credito credito = new Credito();
            credito.setCliente(cliente);
            credito.setMontoAprobado(precioElectrodomestico);
            credito.setMontoSolicitado(precioElectrodomestico);
            credito.setPlazoMeses(numeroCuotas);
            credito.setTasaInteresAnual(TASA_INTERES_ANUAL);
            credito.setMontoMaximoAutorizado(montoMaximo);
            credito.setFechaOtorgamiento(LocalDate.now());
            credito.setEstado("APROBADO");
            
            credito = creditoRepository.save(credito);
            
            // Generar y guardar tabla de amortización
            List<CuotaAmortizacion> cuotas = generarYGuardarTablaAmortizacion(credito, precioElectrodomestico, numeroCuotas);
            
            return new CreditoResult(true, "Crédito aprobado exitosamente", credito, cuotas);
        } catch (Exception e) {
            e.printStackTrace();
            return new CreditoResult(false, "Error al otorgar crédito: " + e.getMessage(), null, null);
        }
    }
    
    /**
     * Genera y guarda la tabla de amortización con cuota fija
     */
    private List<CuotaAmortizacion> generarYGuardarTablaAmortizacion(Credito credito, BigDecimal monto, int numeroCuotas) {
        List<CuotaAmortizacion> cuotas = new ArrayList<>();
        
        // Recargar el crédito desde BD para tener una referencia fresca
        Credito creditoFresco = creditoRepository.findById(credito.getIdCredito())
                .orElseThrow(() -> new RuntimeException("No se encontró el crédito recién creado"));
        
        // Tasa periódica mensual: 16% / 12
        BigDecimal tasaMensual = TASA_INTERES_ANUAL.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                                                    .divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        
        // Calcular cuota fija usando la fórmula:
        // Cuota = Monto * (tasa * (1 + tasa)^n) / ((1 + tasa)^n - 1)
        BigDecimal unaMasTasa = BigDecimal.ONE.add(tasaMensual);
        BigDecimal potencia = unaMasTasa.pow(numeroCuotas); // (1 + tasa)^n
        BigDecimal numerador = tasaMensual.multiply(potencia); // tasa * (1 + tasa)^n
        BigDecimal denominador = potencia.subtract(BigDecimal.ONE); // (1 + tasa)^n - 1
        BigDecimal factor = numerador.divide(denominador, 10, RoundingMode.HALF_UP);
        BigDecimal cuotaFija = monto.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal saldoRestante = monto;
        
        for (int i = 1; i <= numeroCuotas; i++) {
            // Calcular interés de esta cuota
            BigDecimal interesPagado = saldoRestante.multiply(tasaMensual)
                                                    .setScale(2, RoundingMode.HALF_UP);
            
            // Calcular capital pagado
            BigDecimal capitalPagado = cuotaFija.subtract(interesPagado);
            
            // Ajustar última cuota por redondeos
            if (i == numeroCuotas) {
                capitalPagado = saldoRestante;
                cuotaFija = capitalPagado.add(interesPagado);
            }
            
            // Actualizar saldo restante
            saldoRestante = saldoRestante.subtract(capitalPagado);
            
            // Crear cuota con el crédito fresco de BD
            CuotaAmortizacion cuota = new CuotaAmortizacion();
            cuota.setCredito(creditoFresco);
            cuota.setNumeroCuota(i);
            cuota.setValorCuota(cuotaFija);
            cuota.setInteresPagado(interesPagado);
            cuota.setCapitalPagado(capitalPagado);
            cuota.setSaldoRestante(saldoRestante.max(BigDecimal.ZERO));
            
            // Guardar cada cuota inmediatamente
            try {
                cuota = cuotaRepository.save(cuota);
                cuotas.add(cuota);
            } catch (Exception e) {
                System.err.println("Error guardando cuota " + i + ": " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
        
        return cuotas;
    }
    
    /**
     * Consulta la tabla de amortización de un crédito
     */
    public Optional<CreditoResult> consultarTablaAmortizacion(Integer idCredito) {
        Optional<Credito> creditoOpt = creditoRepository.findById(idCredito);
        if (creditoOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Credito credito = creditoOpt.get();
        List<CuotaAmortizacion> cuotas = cuotaRepository.findByIdCredito(idCredito);
        
        return Optional.of(new CreditoResult(true, "Tabla de amortización", credito, cuotas));
    }
    
    /**
     * Calcula el promedio de valores de movimientos
     */
    private BigDecimal calcularPromedio(List<Movimiento> movimientos) {
        if (movimientos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal suma = movimientos.stream()
            .map(Movimiento::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return suma.divide(new BigDecimal(movimientos.size()), 2, RoundingMode.HALF_UP);
    }
    
    // Clases auxiliares para retornar resultados
    
    public static class ValidationResult {
        private boolean valido;
        private String mensaje;
        
        public ValidationResult(boolean valido, String mensaje) {
            this.valido = valido;
            this.mensaje = mensaje;
        }
        
        public boolean isValido() {
            return valido;
        }
        
        public String getMensaje() {
            return mensaje;
        }
    }
    
    public static class CreditoResult {
        private boolean aprobado;
        private String mensaje;
        private Credito credito;
        private List<CuotaAmortizacion> tablaAmortizacion;
        
        public CreditoResult(boolean aprobado, String mensaje, Credito credito, List<CuotaAmortizacion> tablaAmortizacion) {
            this.aprobado = aprobado;
            this.mensaje = mensaje;
            this.credito = credito;
            this.tablaAmortizacion = tablaAmortizacion;
        }
        
        public boolean isAprobado() {
            return aprobado;
        }
        
        public String getMensaje() {
            return mensaje;
        }
        
        public Credito getCredito() {
            return credito;
        }
        
        public List<CuotaAmortizacion> getTablaAmortizacion() {
            return tablaAmortizacion;
        }
    }
}
