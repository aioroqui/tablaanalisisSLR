package ElementosParser;

import Acciones.*;
import Arbol.ArbolAnalisis;
import ElementosGramatica.*;
import Excepciones.*;
import Simbolos.*;
import java.util.*;


/* Analizador que produce anunciadores LR(0) para gramáticas LR(0).*/
public final class AnalizadorLR0 {

    public AnalizadorLR0() {  }

    public static Parser crearParser(Gramatica g) throws GramaticaNoSLRException {
        /* Se aumenta la gramática*/
        g = TransformarGramatica.gramaticaAumentada(g);

        /* Se construye el conjunto de las configuraciones*/
        Set<Set<ElementoLR>> conjConfiguraciones = construirConjConfiguraciones(g);

        /* Se crea los conjuntos para asociar el conjunto de configuraciones 
	 * con el conjunto canónico de configuraciones
         */
        Map<Set<ElementoLR>, Set<ElementoLR>> conjCanonicos
                = new HashMap<Set<ElementoLR>, Set<ElementoLR>>();
        for (Set<ElementoLR> item : conjConfiguraciones) {
            conjCanonicos.put(item, item);
        }

        /* Construimos las tablas accion e ir_a
         * a para el conjunto de configuraciones
         */
        Map<tablaEstSim, Set<ElementoLR>> tablaIra
                = creartablaIra(conjConfiguraciones, conjCanonicos, g);

        Map<Set<ElementoLR>, AccionLR> tablaAccion
                = creartablaAccion(conjConfiguraciones, g);

        /* encontramos el estado inicial; es el único que contiene la clausuara
         * de la produccion inicial
         */
        Set<ElementoLR> inicial = conjCanonicos.get(conjInicialPara(g));

        System.out.println("Gramática a analizar: ");
        System.out.println(g.toString());
        
        formatearTablaIra(tablaIra);
        formatearTablaAccion(tablaAccion);
        
        //formatearTabla(tablaIra, tablaAccion);
        

        return new LR0Parser(tablaIra, tablaAccion, inicial);
    }

    /*
     * Dada una gramática, devuelve el conjunto de la configuración inicial para
     * la gramática
     */
    private static Set<ElementoLR> conjInicialPara(Gramatica g) {
        /* Obtenemos la producción aumentada. */
        Produccion iniPro = g.getProduccionesPara(g.getSimboloInicial()).iterator().next();

        /* Conjunto de elementos que que representa el estado vacío*/
        Set<ElementoLR> conjInicial = new HashSet<>();
        conjInicial.add(new ElementoLR(iniPro, 0, null));
        
        /* Retorna la clausura */
        return clausuraDe(conjInicial, g);
    }

    /* Dada una gramática, construye las configuraciones para la gramática.*/
    private static Set<Set<ElementoLR>> construirConjConfiguraciones(Gramatica g) {
        /* Espacio para almacenar el resultado. */
        Set<Set<ElementoLR>> conjConfiguraciones = new HashSet<Set<ElementoLR>>();

        /* Lista con el conjunto de configuraciones que se necesitarán
         * para ser procesadas
         */
        Queue<Set<ElementoLR>> listaConfProc = new LinkedList<Set<ElementoLR>>();

        /* listaConfProc con la clausura de la producción inicial. */
        listaConfProc.offer(conjInicialPara(g));

        /* Desapilamos y procesamos estos conjuntos hasta que se 
         * hayan generado todas las configuraciones
         */
        while (!listaConfProc.isEmpty()) {
            Set<ElementoLR> act = listaConfProc.remove();

            /* Si ya hemos visto este conjunto, lo saltamos. */
            if (!conjConfiguraciones.add(act)) {
                continue;
            }

            /* Por otro lado lado, se obtiene el conjunto de todas las transicones de este conjunto 
             * y se agregan dentro de listaConPro
             */
            Map<Simbolo, Set<ElementoLR>> sigTranss = buscaSucesores(act, g);
            for (Set<ElementoLR> succ : sigTranss.values()) {
                listaConfProc.offer(succ);
            }
        }

        return conjConfiguraciones;
    }

    /*Dado un conjunto parcual de elementos LR, devuelve la clausura del conjunto*/
    private static Set<ElementoLR> clausuraDe(Set<ElementoLR> inicial,
            Gramatica g) {
        /* Reservamos espacio para el resultado. */
        Set<ElementoLR> resultado = new HashSet<ElementoLR>();

        /* Añadimos el conjunto inicial en listaConfProc. */
        Queue<ElementoLR> listaConfProc = new LinkedList<ElementoLR>(inicial);

        /* Mientras haya elementos a la izquierda, continuamos con el proceso*/
        while (!listaConfProc.isEmpty()) {
            ElementoLR act = listaConfProc.remove();

            /* Si ya hemos procesado este elemento, lo saltamos. */
            if (!resultado.add(act)) {
                continue;
            }

            /* Si el símbolo después del punto es no terminal. 
             * Entonces añadir un nuevo elemento LR con todas sus producciones que
             * pueden ser iniciadas
             */
            Simbolo siguiente = act.getSimboloTrasPunto();
            if (siguiente instanceof Noterminal) {
                for (Produccion p : g.getProduccionesPara((Noterminal) siguiente)) {
                    listaConfProc.add(new ElementoLR(p, 0, null));
                }
            }
        }
        return resultado;
    }

    /* Dado una configuración LR, devuelve el conjunto de sucesores del símbolo*/
    private static Map<Simbolo, Set<ElementoLR>>
            buscaSucesores(Set<ElementoLR> conj, Gramatica g) {
        Map<Simbolo, Set<ElementoLR>> resultado = new HashMap<Simbolo, Set<ElementoLR>>();

        /* Colocamos los sucessores de cada elemento LR 
	 * en el resultado conj sin la clausura.
         */
        for (ElementoLR item : conj) {
            /* Comprobamos que despué´s del punto.  
 	     * Si no hay nada, lo saltamos
             */
            Simbolo trasPunto = item.getSimboloTrasPunto();
            if (trasPunto == null) {
                continue;
            }

            /* Aseguramos que hay un conjunto para intrpducir el nuevo elemento dentro*/
            if (!resultado.containsKey(trasPunto)) {
                resultado.put(trasPunto, new HashSet<ElementoLR>());
            }

            /* Colcoamos el sucesor de ese elemento dentro. */
            resultado.get(trasPunto).add(item.conPuntoHaciaAdelante());
        }

        /* Calculamos la clausuara de cada conjunto de sucesores. */
        for (Map.Entry<Simbolo, Set<ElementoLR>> entrada : resultado.entrySet()) {
            entrada.setValue(clausuraDe(entrada.getValue(), g));
        }

        return resultado;
    }

    /* Creamos la tabla para el analziador LR(0). */
    private static Map<tablaEstSim, Set<ElementoLR>>
            creartablaIra(Set<Set<ElementoLR>> configuraciones,
                    Map<Set<ElementoLR>, Set<ElementoLR>> estadosCanonicos,
                    Gramatica g) {

        Map<tablaEstSim, Set<ElementoLR>> tablaIra
                = new HashMap<tablaEstSim, Set<ElementoLR>>();

        /* Recorremos cada configuración y rellenamos la tabla. */
        for (Set<ElementoLR> estado : configuraciones) {
            /* Buscamos los sucesores de ese estado.*/
            for (Map.Entry<Simbolo, Set<ElementoLR>> succ : buscaSucesores(estado, g).entrySet()) {
                
                /* Si estado sucesor del simbolo dado, es igual que el estado canónico*/
                tablaIra.put(new tablaEstSim(estado, succ.getKey()),
                        estadosCanonicos.get(succ.getValue()));

            }
        }
        return tablaIra;
    }

    /* Crear tabla Accion para analizador LR(0).*/
    private static Map<Set<ElementoLR>, AccionLR>
            creartablaAccion(Set<Set<ElementoLR>> configuraciones, Gramatica g)
            throws GramaticaNoSLRException {
        /* La tablla Accion será un mapa de identidad porque usaremosnlas representaciones 
		 * canónicas para los conjuntos de los elementos de LR(0)
         */

        Map<Set<ElementoLR>, AccionLR> tablaAccion
                = new IdentityHashMap<Set<ElementoLR>, AccionLR>();


        /*Obtenemos el conjunto Siguientes(NoTerminal)*/
        List<Simbolo> siguientes = new ArrayList<Simbolo>();
        //List<Simbolo> simb = new ArrayList<Simbolo>();

        for (Set<ElementoLR> items : configuraciones) {
            for (ElementoLR item : items) {
                //simb = item.getProduccion().getProduccion();
                if (item.getProduccion().siguienteNoterminal() != null
                        && (!siguientes.contains(item.getProduccion().siguienteNoterminal()))) {
                    siguientes.add(item.getProduccion().siguienteNoterminal());
                }

            }

        }

        /* Recorremos todas las configuraciones, rellenando la tabla.*/
        for (Set<ElementoLR> items : configuraciones) {
            /* Comprobamos qué acciones podemos realizar. Iniciamos en null. */
            AccionLR accion = null;
            AccionLR reducir = null;

            /* Para cada elemento en el conjunto, miramos qué producción se ha aplicado. */
            for (ElementoLR item : items) {
                /* Si la producción se ha completadoo, reducimos. */
                if (item.esCompleta()) {
                    /* Aplicaremos reducción(si la producción no implica al símbolo inicial ) 
                     * es una acción "aceptar" si implica al símbolo inicial. */
                    for (int i = 0; i < siguientes.size(); i++) {
                        reducir = (item.getProduccion().getNoterminal().equals(g.getSimboloInicial())
                                ? AccionAceptar.INSTANCE
                                : new AccionReducir(item.getProduccion(), item.simbolosProduccion()));
                    }
                    /* si no se puede aplicar alguna acción, lanza la excepción*/
                    if (accion != null) {
                        throw new GramaticaNoSLRException("CONFLICTO ENCONTRADO: " + accion + " vs " + reducir);
                    }
                    /* Sino, asignanos la accion. */
                    accion = reducir;

                } else {
                    /* Si tenemos que desplazar. */
                    if (accion != null && accion != AccionDesplazar.INSTANCE) {
                        System.out.println("CONFLICTO EN LR0: " + accion + " vs " + AccionDesplazar.INSTANCE);
                        System.out.println("<<--SE CONTINÚA HACIA SLR-->>");

                    }
                    accion = AccionDesplazar.INSTANCE;

                }
            }

            /* Conjunto de acciones para ese estado.*/
            tablaAccion.put(items, accion);
        }
        return tablaAccion;
    }

 
            
            
    /* Una utilidad para representar el par estado-símbolo. Es usado
     * internamente por el parser para reprsentar las entradas en la tabla ir_a.
     */
    private static final class tablaEstSim {

        /* Los estados del autómata.*/
        private final Set<ElementoLR> estado;

        /*El símbolo que se está procesando. */
        private final Simbolo simbolo;

        /* Construir tablaEstSim fdados el par estado/simbolo.*/
        public tablaEstSim(Set<ElementoLR> estado, Simbolo simbolo) {
            assert estado != null;
            assert simbolo != null;

            this.estado = estado;
            this.simbolo = simbolo;
        }

        @Override
        public boolean equals(Object o) {
            /* Comprobamos que no haya otro objeto igual que tablaEstSim. */
            if (!(o instanceof tablaEstSim)) {
                return false;
            }

            tablaEstSim other = (tablaEstSim) o;
            return estado == other.estado && simbolo.equals(other.simbolo);
        }

        @Override
        public int hashCode() {

            return 31 * System.identityHashCode(estado) + simbolo.hashCode();
        }

        public String toString() {
            return "(" + estado + ", " + simbolo + ")";
        }
    }

    /* Analizador LR(0). */
    private static final class LR0Parser implements Parser {

        /*La tabla ir_a. */
        private final Map<tablaEstSim, Set<ElementoLR>> tablaIra;

        /*La tabla accion. */
        private final Map<Set<ElementoLR>, AccionLR> tablaAccion;

        /*Par arbol analisis-estado*/
        private static final class EntradaPila {

            /* El arbol para este nodo o nulo si es la cima de la pila.*/
            public final ArbolAnalisis arbol;

            /*
             * El estado en el que se encuenra cuando el símbolo se encuentra en
             * la cima de la pila.
             */
            public final Set<ElementoLR> estado;

            /* Constructor con un arbol y un estado del autómata.*/
            public EntradaPila(ArbolAnalisis arbol, Set<ElementoLR> estado) {
                this.arbol = arbol;
                this.estado = estado;
            }
        }

        /*Análsis de la pila. */
        private final Deque<EntradaPila> analizandoPila = new ArrayDeque<>();

        /*Si hay una acción "Aceptar", generamos el árbol.*/
        private ArbolAnalisis resultado = null;

        /* Contruimos un nuevo LR(0) con la acción y la tabla ir_a. */
        public LR0Parser(Map<tablaEstSim, Set<ElementoLR>> tablaIra,
                Map<Set<ElementoLR>, AccionLR> tablaAccion,
                Set<ElementoLR> estadoInicial) {
            this.tablaIra = tablaIra;
            this.tablaAccion = tablaAccion;

            /* incicamos el analizas colocando a la entrada el estado inciald del autómata*/
            analizandoPila.offerLast(new EntradaPila(null, estadoInicial));
        }

        /* Procesa un nuevo token de la entrada.*/
        public void siguienteTerminal(Terminal terminal) throws ErrorParseException {
            /* Si ya hemos aceptado (el resultado del parsing es no nulo), 
             * entonces reportamos un error interno en el analizador.
             */
            if (resultado != null) {
                throw new ErrorParseException("EOF esperado, encontrado " + terminal);
            }

            /* Comprobamos en qué estado estanos.*/
            Set<ElementoLR> estado = analizandoPila.peekLast().estado;

            /* La acción de aqui debería ser un desplazamiento.*/
            assert tablaAccion.get(estado) instanceof AccionDesplazar;
            Set<ElementoLR> nuevoEstado = tablaIra.get(new tablaEstSim(estado, terminal));

            /* Podriamos no tener un estado al que ir si no se ha definido la transción
             * Si ocurre, reportameos un error. 
             */
            if (nuevoEstado == null) {
                throw new ErrorParseException("Falta: " + terminal);
            }

            /*Desplazar el token dentro de la pila e introducir este nuevo estado.*/
            analizandoPila.offerLast(new EntradaPila(new ArbolAnalisis(terminal), nuevoEstado));

            /* Mientras la acción actual a aplicaar es reducir o aceptar 
	     * aplicamos la acción.
             */
            while (true) {
                AccionLR accion = tablaAccion.get(nuevoEstado);

                /* Accion = DESPLAZAR, salimos. */
                if (accion instanceof AccionDesplazar) {
                    return;
                } else if (accion instanceof AccionAceptar) { 
                /* Accion = ACEPTAR, copiamos el arbol sobre el elemento de la pila 
                 * y paramos.*/
                    this.resultado = analizandoPila.peekLast().arbol;
                    return;
                }else {
                     /* Accion = REDUCIR aplicamos las reducciones
                      * y comprobamos las transiciones.
                     */ 
                    assert accion instanceof AccionReducir;
                    Produccion produccion = ((AccionReducir) accion).getProduccion();
                    /* Necesitamos reducir terminales y noterminales a noterminales que los derivan.
                    * Para ello:
                    * 1.- Actualizamos la pila
                    * 2.- Crear un nodo en el árbol de análisis para el nuevo noterminal
                    * 3.- Desplazar el símbolo noterminal que acabamos de agregar
                    * Comenzamos sacando los símbolos desde la pila y los añadimos 
                    * a la lista enlazada en orden inverso (para que coincida con la producción)
                     */
                    LinkedList<ArbolAnalisis> hijo = new LinkedList<>();
                    for (int i = 0; i < produccion.getProduccion().size(); ++i) {
                        hijo.offerFirst(analizandoPila.removeLast().arbol);
                    }

                    /* Construct a new parse arbol node for the appropriate
                     * nonterminal that has these hijo.
                     */
                    ArbolAnalisis nuevoArbol = new ArbolAnalisis(produccion.getNoterminal(),
                            hijo);

                    assert !analizandoPila.isEmpty();
                    nuevoEstado = analizandoPila.peekLast().estado;

                    /* Consultamos la tabla GOTO para mmirar el último estado en el que se ha quedado  
		     * Se ha actualziado el nuevo estado pero no hemos parado la pila.
                     */
                    nuevoEstado = tablaIra.get(new tablaEstSim(nuevoEstado,
                            produccion.getNoterminal()));
                    assert nuevoEstado != null;

                    /* Colcoar este nuevo estado, junto con el nuevo arbol,
                     * en la cima de la pila
                     */
                    analizandoPila.offerLast(new EntradaPila(nuevoArbol, nuevoEstado));

                   }
            }
        }

        /*
         * Indica que el final de la entrada ha sido alcanzada. Devuelve el
         * árbol si esta disponible.
         */
        @Override
        public ArbolAnalisis entradaCompleta() throws ErrorParseException {
            if (resultado != null) {
                return resultado;
            }

            throw new ErrorParseException("falta EOF");
        }
    }
    
    /*analizador SLR1*/
   

    public static void listaToString(List lista) {
        for (int i = 0; i < lista.size(); i++) {
            System.out.println(lista.get(i));
        }
    }

    public static void setElementoLRToString(Set<ElementoLR> e) {

        for (Iterator<ElementoLR> it = e.iterator(); it.hasNext();) {
            ElementoLR elem = it.next();
            System.out.println(elem.toString());
        }
    }

    public static void setOfSetToString(Set<Set<ElementoLR>> ele) {
        for (Set<ElementoLR> elem : ele) {
            Iterator<ElementoLR> iter = elem.iterator();

            while (iter.hasNext()) {
                System.out.println(iter.next());
            }
        }
    }

     public static void formatearTablaIra(Map<tablaEstSim, Set<ElementoLR>> tablaI) {
        int i = 0;
        ConstructorTabla tabla = new ConstructorTabla();
        List<Simbolo> siguientes = new ArrayList<Simbolo>();
        tabla.addRow("  ESTADO  ", "  IR_A  ");
        tabla.addRow("-------------", "-------------------");
        for (Set<ElementoLR> items : tablaI.values()){
            for (ElementoLR item : items){
                if (item.getProduccion().siguienteNoterminal() != null &&
                        (!siguientes.contains(item.getProduccion().siguienteNoterminal())))
                    siguientes.add(item.getProduccion().siguienteNoterminal());
            }
        }
        for (tablaEstSim eleI : tablaI.keySet()) {
            for (int n = 0; n < siguientes.size(); n++){
                if (siguientes.get(n).getNombre().endsWith(eleI.simbolo.getNombre())){
                    tabla.addRow(Integer.toHexString(i) + " " + eleI.toString(), tablaI.get(eleI).toString());
                    i++;
                }
            }
           
        }
        System.out.println(tabla.toString());

    }
    
     public static void formatearTablaAccion(Map<Set<ElementoLR>, AccionLR> tablaA) {
         int i = 0;
         ConstructorTabla tabla = new ConstructorTabla();
         tabla.addRow(" ESTADO ","  ACCION  ");
         tabla.addRow("-------------", "-------------------");
         for (Set<ElementoLR> eleA : tablaA.keySet()) {
            tabla.addRow(Integer.toHexString(i)+ " " + eleA.toString(),"  " + tablaA.get(eleA).toString());
            i++;
         }
        System.out.println(tabla.toString());

    }
}
   
 

    
    