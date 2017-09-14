package Simbolos;

public abstract class Simbolo {

    /* El nombre de los símbolos. */
    private String nombre;

    /* Comprobar si un símbolo terminal. */
    private boolean terminal;

    Simbolo(String nombre, boolean esTerminal) {
        if (nombre == null || nombre.isEmpty()) {
            throw new RuntimeException("Nombre inválido " + nombre);
        }

        this.nombre = nombre;
        this.terminal = esTerminal;
    }

    public final boolean esTerminal() {
       
         return terminal;
    }

    public final String getNombre() {
        return nombre;
    }

    @Override
    public int hashCode() {
        return 42 * nombre.hashCode() + (terminal ? 1 : 0);
    }

    @Override
    public boolean equals(Object o) {
        /* Si el objeto no es s�mbolo, no hacemos la comparaci�n. */
        if (!(o instanceof Simbolo)) {
            return false;
        }

        /* Hacemos un cast. */
        Simbolo other = (Simbolo) o;

        /* Confirma que tienen el mismo estado. */
        return terminal == other.terminal && nombre.equals(other.nombre);
    }

    @Override
    public String toString() {

        return nombre;
    }

    public String vslueOf(String str) {
        str = this.nombre;
        return str;
    }
}
