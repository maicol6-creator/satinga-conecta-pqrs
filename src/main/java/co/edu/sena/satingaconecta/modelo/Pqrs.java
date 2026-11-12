package co.edu.sena.satingaconecta.modelo;

import java.time.LocalDateTime;

/** Entidad PQRS (tabla pqrs). */
public class Pqrs {

    private int idPqrs;
    private String radicado;
    private TipoPqrs tipo;
    private String asunto;
    private String descripcion;
    private EstadoPqrs estado;
    private String respuesta;
    private int idUsuario;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public Pqrs() {
        this.estado = EstadoPqrs.RADICADA;
    }

    public Pqrs(TipoPqrs tipo, String asunto, String descripcion, int idUsuario) {
        this();
        this.tipo = tipo;
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.idUsuario = idUsuario;
    }

    public int getIdPqrs() {
        return idPqrs;
    }

    public void setIdPqrs(int idPqrs) {
        this.idPqrs = idPqrs;
    }

    public String getRadicado() {
        return radicado;
    }

    public void setRadicado(String radicado) {
        this.radicado = radicado;
    }

    public TipoPqrs getTipo() {
        return tipo;
    }

    public void setTipo(TipoPqrs tipo) {
        this.tipo = tipo;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoPqrs getEstado() {
        return estado;
    }

    public void setEstado(EstadoPqrs estado) {
        this.estado = estado;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    @Override
    public String toString() {
        return "Pqrs{id=" + idPqrs + ", radicado='" + radicado + "', tipo=" + tipo
                + ", asunto='" + asunto + "', estado=" + estado + ", idUsuario=" + idUsuario + "}";
    }
}
