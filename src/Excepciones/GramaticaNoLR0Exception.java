package Excepciones;
/**
 * Lanza una expceción si la gramática no es LR(0)
 */
public class GramaticaNoLR0Exception extends Exception {
    
    public GramaticaNoLR0Exception(String msj) {
        super("La gramática no es LR0");
    }
} 