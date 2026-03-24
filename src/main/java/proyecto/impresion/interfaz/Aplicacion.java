package proyecto.impresion.interfaz;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada de la aplicación.
 */
public class Aplicacion {
    /**
     * Inicia la interfaz principal en el hilo gráfico de Swing.
     *
     * @param args argumentos de consola (no usados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
            ventanaPrincipal.setVisible(true);
        });
    }
}
