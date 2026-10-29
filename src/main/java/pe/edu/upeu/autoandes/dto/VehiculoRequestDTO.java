package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;

public record VehiculoRequestDTO(
        @NotBlank @Pattern(regexp = "^[A-Z]{3}-\\d{3}$", message = "La placa debe tener el formato ABC-123") String placa,
        @NotBlank String marca,
        @NotBlank String modelo,
        @NotNull @Min(1990) @Max(2027) Integer anio,
        @NotNull Long clienteId) {}
