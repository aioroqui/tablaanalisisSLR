package Excepciones;

/**
 * Excepción para indicar que ha ocurrido un 
 * error durante el análisis
 */
public class ErrorParseException extends Exception {
    /* Cosntructor con el mensaje y la causa del error.*/
    public ErrorParseException(String msj, Throwable causa) {
        super(msj, causa);
    }

    /* Constructro con la causa del error.*/
    public ErrorParseException(Throwable causa) {
        super(causa);
    }

    /* Constructor con el mensaje.*/
    public ErrorParseException(String msj) {
        super(msj);
    }

    /*Constructs a ErrorParseException with no description.*/
    public ErrorParseException() {
        super();
    }
} 