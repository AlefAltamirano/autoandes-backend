package pe.edu.upeu.autoandes.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.entity.Vehiculo;

@Component
public class VehiculoMapper {
    public VehiculoResponseDTO toResponse(Vehiculo v) {
        return new VehiculoResponseDTO(v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(),
                v.getAnio(), v.getCliente().getId(), v.getCliente().getNombres());
    }
}
