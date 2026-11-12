package co.edu.sena.satingaconecta.dao;

import co.edu.sena.satingaconecta.configuracion.ConexionBd;
import co.edu.sena.satingaconecta.excepcion.AccesoDatosException;
import co.edu.sena.satingaconecta.modelo.RolUsuario;
import co.edu.sena.satingaconecta.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de la tabla usuarios mediante JDBC (CRUD completo). */
public class UsuarioDao implements CrudDao<Usuario> {

    private static final String SQL_INSERTAR =
            "INSERT INTO usuarios (documento, nombre_completo, correo, telefono, rol) "
                    + "VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT * FROM usuarios WHERE id_usuario = ?";
    private static final String SQL_BUSCAR_POR_DOCUMENTO =
            "SELECT * FROM usuarios WHERE documento = ?";
    private static final String SQL_LISTAR =
            "SELECT * FROM usuarios ORDER BY id_usuario";
    private static final String SQL_ACTUALIZAR =
            "UPDATE usuarios SET documento = ?, nombre_completo = ?, correo = ?, "
                    + "telefono = ?, rol = ? WHERE id_usuario = ?";
    private static final String SQL_ELIMINAR =
            "DELETE FROM usuarios WHERE id_usuario = ?";

    @Override
    public Usuario insertar(Usuario usuario) {
        try (Connection conexion = ConexionBd.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(
                     SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, usuario.getDocumento());
            sentencia.setString(2, usuario.getNombreCompleto());
            sentencia.setString(3, usuario.getCorreo());
            sentencia.setString(4, usuario.getTelefono());
            sentencia.setString(5, usuario.getRol().name());
            sentencia.executeUpdate();
            try (ResultSet llaves = sentencia.getGeneratedKeys()) {
                if (llaves.next()) {
                    usuario.setIdUsuario(llaves.getInt(1));
                }
            }
            return usuario;
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al insertar el usuario", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        try (Connection conexion = ConexionBd.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_POR_ID)) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al consultar el usuario " + id, e);
        }
    }

    /** Consulta un usuario por su numero de documento. */
    public Optional<Usuario> buscarPorDocumento(String documento) {
        try (Connection conexion = ConexionBd.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_POR_DOCUMENTO)) {
            sentencia.setString(1, documento);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al consultar el documento " + documento, e);
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conexion = ConexionBd.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                usuarios.add(mapear(resultado));
            }
            return usuarios;
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al listar los usuarios", e);
        }
    }

    @Override
    public boolean actualizar(Usuario usuario) {
        try (Connection conexion = ConexionBd.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {
            sentencia.setString(1, usuario.getDocumento());
            sentencia.setString(2, usuario.getNombreCompleto());
            sentencia.setString(3, usuario.getCorreo());
            sentencia.setString(4, usuario.getTelefono());
            sentencia.setString(5, usuario.getRol().name());
            sentencia.setInt(6, usuario.getIdUsuario());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al actualizar el usuario", e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        try (Connection conexion = ConexionBd.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al eliminar el usuario " + id, e);
        }
    }

    private Usuario mapear(ResultSet resultado) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(resultado.getInt("id_usuario"));
        usuario.setDocumento(resultado.getString("documento"));
        usuario.setNombreCompleto(resultado.getString("nombre_completo"));
        usuario.setCorreo(resultado.getString("correo"));
        usuario.setTelefono(resultado.getString("telefono"));
        usuario.setRol(RolUsuario.valueOf(resultado.getString("rol")));
        Timestamp registro = resultado.getTimestamp("fecha_registro");
        if (registro != null) {
            usuario.setFechaRegistro(registro.toLocalDateTime());
        }
        return usuario;
    }
}
