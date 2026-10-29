package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;

    @Column(name = "nombrecompleto", nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.NUMERIC)
    @Builder.Default
    private Boolean activo = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuarioid"),
            inverseJoinColumns = @JoinColumn(name = "rolid"))
    private java.util.Set<Rol> roles;
}
