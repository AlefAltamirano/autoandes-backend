package pe.edu.upeu.autoandes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.entity.Servicio;
import pe.edu.upeu.autoandes.mapper.ServicioMapper;
import pe.edu.upeu.autoandes.service.ServicioService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/servicios")
@RequiredArgsConstructor
public class ServicioController {
    private final ServicioService service;
    private final ServicioMapper mapper;

    @GetMapping @PreAuthorize("hasAuthority('SERVICIO_LEER')")
    public List<ServicioResponseDTO> listar() { return service.listar().stream().map(mapper::toResponse).toList(); }

    @PostMapping @PreAuthorize("hasAuthority('SERVICIO_ESCRIBIR')")
    public ResponseEntity<ServicioResponseDTO> crear(@Valid @RequestBody ServicioRequestDTO r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(service.guardar(Servicio.builder()
                .codigo(r.codigo()).nombre(r.nombre()).precio(r.precio()).duracionMin(r.duracionMin()).activo(true).build())));
    }

    @DeleteMapping("/{id}") @PreAuthorize("hasAuthority('SERVICIO_ESCRIBIR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminar(id); return ResponseEntity.noContent().build(); }

    @PatchMapping("/{id}/estado") @PreAuthorize("hasAuthority('SERVICIO_ESCRIBIR')")
    public ServicioResponseDTO estado(@PathVariable Long id, @Valid @RequestBody EstadoServicioRequestDTO r) {
        service.cambiarEstado(id, r.activo());
        return mapper.toResponse(service.listar().stream().filter(s -> s.getId().equals(id)).findFirst().orElseThrow());
    }
}
