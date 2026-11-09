package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Verificar.matrizCercana;
import static soporte.Verificar.matrizIgual;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MatrizTest {

    private static final double[][] IDENTIDAD_3 = {
        {1, 0, 0},
        {0, 1, 0},
        {0, 0, 1}
    };
    private static final double[][] AMPLIADA_2X3 = {
        {1, 2, 3},
        {4, 5, 6}
    };
    private static final double[][] NO_CUADRADA_2X4 = {
        {1, 2, 3, 4},
        {5, 6, 7, 8}
    };
    private static final double[][] SINGULAR_2X2 = {
        {1, 2},
        {2, 4}
    };
    // ||A|| = 2.0001, ||A^-1|| = 20001
    private static final double[][] MAL_CONDICIONADA = {
        {1, 1},
        {1, 1.0001}
    };

    private static void assertMensajeDeError(Exception e) {
        assertTrue(e.getMessage().startsWith("Error:"), "el mensaje debe empezar con 'Error:': " + e.getMessage());
    }

    @Nested
    @DisplayName("Construcción y acceso")
    class Construccion {

        @Test
        @DisplayName("Acceder fuera de rango lanza IndexOutOfBoundsException")
        void accesoFueraDeRango() {
            Matriz m = new Matriz(2, 2);
            assertThrows(IndexOutOfBoundsException.class, () -> m.getElemento(5, 0));
        }

        @Test
        @DisplayName("Una matriz irregular (filas de distinto largo) se rechaza")
        void matrizIrregular() {
            double[][] irregular = {
                {1, 2, 3},
                {1, 2},
                {1, 2, 3}
            };
            Exception e = assertThrows(IllegalArgumentException.class, () -> new Matriz(irregular));
            assertMensajeDeError(e);
        }

        @Test
        @DisplayName("Dimensiones no positivas se rechazan")
        void dimensionesNoPositivas() {
            assertThrows(IllegalArgumentException.class, () -> new Matriz(0, 3));
            assertThrows(IllegalArgumentException.class, () -> new Matriz(3, 0));
        }
    }

    @Nested
    @DisplayName("Determinante")
    class Determinante {

        @Test
        @DisplayName("2x2")
        void dosPorDos() {
            assertEquals(-2.0, new Matriz(new double[][]{{1, 2}, {3, 4}}).determinante(), 1e-9);
        }

        @Test
        @DisplayName("Triangular superior: producto de la diagonal")
        void triangularSuperior() {
            double[][] datos = {
                {2, 5, 1},
                {0, 3, 7},
                {0, 0, 4}
            };
            assertEquals(24.0, new Matriz(datos).determinante(), 1e-9);
        }

        @Test
        @DisplayName("Un intercambio de filas cambia el signo")
        void intercambioCambiaSigno() {
            assertEquals(-1.0, new Matriz(new double[][]{{0, 1}, {1, 0}}).determinante(), 1e-9);
        }

        @Test
        @DisplayName("Singular: det = 0, sin excepción")
        void singular() {
            double[][] datos = {
                {1, 2, 3},
                {2, 4, 6},
                {1, 1, 1}
            };
            assertEquals(0.0, new Matriz(datos).determinante(), 1e-9);
        }

        @Test
        @DisplayName("Una ampliada se rechaza")
        void ampliada() {
            double[][] datos = {
                {2, -1, -2, 1},
                {-4, 6, 3, 2},
                {-4, -2, 8, 3}
            };
            Exception e = assertThrows(UnsupportedOperationException.class, () -> new Matriz(datos).determinante());
            assertMensajeDeError(e);
        }

        @Test
        @DisplayName("Una no cuadrada se rechaza")
        void noCuadrada() {
            Exception e = assertThrows(UnsupportedOperationException.class, () -> new Matriz(NO_CUADRADA_2X4).determinante());
            assertMensajeDeError(e);
        }

        @Test
        @DisplayName("No modifica la matriz original")
        void noModificaLaOriginal() {
            double[][] datos = {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 9}
            };
            Matriz original = new Matriz(datos);
            original.determinante();
            matrizIgual(datos, original);
        }
    }

    @Nested
    @DisplayName("Inversa")
    class Inversa {

        @Test
        @DisplayName("2x2")
        void dosPorDos() {
            matrizCercana(new double[][]{{0.6, -0.7}, {-0.2, 0.4}}, new Matriz(new double[][]{{4, 7}, {2, 6}}).inversa(), 1e-9);
        }

        @Test
        @DisplayName("La inversa de la identidad es la identidad")
        void identidad() {
            matrizCercana(IDENTIDAD_3, new Matriz(IDENTIDAD_3).inversa(), 1e-9);
        }

        @Test
        @DisplayName("Requiere pivoteo")
        void requierePivoteo() {
            double[][] permutacion = {{0, 1}, {1, 0}};
            matrizCercana(permutacion, new Matriz(permutacion).inversa(), 1e-9);
        }

        @Test
        @DisplayName("A * A^-1 = I (3x3 con pivote cero)")
        void productoEsIdentidad() {
            double[][] datos = {
                {0, 2, 1},
                {1, 1, 1},
                {2, 1, 3}
            };
            Matriz inv = new Matriz(datos).inversa();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    double suma = 0.0;
                    for (int s = 0; s < 3; s++) {
                        suma += datos[i][s] * inv.getElemento(s, j);
                    }
                    assertEquals(i == j ? 1.0 : 0.0, suma, 1e-9, "(A * A^-1)[" + i + "][" + j + "]");
                }
            }
        }

        @Test
        @DisplayName("Singular lanza ArithmeticException")
        void singular() {
            assertThrows(ArithmeticException.class, () -> new Matriz(SINGULAR_2X2).inversa());
        }

        @Test
        @DisplayName("Ampliada y no cuadrada se rechazan")
        void noCuadradas() {
            assertMensajeDeError(assertThrows(UnsupportedOperationException.class, () -> new Matriz(AMPLIADA_2X3).inversa()));
            assertMensajeDeError(assertThrows(UnsupportedOperationException.class, () -> new Matriz(NO_CUADRADA_2X4).inversa()));
        }

        @Test
        @DisplayName("No modifica la matriz original")
        void noModificaLaOriginal() {
            double[][] datos = {
                {0, 2, 1},
                {1, 1, 1},
                {2, 1, 3}
            };
            Matriz original = new Matriz(datos);
            original.inversa();
            matrizIgual(datos, original);
        }
    }

    @Nested
    @DisplayName("Norma infinito")
    class Norma {

        @Test
        @DisplayName("2x2")
        void dosPorDos() {
            assertEquals(7.0, new Matriz(new double[][]{{1, 2}, {3, 4}}).norma(), 1e-9);
        }

        @Test
        @DisplayName("Con negativos: máxima suma por filas (por columnas daría 12)")
        void conNegativos() {
            double[][] datos = {
                {1, 2, 3},
                {-4, 5, -6},
                {7, 0, 1}
            };
            assertEquals(15.0, new Matriz(datos).norma(), 1e-9);
        }

        @Test
        @DisplayName("Identidad y matriz nula")
        void identidadYNula() {
            assertEquals(1.0, new Matriz(IDENTIDAD_3).norma(), 1e-9);
            assertEquals(0.0, new Matriz(new double[][]{{0, 0}, {0, 0}}).norma(), 1e-9);
        }

        @Test
        @DisplayName("Una ampliada se rechaza")
        void ampliada() {
            assertMensajeDeError(assertThrows(UnsupportedOperationException.class, () -> new Matriz(AMPLIADA_2X3).norma()));
        }
    }

    @Nested
    @DisplayName("Número de condición")
    class Condicion {

        @Test
        @DisplayName("cond(I) = 1")
        void identidad() {
            assertEquals(1.0, new Matriz(IDENTIDAD_3).condicion(), 1e-6);
        }

        @Test
        @DisplayName("2x2: ||A|| = 11, ||A^-1|| = 1.3")
        void dosPorDos() {
            assertEquals(14.3, new Matriz(new double[][]{{4, 7}, {2, 6}}).condicion(), 14.3 * 1e-6);
        }

        @Test
        @DisplayName("Mal condicionada")
        void malCondicionada() {
            assertEquals(40004.0001, new Matriz(MAL_CONDICIONADA).condicion(), 40004.0001 * 1e-6);
        }

        @Test
        @DisplayName("Singular lanza ArithmeticException")
        void singular() {
            assertThrows(ArithmeticException.class, () -> new Matriz(SINGULAR_2X2).condicion());
        }

        @Test
        @DisplayName("Una ampliada se rechaza")
        void ampliada() {
            Exception e = assertThrows(UnsupportedOperationException.class, () -> new Matriz(AMPLIADA_2X3).condicion());
            assertTrue(e.getMessage().startsWith("Error: El número de condición"), e.getMessage());
        }

        // Apunte de la cátedra, pág. 18: cond(A) = 6.67 * 1138.6758 = 7594.967585 (multiplica valores redondeados)
        @Test
        @DisplayName("Sistema sensible del apunte de la cátedra")
        void sistemaSensibleCatedra() {
            Matriz A = new Matriz(new double[][]{
                {3.02, -1.05, 2.53},
                {4.33, 0.56, -1.78},
                {-0.83, -0.54, 1.47}
            });
            assertEquals(6.67, A.norma(), 1e-12);
            assertEquals(1138.6758, A.inversa().norma(), 1e-4);
            assertEquals(7594.9676, A.condicion(), 1e-3);
        }
    }

    @Nested
    @DisplayName("Bien condicionado (cond(A) <= MAX_CONDICION)")
    class BienCondicionado {

        @Test
        @DisplayName("Identidad (cond = 1) y 2x2 (cond = 14.3)")
        void bienCondicionadas() {
            assertTrue(new Matriz(IDENTIDAD_3).bienCondicionado());
            assertTrue(new Matriz(new double[][]{{4, 7}, {2, 6}}).bienCondicionado());
        }

        @Test
        @DisplayName("En el límite (cond = 50) está bien condicionada")
        void enElLimite() {
            assertTrue(new Matriz(new double[][]{{50, 0}, {0, 1}}).bienCondicionado());
        }

        @Test
        @DisplayName("Mal condicionada (cond = 40004)")
        void malCondicionada() {
            assertFalse(new Matriz(MAL_CONDICIONADA).bienCondicionado());
        }

        @Test
        @DisplayName("Singular: devuelve false sin excepción")
        void singular() {
            assertFalse(new Matriz(SINGULAR_2X2).bienCondicionado());
        }

        @Test
        @DisplayName("Una ampliada se rechaza")
        void ampliada() {
            assertThrows(UnsupportedOperationException.class, () -> new Matriz(AMPLIADA_2X3).bienCondicionado());
        }
    }

    @Nested
    @DisplayName("Transpuesta")
    class Transpuesta {

        @Test
        @DisplayName("Rectangular 2x3 -> 3x2")
        void rectangular() {
            matrizIgual(new double[][]{{1, 4}, {2, 5}, {3, 6}}, new Matriz(AMPLIADA_2X3).transponer());
        }

        @Test
        @DisplayName("Cuadrada 3x3")
        void cuadrada() {
            double[][] datos = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
            };
            matrizIgual(new double[][]{{1, 4, 7}, {2, 5, 8}, {3, 6, 9}}, new Matriz(datos).transponer());
        }

        @Test
        @DisplayName("Una simétrica queda igual")
        void simetrica() {
            double[][] datos = {{2, -1}, {-1, 3}};
            matrizIgual(datos, new Matriz(datos).transponer());
        }

        @Test
        @DisplayName("Vector fila 1x3 -> columna 3x1")
        void vectorFila() {
            matrizIgual(new double[][]{{1}, {2}, {3}}, new Matriz(new double[][]{{1, 2, 3}}).transponer());
        }

        @Test
        @DisplayName("(A^T)^T = A y la original no se modifica")
        void dobleTranspuesta() {
            Matriz original = new Matriz(AMPLIADA_2X3);
            matrizIgual(AMPLIADA_2X3, original.transponer().transponer());
            matrizIgual(AMPLIADA_2X3, original);
        }
    }

    @Test
    @DisplayName("esSimetrica detecta simétrica, no simétrica y no cuadrada")
    void esSimetrica() {
        assertTrue(new Matriz(new double[][]{{4, 12, -16}, {12, 37, -43}, {-16, -43, 98}}).esSimetrica());
        assertFalse(new Matriz(new double[][]{{1, 2}, {3, 4}}).esSimetrica());
        assertFalse(new Matriz(new double[][]{{1, 2, 3}, {2, 1, 3}}).esSimetrica());
    }
}
