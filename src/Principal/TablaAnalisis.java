package Principal;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import ElementosGramatica.*;
import Excepciones.*;
import Simbolos.*;

public class TablaAnalisis {

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Introduzca el fichero con la gramática.");
        }
        
        if (args.length >= 2) {
            System.out.println("Sólo se procesa el primer fichero introducido.");
        }
        
        String path = args[0];
        //"C:\\TFG_APP\\TablaAnalisis\\src\\tablaanalisis\\Entrada\\reglas.txt";
        Gramatica g = leerGramatica(path);
        ElementosParser.AnalizadorLR0.crearParser(g);

    }


    /*Comprobar que se está generando correctamente
     * Se obtienen las producciones
     */
    public static Gramatica leerGramatica(String path) throws Exception {

        FileReader reader = new FileReader(path);
        BufferedReader in = new BufferedReader(reader);
        
        Set<Noterminal> nterminal = new HashSet<Noterminal>();
        List<String> lineas = new ArrayList<String>();
        List<Produccion> prods = new LinkedList<Produccion>();
        String lineaEntrada;
        int num_linea = 0;

        /*Recuperamos las producciones del fichero.
        * Cada producción será una línea del aechivo de texto.
        * Esras lineas serán almacenadas en enuna lista de tipo String.
         */
        while (true) {
            lineaEntrada = in.readLine();

            if (lineaEntrada == null) {
                break;
            }
            StringTokenizer tok = new StringTokenizer(lineaEntrada);
            if (!tok.hasMoreTokens()) {
                continue;
            }
            lineas.add(lineaEntrada);
            
        }

        Noterminal lIzq;
        

        for (int i = 0; i < lineas.size(); i++) {
            lineaEntrada = (String) lineas.get(i);
            num_linea++;
            StringTokenizer tok = new StringTokenizer(lineas.get(i));
            lIzq = new Noterminal(tok.nextToken());
            
            /*Se comprueba que la parte izquierda de la gramática sólo puede
            * tener un único sómbolo.
            */
            if (lIzq.toString().length() != 1) {
                throw new FormatoGramaticaException("Neceario un símbolo la parte iaquierda de la gramática.");
            }
           
            
            if (!tok.hasMoreTokens()) {
                throw new FormatoGramaticaException("Falta '->'y parte derecha de regls "
                        + (i + 1));
                
            } else {
                String nextTok = tok.nextToken();
                if (!nextTok.equals("->")) {
                    throw new FormatoGramaticaException("Falta '->' en regla: " + (i + 1));

                }
                
            }
            List<Simbolo> lDch = new LinkedList<Simbolo>();
            while (tok.hasMoreTokens()) {
                String next = tok.nextToken();
                Noterminal nt = new Noterminal(next);
                if (nterminal.contains(nt)) {
                    lDch.add(nt);
                } else {
                    Terminal t = new Terminal(next);
                    lDch.add(t);
                }
            }

            Produccion prod = new Produccion(lIzq, lDch, num_linea);
            prods.add(prod);
        }
        reader.close();

        /* Una vez añadidas las producciones
        * Se crea la gramática.
         */
        Noterminal inicial = prods.get(0).getNoterminal();
        Gramatica g = new Gramatica(prods, inicial);

        return g;
    }

}
