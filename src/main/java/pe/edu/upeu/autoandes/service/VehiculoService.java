package pe.edu.upeu.autoandes.service;

import pe.edu.upeu.autoandes.entity.Vehiculo;
import java.util.List;

public interface VehiculoService {
    List<Vehiculo> listar();
    List<Vehiculo> listarPorCliente(Long clienteId);
    Vehiculo obtener(Long id);
    Vehiculo guardar(Vehiculo vehiculo);
    Vehiculo actualizar(Long id, Vehiculo vehiculo);
    void eliminar(Long id);
}
