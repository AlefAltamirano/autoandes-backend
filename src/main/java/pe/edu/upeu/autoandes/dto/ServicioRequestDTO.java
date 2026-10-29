package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.*;

public record ServicioRequestDTO(
        @NotBlank @Pattern(regexp = "^SRV-\\d{3}$", message = "El código debe tener el formato SRV-000") String codigo,
        @NotBlank @Size(min = 3, max = 80) String nombre,
        @NotNull @Positive Double precio,
        @NotNull @Min(15) @Max(480) Integer duracionMin) {}
