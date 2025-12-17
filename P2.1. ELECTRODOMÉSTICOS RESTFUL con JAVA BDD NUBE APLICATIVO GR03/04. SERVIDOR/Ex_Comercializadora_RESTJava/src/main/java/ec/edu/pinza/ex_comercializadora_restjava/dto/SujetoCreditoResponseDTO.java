package ec.edu.pinza.ex_comercializadora_restjava.dto;

public class SujetoCreditoResponseDTO {
    private String cedula;
    private boolean esSujetoCredito;
    private String motivo;
    
    public SujetoCreditoResponseDTO() {
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
