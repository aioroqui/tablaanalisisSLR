package Excepciones;

import java.io.*;

/* Clase que genera una excepción si se produce un error en la lectura
 * del fichero de entrada. 
*/

public class ErrorProcearFicheroException extends IOException {
   public ErrorProcearFicheroException(String msj, Throwable causa) {
        super(msj, causa);
    }

    /* Constructro con la causa del error.*/
    public ErrorProcearFicheroException(Throwable causa) {
        super(causa);
    }

    /* Constructor con el mensaje.*/
    public ErrorProcearFicheroException(String msj) {
        super(msj);
    }

    /*Constructs a ErrorParseException with no description.*/
    public ErrorProcearFicheroException() {
        super();
    }
} 
    

