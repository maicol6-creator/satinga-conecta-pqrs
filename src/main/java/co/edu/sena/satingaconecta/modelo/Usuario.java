package co.edu.sena.satingaconecta.modelo;

import java.time.LocalDateTime;

/** Entidad Usuario (tabla usuarios). */
public class Usuario {

    private int idUsuario;
    private String documento;
    private String nombreCompleto;
    private String correo;
    private String telefono;
    private RolUsuario rol;
    private LocalDateTime fechaRegistro;

    public Usuario() {
        this.rol = RolUsuario.CIUDADANO;
    }

    public Usuario(String documento, String nombreCompleto, String correo,
                   String telefono, RolUsuario rol) {
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.telefono = telefono;
        this.rol = rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + idUsuario + ", documento='" + documento + "', nombre='"
                + nombreCompleto + "', correo='" + correo + "', rol=" + rol + "}";
    }
}
