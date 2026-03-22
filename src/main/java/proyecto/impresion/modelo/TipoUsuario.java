package proyecto.impresion.modelo;

/**
 * Tipos de usuario admitidos en la simulación.
 * Cada tipo tiene una bonificación de prioridad para el envío de documentos.
 */
public enum TipoUsuario {
    PRIORIDAD_ALTA(2_000_000L, "prioridad_alta"),
    PRIORIDAD_MEDIA(1_000_000L, "prioridad_media"),
    PRIORIDAD_BAJA(300_000L, "prioridad_baja");

    private final long bonificacionPrioridad;
    private final String etiquetaCsv;

    TipoUsuario(long bonificacionPrioridad, String etiquetaCsv) {
        this.bonificacionPrioridad = bonificacionPrioridad;
        this.etiquetaCsv = etiquetaCsv;
    }

    /**
     * @return bonificación en milisegundos para prioridad.
     */
    public long obtenerBonificacionPrioridad() {
        return bonificacionPrioridad;
    }

    /**
     * @return texto esperado para CSV.
     */
    public String obtenerEtiquetaCsv() {
        return etiquetaCsv;
    }

    /**
     * Convierte el texto del CSV a un tipo de usuario válido.
     *
     * @param texto texto leído.
     * @return tipo de usuario correspondiente.
     */
    public static TipoUsuario desdeTexto(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("El tipo de usuario no puede ser nulo");
        }
        String normalizado = texto.trim().toLowerCase();
        for (TipoUsuario tipo : values()) {
            if (tipo.etiquetaCsv.equals(normalizado)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de usuario desconocido: " + texto);
    }
}
