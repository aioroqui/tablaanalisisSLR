package Acciones;


/*
 * Clase representa la acción desplazar(shift).  
 * La operación mediante la cual se avanza un símbolo,
 * simultáneamente en la cadena de entrada y en todas las reglas que siguen siendo compatibles con
 * la porción de entrada analizada.
 */
public enum AccionDesplazar implements AccionLR {
    INSTANCE;
    
    @Override
    public String toString() {
         return "D";
    }

}  

