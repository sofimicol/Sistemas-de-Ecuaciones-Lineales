package servicios;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Verificar.ampliar;
import static soporte.Verificar.errorInfinito;
import static soporte.Verificar.matrizCercana;
import static soporte.Verificar.matrizIgual;
import static soporte.Verificar.vectorCercano;

import modelo.Matriz;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MetodosDirectosMatrizTest {

    // Pivote cero en [0][0]; x = (4, 2, 0)
    private static final double[][] PIVOTE_CERO_AMPLIADA = {
        {0, 2, 1, 4},
        {1, 1, 1, 6},
        {2, 1, 3, 10}
    };
    private static final double[] X_PIVOTE_CERO = {4, 2, 0};
    // Fila 1 = 2 * fila 0
    private static final double[][] SINGULAR_AMPLIADA = {
        {1, 2, 3, 4},
        {2, 4, 6, 8},
        {1, 1, 1, 2}
    };
    private static final double[][] A_CROUT = {
        {2, -1, -2},
        {-4, 6, 3},
        {-4, -2, 8}
    };
    // A_CROUT con b elegido para que x = (1, 2, 3)
    private static final double[][] CROUT_AMPLIADA = {
        {2, -1, -2, -6},
        {-4, 6, 3, 17},
        {-4, -2, 8, 16}
    };
    private static final double[] X_CROUT = {1, 2, 3};

    @Nested
    @DisplayName("Gauss-Jordan")
    class Jordan {

        // Ticket de QA T1 (Testeo-Julian.md). El test original usaba [1,2,3; 2,4,6], que es singular:
        // ese caso quedó en "Regla det(A) != 0"
        @Test
        @DisplayName("Sistema 2x2 con solución única")
        void dosPorDos() {
            Matriz Ab = new Matriz(new double[][]{{2, 1, 5}, {1, -1, 1}});
            vectorCercano(new double[]{2, 1}, MetodosDirectosMatriz.JordanSolucionDiagonalUnitaria(Ab, 2), 1e-12);
        }

        @Test
        @DisplayName("Pivote cero: pivotea automáticamente")
        void pivoteCeroAutomatico() {
            vectorCercano(X_PIVOTE_CERO, MetodosDirectosMatriz.JordanSolucion(new Matriz(PIVOTE_CERO_AMPLIADA), 3), 1e-12);
        }

        @Test
        @DisplayName("Pivote cero con pivoteo parcial manual previo")
        void pivoteCeroManual() {
            Matriz Ab = new Matriz(PIVOTE_CERO_AMPLIADA);
            Ab.pivoteoParcial(0);
            vectorCercano(X_PIVOTE_CERO, MetodosDirectosMatriz.JordanSolucion(Ab, 3), 1e-12);
        }

        @Test
        @DisplayName("Sistema 5x5 con solución fraccionaria")
        void cincoPorCinco() {
            Matriz Ab = new Matriz(new double[][]{
                {1, 2, 0, 0, 0, 3},
                {0, 1, 2, 0, 0, 5},
                {0, 0, 1, 2, 0, 7},
                {0, 0, 0, 1, 2, 9},
                {2, 0, 0, 0, 1, 4}
            });
            double[] esperado = {13.0 / 33, 43.0 / 33, 61.0 / 33, 85.0 / 33, 106.0 / 33};
            vectorCercano(esperado, MetodosDirectosMatriz.JordanSolucionDiagonalUnitaria(Ab, 5), 1e-12);
        }

        @Test
        @DisplayName("Sistema singular lanza ArithmeticException")
        void singular() {
            assertThrows(ArithmeticException.class,
                () -> MetodosDirectosMatriz.JordanSolucionDiagonalUnitaria(new Matriz(SINGULAR_AMPLIADA), 3));
        }

        // Ticket de QA 3.3 (Testeo-Julian.md): valores no finitos deben rechazarse antes de calcular
        @Test
        @DisplayName("Un NaN en la matriz se rechaza")
        void nanSeRechaza() {
            Matriz Ab = new Matriz(new double[][]{
                {1, Double.NaN, 3, 1},
                {4, 5, 6, 2},
                {7, 8, 10, 3}
            });
            assertThrows(IllegalArgumentException.class, () -> MetodosDirectosMatriz.JordanSolucionDiagonalUnitaria(Ab, 3));
        }
    }

    @Nested
    @DisplayName("Factorización LU")
    class LU {

        @Test
        @DisplayName("3x3: L unitaria inferior y U superior")
        void tresPorTres() {
            Matriz[] lu = MetodosDirectosMatriz.factorizarLU(new Matriz(A_CROUT));
            matrizCercana(new double[][]{{1, 0, 0}, {-2, 1, 0}, {-2, -1, 1}}, lu[0], 1e-12);
            matrizCercana(new double[][]{{2, -1, -2}, {0, 4, -1}, {0, 0, 3}}, lu[1], 1e-12);
        }

        @Test
        @DisplayName("Pivote cero en [0][0] lanza ArithmeticException")
        void pivoteCero() {
            Matriz A = new Matriz(new double[][]{{0, 2, 1}, {1, 3, 4}, {2, 1, 5}});
            assertThrows(ArithmeticException.class, () -> MetodosDirectosMatriz.factorizarLU(A));
        }

        @Test
        @DisplayName("Singularidad oculta (fila 3 = fila 1 + fila 2) lanza ArithmeticException")
        void singularidadOculta() {
            Matriz A = new Matriz(new double[][]{{1, 2, 3}, {2, 5, 7}, {3, 7, 10}});
            assertThrows(ArithmeticException.class, () -> MetodosDirectosMatriz.factorizarLU(A));
        }
    }

    @Nested
    @DisplayName("Crout")
    class Crout {

        @Test
        @DisplayName("Factorización LU1 (diagonal de U unitaria)")
        void factorizacionLU1() {
            Matriz[] LU1 = MetodosDirectosMatriz.factorizarCroutLU1(new Matriz(A_CROUT));
            matrizCercana(new double[][]{{2, 0, 0}, {-4, 4, 0}, {-4, -4, 3}}, LU1[0], 1e-9);
            matrizCercana(new double[][]{{1, -0.5, -1}, {0, 1, -0.25}, {0, 0, 1}}, LU1[1], 1e-9);
        }

        @Test
        @DisplayName("Factorización L1U (diagonal de L unitaria)")
        void factorizacionL1U() {
            Matriz[] L1U = MetodosDirectosMatriz.factorizarCroutL1U(new Matriz(A_CROUT));
            matrizCercana(new double[][]{{1, 0, 0}, {-2, 1, 0}, {-2, -1, 1}}, L1U[0], 1e-9);
            matrizCercana(new double[][]{{2, -1, -2}, {0, 4, -1}, {0, 0, 3}}, L1U[1], 1e-9);
        }

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = {"LU1", "L1U"})
        @DisplayName("La factorización no modifica la matriz original")
        void factorizacionNoModificaLaOriginal(String variante) {
            Matriz original = new Matriz(A_CROUT);
            factorizar(variante, original);
            matrizIgual(A_CROUT, original);
        }

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = {"LU1", "L1U"})
        @DisplayName("La factorización con pivote cero lanza ArithmeticException")
        void factorizacionPivoteCero(String variante) {
            Matriz A = new Matriz(new double[][]{{0, 2, 1}, {1, 1, 1}, {2, 1, 3}});
            assertThrows(ArithmeticException.class, () -> factorizar(variante, A));
        }

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = {"LU1", "L1U"})
        @DisplayName("Resuelve el sistema sin modificar la ampliada")
        void solucion(String variante) {
            Matriz Ab = new Matriz(CROUT_AMPLIADA);
            vectorCercano(X_CROUT, resolver(variante, Ab), 1e-9);
            matrizIgual(CROUT_AMPLIADA, Ab);
        }

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = {"LU1", "L1U"})
        @DisplayName("Resolver con pivote cero lanza ArithmeticException")
        void solucionPivoteCero(String variante) {
            assertThrows(ArithmeticException.class, () -> resolver(variante, new Matriz(PIVOTE_CERO_AMPLIADA)));
        }

        private Matriz[] factorizar(String variante, Matriz A) {
            return variante.equals("LU1")
                ? MetodosDirectosMatriz.factorizarCroutLU1(A)
                : MetodosDirectosMatriz.factorizarCroutL1U(A);
        }

        private double[] resolver(String variante, Matriz Ab) {
            return variante.equals("LU1")
                ? MetodosDirectosMatriz.CroutSolucionLU1(Ab)
                : MetodosDirectosMatriz.CroutSolucionL1U(Ab);
        }
    }

    @Nested
    @DisplayName("Cholesky")
    class Cholesky {

        // Simétrica definida positiva (L = [[2,0,0],[6,1,0],[-8,5,3]]), b elegido para que x = (1, 2, 3)
        private final double[][] A = {
            {4, 12, -16},
            {12, 37, -43},
            {-16, -43, 98}
        };
        private final double[][] Ab = ampliar(A, new double[]{-20, -43, 192});

        @ParameterizedTest(name = "Cholesky-{0}")
        @ValueSource(strings = {"L", "U"})
        @DisplayName("Resuelve el sistema sin modificar la ampliada")
        void solucion(String variante) {
            Matriz ampliada = new Matriz(Ab);
            vectorCercano(new double[]{1, 2, 3}, resolver(variante, ampliada), 1e-9);
            matrizIgual(Ab, ampliada);
        }

        @ParameterizedTest(name = "Cholesky-{0}")
        @ValueSource(strings = {"L", "U"})
        @DisplayName("No simétrica lanza UnsupportedOperationException")
        void noSimetrica(String variante) {
            Matriz ampliada = new Matriz(ampliar(new double[][]{{4, 1}, {2, 3}}, new double[]{1, 2}));
            assertThrows(UnsupportedOperationException.class, () -> resolver(variante, ampliada));
        }

        // Simétrica pero no definida positiva (autovalores -1 y 3)
        @ParameterizedTest(name = "Cholesky-{0}")
        @ValueSource(strings = {"L", "U"})
        @DisplayName("No definida positiva lanza ArithmeticException")
        void noDefinidaPositiva(String variante) {
            Matriz ampliada = new Matriz(ampliar(new double[][]{{1, 2}, {2, 1}}, new double[]{1, 2}));
            assertThrows(ArithmeticException.class, () -> resolver(variante, ampliada));
        }

        @ParameterizedTest(name = "Cholesky-{0}")
        @ValueSource(strings = {"L", "U"})
        @DisplayName("No ampliada lanza IllegalArgumentException")
        void noAmpliada(String variante) {
            assertThrows(IllegalArgumentException.class, () -> resolver(variante, new Matriz(A)));
        }

        private double[] resolver(String variante, Matriz ampliada) {
            return variante.equals("L")
                ? MetodosDirectosMatriz.CholeskySolucionL(ampliada)
                : MetodosDirectosMatriz.CholeskySolucionU(ampliada);
        }
    }

    // Sistema simétrico y singular (fila 2 = 2 * fila 1) con b compatible: infinitas soluciones
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"Jordan", "JordanDiagonalUnitaria", "CroutL1U", "CroutLU1", "CholeskyL", "CholeskyU"})
    @DisplayName("Regla de solución única: infinitas soluciones se rechazan")
    void reglaSolucionUnica(String metodo) {
        Matriz Ab = new Matriz(new double[][]{{1, 2, 3}, {2, 4, 6}});
        ArithmeticException e = assertThrows(ArithmeticException.class, () -> resolver(metodo, Ab));
        assertTrue(e.getMessage().contains("solución única"), "debe cortar por la regla de solución única: " + e.getMessage());
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"Jordan", "JordanDiagonalUnitaria", "CroutL1U", "CroutLU1", "CholeskyL", "CholeskyU",
        "GaussSimple", "GaussParcial", "GaussTotal"})
    @DisplayName("Valores no finitos (NaN, Infinity) lanzan IllegalArgumentException")
    void valoresNoFinitos(String metodo) {
        Matriz conNaN = new Matriz(new double[][]{{4, Double.NaN, 1}, {Double.NaN, 3, 2}});
        Matriz conInfinito = new Matriz(new double[][]{{4, 1, Double.POSITIVE_INFINITY}, {1, 3, 2}});
        assertThrows(IllegalArgumentException.class, () -> resolver(metodo, conNaN));
        assertThrows(IllegalArgumentException.class, () -> resolver(metodo, conInfinito));
    }

    // Hilbert 12 es simétrica definida positiva, pero cond(A) ~ 4e16 > 1/ε: numéricamente singular
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"Jordan", "JordanDiagonalUnitaria", "CroutL1U", "CroutLU1", "CholeskyL", "CholeskyU"})
    @DisplayName("Regla de solución única: Hilbert 12 es numéricamente singular")
    void hilbertNumericamenteSingular(String metodo) {
        Matriz Ab = new Matriz(ampliar(soporte.SistemasConocidos.hilbert(12), new double[12]));
        assertThrows(ArithmeticException.class, () -> resolver(metodo, Ab));
    }

    private static double[] resolver(String metodo, Matriz Ab) {
        int n = Ab.getCantFilas();
        switch (metodo) {
            case "Jordan": return MetodosDirectosMatriz.JordanSolucion(Ab, n);
            case "JordanDiagonalUnitaria": return MetodosDirectosMatriz.JordanSolucionDiagonalUnitaria(Ab, n);
            case "CroutL1U": return MetodosDirectosMatriz.CroutSolucionL1U(Ab);
            case "CroutLU1": return MetodosDirectosMatriz.CroutSolucionLU1(Ab);
            case "CholeskyL": return MetodosDirectosMatriz.CholeskySolucionL(Ab);
            case "CholeskyU": return MetodosDirectosMatriz.CholeskySolucionU(Ab);
            case "GaussSimple": return MetodosDirectosMatriz.GaussPivoteoSimple(Ab);
            case "GaussParcial": return MetodosDirectosMatriz.GaussPivoteoParcial(Ab);
            default: return MetodosDirectosMatriz.GaussPivoteoTotal(Ab);
        }
    }

    @Nested
    @DisplayName("Eliminación gaussiana")
    class Gauss {

        @Test
        @DisplayName("Pivoteo simple con cero en la diagonal")
        void pivoteoSimple() {
            vectorCercano(X_PIVOTE_CERO, MetodosDirectosMatriz.GaussPivoteoSimple(new Matriz(PIVOTE_CERO_AMPLIADA)), 1e-12);
        }

        // Un pivote muy chico (0.0001) amplifica el error si no se pivotea
        @Test
        @DisplayName("Pivoteo parcial con pivote chico")
        void pivoteoParcial() {
            Matriz Ab = new Matriz(new double[][]{{0.0001, 1, 1}, {1, 1, 2}});
            double x = 1 / (1 - 0.0001);
            vectorCercano(new double[]{x, 2 - x}, MetodosDirectosMatriz.GaussPivoteoParcial(Ab), 1e-12);
        }

        // El mayor valor absoluto está en la columna 2: se intercambian columnas y se reordenan las incógnitas
        @Test
        @DisplayName("Pivoteo total reordena las incógnitas")
        void pivoteoTotal() {
            Matriz Ab = new Matriz(new double[][]{{1, 2, 10, 13}, {1, 5, 2, 8}, {2, 1, 1, 4}});
            vectorCercano(new double[]{1, 1, 1}, MetodosDirectosMatriz.GaussPivoteoTotal(Ab), 1e-12);
        }

        @Test
        @DisplayName("Sin vector b lanza IllegalArgumentException")
        void sinVectorB() {
            assertThrows(IllegalArgumentException.class,
                () -> MetodosDirectosMatriz.GaussPivoteoParcial(new Matriz(new double[][]{{1, 2}, {3, 4}})));
        }

        @Test
        @DisplayName("Sistema singular lanza ArithmeticException")
        void singular() {
            Matriz Ab = new Matriz(new double[][]{{1, 1, 1, 3}, {2, 2, 2, 6}, {0, 2, 1, 3}});
            assertThrows(ArithmeticException.class, () -> MetodosDirectosMatriz.GaussPivoteoTotal(Ab));
        }
    }

    // Apunte de la cátedra (Barberis), págs. 17-19
    @Nested
    @DisplayName("Sistema sensible del apunte de la cátedra")
    class SistemaSensible {

        private final double[][] Ab = {
            {3.02, -1.05, 2.53, -1.61},
            {4.33, 0.56, -1.78, 7.23},
            {-0.83, -0.54, 1.47, -3.38}
        };
        // El apunte dice x = (1, 5, -1): es una errata, A * (1, 5, -1) != b
        private final double[] x = {1, 2, -1};
        // El apunte dice x2 = 6.522890: errata de dígitos
        private final double[] xA11 = {1.127677, 6.522089, 0.733265};
        private final double[] xB3 = {-0.4840218, -51.593141, -21.470771};

        @Test
        @DisplayName("Solución del sistema original")
        void solucion() {
            vectorCercano(x, MetodosDirectosMatriz.GaussPivoteoParcial(new Matriz(Ab)), 1e-9);
        }

        @Test
        @DisplayName("Perturbación a11 = 3")
        void perturbacionA11() {
            double[][] perturbada = copiar(Ab);
            perturbada[0][0] = 3.0;
            vectorCercano(xA11, MetodosDirectosMatriz.GaussPivoteoParcial(new Matriz(perturbada)), 1e-6);
        }

        @Test
        @DisplayName("Perturbación b3 = -3.3")
        void perturbacionB3() {
            double[][] perturbada = copiar(Ab);
            perturbada[2][3] = -3.3;
            vectorCercano(xB3, MetodosDirectosMatriz.GaussPivoteoParcial(new Matriz(perturbada)), 1e-6);
        }

        // ||x* - x|| / ||x|| <= cond(A) * ||δA|| / ||A||   y   <= cond(A) * ||δb|| / ||b||
        @Test
        @DisplayName("Se cumplen las cotas de error relativo")
        void cotasDeErrorRelativo() {
            double cond = new Matriz(Ab).extraerA().condicion();
            double normaX = Matriz.normaInfinitoVector(x);
            double errorA11 = errorInfinito(xA11, x) / normaX;
            double errorB3 = errorInfinito(xB3, x) / normaX;
            assertTrue(errorA11 <= cond * 0.02 / 6.67, "cota con δA: " + errorA11);
            assertTrue(errorB3 <= cond * 0.08 / 7.23, "cota con δb: " + errorB3);
        }

        private double[][] copiar(double[][] datos) {
            double[][] copia = new double[datos.length][];
            for (int i = 0; i < datos.length; i++) {
                copia[i] = datos[i].clone();
            }
            return copia;
        }
    }
}
