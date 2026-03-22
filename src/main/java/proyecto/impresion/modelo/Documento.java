package proyecto.impresion.modelo;

/**
 * Representa un documento creado por un usuario.
 * Aquí se guarda su información básica y su estado dentro de la simulación.
 */
public class Documento {
    private final String nombre;
    private final int tamanoPaginas;
    private final String tipo;
    private boolean enCola;
    private boolean impreso;

    /**
     * Crea un documento nuevo con sus datos principales.
     *
     * @param nombre nombre del documento.
     * @param tamanoPaginas cantidad de páginas.
     * @param tipo tipo o extensión del documento.
     */
    public Documento(String nombre, int tamanoPaginas, String tipo) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del documento es obligatorio");
        }
        if (tamanoPaginas <= 0) {
            throw new IllegalArgumentException("El tamaño debe ser mayor que cero");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo del documento es obligatorio");
        }
        this.nombre = nombre.trim();
        this.tamanoPaginas = tamanoPaginas;
        this.tipo = tipo.trim();
    }

    /**
     * @return nombre del documento.
     */
    public String obtenerNombre() {
        return nombre;
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
    public String obtenerTipo() {
        return tipo;
    }

    /**
     * @return true si el documento está actualmente en cola.
     */
    public boolean estaEnCola() {
        return enCola;
    }

    /**
     * Cambia el estado de cola del documento.
     *
     * @param enCola nuevo estado.
     */
    public void marcarEnCola(boolean enCola) {
        this.enCola = enCola;
    }

    /**
     * @return true si el documento ya fue impreso.
     */
    public boolean estaImpreso() {
        return impreso;
    }

    /**
     * Marca si el documento ya fue impreso.
     *
     * @param impreso nuevo estado de impresión.
     */
    public void marcarImpreso(boolean impreso) {
        this.impreso = impreso;
    }

    @Override
    public String toString() {
        String estado = impreso ? "IMPRESO" : (enCola ? "EN_COLA" : "CREADO");
        return nombre + " | " + tamanoPaginas + " pág. | " + tipo + " | " + estado;
    }
}
