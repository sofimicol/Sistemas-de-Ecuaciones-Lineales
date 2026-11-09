package soporte;

import static org.junit.jupiter.api.Assertions.assertEquals;

import modelo.Matriz;

// Aserciones y utilidades compartidas por los tests
public final class Verificar {

    private Verificar() {
    }

    public static void vectorCercano(double[] esperado, double[] real, double tolerancia) {
        assertEquals(esperado.length, real.length, "largo del vector");
        for (int i = 0; i < esperado.length; i++) {
            assertEquals(esperado[i], real[i], tolerancia, "x[" + i + "]");
        }
    }

    public static void matrizCercana(double[][] esperado, Matriz real, double tolerancia) {
        assertEquals(esperado.length, real.getCantFilas(), "cantidad de filas");
        assertEquals(esperado[0].length, real.getCantColumnas(), "cantidad de columnas");
        for (int i = 0; i < esperado.length; i++) {
            for (int j = 0; j < esperado[i].length; j++) {
                assertEquals(esperado[i][j], real.getElemento(i, j), tolerancia, "elemento [" + i + "][" + j + "]");
            }
        }
    }

    // Comparación exacta: para verificar que un método no modificó la matriz que recibió
    public static void matrizIgual(double[][] esperado, Matriz real) {
        matrizCercana(esperado, real, 0.0);
    }

    public static double errorInfinito(double[] x, double[] esperado) {
        double max = 0.0;
        for (int i = 0; i < x.length; i++) {
            max = Math.max(max, Math.abs(x[i] - esperado[i]));
        }
        return max;
    }

    // Arma la ampliada [A | b]
    public static double[][] ampliar(double[][] A, double[] b) {
        double[][] Ab = new double[A.length][A.length + 1];
        for (int i = 0; i < A.length; i++) {
            System.arraycopy(A[i], 0, Ab[i], 0, A.length);
            Ab[i][A.length] = b[i];
        }
        return Ab;
    }
}
