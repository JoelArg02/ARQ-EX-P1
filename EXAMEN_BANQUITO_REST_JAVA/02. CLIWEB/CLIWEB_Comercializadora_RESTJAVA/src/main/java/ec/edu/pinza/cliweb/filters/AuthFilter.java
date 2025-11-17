package ec.edu.pinza.cliweb.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro de autenticación - verifica sesión en todas las páginas excepto login
 */
@WebFilter("/*")
public class AuthFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String path = httpRequest.getRequestURI();
        boolean isLoginPage = path.endsWith("login.jsp") || path.endsWith("login");
        boolean isLoginAction = path.contains("/login");
        boolean isPublicResource = path.contains("/assets/") || path.contains(".css") || path.contains(".js") || path.contains(".png") || path.contains(".jpg");
        
        boolean isLoggedIn = (session != null && session.getAttribute("usuario") != null);
        
        if (isLoggedIn || isLoginPage || isLoginAction || isPublicResource) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
        }
    }
}
