package pe.edu.upeu.autoandes.service;

import pe.edu.upeu.autoandes.entity.Servicio;
import java.util.List;

public interface ServicioService {
    List<Servicio> listar();
    Servicio obtener(Long id);
    Servicio guardar(Servicio servicio);
    Servicio actualizar(Long id, Servicio servicio);
    void eliminar(Long id);
    void cambiarEstado(Long id, Boolean activo);
}
