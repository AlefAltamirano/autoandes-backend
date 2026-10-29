package pe.edu.upeu.autoandes.dto;

public record ServicioResponseDTO(Long id, String codigo, String nombre, Double precio,
                                  Integer duracionMin, Boolean activo) {}
