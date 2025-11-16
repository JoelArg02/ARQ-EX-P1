package ec.edu.pinza.ex_banquito_restjava.dto;

/**
 * DTO para respuesta de validación de sujeto de crédito
 */
public class SujetoCreditoResponse {
    private String cedula;
    private boolean esSujetoCredito;
    private String motivo;
    
    public SujetoCreditoResponse() {
    }
    
    public SujetoCreditoResponse(String cedula, boolean esSujetoCredito, String motivo) {
        this.cedula = cedula;
        this.esSujetoCredito = esSujetoCredito;
        this.motivo = motivo;
    }
    
    public String getCedula() {
        return cedula;
    }
    
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
    public boolean isEsSujetoCredito() {
        return esSujetoCredito;
    }
    
    public void setEsSujetoCredito(boolean esSujetoCredito) {
        this.esSujetoCredito = esSujetoCredito;
    }
    
    public String getMotivo() {
        return motivo;
    }
    
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
