package pe.edu.upeu.autoandes.service;

import pe.edu.upeu.autoandes.entity.Vehiculo;
import java.util.List;

public interface VehiculoService {
    List<Vehiculo> listar();
    Vehiculo guardar(Vehiculo vehiculo);
    void eliminar(Long id);
}
