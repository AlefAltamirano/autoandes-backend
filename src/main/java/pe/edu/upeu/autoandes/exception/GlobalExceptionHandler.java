package pe.edu.upeu.autoandes.exception;

import pe.edu.upeu.autoandes.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorResponseDTO> response(HttpStatus status, String mensaje,
            HttpServletRequest request, Map<String,String> campos) {
        return ResponseEntity.status(status).body(ErrorResponseDTO.builder().timestamp(LocalDateTime.now())
            .status(status.value()).error(status.getReasonPhrase()).mensaje(mensaje)
            .ruta(request.getRequestURI()).campos(campos).build());
    }
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> missing(RecursoNoEncontradoException e, HttpServletRequest r) {
        return response(HttpStatus.NOT_FOUND,e.getMessage(),r,null);
    }
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponseDTO> conflict(ReglaNegocioException e, HttpServletRequest r) {
        log.warn("Regla rechazada en {}: {}", r.getRequestURI(), e.getMessage());
        return response(HttpStatus.CONFLICT,e.getMessage(),r,null);
    }
    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<ErrorResponseDTO> invalid(SolicitudInvalidaException e, HttpServletRequest r) {
        log.warn("Solicitud rechazada: {}", e.getMessage());
        return response(HttpStatus.BAD_REQUEST,e.getMessage(),r,null);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> validation(MethodArgumentNotValidException e, HttpServletRequest r) {
        Map<String,String> fields = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> fields.put(f.getField(),f.getDefaultMessage()));
        return response(HttpStatus.BAD_REQUEST,"Datos inválidos",r,fields);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class,
        org.springframework.web.bind.MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponseDTO> malformed(Exception e, HttpServletRequest r) {
        return response(HttpStatus.BAD_REQUEST,"Cuerpo o parámetros inválidos",r,null);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> authentication(AuthenticationException e, HttpServletRequest r) {
        return response(HttpStatus.UNAUTHORIZED,"Credenciales inválidas o cuenta inactiva",r,null);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> forbidden(AccessDeniedException e, HttpServletRequest r) {
        return response(HttpStatus.FORBIDDEN,"No tiene permisos para realizar esta operación",r,null);
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> noRoute(NoResourceFoundException e, HttpServletRequest r) {
        return response(HttpStatus.NOT_FOUND,"Ruta no encontrada",r,null);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> integrity(DataIntegrityViolationException e, HttpServletRequest r) {
        log.warn("Conflicto de integridad en {}",r.getRequestURI());
        return response(HttpStatus.CONFLICT,"El registro está duplicado o tiene referencias asociadas",r,null);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> unexpected(Exception e, HttpServletRequest r) {
        log.error("Error inesperado en {}",r.getRequestURI(),e);
        return response(HttpStatus.INTERNAL_SERVER_ERROR,"Error interno del servidor",r,null);
    }
}
