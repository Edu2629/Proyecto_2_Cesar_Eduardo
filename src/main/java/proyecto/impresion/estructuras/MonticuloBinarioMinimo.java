package proyecto.impresion.estructuras;

import proyecto.impresion.modelo.RegistroImpresion;

/**
 * Implementación propia de un montículo binario mínimo.
 * Se usa como cola de prioridad para la impresión.
 */
public class MonticuloBinarioMinimo {
    private RegistroImpresion[] datos;
    private int tamano;
    private final TablaHash<Integer, Integer> indicePorIdRegistro;

    /**
     * Crea el montículo con una capacidad inicial dada.
     *
     * @param capacidadInicial tamaño inicial del arreglo interno.
     */
    public MonticuloBinarioMinimo(int capacidadInicial) {
        this.datos = new RegistroImpresion[Math.max(16, capacidadInicial)];
        this.indicePorIdRegistro = new TablaHash<>();
    }

    /**
     * Crea el montículo con capacidad por defecto.
     */
    public MonticuloBinarioMinimo() {
        this(32);
    }

    /**
     * Inserta un registro en la cola de prioridad.
     *
     * @param registro elemento a insertar.
     */
    public void insertar(RegistroImpresion registro) {
        asegurarCapacidad();
        datos[tamano] = registro;
        indicePorIdRegistro.poner(registro.obtenerIdRegistro(), tamano);
        flotar(tamano);
        tamano++;
    }

    /**
     * Elimina y retorna el registro con menor etiqueta de tiempo.
     *
     * @return mínimo actual o null si está vacío.
     */
    public RegistroImpresion eliminarMinimo() {
        if (tamano == 0) {
            return null;
        }
        RegistroImpresion minimo = datos[0];
        indicePorIdRegistro.eliminar(minimo.obtenerIdRegistro());
        tamano--;

        if (tamano > 0) {
            datos[0] = datos[tamano];
            datos[tamano] = null;
            indicePorIdRegistro.poner(datos[0].obtenerIdRegistro(), 0);
            hundir(0);
        } else {
            datos[0] = null;
        }
        return minimo;
    }

    /**
     * @return elemento mínimo sin eliminarlo.
     */
    public RegistroImpresion obtenerMinimo() {
        return tamano == 0 ? null : datos[0];
    }

    /**
     * @return cantidad de elementos en el montículo.
     */
    public int tamano() {
        return tamano;
    }

    /**
     * @return true si el montículo no tiene elementos.
     */
    public boolean estaVacio() {
        return tamano == 0;
    }

    /**
     * Devuelve una copia lineal del montículo para visualización.
     *
     * @return arreglo con el contenido actual.
     */
    public RegistroImpresion[] copiaLineal() {
        RegistroImpresion[] copia = new RegistroImpresion[tamano];
        for (int i = 0; i < tamano; i++) {
            copia[i] = datos[i];
        }
        return copia;
    }

    /**
     * Busca un registro por id usando índice auxiliar.
     *
     * @param idRegistro id interno.
     * @return registro o null si no existe.
     */
    public RegistroImpresion buscarPorId(int idRegistro) {
        Integer indice = indicePorIdRegistro.obtener(idRegistro);
        if (indice == null || indice < 0 || indice >= tamano) {
            return null;
        }
        return datos[indice];
    }

    /**
     * Elimina un registro simulando la operación sugerida por el enunciado:
     * poner etiqueta mínima, flotar y eliminar mínimo.
     *
     * @param idRegistro id del registro a eliminar.
     * @return true si se pudo eliminar.
     */
    public boolean forzarPrioridadMaximaYEliminar(int idRegistro) {
        Integer indice = indicePorIdRegistro.obtener(idRegistro);
        if (indice == null) {
            return false;
        }
        datos[indice].cambiarEtiquetaTiempo(Long.MIN_VALUE / 4);
        flotar(indice);
        eliminarMinimo();
        return true;
    }

    private void flotar(int indiceInicial) {
        int indice = indiceInicial;
        while (indice > 0) {
            int padre = (indice - 1) / 2;
            if (comparar(datos[indice], datos[padre]) >= 0) {
                break;
            }
            intercambiar(indice, padre);
            indice = padre;
        }
    }

    private void hundir(int indiceInicial) {
        int indice = indiceInicial;
        while (true) {
            int izquierdo = indice * 2 + 1;
            int derecho = indice * 2 + 2;
            int menor = indice;

            if (izquierdo < tamano && comparar(datos[izquierdo], datos[menor]) < 0) {
                menor = izquierdo;
            }
            if (derecho < tamano && comparar(datos[derecho], datos[menor]) < 0) {
                menor = derecho;
            }
            if (menor == indice) {
                break;
            }
            intercambiar(indice, menor);
            indice = menor;
        }
    }

    private int comparar(RegistroImpresion primero, RegistroImpresion segundo) {
        int comparacion = Long.compare(primero.obtenerEtiquetaTiempo(), segundo.obtenerEtiquetaTiempo());
        if (comparacion != 0) {
            return comparacion;
        }
        return Integer.compare(primero.obtenerIdRegistro(), segundo.obtenerIdRegistro());
    }

    private void intercambiar(int a, int b) {
        RegistroImpresion temporal = datos[a];
        datos[a] = datos[b];
        datos[b] = temporal;

        indicePorIdRegistro.poner(datos[a].obtenerIdRegistro(), a);
        indicePorIdRegistro.poner(datos[b].obtenerIdRegistro(), b);
    }

    private void asegurarCapacidad() {
        if (tamano < datos.length) {
            return;
        }
        RegistroImpresion[] nuevo = new RegistroImpresion[datos.length * 2];
        for (int i = 0; i < datos.length; i++) {
            nuevo[i] = datos[i];
        }
        datos = nuevo;
    }
}
