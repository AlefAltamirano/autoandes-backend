package pe.edu.upeu.autoandes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.upeu.autoandes.entity.Cliente;
import pe.edu.upeu.autoandes.entity.Vehiculo;
import pe.edu.upeu.autoandes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.autoandes.exception.ReglaNegocioException;
import pe.edu.upeu.autoandes.repository.ClienteRepository;
import pe.edu.upeu.autoandes.repository.OrdenServicioRepository;
import pe.edu.upeu.autoandes.repository.VehiculoRepository;
import pe.edu.upeu.autoandes.service.VehiculoService;

import java.util.List;

@org.springframework.transaction.annotation.Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository repository;
    private final ClienteRepository clienteRepository;
    private final OrdenServicioRepository ordenServicioRepository;

    @Override
    public List<Vehiculo> listar() {
        return repository.findAll();
    }

    @Override
    public List<Vehiculo> listarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

    @Override
    public Vehiculo obtener(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Vehículo no encontrado"));
    }

    @Override
    public Vehiculo guardar(Vehiculo vehiculo) {
        if (repository.existsByPlaca(vehiculo.getPlaca())) {
            log.warn("Intento de registrar placa duplicada: {}", vehiculo.getPlaca());
            throw new ReglaNegocioException("La placa ya existe");
        }

        Cliente cliente = clienteRepository.findById(vehiculo.getCliente().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));
        
        vehiculo.setCliente(cliente);
        log.info("Vehículo registrado: {}", vehiculo.getPlaca());
        return repository.save(vehiculo);
    }

    @Override
    public Vehiculo actualizar(Long id, Vehiculo vehiculo) {
        Vehiculo actual = obtener(id);
        if (repository.existsByPlacaAndIdNot(vehiculo.getPlaca(), id)) {
            throw new ReglaNegocioException("La placa ya existe");
        }
        Cliente cliente = clienteRepository.findById(vehiculo.getCliente().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));
        actual.setPlaca(vehiculo.getPlaca());
        actual.setMarca(vehiculo.getMarca());
        actual.setModelo(vehiculo.getModelo());
        actual.setAnio(vehiculo.getAnio());
        actual.setCliente(cliente);
        log.info("Vehículo actualizado: {}", id);
        return repository.save(actual);
    }

    @Override
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("Vehículo no encontrado");
        }
        if (ordenServicioRepository.existsByVehiculoId(id)) {
            log.warn("Intento de eliminar vehículo con órdenes: {}", id);
            throw new ReglaNegocioException("No se puede eliminar un vehículo con órdenes asociadas");
        }
        repository.deleteById(id);
        log.info("Vehículo eliminado: {}", id);
    }
}
