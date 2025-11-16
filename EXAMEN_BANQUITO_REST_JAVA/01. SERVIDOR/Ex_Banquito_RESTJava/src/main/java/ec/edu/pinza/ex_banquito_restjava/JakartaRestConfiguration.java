package ec.edu.pinza.ex_banquito_restjava;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.HashSet;
import java.util.Set;

/**
 * Configura los servicios REST de Jakarta EE para BanQuito
 */
@ApplicationPath("")
public class JakartaRestConfiguration extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // Agregar controladores
        classes.add(ec.edu.pinza.ex_banquito_restjava.controllers.CreditoController.class);
        return classes;
    }
    
    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<>();
        
        // Configurar Jackson para serialización JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        
        JacksonJsonProvider provider = new JacksonJsonProvider();
        provider.setMapper(mapper);
        singletons.add(provider);
        
        return singletons;
    }
}
