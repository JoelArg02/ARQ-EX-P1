package ec.edu.espe.arguello.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.annotation.PostConstruct;

import ec.edu.espe.arguello.model.ClienteBanco;
import ec.edu.espe.arguello.model.ElegibilidadCliente;
import ec.edu.espe.arguello.model.MontoMaximoCredito;
import ec.edu.espe.arguello.repository.ClienteBancoRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Arrays;

/**
 * Managed Bean para el manejo de clientes
 */
@Named
@ViewScoped
public class ClienteBean implements Serializable {
    
    @Inject
    private ClienteBancoRepository clienteRepository;
    
    private List<ClienteBanco> clientes;
    private ClienteBanco selectedCliente;
    private ClienteBanco clienteForm;
    private boolean dialogVisible = false;
    private boolean isEdit = false;
    
    // Variables para consultas
    private String cedulaConsulta;
    private ElegibilidadCliente elegibilidadResult;
    private MontoMaximoCredito montoMaximoResult;
    private boolean showElegibilidad = false;
    private boolean showMontoMaximo = false;
    
    @PostConstruct
    public void init() {
        clienteForm = new ClienteBanco();
        loadClientes();
    }
    
    public void loadClientes() {
        try {
            clientes = clienteRepository.getAllClientes();
            addMessage(FacesMessage.SEVERITY_INFO, "Clientes cargados exitosamente", 
                      "Se encontraron " + clientes.size() + " clientes");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar clientes", e.getMessage());
        }
    }
    
    public void newCliente() {
        clienteForm = new ClienteBanco();
        isEdit = false;
        dialogVisible = true;
    }
    
    public void editCliente(ClienteBanco cliente) {
        clienteForm = new ClienteBanco();
        clienteForm.setId(cliente.getId());
        clienteForm.setCedula(cliente.getCedula());
        clienteForm.setNombreCompleto(cliente.getNombreCompleto());
        clienteForm.setEstadoCivil(cliente.getEstadoCivil());
        clienteForm.setFechaNacimiento(cliente.getFechaNacimiento());
        clienteForm.setTieneCreditoActivo(cliente.isTieneCreditoActivo());
        
        isEdit = true;
        dialogVisible = true;
    }
    
    public void saveCliente() {
        try {
            if (isEdit) {
                clienteRepository.updateCliente(clienteForm);
                addMessage(FacesMessage.SEVERITY_INFO, "Cliente actualizado", "Cliente actualizado exitosamente");
            } else {
                clienteRepository.createCliente(clienteForm);
                addMessage(FacesMessage.SEVERITY_INFO, "Cliente creado", "Cliente creado exitosamente");
            }
            
            dialogVisible = false;
            loadClientes();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar cliente", e.getMessage());
        }
    }
    
    public void deleteCliente(ClienteBanco cliente) {
        try {
            if (clienteRepository.deleteCliente(cliente.getId())) {
                addMessage(FacesMessage.SEVERITY_INFO, "Cliente eliminado", "Cliente eliminado exitosamente");
                loadClientes();
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el cliente");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar cliente", e.getMessage());
        }
    }
    
    public void verificarElegibilidad() {
        try {
            if (cedulaConsulta == null || cedulaConsulta.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Ingrese una cédula para consultar");
                return;
            }
            
            elegibilidadResult = clienteRepository.verificarElegibilidadCliente(cedulaConsulta);
            showElegibilidad = true;
            showMontoMaximo = false;
            
            if (elegibilidadResult != null) {
                addMessage(FacesMessage.SEVERITY_INFO, "Consulta realizada", "Elegibilidad verificada");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al verificar elegibilidad", e.getMessage());
        }
    }
    
    public void calcularMontoMaximo() {
        try {
            if (cedulaConsulta == null || cedulaConsulta.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Ingrese una cédula para consultar");
                return;
            }
            
            montoMaximoResult = clienteRepository.calcularMontoMaximoCredito(cedulaConsulta);
            showMontoMaximo = true;
            showElegibilidad = false;
            
            if (montoMaximoResult != null) {
                addMessage(FacesMessage.SEVERITY_INFO, "Consulta realizada", "Monto máximo calculado");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al calcular monto máximo", e.getMessage());
        }
    }
    
    public void cancelDialog() {
        dialogVisible = false;
        clienteForm = new ClienteBanco();
    }
    
    public List<String> getEstadosCiviles() {
        return Arrays.asList("Soltero", "Casado", "Divorciado", "Viudo");
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }
    
    // Getters and Setters
    public List<ClienteBanco> getClientes() {
        return clientes;
    }
    
    public void setClientes(List<ClienteBanco> clientes) {
        this.clientes = clientes;
    }
    
    public ClienteBanco getSelectedCliente() {
        return selectedCliente;
    }
    
    public void setSelectedCliente(ClienteBanco selectedCliente) {
        this.selectedCliente = selectedCliente;
    }
    
    public ClienteBanco getClienteForm() {
        return clienteForm;
    }
    
    public void setClienteForm(ClienteBanco clienteForm) {
        this.clienteForm = clienteForm;
    }
    
    public boolean isDialogVisible() {
        return dialogVisible;
    }
    
    public void setDialogVisible(boolean dialogVisible) {
        this.dialogVisible = dialogVisible;
    }
    
    public boolean isEdit() {
        return isEdit;
    }
    
    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }
    
    public String getCedulaConsulta() {
        return cedulaConsulta;
    }
    
    public void setCedulaConsulta(String cedulaConsulta) {
        this.cedulaConsulta = cedulaConsulta;
    }
    
    public ElegibilidadCliente getElegibilidadResult() {
        return elegibilidadResult;
    }
    
    public void setElegibilidadResult(ElegibilidadCliente elegibilidadResult) {
        this.elegibilidadResult = elegibilidadResult;
    }
    
    public MontoMaximoCredito getMontoMaximoResult() {
        return montoMaximoResult;
    }
    
    public void setMontoMaximoResult(MontoMaximoCredito montoMaximoResult) {
        this.montoMaximoResult = montoMaximoResult;
    }
    
    public boolean isShowElegibilidad() {
        return showElegibilidad;
    }
    
    public void setShowElegibilidad(boolean showElegibilidad) {
        this.showElegibilidad = showElegibilidad;
    }
    
    public boolean isShowMontoMaximo() {
        return showMontoMaximo;
    }
    
    public void setShowMontoMaximo(boolean showMontoMaximo) {
        this.showMontoMaximo = showMontoMaximo;
    }
}