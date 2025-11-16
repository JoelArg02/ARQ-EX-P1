package ec.edu.pinza.ex_comercializadora_restjava;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import java.util.HashSet;
import java.util.Set;

/**
 * Configures Jakarta RESTful Web Services for the application.
 */
@ApplicationPath("api")
public class JakartaRestConfiguration extends Application {
    
    @Provider
    public static class JacksonConfig implements ContextResolver<ObjectMapper> {
        private final ObjectMapper mapper;
        
        public JacksonConfig() {
            mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }
        
        @Override
        public ObjectMapper getContext(Class<?> type) {
            return mapper;
        }
    }
}
