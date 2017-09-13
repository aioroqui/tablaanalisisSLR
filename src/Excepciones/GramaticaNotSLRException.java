package Excepciones;
/**
 * Lanza una expceción si la gramática no es LR(0)
 */
public class GramaticaNotSLRException extends Exception {
    
    public GramaticaNotSLRException(String msj) {
        super("La gramática no es SLR");
    }
} 