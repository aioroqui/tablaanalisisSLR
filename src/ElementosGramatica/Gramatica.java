package ElementosGramatica;

import Simbolos.*;
import java.util.*;

/* La clase que reprensenta una gramática libre de contexto.
 * Esta gramática se recibe del fichero de entrada.
 */
public final class Gramatica implements Iterable<Produccion> {

    /* Las producciones que contienen la gramáica. */
    private final Collection<Produccion> producciones;

    /* Elemntos no terminales de la gramática. */
    private final Set<Noterminal> noterminales = new HashSet<Noterminal>();

    /* Elemetnos terminales de la gramática.*/
    private final Set<Terminal> terminales = new HashSet<Terminal>();

    /* El símbolo inicial.*/
    private final Noterminal inicial;

    /* Tipo Map para asociar los no terminales con sus producciones. */
    private final Map<Noterminal, Collection<Produccion>> produccionesPara;

    public Gramatica(Collection<Produccion> producciones, Noterminal inicial) {
        if (producciones == null || inicial == null) {
            throw new NullPointerException();
        }

        this.producciones = producciones;
        this.inicial = inicial;

        /* Encontrar los elementos terminales y no terminales. */
        for (Produccion p : producciones) {
            noterminales.add(p.getNoterminal());
            for (Simbolo s : p.getProduccion()) {
                if (s instanceof Noterminal) {
                    noterminales.add((Noterminal) s);
                } else // s instanceof Terminal
                {
                    terminales.add((Terminal) s);
                }
            }
        }

        /* El símbolo inicial es un no terminal de la gramática*/
        noterminales.add(inicial);

        /* Construir mapa noterminales a producciones. */
        produccionesPara = crearProduccion();
    }

    /*
     * Construye el par asociando cada noterminal cion su conjunto de
     * producciones producciones it yields. This makes it easier to determine
     * what possible
     */
    private Map<Noterminal, Collection<Produccion>> crearProduccion() {

        Map<Noterminal, Collection<Produccion>> resultado
                = new HashMap<Noterminal, Collection<Produccion>>();

        /* Crear nuevo conjunto para cada no terminal. */
        for (Noterminal nt : getNoterminales()) {
            resultado.put(nt, new HashSet<Produccion>());
        }

        /* Escanear en la gramática en busca de estos conjuntos. */
        for (Produccion prod : this) {
            resultado.get(prod.getNoterminal()).add(prod);
        }

        /* Actualizamos el conjunto */
        for (Noterminal nt : getNoterminales()) {
            resultado.put(nt, Collections.unmodifiableCollection(resultado.get(nt)));
        }

        return resultado;
    }

    /* Devuelve el símbolo incial de la gramática.*/
    public Noterminal getSimboloInicial() {
        return inicial;
    }

    /*Retorna las producciones de la gramática*/
    public Collection<Produccion> getProducciones() {
        return Collections.unmodifiableCollection(producciones);
    }

    @Override
    public Iterator<Produccion> iterator() {
        return getProducciones().iterator();
    }

    public Collection<Terminal> getTerminales() {
        return Collections.unmodifiableCollection(terminales);
    }

    public Collection<Noterminal> getNoterminales() {
        return Collections.unmodifiableCollection(noterminales);
    }

    public Collection<Produccion> getProduccionesPara(Noterminal nt) {
        Collection<Produccion> resultado = produccionesPara.get(nt);
        if (resultado == null) {
            throw new IllegalArgumentException("Símbolo Noterminal " + nt + " no encontrado.");
        }
        return resultado;
    }

    @Override
    public String toString() {
        StringBuilder cadena = new StringBuilder();
        for (Produccion p : this) {
            cadena.append(p.toString());
            cadena.append("\n");
        }
        return cadena.toString();
    }
}
