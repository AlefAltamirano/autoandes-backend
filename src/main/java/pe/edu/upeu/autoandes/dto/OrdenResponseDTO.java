package pe.edu.upeu.autoandes.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrdenResponseDTO(Long id, String numero, LocalDateTime fecha, String placa,
                               Double kilometraje, String estado, Double total,
                               String usuarioRegistro, String motivoAnulacion,
                               List<DetalleOrdenResponseDTO> detalles) {}
