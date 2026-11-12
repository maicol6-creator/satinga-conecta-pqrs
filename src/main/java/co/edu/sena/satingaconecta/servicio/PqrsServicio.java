package co.edu.sena.satingaconecta.servicio;

import co.edu.sena.satingaconecta.dao.PqrsDao;
import co.edu.sena.satingaconecta.dao.UsuarioDao;
import co.edu.sena.satingaconecta.modelo.EstadoPqrs;
import co.edu.sena.satingaconecta.modelo.Pqrs;
import co.edu.sena.satingaconecta.modelo.TipoPqrs;

import java.util.List;
import java.util.Optional;

/**
 * Logica de negocio del modulo PQRS: valida los datos antes de llegar a la base
 * de datos y coordina los DAO.
 */
public class PqrsServicio {

    private static final int LONGITUD_MAXIMA_ASUNTO = 150;
    private static final int LONGITUD_MINIMA_DESCRIPCION = 10;

    private final PqrsDao pqrsDao;
    private final UsuarioDao usuarioDao;

    public PqrsServicio() {
        this(new PqrsDao(), new UsuarioDao());
    }

    public PqrsServicio(PqrsDao pqrsDao, UsuarioDao usuarioDao) {
        this.pqrsDao = pqrsDao;
        this.usuarioDao = usuarioDao;
    }

    /** Registra (INSERT) una nueva PQRS en estado RADICADA. */
    public Pqrs radicar(TipoPqrs tipo, String asunto, String descripcion, int idUsuario) {
        validarDatos(tipo, asunto, descripcion);
        if (usuarioDao.buscarPorId(idUsuario).isEmpty()) {
            throw new IllegalArgumentException("No existe el usuario con id " + idUsuario);
        }
        return pqrsDao.insertar(new Pqrs(tipo, asunto.trim(), descripcion.trim(), idUsuario));
    }

    /** Consulta (SELECT) una PQRS por id. */
    public Optional<Pqrs> consultarPorId(int idPqrs) {
        return pqrsDao.buscarPorId(idPqrs);
    }

    /** Consulta (SELECT) una PQRS por su numero de radicado. */
    public Optional<Pqrs> consultarPorRadicado(String radicado) {
        if (radicado == null || radicado.isBlank()) {
            throw new IllegalArgumentException("El radicado es obligatorio");
        }
        return pqrsDao.buscarPorRadicado(radicado.trim());
    }

    /** Consulta (SELECT) todas las PQRS. */
    public List<Pqrs> listarTodas() {
        return pqrsDao.listarTodos();
    }

    /** Consulta (SELECT) las PQRS filtradas por estado. */
    public List<Pqrs> listarPorEstado(EstadoPqrs estado) {
        return pqrsDao.listarPorEstado(estado);
    }

    /** Consulta (SELECT) las PQRS de un usuario. */
    public List<Pqrs> listarPorUsuario(int idUsuario) {
        return pqrsDao.listarPorUsuario(idUsuario);
    }

    /** Edita (UPDATE) el contenido de una PQRS. */
    public boolean modificarContenido(int idPqrs, TipoPqrs tipo, String asunto, String descripcion) {
        validarDatos(tipo, asunto, descripcion);
        Pqrs pqrs = obtenerObligatoria(idPqrs);
        pqrs.setTipo(tipo);
        pqrs.setAsunto(asunto.trim());
        pqrs.setDescripcion(descripcion.trim());
        return pqrsDao.actualizar(pqrs);
    }

    /** Actualiza (UPDATE) el estado y, opcionalmente, la respuesta oficial. */
    public boolean responder(int idPqrs, EstadoPqrs nuevoEstado, String respuesta) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        Pqrs pqrs = obtenerObligatoria(idPqrs);
        pqrs.setEstado(nuevoEstado);
        if (respuesta != null && !respuesta.isBlank()) {
            pqrs.setRespuesta(respuesta.trim());
        }
        return pqrsDao.actualizar(pqrs);
    }

    /** Elimina (DELETE) una PQRS. */
    public boolean eliminar(int idPqrs) {
        return pqrsDao.eliminar(idPqrs);
    }

    // ------------------------------------------------------------------

    private Pqrs obtenerObligatoria(int idPqrs) {
        return pqrsDao.buscarPorId(idPqrs).orElseThrow(
                () -> new IllegalArgumentException("No existe la PQRS con id " + idPqrs));
    }

    private void validarDatos(TipoPqrs tipo, String asunto, String descripcion) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de PQRS es obligatorio");
        }
        if (asunto == null || asunto.isBlank()) {
            throw new IllegalArgumentException("El asunto es obligatorio");
        }
        if (asunto.trim().length() > LONGITUD_MAXIMA_ASUNTO) {
            throw new IllegalArgumentException(
                    "El asunto no puede superar " + LONGITUD_MAXIMA_ASUNTO + " caracteres");
        }
        if (descripcion == null || descripcion.trim().length() < LONGITUD_MINIMA_DESCRIPCION) {
            throw new IllegalArgumentException("La descripcion debe tener al menos "
                    + LONGITUD_MINIMA_DESCRIPCION + " caracteres");
        }
    }
}
