package ElementosGramatica;

import Simbolos.*;
import java.util.*;

/*
 * La clase representa un elemento de LR
 * El primer concepto que debemos conocer es el de elemento.
 * un elemento es una producción con un punto en cierta posición, 
 * esta posición le indica al parser que parte de la producción se 
 * está analizando, ejemplo: la producción A –> XYZ tendrá los siguientes elementos: 
 */
public final class ElementoLR {

    /* La produccion asociada con ese item. */
    private final Produccion produccion;

    /*
     * El indice en la producción, especifacado como el símbolo que tenemos 
     * justo delante
     */
    private final int index;

    /* El simbolo adelanto. */
    private final SimboloGeneralizado simbadelanto;

    /*
     * Constructs a new LR item given the specified produccion, index, and
     * simbadelanto.
     * parámtero: produccion la produccion que estamos procesando.
     * parámtero: index La marca en la producción
     * parámtero: simbadelanto El simbadelanto para esta producción
     */
    public ElementoLR(Produccion produccion, int index, SimboloGeneralizado simbadelanto) {
        if (produccion == null) {
            throw new NullPointerException();
        }
        if (index < 0 || index > produccion.getProduccion().size()) {
            throw new IndexOutOfBoundsException();
        }

        this.produccion = produccion;
        this.index = index;
        this.simbadelanto = simbadelanto;
    }

    /* Index devuelto dentro de la producción.*/
    public int getIndex() {
        return index;
    }

    /* Devuelve la producción  asociada con el elemneto LR.*/
    public Produccion getProduccion() {
        return produccion;
    }

    /* Devuelve el simbadelanto asociado al item.*/
    public SimboloGeneralizado getSimboloAdelanto() {
        return simbadelanto;
    }

    /* Devuelve el símbolo inmediamtemente despuñés de la marca 
     * o nulo si no está definido
     */
    public Simbolo getSimboloTrasPunto() {
        return esCompleta() ? null : produccion.getProduccion().get(index);
    }

    /*
     * Devuelve un elemento nuevo correpondiente al elemento formado por 
     * el movieminto durante el paso. si el punto está ya al final, 
     * esto genera una excepción
     */
    public ElementoLR conPuntoHaciaAdelante() {
        /* si no podemos adelantar el punto, lanzamos un mensaje. */
        if (esCompleta()) {
            throw new RuntimeException("Punto en el final de la producción." + this);
        }

        return new ElementoLR(produccion, index + 1, simbadelanto);
    }

    /* Devuelve si el punto está al final de la producción.*/
    public boolean esCompleta() {
        return index == produccion.getProduccion().size();
    }
    
    public List<Simbolo> simbolosProduccion(){
        return  produccion.getProduccion();
    }

    /* Devuelve si ElementoLR es igual a other objeto o*/
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ElementoLR)) {
            return false;
        }

        ElementoLR other = (ElementoLR) o;
        if (index != other.index
                || !produccion.equals(other.produccion)) {
            return false;
        }

        /* Because lookaheads can be null, we have to be more careful with how
         * we check if they're equal.
         */
        if ((simbadelanto == null) != (other.simbadelanto == null)) {
            return false;
        }
        if (simbadelanto == null) {
            return true;
        }
        return simbadelanto.equals(other.simbadelanto);
    }

    @Override
    public int hashCode() {
        int resultado = index;
        resultado = resultado * 42 + produccion.hashCode();
        resultado = resultado * 42 + (simbadelanto == null ? 0 : simbadelanto.hashCode());
        return resultado;
    }

    @Override
    public String toString() {
        StringBuilder prod = new StringBuilder();
        prod.append(produccion.getNumProduccion());
        prod.append(". ");
        prod.append(produccion.getNoterminal());
        prod.append(" -> ");

        List<Simbolo> simb = produccion.getProduccion();
        for (int i = 0; i < simb.size(); ++i) {
            /* Poner punto aquí si el item es justo anterior al carácter. */
            if (index == i) {
                prod.append(".");
            }
            prod.append(simb.get(i));
            prod.append(" ");
        }

        /* Si el index está después de la produccuón, colocar un punto aqui. */
        if (index == simb.size()) {
            prod.append(".");
        }

        /* Si tenemos un simbolo de adelanto, devolverlo. */
        if (simbadelanto != null) {
            prod.append("  [");
            prod.append(simbadelanto);
            prod.append("]");
        }

        return prod.toString();
    }
}
