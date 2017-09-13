package Excepciones;
/**
 * Lanza una expceción si la gramática no es LR(0)
 */
public class GramaticaNoSLRException extends Exception {
    
    public GramaticaNoSLRException(String msj) {
        super(msj);
    }
} 