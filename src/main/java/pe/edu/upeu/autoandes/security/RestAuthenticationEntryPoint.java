package pe.edu.upeu.autoandes.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pe.edu.upeu.autoandes.dto.ErrorResponseDTO;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    @Override public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex) throws IOException {
        res.setCharacterEncoding("UTF-8"); res.setStatus(401); res.setContentType("application/json");
        mapper.writeValue(res.getOutputStream(), ErrorResponseDTO.builder().timestamp(LocalDateTime.now()).status(401)
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase()).mensaje("Autenticación requerida o inválida")
                .ruta(req.getRequestURI()).build());
    }
}

