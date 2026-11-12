package co.edu.sena.satingaconecta.dao;

import java.util.List;
import java.util.Optional;

/**
 * Contrato CRUD generico para los DAO de Satinga Conecta.
 *
 * @param <T> tipo de la entidad
 */
public interface CrudDao<T> {

    /** Inserta la entidad y devuelve la misma con su id generado. */
    T insertar(T entidad);

    /** Consulta una entidad por su identificador. */
    Optional<T> buscarPorId(int id);

    /** Consulta todas las entidades. */
    List<T> listarTodos();

    /** Actualiza la entidad; devuelve true si se modifico una fila. */
    boolean actualizar(T entidad);

    /** Elimina por identificador; devuelve true si se elimino una fila. */
    boolean eliminar(int id);
}
