package co.edu.sena.satingaconecta.prueba;

import co.edu.sena.satingaconecta.dao.UsuarioDao;
import co.edu.sena.satingaconecta.modelo.EstadoPqrs;
import co.edu.sena.satingaconecta.modelo.Pqrs;
import co.edu.sena.satingaconecta.modelo.RolUsuario;
import co.edu.sena.satingaconecta.modelo.TipoPqrs;
import co.edu.sena.satingaconecta.modelo.Usuario;
import co.edu.sena.satingaconecta.servicio.PqrsServicio;

/**
 * Prueba automatica del CRUD de PQRS contra la base de datos real.
 * Ejecuta INSERT, SELECT, UPDATE y DELETE y falla con codigo 1 si algo no se cumple.
 */
public class PruebaCrudPqrs {

    public static void main(String[] args) {
        PqrsServicio servicio = new PqrsServicio();
        UsuarioDao usuarioDao = new UsuarioDao();

        // Usuario temporal para no depender de datos previos
        Usuario usuario = usuarioDao.insertar(new Usuario("PRUEBA-" + System.nanoTime(),
                "Usuario de Prueba", "prueba" + System.nanoTime() + "@satinga.local",
                "3001112233", RolUsuario.CIUDADANO));
        System.out.println("[OK] Usuario creado: " + usuario);

        // INSERT
        Pqrs creada = servicio.radicar(TipoPqrs.PETICION, "Alumbrado publico",
                "Solicito reparar el alumbrado de la calle principal del barrio.", usuario.getIdUsuario());
        verificar(creada.getIdPqrs() > 0 && creada.getRadicado().startsWith("PQRS-"),
                "INSERT genera id y radicado: " + creada.getRadicado());

        // SELECT
        Pqrs consultada = servicio.consultarPorRadicado(creada.getRadicado()).orElseThrow();
        verificar(consultada.getEstado() == EstadoPqrs.RADICADA, "SELECT por radicado devuelve estado RADICADA");
        verificar(servicio.listarPorUsuario(usuario.getIdUsuario()).size() == 1, "SELECT por usuario devuelve 1 registro");

        // UPDATE
        verificar(servicio.modificarContenido(creada.getIdPqrs(), TipoPqrs.QUEJA, "Alumbrado danado",
                "El alumbrado lleva dos semanas sin funcionar en la calle principal."), "UPDATE de contenido");
        verificar(servicio.responder(creada.getIdPqrs(), EstadoPqrs.RESUELTA, "Se reparo el alumbrado."),
                "UPDATE de estado y respuesta");
        Pqrs actualizada = servicio.consultarPorId(creada.getIdPqrs()).orElseThrow();
        verificar(actualizada.getTipo() == TipoPqrs.QUEJA && actualizada.getEstado() == EstadoPqrs.RESUELTA
                && "Se reparo el alumbrado.".equals(actualizada.getRespuesta()), "Cambios persistidos en la base de datos");
        verificar(servicio.listarPorEstado(EstadoPqrs.RESUELTA).stream()
                .anyMatch(p -> p.getIdPqrs() == creada.getIdPqrs()), "SELECT por estado encuentra la PQRS");

        // Validaciones
        try {
            servicio.radicar(TipoPqrs.RECLAMO, " ", "Descripcion suficientemente larga", usuario.getIdUsuario());
            verificar(false, "Debio rechazar asunto vacio");
        } catch (IllegalArgumentException e) {
            System.out.println("[OK] Validacion rechaza asunto vacio: " + e.getMessage());
        }

        // DELETE
        verificar(servicio.eliminar(creada.getIdPqrs()), "DELETE de la PQRS");
        verificar(servicio.consultarPorId(creada.getIdPqrs()).isEmpty(), "La PQRS ya no existe");
        verificar(usuarioDao.eliminar(usuario.getIdUsuario()), "Limpieza del usuario temporal");

        System.out.println("TODAS LAS PRUEBAS PASARON");
    }

    private static void verificar(boolean condicion, String descripcion) {
        if (!condicion) {
            System.out.println("[FALLO] " + descripcion);
            System.exit(1);
        }
        System.out.println("[OK] " + descripcion);
    }
}
