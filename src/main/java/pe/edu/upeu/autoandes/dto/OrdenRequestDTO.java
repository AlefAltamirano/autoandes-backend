package pe.edu.upeu.autoandes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record OrdenRequestDTO(
        @NotNull Long vehiculoId,
        @NotNull @PositiveOrZero Double kilometraje,
        @NotEmpty @Size(max = 10) List<@Valid DetalleOrdenRequestDTO> detalles) {}
