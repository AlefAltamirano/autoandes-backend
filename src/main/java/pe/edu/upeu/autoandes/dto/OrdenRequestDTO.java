package pe.edu.upeu.autoandes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record OrdenRequestDTO(
        @NotNull Long vehiculoId,
        @NotNull @PositiveOrZero @Max(9999999) @Digits(integer = 7, fraction = 0) Double kilometraje,
        @NotEmpty @Size(max = 10) List<@NotNull @Valid DetalleOrdenRequestDTO> detalles) {}
