package pe.edu.upeu.autoandes.service;

import pe.edu.upeu.autoandes.dto.*;
import java.time.LocalDate;
import java.util.List;

public interface OrdenServicioService {
    OrdenResponseDTO registrar(OrdenRequestDTO request, String usuario);
    List<OrdenResponseDTO> listar(String placa, String estado, LocalDate desde, LocalDate hasta);
    OrdenResponseDTO anular(Long id, String motivo);
    List<ReporteIngresoServicioDTO> reporte();
}
