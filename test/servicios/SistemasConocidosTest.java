package servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.SistemasConocidos.ampliadaConSolucion;
import static soporte.SistemasConocidos.diagonalDominante;
import static soporte.SistemasConocidos.escalar;
import static soporte.SistemasConocidos.inversaPoisson1D;
import static soporte.SistemasConocidos.omegaOptimoPoisson1D;
import static soporte.SistemasConocidos.poisson1D;
import static soporte.SistemasConocidos.solucion;
import static soporte.SistemasConocidos.wilkinson;
import static soporte.Verificar.errorInfinito;
import static soporte.Verificar.matrizCercana;
import static soporte.Verificar.vectorCercano;

import java.util.stream.Stream;
import modelo.Matriz;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

// Sistemas de 50 x 50 y 100 x 100 con propiedades conocidas (ver soporte.SistemasConocidos)
class SistemasConocidosTest {

    private static final String[] DIRECTOS_GENERALES = {
        "Jordan", "JordanDiagonalUnitaria", "CroutL1U", "CroutLU1", "GaussSimple", "GaussParcial", "GaussTotal"
    };

    static Stream<Arguments> directosGenerales() {
        return combinar(DIRECTOS_GENERALES);
    }

    static Stream<Arguments> directosConCholesky() {
        String[] metodos = Stream.concat(Stream.of(DIRECTOS_GENERALES), Stream.of("CholeskyL", "CholeskyU")).toArray(String[]::new);
        return combinar(metodos);
    }

    private static Stream<Arguments> combinar(String[] metodos) {
        return Stream.of(50, 100).flatMap(n -> Stream.of(metodos).map(metodo -> Arguments.of(metodo, n)));
    }

    @Nested
    @DisplayName("Poisson 1D, tridiag(-1, 2, -1)")
    class Poisson {

        @ParameterizedTest(name = "n = {0}")
        @ValueSource(ints = {50, 100})
        @DisplayName("det(A) = n + 1")
        void determinante(int n) {
            assertEquals(n + 1, new Matriz(poisson1D(n)).determinante(), (n + 1) * 1e-12);
        }

        @ParameterizedTest(name = "n = {0}")
        @ValueSource(ints = {50, 100})
        @DisplayName("La inversa coincide con la fórmula cerrada")
        void inversa(int n) {
            matrizCercana(inversaPoisson1D(n), new Matriz(poisson1D(n)).inversa(), 1e-9);
        }

        // ||A|| = 4 y ||A^-1|| = max_i i(n+1-i)/2: cond = 1300 (n = 50) y 5100 (n = 100)
        @ParameterizedTest(name = "n = {0}")
        @ValueSource(ints = {50, 100})
        @DisplayName("cond(A) = 4 * max_i i(n+1-i)/2")
        void condicion(int n) {
            double maxFila = 0;
            for (int i = 1; i <= n; i++) {
                maxFila = Math.max(maxFila, i * (n + 1 - i) / 2.0);
            }
            assertEquals(4 * maxFila, new Matriz(poisson1D(n)).condicion(), 4 * maxFila * 1e-9);
        }

        @ParameterizedTest(name = "{0}, n = {1}")
        @MethodSource("servicios.SistemasConocidosTest#directosConCholesky")
        @DisplayName("Métodos directos")
        void directos(String metodo, int n) {
            vectorCercano(solucion(n), resolverDirecto(metodo, new Matriz(ampliadaConSolucion(poisson1D(n), solucion(n)))), 1e-10);
        }

        // El corte es por paso (||x_k+1 - x_k|| < tol): con convergencia lenta el error queda ~1000 veces por encima
        @ParameterizedTest(name = "{0}, n = {1}")
        @MethodSource("servicios.SistemasConocidosTest#iterativosPoisson")
        @DisplayName("Métodos iterativos")
        void iterativos(String metodo, int n) {
            Matriz Ab = new Matriz(ampliadaConSolucion(poisson1D(n), solucion(n)));
            double[] x0 = new double[n];
            double[] x;
            switch (metodo) {
                case "Jacobi": x = MetodosIterativosMatriz.JacobiSolucion(Ab, x0, 1e-12, 200000); break;
                case "GaussSeidel": x = MetodosIterativosMatriz.GaussSeidelSolucion(Ab, x0, 1e-12, 200000); break;
                case "SOR ω óptimo": x = MetodosIterativosMatriz.SORSolucion(Ab, x0, omegaOptimoPoisson1D(n), 1e-12, 200000); break;
                default: x = MetodosIterativosMatriz.MejoramientoIterativo(Ab, x0, 1e-12, 20);
            }
            vectorCercano(solucion(n), x, 1e-8);
        }

        @ParameterizedTest(name = "n = {0}")
        @ValueSource(ints = {50, 100})
        @DisplayName("Con 300 iteraciones, SOR con ω óptimo converge y Gauss-Seidel no")
        void sorOptimoAceleraAGaussSeidel(int n) {
            double[][] Ab = ampliadaConSolucion(poisson1D(n), solucion(n));
            double errorSOR = errorInfinito(MetodosIterativosMatriz.SORSolucion(new Matriz(Ab), new double[n], omegaOptimoPoisson1D(n), 0, 300), solucion(n));
            double errorGS = errorInfinito(MetodosIterativosMatriz.GaussSeidelSolucion(new Matriz(Ab), new double[n], 0, 300), solucion(n));
            assertTrue(errorSOR < 1e-5, "error SOR = " + errorSOR);
            assertTrue(errorGS > 0.1, "error Gauss-Seidel = " + errorGS);
        }
    }

    static Stream<Arguments> iterativosPoisson() {
        return combinar(new String[]{"Jacobi", "GaussSeidel", "SOR ω óptimo", "Mejoramiento"});
    }

    @Nested
    @DisplayName("Estrictamente diagonal dominante")
    class DiagonalDominante {

        @ParameterizedTest(name = "{0}, n = {1}")
        @MethodSource("servicios.SistemasConocidosTest#directosGenerales")
        @DisplayName("Métodos directos")
        void directos(String metodo, int n) {
            vectorCercano(solucion(n), resolverDirecto(metodo, new Matriz(ampliadaConSolucion(diagonalDominante(n), solucion(n)))), 1e-10);
        }

        @ParameterizedTest(name = "{0}, n = {1}")
        @MethodSource("servicios.SistemasConocidosTest#iterativosDiagonalDominante")
        @DisplayName("Métodos iterativos")
        void iterativos(String metodo, int n) {
            Matriz Ab = new Matriz(ampliadaConSolucion(diagonalDominante(n), solucion(n)));
            double[] x0 = new double[n];
            double[] x;
            switch (metodo) {
                case "Jacobi": x = MetodosIterativosMatriz.JacobiSolucion(Ab, x0, 1e-12, 1000); break;
                case "GaussSeidel": x = MetodosIterativosMatriz.GaussSeidelSolucion(Ab, x0, 1e-12, 1000); break;
                case "SOR ω = 0.9": x = MetodosIterativosMatriz.SORSolucion(Ab, x0, 0.9, 1e-12, 1000); break;
                case "Relajacion": x = MetodosIterativosMatriz.Relajacion(Ab, x0, 1e-12, 100000); break;
                default: x = MetodosIterativosMatriz.MejoramientoIterativo(Ab, x0, 1e-12, 20);
            }
            vectorCercano(solucion(n), x, 1e-10);
        }
    }

    static Stream<Arguments> iterativosDiagonalDominante() {
        return combinar(new String[]{"Jacobi", "GaussSeidel", "SOR ω = 0.9", "Relajacion", "Mejoramiento"});
    }

    static Stream<Arguments> directosPorEscala() {
        String[] metodos = Stream.concat(Stream.of(DIRECTOS_GENERALES), Stream.of("CholeskyL", "CholeskyU")).toArray(String[]::new);
        return Stream.of(1e-20, 1e20).flatMap(alfa -> Stream.of(metodos).map(metodo -> Arguments.of(metodo, alfa)));
    }

    // det(αA) = α^n det(A): con n = 50 y α = 1e-20 el determinante da 0 por underflow. Los criterios de cero
    // relativos a ||A|| no deben cambiar con la escala
    @Nested
    @DisplayName("Independencia de la escala (Poisson 1D de 50 x 50 escalada)")
    class Escala {

        @ParameterizedTest(name = "{0}, α = {1}")
        @MethodSource("servicios.SistemasConocidosTest#directosPorEscala")
        @DisplayName("Métodos directos sobre α * A")
        void directos(String metodo, double alfa) {
            int n = 50;
            Matriz Ab = new Matriz(ampliadaConSolucion(escalar(poisson1D(n), alfa), solucion(n)));
            vectorCercano(solucion(n), resolverDirecto(metodo, Ab), 1e-10);
        }

        @ParameterizedTest(name = "α = {0}")
        @ValueSource(doubles = {1e-20, 1e20})
        @DisplayName("Métodos iterativos sobre α * A")
        void iterativos(double alfa) {
            int n = 50;
            double[][] Ab = ampliadaConSolucion(escalar(poisson1D(n), alfa), solucion(n));
            vectorCercano(solucion(n), MetodosIterativosMatriz.SORSolucion(new Matriz(Ab), new double[n], omegaOptimoPoisson1D(n), 1e-12, 200000), 1e-8);
            vectorCercano(solucion(n), MetodosIterativosMatriz.GaussSeidelSolucion(new Matriz(Ab), new double[n], 1e-12, 200000), 1e-8);
            // Sin MejoramientoIterativo: corta con ||r|| <= tol absoluto y con α = 1e-20 ya el residuo inicial es menor
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("servicios.SistemasConocidosTest#directosConCholeskyN50")
        @DisplayName("0.1 * I de 50 x 50 (det = 1e-50) se resuelve")
        void identidadEscalada(String metodo) {
            int n = 50;
            double[][] A = new double[n][n];
            for (int i = 0; i < n; i++) {
                A[i][i] = 0.1;
            }
            vectorCercano(solucion(n), resolverDirecto(metodo, new Matriz(ampliadaConSolucion(A, solucion(n)))), 1e-12);
        }
    }

    static Stream<String> directosConCholeskyN50() {
        return Stream.concat(Stream.of(DIRECTOS_GENERALES), Stream.of("CholeskyL", "CholeskyU"));
    }

    @Nested
    @DisplayName("Matriz de Wilkinson (crecimiento 2^(n-1) con pivoteo parcial)")
    class Wilkinson {

        @ParameterizedTest(name = "n = {0}")
        @ValueSource(ints = {50, 100})
        @DisplayName("det(A) = 2^(n-1) y cond(A) = n: está bien condicionada")
        void propiedades(int n) {
            Matriz A = new Matriz(wilkinson(n));
            assertEquals(Math.pow(2, n - 1), A.determinante(), Math.pow(2, n - 1) * 1e-12);
            assertEquals(n, A.condicion(), n * 1e-9);
        }

        @ParameterizedTest(name = "n = {0}")
        @ValueSource(ints = {50, 100})
        @DisplayName("El pivoteo total la resuelve")
        void pivoteoTotal(int n) {
            vectorCercano(solucion(n), MetodosDirectosMatriz.GaussPivoteoTotal(new Matriz(ampliadaConSolucion(wilkinson(n), solucion(n)))), 1e-10);
        }

        // Con n = 50 los elementos llegan a 2^49 y todavía entran en los 53 bits de un double
        @Test
        @DisplayName("n = 50: el pivoteo parcial todavía la resuelve")
        void pivoteoParcialConN50() {
            int n = 50;
            vectorCercano(solucion(n), MetodosDirectosMatriz.GaussPivoteoParcial(new Matriz(ampliadaConSolucion(wilkinson(n), solucion(n)))), 1e-10);
        }

        // Inestabilidad del algoritmo, no del sistema: cond(A) = 100 y aun así se pierde toda la precisión
        @Test
        @DisplayName("n = 100: el pivoteo parcial pierde toda la precisión")
        void pivoteoParcialConN100() {
            int n = 100;
            double[] x = MetodosDirectosMatriz.GaussPivoteoParcial(new Matriz(ampliadaConSolucion(wilkinson(n), solucion(n))));
            assertTrue(errorInfinito(x, solucion(n)) > 1, "se esperaba pérdida de precisión: error = " + errorInfinito(x, solucion(n)));
        }

        @Test
        @DisplayName("n = 100: el mejoramiento iterativo recupera la solución")
        void mejoramientoConN100() {
            int n = 100;
            Matriz Ab = new Matriz(ampliadaConSolucion(wilkinson(n), solucion(n)));
            vectorCercano(solucion(n), MetodosIterativosMatriz.MejoramientoIterativo(Ab, new double[n], 1e-12, 20), 1e-10);
        }
    }

    private static double[] resolverDirecto(String metodo, Matriz Ab) {
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
}
