package ec.edu.pinza.cliweb.controllers;

import ec.edu.pinza.cliweb.services.BanquitoRestClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/consulta-credito")
public class ConsultaCreditoController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/consulta-credito.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String cedula = request.getParameter("cedula");
        String accion = request.getParameter("accion");

        if (cedula == null || cedula.trim().isEmpty()) {
            request.getSession().setAttribute("error", "La cédula es obligatoria");
            response.sendRedirect(request.getContextPath() + "/consulta-credito");
            return;
        }

        BanquitoRestClient banquitoClient = new BanquitoRestClient();
        
        try {
            String resultado = "";
            
            if ("validar".equals(accion)) {
                Map<String, Object> responseData = banquitoClient.validarSujetoCredito(cedula);
                resultado = generarResultadoValidacion(responseData, cedula);
            } else if ("monto".equals(accion)) {
                Map<String, Object> responseData = banquitoClient.obtenerMontoMaximo(cedula);
                resultado = generarResultadoMontoMaximo(responseData, cedula);
            }
            
            request.setAttribute("resultado", resultado);
            request.getRequestDispatcher("/consulta-credito.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Error al consultar: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/consulta-credito");
        } finally {
            banquitoClient.close();
        }
    }

    private String generarResultadoValidacion(Map<String, Object> data, String cedula) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='result-title'>🔍 Validación de Sujeto de Crédito</div>");
        html.append("<div class='result-item'>");
        html.append("<span class='result-label'>Cédula:</span>");
        html.append("<span class='result-value'>").append(cedula).append("</span>");
        html.append("</div>");
        html.append("<div class='result-item'>");
        html.append("<span class='result-label'>Es sujeto de crédito:</span>");
        
        Object esSujetoObj = data.get("esSujetoCredito");
        boolean esSujeto = esSujetoObj != null && (esSujetoObj instanceof Boolean ? (Boolean)esSujetoObj : Boolean.parseBoolean(esSujetoObj.toString()));
        
        html.append("<span class='result-value ").append(esSujeto ? "success" : "error").append("'>");
        html.append(esSujeto ? "✅ SÍ" : "❌ NO");
        html.append("</span>");
        html.append("</div>");
        html.append("<div class='result-item'>");
        html.append("<span class='result-label'>Mensaje:</span>");
        html.append("<span class='result-value'>").append(data.get("motivo")).append("</span>");
        html.append("</div>");
        return html.toString();
    }

    private String generarResultadoMontoMaximo(Map<String, Object> data, String cedula) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='result-title'>💰 Monto Máximo de Crédito</div>");
        html.append("<div class='result-item'>");
        html.append("<span class='result-label'>Cédula:</span>");
        html.append("<span class='result-value'>").append(cedula).append("</span>");
        html.append("</div>");
        html.append("<div class='result-item'>");
        html.append("<span class='result-label'>Monto máximo autorizado:</span>");
        html.append("<span class='result-value success'>$").append(data.get("montoMaximo")).append("</span>");
        html.append("</div>");
        html.append("<div class='result-item'>");
        html.append("<span class='result-label'>Mensaje:</span>");
        html.append("<span class='result-value'>").append(data.get("mensaje")).append("</span>");
        html.append("</div>");
        return html.toString();
    }
}
