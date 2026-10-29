package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ordenes_servicio")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String codigo;
    
    @Column(nullable = false)
    private LocalDateTime fechaRegistro;
    
    @Column(nullable = false)
    private Double kilometraje;
    
    @Column(nullable = false)
    private String estado; // REGISTRADA, ANULADA
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;
    
    @OneToMany(mappedBy = "ordenServicio", cascade = CascadeType.ALL)
    private List<DetalleOrden> detalles;
}
