package pe.edu.upeu.autoandes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.ReporteIngresoServicioDTO;
import pe.edu.upeu.autoandes.service.OrdenServicioService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {
    private final OrdenServicioService service;
    @GetMapping("/ingresos-por-servicio") @PreAuthorize("hasAuthority('REPORTE_VER')")
    public List<ReporteIngresoServicioDTO> ingresos() { return service.reporte(); }
}
