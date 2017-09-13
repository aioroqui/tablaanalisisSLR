package Arbol;

import java.util.*;
import Simbolos.Noterminal;
import Simbolos.Simbolo;

/**
 * Clase que represena un árbol demostrando una derivación de alguna cadena en
 * la grmática  Cada nodo del árbol, alberga un símbolo
 * (terminal o no terminal) junto con una lista de los nodos hijo.
 */
public class ArbolAnalisis implements Iterable<ArbolAnalisis> {

    /**
     * El símbolo de la gramática que será representado por este nodo del árbol.
     */
    private final Simbolo simbolo;

    /* Los hijo de los nodos de este arbol, en el orden en el que aparecen. */
    private final List<ArbolAnalisis> hijo;

    /**
     * Construye el árbol dados el símbolo y el hijo
     *
     * @param simbolo
     * @param hijo
     */
    public ArbolAnalisis(Simbolo simbolo, List<ArbolAnalisis> hijo) {
        if (simbolo == null || hijo == null) {
            throw new NullPointerException();
        }

        this.simbolo = simbolo;
        this.hijo = hijo;
    }

    /*Cosntructor con solo el símbolo.*/
    public ArbolAnalisis(Simbolo simbolo) {
        this(simbolo, new ArrayList<ArbolAnalisis>());
    }

    /* Devuelve el símbolo contenido en el nodo.*/
    public Simbolo getSimbolo() {
        return simbolo;
    }

    /* Retorna el hijo. */
    public List<ArbolAnalisis> getHijo() {
        return hijo;
    }

    /* Retorna un iterator para incluir el hijo al árbol. */
    @Override
    public Iterator<ArbolAnalisis> iterator() {
        return getHijo().iterator();
    }

    @Override
    public String toString() {
        StringBuilder cadena = new StringBuilder();
        cadena.append(simbolo);
        if (simbolo instanceof Noterminal) {
            cadena.append(" -> ");
            cadena.append(getHijo());
        }
        return cadena.toString();
    }
}
