package proyecto.impresion.modelo;

import proyecto.impresion.estructuras.ListaEnlazada;

/**
 * Representa un usuario dentro de la simulación.
 * Cada usuario tiene un identificador, un tipo de prioridad y sus documentos.
 */
public class Usuario {
    private final String identificador;
    private final TipoUsuario tipoUsuario;
    private final ListaEnlazada<Documento> documentos;

    /**
     * Crea un usuario con su tipo de prioridad.
     */
    public Usuario(String identificador, TipoUsuario tipoUsuario) {
        if (identificador == null || identificador.isBlank()) {
            throw new IllegalArgumentException("El identificador de usuario es obligatorio");
        }
        if (tipoUsuario == null) {
            throw new IllegalArgumentException("El tipo de usuario es obligatorio");
        }
        this.identificador = identificador.trim();
        this.tipoUsuario = tipoUsuario;
        this.documentos = new ListaEnlazada<>();
    }

    /**
     * @return identificador del usuario.
     */
    public String obtenerIdentificador() {
        return identificador;
    }

    /**
     * @return tipo de usuario.
     */
    public TipoUsuario obtenerTipoUsuario() {
        return tipoUsuario;
    }

    /**
     * @return lista de documentos del usuario.
     */
    public ListaEnlazada<Documento> obtenerDocumentos() {
        return documentos;
    }

    /**
     * Busca un documento por su nombre dentro de este usuario.
     *
     * @param nombreDocumento nombre a buscar.
     * @return documento encontrado o null si no existe.
     */
    public Documento buscarDocumentoPorNombre(String nombreDocumento) {
        if (nombreDocumento == null) {
            return null;
        }
        for (Documento documento : documentos) {
            if (documento.obtenerNombre().equalsIgnoreCase(nombreDocumento.trim())) {
                return documento;
            }
        }
        return null;
    }
}
