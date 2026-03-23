package proyecto.impresion.servicio;

/**
 * Reloj simple de la simulación.
 * Cuenta tiempo desde que se crea el objeto.
 */
public class RelojSimulacion {
    private final long inicioMillis;

    /**
     * Inicia el reloj tomando el instante actual.
     */
    public RelojSimulacion() {
        this.inicioMillis = System.currentTimeMillis();
    }

    /**
     * @return milisegundos transcurridos desde el inicio.
     */
    public long tiempoTranscurridoMillis() {
        return System.currentTimeMillis() - inicioMillis;
    }
}
