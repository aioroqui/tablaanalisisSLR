package Simbolos;

/**
 * Cosntantes que reprensetan símbolos especiales no pueden aparecer en una
 * gramática pero podrían aparecer en contextos parecidos.
 */
public final class SimbolosEspeciales extends SimboloGeneralizado {

    /**
     * simbolo especial que representa la cadena vacía.
     */
    public static SimbolosEspeciales EPSILON = new SimbolosEspeciales("\u03B5");

    /**
     * símbolo especial que representa en el final de una entrada.
     */
    public static SimbolosEspeciales EOF = new SimbolosEspeciales("$");

    /**
     * el nombre del símbolo especial.
     */
    private final String nombre;

    private SimbolosEspeciales(String nombre) {
        this.nombre = nombre;
    }

    public String toString() {
        return nombre.toString();
    }
}
