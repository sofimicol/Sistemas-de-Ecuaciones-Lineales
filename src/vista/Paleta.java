package vista;

import javafx.scene.paint.Color;

/**
 * Paleta centralizada del proyecto.
 */
public final class Paleta {

    private Paleta() { /* utility class */ }

    // ===== FONDOS =====
    public static final String FONDO_GLOBAL   = "#0F1115"; // warm-black global
    public static final String FONDO_LATERAL  = "#0B0D10"; // sidebar
    public static final String FONDO_TARJETA  = "#16181D"; // cards / dialogs
    public static final String FONDO_TARJETA2 = "#1E2128"; // cards elevadas / hover

    // ===== FONDO DE CUADRÍCULA (Corregido con porcentajes y mayor visibilidad) =====
    public static final String FONDO_GRILLA = 
        "-fx-background-color: " +
        "linear-gradient(from 0px 0px to 40px 0px, repeat, rgba(220, 215, 200, 0.15) 0%, rgba(220, 215, 200, 0.15) 2%, transparent 2%, transparent 100%), " +
        "linear-gradient(from 0px 0px to 0px 40px, repeat, rgba(220, 215, 200, 0.15) 0%, rgba(220, 215, 200, 0.15) 2%, transparent 2%, transparent 100%), " +
        FONDO_GLOBAL + ";";

    // ===== BORDES =====
    public static final String BORDE_SUTIL = "#2A2E38";
    public static final String BORDE_MEDIO = "#3A3F4D";

    // ===== TEXTOS =====
    public static final String TEXTO_PRINCIPAL  = "#E5E7EB";
    public static final String TEXTO_PRIMARIO   = TEXTO_PRINCIPAL;
    public static final String TEXTO_SECUNDARIO = "#8A93A6";

    // ===== ACENTOS =====
    public static final String ACENTO       = "#2DD4BF";
    public static final String ACENTO_AMBER = "#F59E0B";

    // ===== CELDAS =====
    public static final String CELDA_L_BG    = "#132625";
    public static final String CELDA_L_BORDE = "#2DD4BF";
    public static final String CELDA_L_TEXTO = "#5EEAD4";

    public static final String CELDA_U_BG    = "#2A2110";
    public static final String CELDA_U_BORDE = "#F59E0B";
    public static final String CELDA_U_TEXTO = "#FCD34D";

    public static final String PIVOTE_BG    = "#3A3416";
    public static final String PIVOTE_BORDE = "#FBBF24";

    public static final String CELDA_NORMAL_BG    = "#1E2128";
    public static final String CELDA_NORMAL_BORDE = "#2A2E38";
    public static final String CELDA_NORMAL_FOCUS = "#2DD4BF";

    public static final String VECTOR_B_BG    = "#0F2A2A";
    public static final String VECTOR_B_BORDE = "#2DD4BF";

    public static final String CELDA_VACIA_BG    = "#16181D";
    public static final String CELDA_VACIA_TEXTO = "#4B5563";

    // ===== ESTADOS =====
    public static final String EXITO_FONDO = "#0F2A22";
    public static final String EXITO_BORDE = "#10B981";
    public static final String EXITO_TEXTO = "#34D399";

    public static final String ERROR_FONDO = "#2A1010";
    public static final String ERROR_BORDE = "#EF4444";
    public static final String ERROR_TEXTO = "#F87171";

    public static final String ADVERTENCIA_FONDO = "#2A2410";
    public static final String ADVERTENCIA_BORDE = "#F59E0B";
    public static final String ADVERTENCIA_TEXTO = "#FBBF24";

    // ===== HELPER =====
    public static Color color(String hex) {
        return Color.web(hex);
    }
}
