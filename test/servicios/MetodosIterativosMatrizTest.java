package servicios;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Verificar.ampliar;
import static soporte.Verificar.errorInfinito;
import static soporte.Verificar.vectorCercano;

import modelo.Matriz;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MetodosIterativosMatrizTest {

    // Burden 7.3, Ejemplo 1: diagonal estrictamente dominante, x = (1, 2, -1, 1)
    private static final double[][] BURDEN_73 = {
        {10, -1, 2, 0, 6},
        {-1, 11, -1, 3, 25},
        {2, -1, 10, -1, -11},
        {0, 3, -1, 8, 15}
    };
    private static final double[] X_BURDEN_73 = {1, 2, -1, 1};
    // Burden 7.4, Ejemplo 3: x = (3, 4, -5), se itera desde x0 = (1, 1, 1)
    private static final double[][] BURDEN_74 = {
        {4, 3, 0, 24},
        {3, 4, -1, 30},
        {0, -1, 4, -24}
    };
    private static final double[] X_BURDEN_74 = {3, 4, -5};
    private static final double[] UNOS = {1, 1, 1};

    // Sin convergencia los métodos no lanzan excepción: devuelven la última iteración, lejos de la solución
    private static void assertDiverge(double[] x, double[] solucion) {
        assertTrue(errorInfinito(x, solucion) > 1, "se esperaba divergencia y devolvió " + java.util.Arrays.toString(x));
    }

    @Nested
    @DisplayName("Burden & Faires, capítulo 7")
    class Burden {

        @Test
        @DisplayName("Jacobi (7.3)")
        void jacobi() {
            vectorCercano(X_BURDEN_73, MetodosIterativosMatriz.JacobiSolucion(new Matriz(BURDEN_73), new double[4], 1e-10, 1000), 1e-8);
        }

        @Test
        @DisplayName("Gauss-Seidel (7.3)")
        void gaussSeidel() {
            vectorCercano(X_BURDEN_73, MetodosIterativosMatriz.GaussSeidelSolucion(new Matriz(BURDEN_73), new double[4], 1e-10, 1000), 1e-8);
        }

        @Test
        @DisplayName("Relajación (7.3)")
        void relajacion() {
            vectorCercano(X_BURDEN_73, MetodosIterativosMatriz.Relajacion(new Matriz(BURDEN_73), new double[4], 1e-10, 10000), 1e-8);
        }

        @Test
        @DisplayName("x0 no se modifica")
        void x0NoSeModifica() {
            double[] x0 = new double[4];
            MetodosIterativosMatriz.JacobiSolucion(new Matriz(BURDEN_73), x0, 1e-10, 1000);
            assertArrayEquals(new double[4], x0);
        }

        @Test
        @DisplayName("SOR con ω = 1.25 (7.4)")
        void sor() {
            vectorCercano(X_BURDEN_74, MetodosIterativosMatriz.SORSolucion(new Matriz(BURDEN_74), UNOS.clone(), 1.25, 1e-10, 1000), 1e-8);
        }

        @Test
        @DisplayName("Con 7 iteraciones, SOR (ω = 1.25) queda más cerca que Gauss-Seidel (7.4)")
        void sorAceleraAGaussSeidel() {
            double errorSOR = errorInfinito(MetodosIterativosMatriz.SORSolucion(new Matriz(BURDEN_74), UNOS.clone(), 1.25, 0, 7), X_BURDEN_74);
            double errorGS = errorInfinito(MetodosIterativosMatriz.GaussSeidelSolucion(new Matriz(BURDEN_74), UNOS.clone(), 0, 7), X_BURDEN_74);
            assertTrue(errorSOR < errorGS, "error SOR = " + errorSOR + ", error Gauss-Seidel = " + errorGS);
        }

        // Burden 7.3, ejercicios: matrices donde converge solo uno de los dos métodos, x = (1, 1, 1)
        private final double[][] soloJacobi = {
            {1, 2, -2, 1},
            {1, 1, 1, 3},
            {2, 2, 1, 5}
        };
        private final double[][] soloSeidel = {
            {2, -1, 1, 2},
            {2, 2, 2, 6},
            {-1, -1, 2, 0}
        };

        @Test
        @DisplayName("Contraejemplo: Jacobi converge y Gauss-Seidel diverge")
        void soloJacobiConverge() {
            vectorCercano(UNOS, MetodosIterativosMatriz.JacobiSolucion(new Matriz(soloJacobi), new double[3], 1e-10, 100), 1e-8);
            assertDiverge(MetodosIterativosMatriz.GaussSeidelSolucion(new Matriz(soloJacobi), new double[3], 1e-10, 100), UNOS);
        }

        @Test
        @DisplayName("Contraejemplo: Gauss-Seidel converge y Jacobi diverge")
        void soloSeidelConverge() {
            vectorCercano(UNOS, MetodosIterativosMatriz.GaussSeidelSolucion(new Matriz(soloSeidel), new double[3], 1e-10, 100), 1e-8);
            assertDiverge(MetodosIterativosMatriz.JacobiSolucion(new Matriz(soloSeidel), new double[3], 1e-10, 100), UNOS);
        }

        // Burden 7.5, Ejemplo 4: matriz mal condicionada, x = (1, 1, 1)
        @Test
        @DisplayName("Mejoramiento iterativo (7.5)")
        void mejoramientoIterativo() {
            double[][] Ab = {
                {3.3330, 15920, -10.333, 15913},
                {2.2220, 16.710, 9.6120, 28.544},
                {1.5611, 5.1791, 1.6852, 8.4254}
            };
            vectorCercano(UNOS, MetodosIterativosMatriz.MejoramientoIterativo(new Matriz(Ab), new double[3], 1e-10, 20), 1e-8);
        }
    }

    // Apunte de la cátedra (Barberis), págs. 20-26
    @Nested
    @DisplayName("Casos del apunte de la cátedra")
    class Catedra {

        // Sistema sensible de la pág. 17 (solución correcta: (1, 2, -1))
        private final double[][] sensible = {
            {3.02, -1.05, 2.53, -1.61},
            {4.33, 0.56, -1.78, 7.23},
            {-0.83, -0.54, 1.47, -3.38}
        };
        private final double[] xSensible = {1, 2, -1};

        @Test
        @DisplayName("Mejoramiento desde la solución perturbada (a11 = 3)")
        void mejoramientoDesdePerturbada() {
            double[] x0 = {1.127677, 6.522089, 0.733265};
            vectorCercano(xSensible, MetodosIterativosMatriz.MejoramientoIterativo(new Matriz(sensible), x0, 1e-12, 20), 1e-8);
        }

        @Test
        @DisplayName("Mejoramiento desde x0 = 0")
        void mejoramientoDesdeCero() {
            vectorCercano(xSensible, MetodosIterativosMatriz.MejoramientoIterativo(new Matriz(sensible), new double[3], 1e-12, 20), 1e-8);
        }

        // Pág. 24: Gauss-Seidel converge si A es estrictamente diagonal dominante o simétrica definida positiva.
        // Esta matriz es SDP y no es diagonal dominante; para Jacobi no hay garantía (acá diverge)
        private final double[][] sdp = ampliar(new double[][]{{4, 12, -16}, {12, 37, -43}, {-16, -43, 98}}, new double[]{-20, -43, 192});
        private final double[] xSdp = {1, 2, 3};

        @Test
        @DisplayName("Gauss-Seidel converge con A simétrica definida positiva")
        void gaussSeidelConSDP() {
            vectorCercano(xSdp, MetodosIterativosMatriz.GaussSeidelSolucion(new Matriz(sdp), new double[3], 1e-12, 10000), 1e-8);
        }

        @Test
        @DisplayName("Jacobi no tiene garantía con A simétrica definida positiva")
        void jacobiConSDP() {
            assertDiverge(MetodosIterativosMatriz.JacobiSolucion(new Matriz(sdp), new double[3], 1e-12, 100), xSdp);
        }

        // Pág. 26: con ω = 1, SOR es Gauss-Seidel
        @ParameterizedTest(name = "{0} iteraciones")
        @ValueSource(ints = {1, 3, 7})
        @DisplayName("SOR con ω = 1 coincide con Gauss-Seidel")
        void sorConOmegaUnoEsGaussSeidel(int iteraciones) {
            double[] xSOR = MetodosIterativosMatriz.SORSolucion(new Matriz(BURDEN_74), UNOS.clone(), 1.0, 0, iteraciones);
            double[] xGS = MetodosIterativosMatriz.GaussSeidelSolucion(new Matriz(BURDEN_74), UNOS.clone(), 0, iteraciones);
            assertArrayEquals(xGS, xSOR);
        }

        // Pág. 25: con 0 < ω < 1 el método es de subrelajación
        @Test
        @DisplayName("SOR con ω = 0.5 (subrelajación) converge")
        void subrelajacion() {
            vectorCercano(X_BURDEN_73, MetodosIterativosMatriz.SORSolucion(new Matriz(BURDEN_73), new double[4], 0.5, 1e-10, 1000), 1e-8);
        }
    }

    @Nested
    @DisplayName("Errores")
    class Errores {

        @ParameterizedTest(name = "ω = {0}")
        @ValueSource(doubles = {0, 2, -0.5, 2.5})
        @DisplayName("SOR con ω fuera de (0, 2) lanza IllegalArgumentException")
        void omegaFueraDeRango(double omega) {
            assertThrows(IllegalArgumentException.class,
                () -> MetodosIterativosMatriz.SORSolucion(new Matriz(BURDEN_74), new double[3], omega, 1e-10, 100));
        }

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = {"Jacobi", "GaussSeidel", "SOR", "Relajacion"})
        @DisplayName("Cero en la diagonal lanza ArithmeticException")
        void ceroEnLaDiagonal(String metodo) {
            Matriz Ab = new Matriz(new double[][]{{0, 1, 1}, {1, 1, 2}});
            assertThrows(ArithmeticException.class, () -> resolver(metodo, Ab));
        }

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = {"Jacobi", "GaussSeidel", "SOR", "Relajacion", "Mejoramiento"})
        @DisplayName("Matriz no ampliada lanza IllegalArgumentException")
        void noAmpliada(String metodo) {
            Matriz A = new Matriz(new double[][]{{4, 1}, {1, 3}});
            assertThrows(IllegalArgumentException.class, () -> resolver(metodo, A));
        }

        private double[] resolver(String metodo, Matriz Ab) {
            double[] x0 = new double[Ab.getCantFilas()];
            switch (metodo) {
                case "Jacobi": return MetodosIterativosMatriz.JacobiSolucion(Ab, x0, 1e-10, 100);
                case "GaussSeidel": return MetodosIterativosMatriz.GaussSeidelSolucion(Ab, x0, 1e-10, 100);
                case "SOR": return MetodosIterativosMatriz.SORSolucion(Ab, x0, 1.25, 1e-10, 100);
                case "Relajacion": return MetodosIterativosMatriz.Relajacion(Ab, x0, 1e-10, 100);
                default: return MetodosIterativosMatriz.MejoramientoIterativo(Ab, x0, 1e-10, 100);
            }
        }
    }
}
