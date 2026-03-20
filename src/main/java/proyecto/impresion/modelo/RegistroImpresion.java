package proyecto.impresion.modelo;

/**
 * Registro que realmente entra al montículo de impresión.
 * No guarda propietario, solo datos del documento y prioridad temporal.
 */
public class RegistroImpresion {
    private final int idRegistro;
    private final String nombreDocumento;
    private final int tamanoPaginas;
    private final String tipoDocumento;
    private long etiquetaTiempo;
    private final boolean prioritario;

    /**
     * Crea el registro que será encolado.
     *
     * @param idRegistro identificador interno del registro.
     * @param nombreDocumento nombre del documento.
     * @param tamanoPaginas tamaño en páginas.
     * @param tipoDocumento tipo del documento.
     * @param etiquetaTiempo valor de prioridad temporal.
     * @param prioritario indica si se encoló con prioridad.
     */
    public RegistroImpresion(
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
    }

    /**
     * @return identificador del registro.
     */
    public int obtenerIdRegistro() {
        return idRegistro;
    }

    /**
     * @return nombre del documento.
     */
    public String obtenerNombreDocumento() {
        return nombreDocumento;
    }

    /**
     * @return tamaño en páginas.
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
     * @return etiqueta temporal usada por el montículo.
     */
    public long obtenerEtiquetaTiempo() {
        return etiquetaTiempo;
    }

    /**
     * Actualiza la etiqueta temporal de este registro.
     *
     * @param etiquetaTiempo nueva etiqueta.
     */
    public void cambiarEtiquetaTiempo(long etiquetaTiempo) {
        this.etiquetaTiempo = etiquetaTiempo;
    }

    /**
     * @return true si este registro fue enviado como prioritario.
     */
    public boolean esPrioritario() {
        return prioritario;
    }

    @Override
    public String toString() {
        String marca = prioritario ? "PRIORITARIO" : "NORMAL";
        return "[id=" + idRegistro + "] " + nombreDocumento + " | " + tamanoPaginas + " pág. | "
                + tipoDocumento + " | t=" + etiquetaTiempo + " | " + marca;
    }
}
