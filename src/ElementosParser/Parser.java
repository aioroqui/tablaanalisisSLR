package ElementosParser;

import Arbol.ArbolAnalisis;
import Excepciones.ErrorParseException;
import Simbolos.Terminal;

/**
 * Interfaz que represnta un objeto que puede analizar una secuencia de
 * terminales dentro del árbol de análisis
 */
public interface Parser {

    /**
     * Introduce otro terminal en el parser. El paser continuará el proceso o
     * lanzará un error de análisis
     */
    public void siguienteTerminal(Terminal terminal) throws ErrorParseException;

    /**
     * Indica al paser que se ha alcanzado el final de la cadena El parser
     * devolverá el árbol de análisis creado. Si se produce un error lanzará un
     * ErrorParseException.
     *
     * @return
     * @throws tablaanalisis.ErrorParseException
     */
    public ArbolAnalisis entradaCompleta() throws ErrorParseException;
}
