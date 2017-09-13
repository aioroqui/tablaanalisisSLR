package Acciones;


import ElementosGramatica.Produccion;
import Simbolos.Simbolo;
import java.util.List;

/*
 * Clase para indicar al parser que debe de aplicar una reducción.
 * Se aplica cuando se ha identificado en la cadena de entrada la 
 * parte derecha de alguna de las reglas de la gramática. 
 * Esta operación consiste en reemplazar, en la cadena de entrada, dicha parte
 * derecha por el símbolo no terminal de la regla correspondiente. 
 */
public final class AccionReducir implements AccionLR {

    /* La producción que será cambiada.*/
    private final Produccion produccion;
    private final List<Simbolo> simbolo;

     public AccionReducir(Produccion produccion, List<Simbolo> simbolo) {
        if (produccion == null) {
            throw new NullPointerException();
        }
        this.produccion = produccion;
        this.simbolo = simbolo;
    }

    /*Devuelve la produccion asocaida con la reducción.*/
    public Produccion getProduccion() {
        return produccion;
    }
    
     public List<Simbolo> getSimbolo() {
        return simbolo;
    }

    /* Función para comprobar que si otro objeto es igual que que AccionReducir. */
    public boolean equals(Object o) {
        /* See if the other object is a AccionReducir and fail if it isn't. */
        if (!(o instanceof AccionReducir)) {
            return false;
        }

        AccionReducir other = (AccionReducir) o;
        
        return produccion.equals(other.produccion);
    }

    public int hashCode() {
        return produccion.hashCode();
    }

    public String toString() {
        StringBuilder reducir = new StringBuilder();
        reducir.append("R");
        reducir.append(produccion.getNumProduccion());
            
        return reducir.toString();
    }
}

