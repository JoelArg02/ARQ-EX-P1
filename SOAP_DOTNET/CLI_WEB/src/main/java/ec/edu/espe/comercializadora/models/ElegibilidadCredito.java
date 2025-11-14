package ec.edu.espe.comercializadora.models;

public class ElegibilidadCredito {
    private boolean esElegible;
    private String mensaje;
    private double montoMaximoCredito;

    public ElegibilidadCredito() {}

    public ElegibilidadCredito(boolean esElegible, String mensaje, double montoMaximoCredito) {
        this.esElegible = esElegible;
        this.mensaje = mensaje;
        this.montoMaximoCredito = montoMaximoCredito;
    }

    public boolean isEsElegible() {
        return esElegible;
    }

    public void setEsElegible(boolean esElegible) {
        this.esElegible = esElegible;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public double getMontoMaximoCredito() {
        return montoMaximoCredito;
    }

    public void setMontoMaximoCredito(double montoMaximoCredito) {
        this.montoMaximoCredito = montoMaximoCredito;
    }
}