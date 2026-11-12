package co.edu.sena.satingaconecta.dao;

import co.edu.sena.satingaconecta.configuracion.ConexionBd;
import co.edu.sena.satingaconecta.excepcion.AccesoDatosException;
import co.edu.sena.satingaconecta.modelo.EstadoPqrs;
import co.edu.sena.satingaconecta.modelo.Pqrs;
import co.edu.sena.satingaconecta.modelo.TipoPqrs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Acceso a datos de la tabla pqrs mediante JDBC (CRUD completo). */
public class PqrsDao implements CrudDao<Pqrs> {

    private static final String SQL_INSERTAR =
            "INSERT INTO pqrs (radicado, tipo, asunto, descripcion, estado, id_usuario) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_ASIGNAR_RADICADO =
            "UPDATE pqrs SET radicado = ? WHERE id_pqrs = ?";
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT * FROM pqrs WHERE id_pqrs = ?";
    private static final String SQL_BUSCAR_POR_RADICADO =
            "SELECT * FROM pqrs WHERE radicado = ?";
    private static final String SQL_LISTAR =
            "SELECT * FROM pqrs ORDER BY id_pqrs";
    private static final String SQL_LISTAR_POR_ESTADO =
            "SELECT * FROM pqrs WHERE estado = ? ORDER BY id_pqrs";
    private static final String SQL_LISTAR_POR_USUARIO =
            "SELECT * FROM pqrs WHERE id_usuario = ? ORDER BY id_pqrs";
    private static final String SQL_ACTUALIZAR =
            "UPDATE pqrs SET tipo = ?, asunto = ?, descripcion = ?, estado = ?, "
                    + "respuesta = ? WHERE id_pqrs = ?";
    private static final String SQL_ELIMINAR =
            "DELETE FROM pqrs WHERE id_pqrs = ?";

    /**
     * Inserta la PQRS y le asigna el radicado con formato PQRS-AAAA-NNNNNN.
     * Ambas operaciones se ejecutan en una sola transaccion.
     */
    @Override
    public Pqrs insertar(Pqrs pqrs) {
        try (Connection conexion = ConexionBd.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try {
                int idGenerado = insertarFila(conexion, pqrs);
                String radicado = String.format("PQRS-%d-%06d", Year.now().getValue(), idGenerado);
                asignarRadicado(conexion, idGenerado, radicado);
                conexion.commit();
                pqrs.setIdPqrs(idGenerado);
                pqrs.setRadicado(radicado);
                return pqrs;
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al insertar la PQRS", e);
        }
    }

    @Override
    public Optional<Pqrs> buscarPorId(int id) {
        try (Connection conexion = ConexionBd.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_POR_ID)) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al consultar la PQRS " + id, e);
        }
    }

    /** Consulta una PQRS por su numero de radicado. */
    public Optional<Pqrs> buscarPorRadicado(String radicado) {
        try (Connection conexion = ConexionBd.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_POR_RADICADO)) {
            sentencia.setString(1, radicado);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al consultar el radicado " + radicado, e);
        }
    }

    @Override
    public List<Pqrs> listarTodos() {
        return listar(SQL_LISTAR, null);
    }

    /** Lista las PQRS que se encuentran en el estado indicado. */
    public List<Pqrs> listarPorEstado(EstadoPqrs estado) {
        return listar(SQL_LISTAR_POR_ESTADO, estado.name());
    }

    /** Lista las PQRS radicadas por un usuario. */
    public List<Pqrs> listarPorUsuario(int idUsuario) {
        return listar(SQL_LISTAR_POR_USUARIO, idUsuario);
    }

    @Override
    public boolean actualizar(Pqrs pqrs) {
        try (Connection conexion = ConexionBd.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {
            sentencia.setString(1, pqrs.getTipo().name());
            sentencia.setString(2, pqrs.getAsunto());
            sentencia.setString(3, pqrs.getDescripcion());
            sentencia.setString(4, pqrs.getEstado().name());
            sentencia.setString(5, pqrs.getRespuesta());
            sentencia.setInt(6, pqrs.getIdPqrs());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al actualizar la PQRS", e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        try (Connection conexion = ConexionBd.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al eliminar la PQRS " + id, e);
        }
    }

    // ------------------------------------------------------------------
    // Metodos auxiliares privados
    // ------------------------------------------------------------------

    private int insertarFila(Connection conexion, Pqrs pqrs) throws SQLException {
        // El radicado definitivo se asigna despues de conocer el id generado.
        String radicadoTemporal = "TMP-" + UUID.randomUUID().toString().substring(0, 20);
        try (PreparedStatement sentencia = conexion.prepareStatement(
                SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, radicadoTemporal);
            sentencia.setString(2, pqrs.getTipo().name());
            sentencia.setString(3, pqrs.getAsunto());
            sentencia.setString(4, pqrs.getDescripcion());
            sentencia.setString(5, pqrs.getEstado().name());
            sentencia.setInt(6, pqrs.getIdUsuario());
            sentencia.executeUpdate();
            try (ResultSet llaves = sentencia.getGeneratedKeys()) {
                if (llaves.next()) {
                    return llaves.getInt(1);
                }
                throw new SQLException("La base de datos no devolvio el id generado");
            }
        }
    }

    private void asignarRadicado(Connection conexion, int idPqrs, String radicado)
            throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_ASIGNAR_RADICADO)) {
            sentencia.setString(1, radicado);
            sentencia.setInt(2, idPqrs);
            sentencia.executeUpdate();
        }
    }

    private List<Pqrs> listar(String sql, Object parametro) {
        List<Pqrs> lista = new ArrayList<>();
        try (Connection conexion = ConexionBd.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            if (parametro != null) {
                sentencia.setObject(1, parametro);
            }
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    lista.add(mapear(resultado));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al listar las PQRS", e);
        }
    }

    private Pqrs mapear(ResultSet resultado) throws SQLException {
        Pqrs pqrs = new Pqrs();
        pqrs.setIdPqrs(resultado.getInt("id_pqrs"));
        pqrs.setRadicado(resultado.getString("radicado"));
        pqrs.setTipo(TipoPqrs.valueOf(resultado.getString("tipo")));
        pqrs.setAsunto(resultado.getString("asunto"));
        pqrs.setDescripcion(resultado.getString("descripcion"));
        pqrs.setEstado(EstadoPqrs.valueOf(resultado.getString("estado")));
        pqrs.setRespuesta(resultado.getString("respuesta"));
        pqrs.setIdUsuario(resultado.getInt("id_usuario"));
        Timestamp creacion = resultado.getTimestamp("fecha_creacion");
        if (creacion != null) {
            pqrs.setFechaCreacion(creacion.toLocalDateTime());
        }
        Timestamp actualizacion = resultado.getTimestamp("fecha_actualizacion");
        if (actualizacion != null) {
            pqrs.setFechaActualizacion(actualizacion.toLocalDateTime());
        }
        return pqrs;
    }
}
