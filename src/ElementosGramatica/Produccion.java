package ElementosGramatica;

import Simbolos.*;
import java.util.*;

/*
 * La clase representa una producción, 
 * La producción se compone por un elemente no terminal seguidado de una secuencia
 * de símbolos terminales y no terminalesThe produccion expands out a
 */
public final class Produccion implements Iterable<Simbolo> {

    /* internanmente, una producción es un par
     * formador por un símbolo no terminal seguido de una
     * secuencia de elementos terminales
     */
    private final Noterminal nt;
    private final List<Simbolo> produccion;
    private final int num_produccion;

    public Produccion(Noterminal nt, List<Simbolo> produccion, int num_produccion) {

        if (nt == null || produccion == null) {
            throw new NullPointerException("Argumentos no pueden ser nulos.");
        }

        this.nt = nt;
        this.produccion = produccion;
        this.num_produccion = num_produccion;
    }
    
   

    public Noterminal getNoterminal() {
        return nt;
    }

    public List<Simbolo> getProduccion() {
        return Collections.unmodifiableList(produccion);
    }

    public Iterator<Simbolo> iterator() {
        return getProduccion().iterator();
    }
    
    public int getNumProduccion(){
        return num_produccion;
    }
    
    public Simbolo siguienteNoterminal() {
        Simbolo siguiente = null;

        for (int i = 0; i < produccion.size(); i++) {
            if (produccion.get(i).getNombre().equals(nt.getNombre())) {
                siguiente = produccion.get(i + 1);
            }
        }

        return siguiente;
    }

    @Override
    public String toString() {
        return num_produccion + "." + nt + " -> " + produccion;
    }
}
