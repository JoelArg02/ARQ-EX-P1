package ec.edu.espe.arguello.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.annotation.PostConstruct;

import ec.edu.espe.arguello.model.CreditoBanco;
import ec.edu.espe.arguello.model.ClienteBanco;
import ec.edu.espe.arguello.model.AmortizacionCredito;
import ec.edu.espe.arguello.repository.CreditoBancoRepository;
import ec.edu.espe.arguello.repository.ClienteBancoRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Arrays;

/**
 * Managed Bean para el manejo de créditos
 */
@Named
@ViewScoped
public class CreditoBean implements Serializable {
    
    @Inject
    private CreditoBancoRepository creditoRepository;
    
    @Inject
    private ClienteBancoRepository clienteRepository;
    
    private List<CreditoBanco> creditos;
    private List<ClienteBanco> clientes;
    private CreditoBanco selectedCredito;
    private boolean isEdit = false;
    
    // Para nuevo crédito
    private String cedulaCliente;
    private double montoSolicitado;
    private int numeroCuotas = 12;
    private boolean nuevoDialogVisible = false;
    
    // Para tabla de amortización
    private List<AmortizacionCredito> amortizaciones;
    private boolean amortizacionDialogVisible = false;
    private CreditoBanco creditoAmortizacion;
    
    @PostConstruct
    public void init() {
        loadCreditos();
        loadClientes();
    }
    
    public void loadCreditos() {
        try {
            creditos = creditoRepository.getAllCreditos();
            addMessage(FacesMessage.SEVERITY_INFO, "Créditos cargados exitosamente", 
                      "Se encontraron " + creditos.size() + " créditos");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar créditos", e.getMessage());
        }
    }
    
    public void loadClientes() {
        try {
            clientes = clienteRepository.getAllClientes();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar clientes", e.getMessage());
        }
    }
    
    public void newCredito() {
        cedulaCliente = "";
        montoSolicitado = 0;
        numeroCuotas = 12;
        nuevoDialogVisible = true;
    }
    
    public void saveCredito() {
        try {
            CreditoBanco credito = creditoRepository.createCredito(cedulaCliente, montoSolicitado, numeroCuotas);
            if (credito != null && credito.getId() > 0) {
                addMessage(FacesMessage.SEVERITY_INFO, "Crédito aprobado", 
                          "El crédito ha sido aprobado exitosamente");
                nuevoDialogVisible = false;
                loadCreditos();
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo aprobar el crédito");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al aprobar crédito", e.getMessage());
        }
    }
    
    public void deleteCredito(CreditoBanco credito) {
        try {
            if (creditoRepository.deleteCredito(credito.getId())) {
                addMessage(FacesMessage.SEVERITY_INFO, "Crédito eliminado", "Crédito eliminado exitosamente");
                loadCreditos();
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el crédito");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar crédito", e.getMessage());
        }
    }
    
    public void verAmortizacion(CreditoBanco credito) {
        try {
            creditoAmortizacion = credito;
            amortizaciones = creditoRepository.getAmortizacionesByCredito(credito.getId());
            amortizacionDialogVisible = true;
            
            if (amortizaciones != null && !amortizaciones.isEmpty()) {
                addMessage(FacesMessage.SEVERITY_INFO, "Tabla de amortización cargada", 
                          "Se encontraron " + amortizaciones.size() + " cuotas");
            } else {
                addMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No se encontraron datos de amortización");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar amortización", e.getMessage());
        }
    }
    
    public void cancelNuevoDialog() {
        nuevoDialogVisible = false;
        cedulaCliente = "";
        montoSolicitado = 0;
        numeroCuotas = 12;
    }
    
    public void cancelAmortizacionDialog() {
        amortizacionDialogVisible = false;
        amortizaciones = null;
        creditoAmortizacion = null;
    }
    
    public String getClienteNombre(int clienteId) {
        if (clientes != null) {
            for (ClienteBanco cliente : clientes) {
                if (cliente.getId() == clienteId) {
                    return cliente.getNombreCompleto();
                }
            }
        }
        return "Cliente no encontrado";
    }
    
    public String getClienteCedula(int clienteId) {
        if (clientes != null) {
            for (ClienteBanco cliente : clientes) {
                if (cliente.getId() == clienteId) {
                    return cliente.getCedula();
                }
            }
        }
        return "";
    }
    
    public List<Integer> getNumeroCuotasOpciones() {
        return Arrays.asList(6, 12, 18, 24, 36, 48, 60);
    }
    
    public double getTotalIntereses() {
        if (amortizaciones != null && !amortizaciones.isEmpty()) {
            return amortizaciones.stream()
                .mapToDouble(AmortizacionCredito::getInteres)
                .sum();
        }
        return 0.0;
    }
    
    public double getTotalCapital() {
        if (amortizaciones != null && !amortizaciones.isEmpty()) {
            return amortizaciones.stream()
                .mapToDouble(AmortizacionCredito::getCapital)
                .sum();
        }
        return 0.0;
    }
    
    public double getTotalPagos() {
        if (amortizaciones != null && !amortizaciones.isEmpty()) {
            return amortizaciones.stream()
                .mapToDouble(AmortizacionCredito::getMontoCuota)
                .sum();
        }
        return 0.0;
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }
    
    // Getters and Setters
    public List<CreditoBanco> getCreditos() {
        return creditos;
    }
    
    public void setCreditos(List<CreditoBanco> creditos) {
        this.creditos = creditos;
    }
    
    public List<ClienteBanco> getClientes() {
        return clientes;
    }
    
    public void setClientes(List<ClienteBanco> clientes) {
        this.clientes = clientes;
    }
    
    public CreditoBanco getSelectedCredito() {
        return selectedCredito;
    }
    
    public void setSelectedCredito(CreditoBanco selectedCredito) {
        this.selectedCredito = selectedCredito;
    }
    
    public boolean isEdit() {
        return isEdit;
    }
    
    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }
    
    public String getCedulaCliente() {
        return cedulaCliente;
    }
    
    public void setCedulaCliente(String cedulaCliente) {
        this.cedulaCliente = cedulaCliente;
    }
    
    public double getMontoSolicitado() {
        return montoSolicitado;
    }
    
    public void setMontoSolicitado(double montoSolicitado) {
        this.montoSolicitado = montoSolicitado;
    }
    
    public int getNumeroCuotas() {
        return numeroCuotas;
    }
    
    public void setNumeroCuotas(int numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }
    
    public boolean isNuevoDialogVisible() {
        return nuevoDialogVisible;
    }
    
    public void setNuevoDialogVisible(boolean nuevoDialogVisible) {
        this.nuevoDialogVisible = nuevoDialogVisible;
    }
    
    public List<AmortizacionCredito> getAmortizaciones() {
        return amortizaciones;
    }
    
    public void setAmortizaciones(List<AmortizacionCredito> amortizaciones) {
        this.amortizaciones = amortizaciones;
    }
    
    public boolean isAmortizacionDialogVisible() {
        return amortizacionDialogVisible;
    }
    
    public void setAmortizacionDialogVisible(boolean amortizacionDialogVisible) {
        this.amortizacionDialogVisible = amortizacionDialogVisible;
    }
    
    public CreditoBanco getCreditoAmortizacion() {
        return creditoAmortizacion;
    }
    
    public void setCreditoAmortizacion(CreditoBanco creditoAmortizacion) {
        this.creditoAmortizacion = creditoAmortizacion;
    }
}