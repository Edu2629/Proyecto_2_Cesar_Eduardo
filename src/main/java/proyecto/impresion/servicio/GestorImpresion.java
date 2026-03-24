package proyecto.impresion.servicio;

import proyecto.impresion.estructuras.ListaEnlazada;
import proyecto.impresion.estructuras.MonticuloBinarioMinimo;
import proyecto.impresion.estructuras.TablaHash;
import proyecto.impresion.modelo.Documento;
import proyecto.impresion.modelo.RegistroImpresion;
import proyecto.impresion.modelo.RegistroUsuarioCola;
import proyecto.impresion.modelo.TipoUsuario;
import proyecto.impresion.modelo.Usuario;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Clase principal de lógica del proyecto.
 * Aquí se coordinan usuarios, documentos, cola de impresión, reloj y tablas hash.
 */
public class GestorImpresion {
    private final TablaHash<String, Usuario> usuarios;
    private final TablaHash<String, ListaEnlazada<RegistroUsuarioCola>> registrosColaPorUsuario;
    private final TablaHash<Integer, RegistroUsuarioCola> registroColaPorId;
    private final TablaHash<Integer, Documento> documentoPorIdRegistro;
    private final MonticuloBinarioMinimo colaImpresion;
    private final RelojSimulacion reloj;
    private int secuenciaRegistros;

    /**
     * Inicializa todas las estructuras necesarias para la simulación.
     */
    public GestorImpresion() {
        this.usuarios = new TablaHash<>();
        this.registrosColaPorUsuario = new TablaHash<>();
        this.registroColaPorId = new TablaHash<>();
        this.documentoPorIdRegistro = new TablaHash<>();
        this.colaImpresion = new MonticuloBinarioMinimo();
        this.reloj = new RelojSimulacion();
    }

    /**
     * @return tiempo actual de la simulación en milisegundos.
     */
    public long obtenerTiempoSimulacion() {
        return reloj.tiempoTranscurridoMillis();
    }

    /**
     * Agrega un usuario al sistema.
     *
     * @param identificador nombre de usuario.
     * @param tipoUsuario tipo o nivel de prioridad del usuario.
     */
    public void agregarUsuario(String identificador, TipoUsuario tipoUsuario) {
        validarIdentificador(identificador);
        if (tipoUsuario == null) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de usuario");
        }
        String clave = identificador.trim().toLowerCase();
        if (usuarios.contieneClave(clave)) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        usuarios.poner(clave, new Usuario(identificador.trim(), tipoUsuario));
    }

    /**
     * Carga usuarios desde un archivo CSV.
     *
     * @param archivoCsv archivo a leer.
     * @return cantidad de usuarios agregados correctamente.
     * @throws IOException si ocurre un error de lectura.
     */
    public int cargarUsuariosDesdeCsv(File archivoCsv) throws IOException {
        if (archivoCsv == null) {
            throw new IllegalArgumentException("Debe seleccionar un archivo CSV");
        }

        int agregados = 0;
        try (BufferedReader lector = new BufferedReader(new FileReader(archivoCsv))) {
            String linea;
            int numeroLinea = 0;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) {
                    continue;
                }
                String[] partes = linea.split(",");
                if (partes.length < 2) {
                    continue;
                }
                String usuarioTexto = partes[0].trim();
                String tipoTexto = partes[1].trim();

                if (numeroLinea == 1 && "usuario".equalsIgnoreCase(usuarioTexto)) {
                    continue;
                }

                try {
                    agregarUsuario(usuarioTexto, TipoUsuario.desdeTexto(tipoTexto));
                    agregados++;
                } catch (IllegalArgumentException excepcion) {
                }
            }
        }
        return agregados;
    }

    /**
     * Elimina un usuario y sus documentos no encolados.
     * Los documentos ya en cola se mantienen.
     *
     * @param identificador usuario a eliminar.
     */
    public void eliminarUsuario(String identificador) {
        Usuario usuario = obtenerUsuario(identificador);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        ListaEnlazada<Documento> documentos = usuario.obtenerDocumentos();
        ListaEnlazada<Documento> conservar = new ListaEnlazada<>();
        for (Documento documento : documentos) {
            if (documento.estaEnCola()) {
                conservar.agregar(documento);
            }
        }
        documentos.limpiar();
        for (Documento documento : conservar) {
            documentos.agregar(documento);
        }
        usuarios.eliminar(usuario.obtenerIdentificador().toLowerCase());
    }

    /**
     * Crea un documento para un usuario.
     */
    public void crearDocumento(String usuarioId, String nombreDocumento, int tamanoPaginas, String tipoDocumento) {
        Usuario usuario = exigirUsuario(usuarioId);
        if (usuario.buscarDocumentoPorNombre(nombreDocumento) != null) {
            throw new IllegalArgumentException("Ya existe un documento con ese nombre para el usuario");
        }
        usuario.obtenerDocumentos().agregar(new Documento(nombreDocumento, tamanoPaginas, tipoDocumento));
    }

    /**
     * Elimina un documento que todavía no fue enviado a cola.
     */
    public void eliminarDocumentoCreado(String usuarioId, String nombreDocumento) {
        Usuario usuario = exigirUsuario(usuarioId);
        Documento documento = exigirDocumento(usuario, nombreDocumento);
        if (documento.estaEnCola()) {
            throw new IllegalArgumentException("No se puede eliminar un documento que ya está en cola");
        }
        usuario.obtenerDocumentos().eliminarPrimeroIgual(documento);
    }

    /**
     * Envía un documento a la cola de impresión.
     *
     * @param usuarioId usuario dueño del documento.
     * @param nombreDocumento documento a encolar.
     * @param prioritario si se aplica prioridad especial.
     * @return registro creado y encolado.
     */
    public RegistroImpresion enviarDocumentoACola(String usuarioId, String nombreDocumento, boolean prioritario) {
        Usuario usuario = exigirUsuario(usuarioId);
        Documento documento = exigirDocumento(usuario, nombreDocumento);
        if (documento.estaEnCola()) {
            throw new IllegalArgumentException("Ese documento ya fue enviado a la cola");
        }

        long tiempo = reloj.tiempoTranscurridoMillis();
        if (prioritario) {
            tiempo -= usuario.obtenerTipoUsuario().obtenerBonificacionPrioridad();
        }

        RegistroImpresion registro = new RegistroImpresion(
                ++secuenciaRegistros,
                documento.obtenerNombre(),
                documento.obtenerTamanoPaginas(),
                documento.obtenerTipo(),
                tiempo,
                prioritario
        );

        colaImpresion.insertar(registro);
        documento.marcarEnCola(true);
        registrarDocumentoEnHashUsuario(usuario.obtenerIdentificador(), registro, documento);
        return registro;
    }

    /**
     * Libera la impresora, equivale a eliminar mínimo del montículo.
     *
     * @return registro impreso o null si la cola está vacía.
     */
    public RegistroImpresion liberarImpresora() {
        RegistroImpresion impreso = colaImpresion.eliminarMinimo();
        if (impreso == null) {
            return null;
        }

        RegistroUsuarioCola registroUsuario = registroColaPorId.obtener(impreso.obtenerIdRegistro());
        if (registroUsuario != null) {
            registroUsuario.marcarActivo(false);
            registroUsuario.actualizarEtiquetaTiempo(impreso.obtenerEtiquetaTiempo());
        }

        Documento documento = documentoPorIdRegistro.eliminar(impreso.obtenerIdRegistro());
        if (documento != null) {
            documento.marcarEnCola(false);
            documento.marcarImpreso(true);
        }
        return impreso;
    }

    /**
     * Elimina un documento en cola usando usuario + nombre.
     * Se aplica el mecanismo de prioridad extrema y luego eliminar mínimo.
     *
     * @return true si se eliminó correctamente.
     */
    public boolean eliminarDocumentoEnColaPorUsuario(String usuarioId, String nombreDocumento) {
        ListaEnlazada<RegistroUsuarioCola> lista = registrosColaPorUsuario.obtener(usuarioId.trim().toLowerCase());
        if (lista == null) {
            return false;
        }

        for (RegistroUsuarioCola registroUsuario : lista) {
            if (registroUsuario.estaActivo()
                    && registroUsuario.obtenerNombreDocumento().equalsIgnoreCase(nombreDocumento.trim())) {
                boolean eliminado = colaImpresion.forzarPrioridadMaximaYEliminar(registroUsuario.obtenerIdRegistro());
                if (eliminado) {
                    registroUsuario.marcarActivo(false);
                    Documento documento = documentoPorIdRegistro.eliminar(registroUsuario.obtenerIdRegistro());
                    if (documento != null) {
                        documento.marcarEnCola(false);
                    }
                }
                return eliminado;
            }
        }
        return false;
    }

    /**
     * Busca un usuario por su identificador.
     *
     * @param identificador nombre de usuario.
     * @return usuario o null si no existe.
     */
    public Usuario obtenerUsuario(String identificador) {
        if (identificador == null) {
            return null;
        }
        return usuarios.obtener(identificador.trim().toLowerCase());
    }

    /**
     * @return lista con todos los usuarios actuales.
     */
    public ListaEnlazada<Usuario> obtenerUsuarios() {
        ListaEnlazada<Usuario> lista = new ListaEnlazada<>();
        for (String clave : usuarios.claves()) {
            Usuario usuario = usuarios.obtener(clave);
            if (usuario != null) {
                lista.agregar(usuario);
            }
        }
        return lista;
    }

    /**
     * @return usuarios que tienen al menos un documento activo en cola.
     */
    public ListaEnlazada<String> obtenerUsuariosConRegistrosEnCola() {
        ListaEnlazada<String> respuesta = new ListaEnlazada<>();
        for (String usuario : registrosColaPorUsuario.claves()) {
            ListaEnlazada<RegistroUsuarioCola> lista = registrosColaPorUsuario.obtener(usuario);
            boolean tieneActivos = false;
            if (lista != null) {
                for (RegistroUsuarioCola registro : lista) {
                    if (registro.estaActivo()) {
                        tieneActivos = true;
                        break;
                    }
                }
            }
            if (tieneActivos) {
                respuesta.agregar(usuario);
            }
        }
        return respuesta;
    }

    /**
     * @param usuarioId usuario consultado.
     * @return registros activos en cola para ese usuario.
     */
    public ListaEnlazada<RegistroUsuarioCola> obtenerRegistrosActivosPorUsuario(String usuarioId) {
        ListaEnlazada<RegistroUsuarioCola> activos = new ListaEnlazada<>();
        ListaEnlazada<RegistroUsuarioCola> registros = registrosColaPorUsuario.obtener(usuarioId.trim().toLowerCase());
        if (registros == null) {
            return activos;
        }
        for (RegistroUsuarioCola registro : registros) {
            if (registro.estaActivo()) {
                activos.agregar(registro);
            }
        }
        return activos;
    }

    /**
     * @return copia lineal del contenido del montículo.
     */
    public RegistroImpresion[] obtenerColaLineal() {
        return colaImpresion.copiaLineal();
    }

    /**
     * @return tamaño actual de la cola de impresión.
     */
    public int tamanoCola() {
        return colaImpresion.tamano();
    }

    private void registrarDocumentoEnHashUsuario(String usuarioId, RegistroImpresion registro, Documento documento) {
        String clave = usuarioId.trim().toLowerCase();
        ListaEnlazada<RegistroUsuarioCola> lista = registrosColaPorUsuario.obtener(clave);
        if (lista == null) {
            lista = new ListaEnlazada<>();
            registrosColaPorUsuario.poner(clave, lista);
        }

        RegistroUsuarioCola registroUsuarioCola = new RegistroUsuarioCola(
                registro.obtenerIdRegistro(),
                registro.obtenerNombreDocumento(),
                registro.obtenerTamanoPaginas(),
                registro.obtenerTipoDocumento(),
                registro.obtenerEtiquetaTiempo(),
                registro.esPrioritario()
        );

        lista.agregar(registroUsuarioCola);
        registroColaPorId.poner(registro.obtenerIdRegistro(), registroUsuarioCola);
        documentoPorIdRegistro.poner(registro.obtenerIdRegistro(), documento);
    }

    private void validarIdentificador(String identificador) {
        if (identificador == null || identificador.isBlank()) {
            throw new IllegalArgumentException("El identificador de usuario es obligatorio");
        }
    }

    private Usuario exigirUsuario(String usuarioId) {
        Usuario usuario = obtenerUsuario(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        return usuario;
    }

    private Documento exigirDocumento(Usuario usuario, String nombreDocumento) {
        Documento documento = usuario.buscarDocumentoPorNombre(nombreDocumento);
        if (documento == null) {
            throw new IllegalArgumentException("El documento no existe para el usuario");
        }
        return documento;
    }
}
