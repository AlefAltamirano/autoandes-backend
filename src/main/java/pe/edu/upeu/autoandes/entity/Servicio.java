package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "servicios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String codigo;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private Double precio;
    
    @Column(nullable = false)
    private Boolean activo = true;
}
