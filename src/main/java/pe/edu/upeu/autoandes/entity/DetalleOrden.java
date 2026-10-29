package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalles_orden")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private OrdenServicio ordenServicio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(nullable = false)
    private Double precioUnitario;
    
    @Column(nullable = false)
    private Double subtotal;
}
