# Satinga Conecta – Módulo PQRS (JDBC + CRUD)

**Evidencia:** GA7-220501096-AA2-EV01 – Codificación de módulos del software
**Programa:** Análisis y Desarrollo de Software (ADSO) – SENA
**Proyecto formativo:** Construcción de software integrador de tecnologías orientadas a servicios
**Aprendiz:** Maicol Caicedo

## 1. Descripción del módulo

Satinga Conecta es la plataforma cívica de Bocas de Satinga (Olaya Herrera, Nariño) que conecta a
ciudadanos, funcionarios y administradores. Este repositorio contiene la codificación del módulo
**PQRS** (Peticiones, Quejas, Reclamos y Sugerencias), uno de los módulos cívicos definidos en el diseño
del proyecto. Se implementa en Java con conexión a MySQL/MariaDB mediante **JDBC** y las cuatro
operaciones **CRUD**.

| Operación | Dónde se implementa | Opción del menú |
|-----------|--------------------|-----------------|
| Inserción (Create) | `PqrsDao.insertar`, `PqrsServicio.radicar` | 1. Radicar PQRS |
| Consulta (Read) | `PqrsDao.buscarPorId / buscarPorRadicado / listarTodos / listarPorEstado / listarPorUsuario` | 2, 3, 4 |
| Actualización (Update) | `PqrsDao.actualizar`, `PqrsServicio.modificarContenido / responder` | 5, 6 |
| Eliminación (Delete) | `PqrsDao.eliminar`, `PqrsServicio.eliminar` | 7. Eliminar |

La misma estructura CRUD existe para la tabla de apoyo `usuarios` (`UsuarioDao`).

## 2. Relación con los artefactos del ciclo de software

| Artefacto | Cómo se refleja en el código |
|-----------|------------------------------|
| Diagrama de clases | Clases `Usuario` y `Pqrs` del paquete `modelo`, con los enums `RolUsuario`, `TipoPqrs` y `EstadoPqrs`; la relación 1–N usuario → PQRS se implementa con la llave foránea `id_usuario`. |
| Modelo relacional / SQL | `database/satinga_pqrs.sql` (base `satinga_pqrs`, tablas `usuarios` y `pqrs`, normalizadas hasta 3FN, con llaves primarias, foráneas, únicas e índices). |
| Casos de uso e historias de usuario | El ciudadano radica y consulta su PQRS; el funcionario la responde y cambia su estado; el administrador puede modificarla o eliminarla (menú del módulo). |
| Prototipos / diseño | La interfaz web se construye sobre esta lógica; el menú de consola valida el módulo antes de conectarlo con el front-end. |
| Plan de trabajo (AA1-EV01) | Git para versionamiento, Maven para dependencias y GitHub como repositorio remoto. |

## 3. Arquitectura por capas

```
vista      ->  MenuPqrs            (interacción con el usuario)
servicio   ->  PqrsServicio        (reglas de negocio y validaciones)
dao        ->  CrudDao, PqrsDao, UsuarioDao   (JDBC, SQL con PreparedStatement)
modelo     ->  Usuario, Pqrs, enums           (entidades)
configuracion -> ConexionBd        (conexión JDBC)
excepcion  ->  AccesoDatosException
```

Decisiones técnicas relevantes:

- **JDBC con `PreparedStatement`** en todas las consultas (previene inyección SQL).
- **`try-with-resources`** para cerrar conexiones, sentencias y resultados.
- **Transacción** al radicar: se inserta la PQRS y se asigna el radicado `PQRS-AAAA-NNNNNN` con
  `commit`/`rollback`.
- Credenciales fuera del código: `db.properties` o variables de entorno `SATINGA_DB_URL`,
  `SATINGA_DB_USER` y `SATINGA_DB_PASSWORD`.

## 4. Estándares de codificación aplicados

| Elemento | Convención | Ejemplos del proyecto |
|----------|------------|-----------------------|
| Paquetes | minúsculas, nombre de dominio inverso, sin guiones | `co.edu.sena.satingaconecta.dao` |
| Clases e interfaces | `PascalCase`, sustantivos | `PqrsDao`, `PqrsServicio`, `CrudDao` |
| Métodos | `camelCase`, comienzan con verbo | `insertar`, `buscarPorRadicado`, `listarPorEstado` |
| Variables y atributos | `camelCase`, nombres descriptivos | `idUsuario`, `fechaCreacion`, `nuevoEstado` |
| Constantes | `MAYUSCULAS_CON_GUION_BAJO` | `SQL_INSERTAR`, `LONGITUD_MAXIMA_ASUNTO` |
| Tablas y columnas SQL | `snake_case` en minúsculas | `pqrs`, `id_usuario`, `fecha_creacion` |

Además: indentación de 4 espacios, una clase pública por archivo, Javadoc en clases y métodos públicos,
y mensajes de commit con prefijo (`feat`, `docs`, `test`, `chore`).

## 5. Requisitos

- JDK 17 o superior
- Maven 3.8 o superior
- MySQL 8 / MariaDB (por ejemplo XAMPP o MySQL Workbench)

## 6. Cómo ejecutarlo

1. **Crear la base de datos:** abrir `database/satinga_pqrs.sql` en MySQL Workbench (o importarlo en
   phpMyAdmin) y ejecutarlo.
2. **Configurar la conexión:** editar `src/main/resources/db.properties` con el usuario y la clave de
   su servidor (en XAMPP: usuario `root` y clave vacía).
3. **Ejecutar el menú:**
   ```bash
   mvn compile exec:java
   ```
4. **Ejecutar la prueba automática del CRUD:**
   ```bash
   mvn test-compile exec:java -Dexec.classpathScope=test \
       -Dexec.mainClass=co.edu.sena.satingaconecta.prueba.PruebaCrudPqrs
   ```
   La prueba inserta, consulta, actualiza y elimina una PQRS y termina con el mensaje
   `TODAS LAS PRUEBAS PASARON`.

## 7. Versionamiento

El proyecto se gestiona con **Git** (rama `main`) y se publica en **GitHub**. El historial de commits
sigue la evolución del módulo: estructura y base de datos, modelos y conexión, DAO, servicio y menú,
pruebas y documentación. El enlace del repositorio está en `ENLACE_REPOSITORIO.txt`.
