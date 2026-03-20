package proyecto.impresion.estructuras;

/**
 * Implementación propia de tabla hash con encadenamiento.
 * Se usa para búsquedas rápidas por clave en la simulación.
 *
 * @param <K> tipo de clave.
 * @param <V> tipo de valor.
 */
public class TablaHash<K, V> {
    private static final double FACTOR_CARGA_MAXIMO = 0.75;
    private Entrada<K, V>[] cubetas;
    private int tamano;

    /**
     * Crea la tabla con una capacidad inicial.
     *
     * @param capacidadInicial número de cubetas inicial.
     */
    @SuppressWarnings("unchecked")
    public TablaHash(int capacidadInicial) {
        int capacidad = Math.max(8, capacidadInicial);
        this.cubetas = (Entrada<K, V>[]) new Entrada[capacidad];
    }

    /**
     * Crea la tabla con capacidad por defecto.
     */
    public TablaHash() {
        this(16);
    }

    /**
     * Inserta o reemplaza un valor asociado a una clave.
     *
     * @param clave clave de búsqueda.
     * @param valor valor a guardar.
     */
    public void poner(K clave, V valor) {
        if (clave == null) {
            throw new IllegalArgumentException("La clave no puede ser nula");
        }
        if ((double) (tamano + 1) / cubetas.length > FACTOR_CARGA_MAXIMO) {
            redimensionar();
        }

        int indice = indiceDe(clave, cubetas.length);
        Entrada<K, V> actual = cubetas[indice];
        while (actual != null) {
            if (actual.clave.equals(clave)) {
                actual.valor = valor;
                return;
            }
            actual = actual.siguiente;
        }

        Entrada<K, V> nueva = new Entrada<>(clave, valor);
        nueva.siguiente = cubetas[indice];
        cubetas[indice] = nueva;
        tamano++;
    }

    /**
     * Busca el valor asociado a una clave.
     *
     * @param clave clave a buscar.
     * @return valor o null si no existe.
     */
    public V obtener(K clave) {
        if (clave == null) {
            return null;
        }
        int indice = indiceDe(clave, cubetas.length);
        Entrada<K, V> actual = cubetas[indice];
        while (actual != null) {
            if (actual.clave.equals(clave)) {
                return actual.valor;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    /**
     * Verifica si la clave tiene un valor asociado.
     *
     * @param clave clave a comprobar.
     * @return true si existe un valor para esa clave.
     */
    public boolean contieneClave(K clave) {
        return obtener(clave) != null;
    }

    /**
     * Elimina el valor asociado a una clave.
     *
     * @param clave clave a eliminar.
     * @return valor eliminado o null si no existía.
     */
    public V eliminar(K clave) {
        if (clave == null) {
            return null;
        }
        int indice = indiceDe(clave, cubetas.length);
        Entrada<K, V> anterior = null;
        Entrada<K, V> actual = cubetas[indice];

        while (actual != null) {
            if (actual.clave.equals(clave)) {
                if (anterior == null) {
                    cubetas[indice] = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }
                tamano--;
                return actual.valor;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return null;
    }

    /**
     * @return número de pares clave-valor guardados.
     */
    public int tamano() {
        return tamano;
    }

    /**
     * Devuelve una lista con todas las claves actuales.
     *
     * @return lista de claves.
     */
    public ListaEnlazada<K> claves() {
        ListaEnlazada<K> lista = new ListaEnlazada<>();
        for (Entrada<K, V> cubeta : cubetas) {
            Entrada<K, V> actual = cubeta;
            while (actual != null) {
                lista.agregar(actual.clave);
                actual = actual.siguiente;
            }
        }
        return lista;
    }

    private int indiceDe(K clave, int longitud) {
        int hash = clave.hashCode();
        hash ^= (hash >>> 16);
        return Math.abs(hash) % longitud;
    }

    @SuppressWarnings("unchecked")
    private void redimensionar() {
        Entrada<K, V>[] antiguas = cubetas;
        cubetas = (Entrada<K, V>[]) new Entrada[antiguas.length * 2];
        tamano = 0;

        for (Entrada<K, V> cubeta : antiguas) {
            Entrada<K, V> actual = cubeta;
            while (actual != null) {
                poner(actual.clave, actual.valor);
                actual = actual.siguiente;
            }
        }
    }

    private static class Entrada<K, V> {
        private final K clave;
        private V valor;
        private Entrada<K, V> siguiente;

        private Entrada(K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
        }
    }
}
