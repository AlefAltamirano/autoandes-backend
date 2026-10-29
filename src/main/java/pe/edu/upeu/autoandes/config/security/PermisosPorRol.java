package pe.edu.upeu.autoandes.config.security;

import java.util.*;

public final class PermisosPorRol {
    private static final Set<String> TODOS = Set.of(Permiso.values()).stream().map(Enum::name).collect(java.util.stream.Collectors.toUnmodifiableSet());
    private static final Map<String, Set<String>> MAP = Map.of(
            "ADMIN", TODOS,
            "ASESOR", Set.of("VEHICULO_LEER", "VEHICULO_ESCRIBIR", "SERVICIO_LEER", "ORDEN_LEER", "ORDEN_REGISTRAR"),
            "JEFE_TALLER", Set.of("VEHICULO_LEER", "SERVICIO_LEER", "SERVICIO_ESCRIBIR", "ORDEN_LEER", "ORDEN_ANULAR", "REPORTE_VER"));
    private PermisosPorRol() {}
    public static Set<String> para(String rol) { return MAP.getOrDefault(rol, Set.of()); }
}
