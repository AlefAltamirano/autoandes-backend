package pe.edu.upeu.autoandes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.upeu.autoandes.entity.Servicio;
import pe.edu.upeu.autoandes.exception.ReglaNegocioException;
import pe.edu.upeu.autoandes.repository.ServicioRepository;
import pe.edu.upeu.autoandes.repository.OrdenServicioRepository;
import pe.edu.upeu.autoandes.service.ServicioService;

import java.util.List;

@org.springframework.transaction.annotation.Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository repository;
    private final OrdenServicioRepository ordenServicioRepository;

    @Override
    public List<Servicio> listar() {
        return repository.findAll();
    }

    @Override
    public Servicio obtener(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new pe.edu.upeu.autoandes.exception.RecursoNoEncontradoException("Servicio no encontrado"));
    }

    @Override
    public Servicio guardar(Servicio servicio) {
        if (repository.existsByCodigo(servicio.getCodigo())) {
            log.warn("Intento de registrar código duplicado: {}", servicio.getCodigo());
            throw new ReglaNegocioException("El código ya existe");
        }

        log.info("Registrando nuevo servicio: {}", servicio.getCodigo());
        return repository.save(servicio);
    }

    @Override
    public Servicio actualizar(Long id, Servicio servicio) {
        Servicio actual = obtener(id);
        if (repository.existsByCodigoAndIdNot(servicio.getCodigo(), id)) {
            throw new ReglaNegocioException("El código ya existe");
        }
        actual.setCodigo(servicio.getCodigo());
        actual.setNombre(servicio.getNombre());
        actual.setPrecio(servicio.getPrecio());
        actual.setDuracionMin(servicio.getDuracionMin());
        log.info("Servicio actualizado: {}", id);
        return repository.save(actual);
    }

    @Override
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new pe.edu.upeu.autoandes.exception.RecursoNoEncontradoException("Servicio no encontrado");
        if (ordenServicioRepository.existsByDetallesServicioId(id)) {
            throw new ReglaNegocioException("No se puede eliminar un servicio con órdenes asociadas");
        }
        repository.deleteById(id);
        log.info("Servicio eliminado: {}", id);
    }

    @Override
    public void cambiarEstado(Long id, Boolean activo) {
        Servicio s = repository.findById(id)
                .orElseThrow(() -> new pe.edu.upeu.autoandes.exception.RecursoNoEncontradoException("Servicio no encontrado"));
        s.setActivo(activo);
        repository.save(s);
        log.info("Servicio {} cambió estado a {}", id, activo);
    }
}
