package soporte;

import java.util.Random;

// Sistemas de prueba de la bibliografía. Ninguna fuente fija b: se elige una solución x* y se arma b = A * x*
public final class SistemasConocidos {

    private SistemasConocidos() {
    }

    // Solución elegida x* = (1, 2, 3, 1, 2, 3, ...). No es constante, para detectar incógnitas desordenadas
    public static double[] solucion(int n) {
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = 1 + (i % 3);
        }
        return x;
    }

    // Poisson 1D, tridiag(-1, 2, -1): simétrica definida positiva, det = n + 1
    // (Burden & Faires §12.1; Golub & Van Loan cap. 4; Saad cap. 2)
    public static double[][] poisson1D(int n) {
        double[][] A = new double[n][n];
        for (int i = 0; i < n; i++) {
            A[i][i] = 2;
            if (i > 0) {
                A[i][i - 1] = -1;
            }
            if (i < n - 1) {
                A[i][i + 1] = -1;
            }
        }
        return A;
    }

    // Inversa cerrada de Poisson 1D (índices desde 1): (A^-1)[i][j] = min(i, j) * (n + 1 - max(i, j)) / (n + 1)
    public static double[][] inversaPoisson1D(int n) {
        double[][] inversa = new double[n][n];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                inversa[i - 1][j - 1] = (double) Math.min(i, j) * (n + 1 - Math.max(i, j)) / (n + 1);
            }
        }
        return inversa;
    }

    // ω óptimo de SOR para Poisson 1D: 2 / (1 + sin(π / (n + 1))) (Young, 1971; Burden & Faires cap. 7)
    public static double omegaOptimoPoisson1D(int n) {
        return 2 / (1 + Math.sin(Math.PI / (n + 1)));
    }

    // Matriz de Wilkinson: 1 en la diagonal, -1 debajo y 1 en la última columna. det = 2^(n-1), cond∞ = n.
    // Con pivoteo parcial los elementos crecen como 2^(n-1) (Wilkinson, 1961; Higham cap. 9)
    public static double[][] wilkinson(int n) {
        double[][] A = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                A[i][j] = -1;
            }
            A[i][i] = 1;
            A[i][n - 1] = 1;
        }
        return A;
    }

    // Estrictamente diagonal dominante, no simétrica: fuera de la diagonal valores en [-1, 1) con semilla fija
    // y a_ii = suma de |a_ij| + 1. Jacobi y Gauss-Seidel convergen (Golub & Van Loan cap. 11)
    public static double[][] diagonalDominante(int n) {
        Random azar = new Random(42);
        double[][] A = new double[n][n];
        for (int i = 0; i < n; i++) {
            double suma = 0.0;
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    A[i][j] = azar.nextDouble() * 2 - 1;
                    suma += Math.abs(A[i][j]);
                }
            }
            A[i][i] = suma + 1;
        }
        return A;
    }

    // Hilbert: H[i][j] = 1 / (i + j + 1). cond(A) crece como e^(3.5 n): ~3e10 con n = 8 y ~4e16 con n = 12,
    // donde ya supera 1/ε (Higham cap. 28)
    public static double[][] hilbert(int n) {
        double[][] H = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                H[i][j] = 1.0 / (i + j + 1);
            }
        }
        return H;
    }

    // α * A
    public static double[][] escalar(double[][] A, double alfa) {
        double[][] resultado = new double[A.length][];
        for (int i = 0; i < A.length; i++) {
            resultado[i] = new double[A[i].length];
            for (int j = 0; j < A[i].length; j++) {
                resultado[i][j] = alfa * A[i][j];
            }
        }
        return resultado;
    }

    // Arma [A | A * x]
    public static double[][] ampliadaConSolucion(double[][] A, double[] x) {
        int n = A.length;
        double[] b = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                b[i] += A[i][j] * x[j];
            }
        }
        return Verificar.ampliar(A, b);
    }
}
