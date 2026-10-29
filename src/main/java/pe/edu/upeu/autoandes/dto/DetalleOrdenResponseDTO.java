package pe.edu.upeu.autoandes.dto;

public record DetalleOrdenResponseDTO(Long servicioId, String codigo, String servicio,
                                      Integer cantidad, Double precioUnitario, Double subtotal) {}
