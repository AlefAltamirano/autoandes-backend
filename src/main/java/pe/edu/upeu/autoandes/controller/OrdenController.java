package pe.edu.upeu.autoandes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.service.OrdenServicioService;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes")
@RequiredArgsConstructor
public class OrdenController {
    private final OrdenServicioService service;

    @PostMapping @PreAuthorize("hasAuthority('ORDEN_REGISTRAR')")
    public OrdenResponseDTO crear(@Valid @RequestBody OrdenRequestDTO r, Authentication auth) {
        return service.registrar(r, auth.getName());
    }

    @GetMapping @PreAuthorize("hasAuthority('ORDEN_LEER')")
    public List<OrdenResponseDTO> listar(@RequestParam(required = false) String placa,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return service.listar(placa, estado, desde, hasta);
    }

    @PatchMapping("/{id}/anular") @PreAuthorize("hasAuthority('ORDEN_ANULAR')")
    public OrdenResponseDTO anular(@PathVariable Long id, @Valid @RequestBody AnularOrdenRequestDTO r) {
        return service.anular(id, r.motivo());
    }
}
