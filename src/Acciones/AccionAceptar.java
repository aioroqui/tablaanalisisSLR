package Acciones;

/**
 * La clase acción para representar que el analizador deberá aceptar Todas las
 * acciones aceptadas se crearán como una única instancia
 */
public enum AccionAceptar implements AccionLR {
    INSTANCE;

    @Override
    public String toString() {
        return "sacc";
    }
}
