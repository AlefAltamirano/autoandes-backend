package pe.edu.upeu.autoandes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.upeu.autoandes.entity.Vehiculo;
import pe.edu.upeu.autoandes.exception.ReglaNegocioException;
import pe.edu.upeu.autoandes.repository.VehiculoRepository;
import pe.edu.upeu.autoandes.service.VehiculoService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository repository;

    @Override
    public List<Vehiculo> listar() {
        return repository.findAll();
    }

    @Override
    public Vehiculo guardar(Vehiculo vehiculo) {
        if (repository.existsByPlaca(vehiculo.getPlaca())) {
            log.warn("Placa ya registrada: {}", vehiculo.getPlaca());
            throw new ReglaNegocioException("La placa ya existe");
        }
        log.info("Vehículo registrado: {}", vehiculo.getPlaca());
        return repository.save(vehiculo);
    }

    @Override
    public void eliminar(Long id) {
        // Validación pendiente: verificar si tiene órdenes de servicio antes de eliminar
        repository.deleteById(id);
        log.info("Vehículo eliminado: {}", id);
    }
}
