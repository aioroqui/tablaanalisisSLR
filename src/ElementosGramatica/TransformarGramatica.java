package ElementosGramatica;

import Simbolos.*;
import java.util.*;

/* Para aplicar las transformaciones automáticas de las gramáticas.*/
public final class TransformarGramatica {

    /* No necesitará ser instanciada. */
    private TransformarGramatica() {  }

    /* Creación de la gramática aumentada.*/
    public static Gramatica gramaticaAumentada(Gramatica g) {
        /* Se buscará un noterminal que no esté actualmente inciando la gramática.
	 * Para ello, obtenemos todos los nombres que están siendo usados y les
         * añadimos un marcador al final en orden alfabético.
         */
        SortedSet<String> nombres = new TreeSet<String>();
        for (Noterminal nt : g.getNoterminales()) {
            nombres.add(nt.getNombre());
        }

        /* Creamos un no terminal. */
        Noterminal inicial = new Noterminal("S'");

        /* Obtenemos la lista con las prodcciones actuales y las introducimos. */
        List<Produccion> producciones = new ArrayList<Produccion>(g.getProducciones());
        producciones.add(new Produccion(inicial, Arrays.asList(new Simbolo[]{g.getSimboloInicial()}), 0));
        

        /* Cosntruimos la nueva gramática. */
        return new Gramatica(producciones, inicial);
    }
}
