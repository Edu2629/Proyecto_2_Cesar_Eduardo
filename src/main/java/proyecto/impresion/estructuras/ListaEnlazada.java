package proyecto.impresion.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implementación simple de lista enlazada genérica.
 * Se utiliza como estructura auxiliar en varias partes del proyecto.
 *
 * @param <T> tipo de dato almacenado.
 */
public class ListaEnlazada<T> implements Iterable<T> {
    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamano;

    /**
     * Agrega un elemento al final de la lista.
     *
     * @param elemento valor a agregar.
     */
    public void agregar(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            cola = nuevo;
        }
        tamano++;
    }

    /**
     * Elimina la primera ocurrencia de un elemento.
     *
     * @param elemento elemento a buscar y eliminar.
     * @return true si se eliminó, false si no se encontró.
     */
    public boolean eliminarPrimeroIgual(T elemento) {
        Nodo<T> anterior = null;
        Nodo<T> actual = cabeza;
        while (actual != null) {
            boolean coincide = (actual.valor == null && elemento == null)
                    || (actual.valor != null && actual.valor.equals(elemento));
            if (coincide) {
                if (anterior == null) {
                    cabeza = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }
                if (actual == cola) {
                    cola = anterior;
                }
                tamano--;
                return true;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return false;
    }

    /**
     * Obtiene el elemento en una posición específica.
     *
     * @param indice posición a consultar.
     * @return elemento en ese índice.
     */
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException("Índice inválido: " + indice);
        }
        Nodo<T> actual = cabeza;
        int contador = 0;
        while (actual != null) {
            if (contador == indice) {
                return actual.valor;
            }
            actual = actual.siguiente;
            contador++;
        }
        throw new IndexOutOfBoundsException("Índice inválido: " + indice);
    }

    /**
     * @return cantidad de elementos en la lista.
     */
    public int tamano() {
        return tamano;
    }

    /**
     * @return true si no hay elementos.
     */
    public boolean estaVacia() {
        return tamano == 0;
    }

    /**
     * Elimina todos los elementos de la lista.
     */
    public void limpiar() {
        cabeza = null;
        cola = null;
        tamano = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Nodo<T> actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (actual == null) {
                    throw new NoSuchElementException();
                }
                T valor = actual.valor;
                actual = actual.siguiente;
                return valor;
            }
        };
    }

    private static class Nodo<T> {
        private final T valor;
        private Nodo<T> siguiente;

        private Nodo(T valor) {
            this.valor = valor;
        }
    }
}
