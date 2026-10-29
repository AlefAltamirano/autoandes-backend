package pe.edu.upeu.autoandes.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.entity.*;
import pe.edu.upeu.autoandes.exception.*;
import pe.edu.upeu.autoandes.repository.*;
import pe.edu.upeu.autoandes.service.OrdenServicioService;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrdenServicioServiceImpl implements OrdenServicioService {
    private final OrdenServicioRepository ordenes;
    private final VehiculoRepository vehiculos;
    private final ServicioRepository servicios;

    @Override
    @Transactional
    public OrdenResponseDTO registrar(OrdenRequestDTO request, String usuario) {
        if (request.detalles().stream().map(DetalleOrdenRequestDTO::servicioId).distinct().count()
                != request.detalles().size()) {
            throw new ReglaNegocioException("No se puede repetir un servicio en la orden");
        }
        Vehiculo vehiculo = vehiculos.findById(request.vehiculoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo no encontrado"));
        ordenes.findFirstByVehiculoAndEstadoOrderByFechaDesc(vehiculo, "REGISTRADA")
                .ifPresent(last -> {
                    if (request.kilometraje() < last.getKilometraje()) {
                        throw new ReglaNegocioException("El kilometraje no puede ser menor al de la última orden: "
                                + last.getKilometraje());
                    }
                });
        OrdenServicio orden = OrdenServicio.builder().numero(nextNumber()).fecha(LocalDateTime.now())
                .vehiculo(vehiculo).kilometraje(request.kilometraje()).estado("REGISTRADA")
                .usuarioRegistro(usuario).total(0d).detalles(new ArrayList<>()).build();
        double total = 0;
        for (DetalleOrdenRequestDTO item : request.detalles()) {
            Servicio servicio = servicios.findById(item.servicioId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Servicio no encontrado: " + item.servicioId()));
            if (!Boolean.TRUE.equals(servicio.getActivo())) {
                throw new ReglaNegocioException("El servicio " + servicio.getCodigo() + " no está activo");
            }
            double subtotal = servicio.getPrecio() * item.cantidad();
            orden.getDetalles().add(DetalleOrden.builder().ordenServicio(orden).servicio(servicio)
                    .cantidad(item.cantidad()).precioUnitario(servicio.getPrecio()).subtotal(subtotal).build());
            total += subtotal;
        }
        orden.setTotal(total);
        return toResponse(ordenes.save(orden));
    }

    private String nextNumber() {
        return String.format("OS-%06d", ordenes.count() + 1);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> listar(String placa, String estado, LocalDate desde, LocalDate hasta) {
        LocalDateTime from = desde == null ? null : desde.atStartOfDay();
        LocalDateTime to = hasta == null ? null : hasta.plusDays(1).atStartOfDay().minusNanos(1);
        return ordenes.buscarConFiltros(placa, estado, from, to).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public OrdenResponseDTO anular(Long id, String motivo) {
        OrdenServicio orden = ordenes.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada"));
        if (!"REGISTRADA".equals(orden.getEstado())) throw new ReglaNegocioException("Solo se pueden anular órdenes registradas");
        if (!orden.getFecha().toLocalDate().equals(LocalDate.now())) throw new ReglaNegocioException("Solo se puede anular el mismo día");
        orden.setEstado("ANULADA");
        orden.setMotivoAnulacion(motivo);
        return toResponse(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteIngresoServicioDTO> reporte() { return ordenes.obtenerReporteIngresos(); }

    private OrdenResponseDTO toResponse(OrdenServicio o) {
        return new OrdenResponseDTO(o.getId(), o.getNumero(), o.getFecha(), o.getVehiculo().getPlaca(),
                o.getKilometraje(), o.getEstado(), o.getTotal(), o.getUsuarioRegistro(), o.getMotivoAnulacion(),
                o.getDetalles().stream().map(d -> new DetalleOrdenResponseDTO(d.getServicio().getId(),
                        d.getServicio().getCodigo(), d.getServicio().getNombre(), d.getCantidad(),
                        d.getPrecioUnitario(), d.getSubtotal())).toList());
    }
}
