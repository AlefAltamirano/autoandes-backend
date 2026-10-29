package pe.edu.upeu.autoandes.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import pe.edu.upeu.autoandes.exception.ErrorResponseDTO;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Override public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex) throws IOException {
        res.setStatus(403); res.setContentType("application/json");
        mapper.writeValue(res.getOutputStream(), ErrorResponseDTO.builder().timestamp(LocalDateTime.now()).status(403)
                .error("Forbidden").mensaje("No tiene permisos para realizar esta operación").ruta(req.getRequestURI()).build());
    }
}
