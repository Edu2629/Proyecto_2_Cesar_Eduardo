# Simulador de Cola de Impresión con Montículo y Tabla Hash

Aplicación de escritorio en Java que simula la cola de impresión de un sistema operativo usando un **montículo binario mínimo** para prioridades y una **tabla de dispersión** para acceso rápido por usuario.

## Integrantes

- César Bezara
- Eduardo Peraza

## Repositorio

- URL GitHub: https://github.com/Edu2629/Proyecto_2_Cesar_Eduardo.git

## Requisitos

- Java 17+
- Maven 3.9+

## Compilar

```bash
mvn clean compile
```

## Ejecutar

```bash
mvn exec:java
```

## Funcionalidades implementadas

- Gestión de usuarios con tipo de prioridad (`prioridad_alta`, `prioridad_media`, `prioridad_baja`).
- Carga masiva de usuarios desde CSV usando `JFileChooser`.
- Gestión de documentos por usuario (crear, listar, eliminar si no está en cola).
- Envío de documento a cola con modo normal o prioritario.
- Cola de impresión implementada con montículo binario mínimo propio (`insertar` y `eliminar_min`).
- Reloj de simulación en tiempo real.
- Liberar impresora (extrae e imprime el mínimo).
- Eliminación de documento en cola por usuario, respetando la lógica de montículo:
  1. Alterar etiqueta de tiempo para máxima prioridad.
  2. Flotar al inicio del montículo.
  3. Eliminar por `eliminar_min` sin imprimir.
- Vista lineal de la cola y vista de árbol del montículo.

## Estructuras de datos implementadas

- `ListaEnlazada<T>`
- `TablaHash<K, V>` con encadenamiento
- `MonticuloBinarioMinimo`

No se utilizaron librerías de estructuras de datos para resolver el problema.

## Diagrama de clases

- Ver archivo en `docs/diagrama-clases.md`.

## Organización del proyecto

- `src/main/java/proyecto/impresion/modelo`: entidades del dominio.
- `src/main/java/proyecto/impresion/estructuras`: TDAs implementados manualmente.
- `src/main/java/proyecto/impresion/servicio`: lógica de simulación.
- `src/main/java/proyecto/impresion/interfaz`: interfaz gráfica Swing.
