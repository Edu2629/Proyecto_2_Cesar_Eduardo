package proyecto.impresion.modelo;

/**
 * Registro auxiliar guardado en la tabla hash por usuario.
 * Sirve para ubicar y gestionar documentos en cola sin recorrer el montículo.
 */
public class RegistroUsuarioCola {
    private final int idRegistro;
    private final String nombreDocumento;
    private final int tamanoPaginas;
    private final String tipoDocumento;
    private final boolean prioritario;
    private long etiquetaTiempo;
    private boolean activo;

    /**
     * Crea el registro de seguimiento de un documento en cola.
     */
    public RegistroUsuarioCola(
            int idRegistro,
            String nombreDocumento,
            int tamanoPaginas,
            String tipoDocumento,
            long etiquetaTiempo,
            boolean prioritario
    ) {
        this.idRegistro = idRegistro;
        this.nombreDocumento = nombreDocumento;
        this.tamanoPaginas = tamanoPaginas;
        this.tipoDocumento = tipoDocumento;
        this.etiquetaTiempo = etiquetaTiempo;
        this.prioritario = prioritario;
        this.activo = true;
    }

    /**
     * @return id interno del registro.
     */
    public int obtenerIdRegistro() {
        return idRegistro;
    }

    /**
     * @return nombre del documento asociado.
     */
    public String obtenerNombreDocumento() {
        return nombreDocumento;
    }

    /**
     * @return tamaño del documento en páginas.
     */
    public int obtenerTamanoPaginas() {
        return tamanoPaginas;
    }

    /**
     * @return tipo del documento.
     */
    public String obtenerTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * @return true si el documento fue marcado como prioritario al encolar.
     */
    public boolean esPrioritario() {
        return prioritario;
    }

    /**
     * @return etiqueta de tiempo usada para prioridad.
     */
    public long obtenerEtiquetaTiempo() {
        return etiquetaTiempo;
    }

    /**
     * Actualiza la etiqueta temporal del registro.
     *
     * @param etiquetaTiempo nuevo valor.
     */
    public void actualizarEtiquetaTiempo(long etiquetaTiempo) {
        this.etiquetaTiempo = etiquetaTiempo;
    }

    /**
     * @return true si este registro sigue activo en cola.
     */
    public boolean estaActivo() {
        return activo;
    }

    /**
     * Cambia el estado activo/inactivo del registro.
     *
     * @param activo nuevo estado.
     */
    public void marcarActivo(boolean activo) {
        this.activo = activo;
    }
}
