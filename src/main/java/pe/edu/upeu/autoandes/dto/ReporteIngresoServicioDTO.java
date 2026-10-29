package pe.edu.upeu.autoandes.dto;

public class ReporteIngresoServicioDTO {
    private final String codigo;
    private final String servicio;
    private final Long cantidadTotal;
    private final Double importeTotal;

    public ReporteIngresoServicioDTO(String codigo, String servicio, Number cantidadTotal, Number importeTotal) {
        this.codigo = codigo;
        this.servicio = servicio;
        this.cantidadTotal = cantidadTotal == null ? 0L : cantidadTotal.longValue();
        this.importeTotal = importeTotal == null ? 0D : importeTotal.doubleValue();
    }

    public String getCodigo() { return codigo; }
    public String getServicio() { return servicio; }
    public Long getCantidadTotal() { return cantidadTotal; }
    public Double getImporteTotal() { return importeTotal; }
}
