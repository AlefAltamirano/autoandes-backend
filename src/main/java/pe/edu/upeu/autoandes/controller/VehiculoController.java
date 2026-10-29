package pe.edu.upeu.autoandes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.entity.Vehiculo;
import pe.edu.upeu.autoandes.mapper.VehiculoMapper;
import pe.edu.upeu.autoandes.service.VehiculoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehiculos")
@RequiredArgsConstructor
@Tag(name = "Vehículos", description = "CRUD de vehículos")
public class VehiculoController {
    private final VehiculoService service;
    private final VehiculoMapper mapper;

    @GetMapping @PreAuthorize("hasAuthority('VEHICULO_LEER')")
    public List<VehiculoResponseDTO> listar(@RequestParam(required = false) Long clienteId) {
        return (clienteId == null ? service.listar() : service.listarPorCliente(clienteId))
                .stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('VEHICULO_LEER')")
    public VehiculoResponseDTO obtener(@PathVariable Long id) {
        return mapper.toResponse(service.obtener(id));
    }

    @PostMapping @PreAuthorize("hasAuthority('VEHICULO_ESCRIBIR')")
    public ResponseEntity<VehiculoResponseDTO> crear(@Valid @RequestBody VehiculoRequestDTO request) {
        Vehiculo v = Vehiculo.builder().placa(request.placa()).marca(request.marca()).modelo(request.modelo())
                .anio(request.anio()).cliente(pe.edu.upeu.autoandes.entity.Cliente.builder().id(request.clienteId()).build()).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(service.guardar(v)));
    }

    @PutMapping("/{id}") @PreAuthorize("hasAuthority('VEHICULO_ESCRIBIR')")
    public VehiculoResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody VehiculoRequestDTO request) {
        Vehiculo v = Vehiculo.builder().placa(request.placa()).marca(request.marca()).modelo(request.modelo())
                .anio(request.anio()).cliente(pe.edu.upeu.autoandes.entity.Cliente.builder().id(request.clienteId()).build()).build();
        return mapper.toResponse(service.actualizar(id, v));
    }

    @DeleteMapping("/{id}") @PreAuthorize("hasAuthority('VEHICULO_ESCRIBIR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminar(id); return ResponseEntity.noContent().build(); }
}
