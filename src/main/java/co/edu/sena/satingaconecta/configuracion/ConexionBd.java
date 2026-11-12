package co.edu.sena.satingaconecta.configuracion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fabrica de conexiones JDBC para Satinga Conecta.
 * Lee los datos de conexion de db.properties y permite sobrescribirlos
 * con las variables de entorno SATINGA_DB_URL, SATINGA_DB_USER y SATINGA_DB_PASSWORD.
 */
public final class ConexionBd {

    private static final String ARCHIVO_PROPIEDADES = "db.properties";
    private static final Properties PROPIEDADES = cargarPropiedades();

    private ConexionBd() {
        // Clase utilitaria: no se instancia.
    }

    /**
     * Abre una nueva conexion a la base de datos. Quien la solicita debe cerrarla
     * (se recomienda try-with-resources).
     */
    public static Connection obtenerConexion() throws SQLException {
        String url = valorConfigurado("SATINGA_DB_URL", "db.url");
        String usuario = valorConfigurado("SATINGA_DB_USER", "db.usuario");
        String clave = valorConfigurado("SATINGA_DB_PASSWORD", "db.clave");
        return DriverManager.getConnection(url, usuario, clave);
    }

    private static String valorConfigurado(String variableEntorno, String llavePropiedad) {
        String valorEntorno = System.getenv(variableEntorno);
        if (valorEntorno != null) {
            return valorEntorno;
        }
        return PROPIEDADES.getProperty(llavePropiedad, "");
    }

    private static Properties cargarPropiedades() {
        Properties propiedades = new Properties();
        try (InputStream flujo = ConexionBd.class.getClassLoader()
                .getResourceAsStream(ARCHIVO_PROPIEDADES)) {
            if (flujo != null) {
                propiedades.load(flujo);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + ARCHIVO_PROPIEDADES, e);
        }
        return propiedades;
    }
}
