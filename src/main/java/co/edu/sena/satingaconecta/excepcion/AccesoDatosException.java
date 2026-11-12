package co.edu.sena.satingaconecta.excepcion;

/**
 * Excepcion no verificada que envuelve los errores de acceso a datos (SQLException)
 * para que las capas superiores no dependan de JDBC.
 */
public class AccesoDatosException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccesoDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
