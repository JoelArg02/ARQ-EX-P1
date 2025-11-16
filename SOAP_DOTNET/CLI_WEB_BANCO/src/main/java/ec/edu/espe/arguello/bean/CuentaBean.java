package ec.edu.espe.arguello.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.annotation.PostConstruct;

import ec.edu.espe.arguello.model.Cuenta;
import ec.edu.espe.arguello.model.ClienteBanco;
import ec.edu.espe.arguello.model.Movimiento;
import ec.edu.espe.arguello.repository.CuentaRepository;
import ec.edu.espe.arguello.repository.ClienteBancoRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Arrays;

/**
 * Managed Bean para el manejo de cuentas
 */
@Named
@ViewScoped
public class CuentaBean implements Serializable {
    
    @Inject
    private CuentaRepository cuentaRepository;
    
    @Inject
    private ClienteBancoRepository clienteRepository;
    
    private List<Cuenta> cuentas;
    private List<ClienteBanco> clientes;
    private Cuenta selectedCuenta;
    private Cuenta cuentaForm;
    private boolean dialogVisible = false;
    private boolean isEdit = false;
    
    // Para operaciones bancarias
    private Movimiento movimientoForm;
    private boolean operacionDialogVisible = false;
    private Cuenta cuentaOperacion;
    
    @PostConstruct
    public void init() {
        cuentaForm = new Cuenta();
        movimientoForm = new Movimiento();
        loadCuentas();
        loadClientes();
    }
    
    public void loadCuentas() {
        try {
            cuentas = cuentaRepository.getAllCuentas();
            addMessage(FacesMessage.SEVERITY_INFO, "Cuentas cargadas exitosamente", 
                      "Se encontraron " + cuentas.size() + " cuentas");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar cuentas", e.getMessage());
        }
    }
    
    public void loadClientes() {
        try {
            clientes = clienteRepository.getAllClientes();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar clientes", e.getMessage());
        }
    }
    
    public void newCuenta() {
        cuentaForm = new Cuenta();
        isEdit = false;
        dialogVisible = true;
    }
    
    public void editCuenta(Cuenta cuenta) {
        cuentaForm = new Cuenta();
        cuentaForm.setId(cuenta.getId());
        cuentaForm.setClienteBancoId(cuenta.getClienteBancoId());
        cuentaForm.setNumeroCuenta(cuenta.getNumeroCuenta());
        cuentaForm.setSaldo(cuenta.getSaldo());
        cuentaForm.setTipoCuenta(cuenta.getTipoCuenta());
        
        isEdit = true;
        dialogVisible = true;
    }
    
    public void saveCuenta() {
        try {
            if (isEdit) {
                cuentaRepository.updateCuenta(cuentaForm);
                addMessage(FacesMessage.SEVERITY_INFO, "Cuenta actualizada", "Cuenta actualizada exitosamente");
            } else {
                cuentaRepository.createCuenta(cuentaForm);
                addMessage(FacesMessage.SEVERITY_INFO, "Cuenta creada", "Cuenta creada exitosamente");
            }
            
            org.primefaces.PrimeFaces.current().ajax().addCallbackParam("saved", true);
            dialogVisible = false;
            loadCuentas();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar cuenta", e.getMessage());
            org.primefaces.PrimeFaces.current().ajax().addCallbackParam("saved", false);
        }
    }
    
    public void deleteCuenta(Cuenta cuenta) {
        try {
            if (cuentaRepository.deleteCuenta(cuenta.getId())) {
                addMessage(FacesMessage.SEVERITY_INFO, "Cuenta eliminada", "Cuenta eliminada exitosamente");
                loadCuentas();
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar la cuenta");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar cuenta", e.getMessage());
        }
    }
    
    public void openOperacionDialog(Cuenta cuenta) {
        cuentaOperacion = cuenta;
        movimientoForm = new Movimiento();
        movimientoForm.setCuentaId(cuenta.getId());
        operacionDialogVisible = true;
    }
    
    public void realizarOperacion() {
        try {
            cuentaRepository.createMovimiento(movimientoForm);
            addMessage(FacesMessage.SEVERITY_INFO, "Operación exitosa", 
                      "La operación " + movimientoForm.getTipoMovimiento() + " se realizó correctamente");
            
            org.primefaces.PrimeFaces.current().ajax().addCallbackParam("saved", true);
            
            operacionDialogVisible = false;
            loadCuentas(); // Recargar para mostrar saldo actualizado
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error en la operación", e.getMessage());
            org.primefaces.PrimeFaces.current().ajax().addCallbackParam("saved", false);
        }
    }
    
    public void cancelDialog() {
        dialogVisible = false;
        cuentaForm = new Cuenta();
    }
    
    public void cancelOperacionDialog() {
        operacionDialogVisible = false;
        movimientoForm = new Movimiento();
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
    
    public List<String> getTiposCuenta() {
        return Arrays.asList("Ahorros", "Corriente");
    }
    
    public List<String> getTiposMovimiento() {
        return Arrays.asList("Deposito", "Retiro");
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }
    
    // Getters and Setters
    public List<Cuenta> getCuentas() {
        return cuentas;
    }
    
    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }
    
    public List<ClienteBanco> getClientes() {
        return clientes;
    }
    
    public void setClientes(List<ClienteBanco> clientes) {
        this.clientes = clientes;
    }
    
    public Cuenta getSelectedCuenta() {
        return selectedCuenta;
    }
    
    public void setSelectedCuenta(Cuenta selectedCuenta) {
        this.selectedCuenta = selectedCuenta;
    }
    
    public Cuenta getCuentaForm() {
        return cuentaForm;
    }
    
    public void setCuentaForm(Cuenta cuentaForm) {
        this.cuentaForm = cuentaForm;
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
    
    public Movimiento getMovimientoForm() {
        return movimientoForm;
    }
    
    public void setMovimientoForm(Movimiento movimientoForm) {
        this.movimientoForm = movimientoForm;
    }
    
    public boolean isOperacionDialogVisible() {
        return operacionDialogVisible;
    }
    
    public void setOperacionDialogVisible(boolean operacionDialogVisible) {
        this.operacionDialogVisible = operacionDialogVisible;
    }
    
    public Cuenta getCuentaOperacion() {
        return cuentaOperacion;
    }
    
    public void setCuentaOperacion(Cuenta cuentaOperacion) {
        this.cuentaOperacion = cuentaOperacion;
    }
}