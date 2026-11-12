package co.edu.sena.satingaconecta.vista;

import co.edu.sena.satingaconecta.excepcion.AccesoDatosException;
import co.edu.sena.satingaconecta.modelo.EstadoPqrs;
import co.edu.sena.satingaconecta.modelo.Pqrs;
import co.edu.sena.satingaconecta.modelo.TipoPqrs;
import co.edu.sena.satingaconecta.servicio.PqrsServicio;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/** Menu de consola del modulo PQRS de Satinga Conecta. */
public class MenuPqrs {

    private final PqrsServicio servicio = new PqrsServicio();
    private final Scanner lector = new Scanner(System.in);

    public static void main(String[] args) {
        new MenuPqrs().iniciar();
    }

    public void iniciar() {
        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            String opcion = lector.nextLine().trim();
            try {
                switch (opcion) {
                    case "1" -> radicar();
                    case "2" -> consultarPorRadicado();
                    case "3" -> listarTodas();
                    case "4" -> listarPorEstado();
                    case "5" -> modificarContenido();
                    case "6" -> responder();
                    case "7" -> eliminar();
                    case "0" -> continuar = false;
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Dato invalido: " + e.getMessage());
            } catch (AccesoDatosException e) {
                System.out.println("Error de base de datos: " + e.getMessage()
                        + " -> " + e.getCause().getMessage());
            }
        }
        System.out.println("Hasta pronto - Satinga Conecta.");
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("=== SATINGA CONECTA - MODULO PQRS ===");
        System.out.println("1. Radicar PQRS            (INSERT)");
        System.out.println("2. Consultar por radicado  (SELECT)");
        System.out.println("3. Listar todas            (SELECT)");
        System.out.println("4. Listar por estado       (SELECT)");
        System.out.println("5. Modificar contenido     (UPDATE)");
        System.out.println("6. Responder / cambiar estado (UPDATE)");
        System.out.println("7. Eliminar                (DELETE)");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opcion: ");
    }

    private void radicar() {
        TipoPqrs tipo = leerTipo();
        String asunto = leerTexto("Asunto: ");
        String descripcion = leerTexto("Descripcion: ");
        int idUsuario = leerEntero("Id del usuario que radica: ");
        Pqrs creada = servicio.radicar(tipo, asunto, descripcion, idUsuario);
        System.out.println("PQRS radicada con exito. Radicado: " + creada.getRadicado());
    }

    private void consultarPorRadicado() {
        String radicado = leerTexto("Radicado: ");
        Optional<Pqrs> pqrs = servicio.consultarPorRadicado(radicado);
        if (pqrs.isPresent()) {
            imprimirDetalle(pqrs.get());
        } else {
            System.out.println("No se encontro el radicado " + radicado);
        }
    }

    private void listarTodas() {
        imprimirLista(servicio.listarTodas());
    }

    private void listarPorEstado() {
        imprimirLista(servicio.listarPorEstado(leerEstado()));
    }

    private void modificarContenido() {
        int id = leerEntero("Id de la PQRS: ");
        TipoPqrs tipo = leerTipo();
        String asunto = leerTexto("Nuevo asunto: ");
        String descripcion = leerTexto("Nueva descripcion: ");
        boolean actualizada = servicio.modificarContenido(id, tipo, asunto, descripcion);
        System.out.println(actualizada ? "PQRS actualizada." : "No se realizaron cambios.");
    }

    private void responder() {
        int id = leerEntero("Id de la PQRS: ");
        EstadoPqrs estado = leerEstado();
        String respuesta = leerTexto("Respuesta (opcional, Enter para omitir): ", true);
        boolean actualizada = servicio.responder(id, estado, respuesta);
        System.out.println(actualizada ? "Estado actualizado." : "No se realizaron cambios.");
    }

    private void eliminar() {
        int id = leerEntero("Id de la PQRS a eliminar: ");
        boolean eliminada = servicio.eliminar(id);
        System.out.println(eliminada ? "PQRS eliminada." : "No existe una PQRS con ese id.");
    }

    // ------------------------------------------------------------------
    // Utilidades de entrada y salida
    // ------------------------------------------------------------------

    private void imprimirLista(List<Pqrs> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay registros.");
            return;
        }
        System.out.printf("%-4s %-18s %-11s %-11s %s%n", "ID", "RADICADO", "TIPO", "ESTADO", "ASUNTO");
        for (Pqrs pqrs : lista) {
            System.out.printf("%-4d %-18s %-11s %-11s %s%n", pqrs.getIdPqrs(), pqrs.getRadicado(),
                    pqrs.getTipo(), pqrs.getEstado(), pqrs.getAsunto());
        }
    }

    private void imprimirDetalle(Pqrs pqrs) {
        System.out.println("Radicado:    " + pqrs.getRadicado());
        System.out.println("Tipo:        " + pqrs.getTipo());
        System.out.println("Estado:      " + pqrs.getEstado());
        System.out.println("Asunto:      " + pqrs.getAsunto());
        System.out.println("Descripcion: " + pqrs.getDescripcion());
        System.out.println("Respuesta:   " + (pqrs.getRespuesta() == null ? "(sin respuesta)" : pqrs.getRespuesta()));
        System.out.println("Creada:      " + pqrs.getFechaCreacion());
    }

    private String leerTexto(String mensaje) {
        return leerTexto(mensaje, false);
    }

    private String leerTexto(String mensaje, boolean opcional) {
        System.out.print(mensaje);
        String texto = lector.nextLine();
        if (!opcional && texto.isBlank()) {
            throw new IllegalArgumentException("El campo no puede estar vacio");
        }
        return texto;
    }

    private int leerEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(lector.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Debe ingresar un numero entero");
        }
    }

    private TipoPqrs leerTipo() {
        System.out.print("Tipo (PETICION, QUEJA, RECLAMO, SUGERENCIA): ");
        return convertirEnum(TipoPqrs.class, lector.nextLine());
    }

    private EstadoPqrs leerEstado() {
        System.out.print("Estado (RADICADA, EN_TRAMITE, RESUELTA, CERRADA): ");
        return convertirEnum(EstadoPqrs.class, lector.nextLine());
    }

    private <E extends Enum<E>> E convertirEnum(Class<E> clase, String texto) {
        try {
            return Enum.valueOf(clase, texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor no permitido: " + texto);
        }
    }
}
