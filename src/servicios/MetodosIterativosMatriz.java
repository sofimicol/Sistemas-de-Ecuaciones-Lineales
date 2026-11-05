package servicios;

import modelo.Matriz;

public class MetodosIterativosMatriz {

    private static final double EPSILON = 1e-12;

    // ==== Jacobi ====
    public static double[] JacobiSolucion(Matriz Ab, double[] x0, double tolerancia, int maxIter) {
        Matriz A = Ab.extraerA();
        verificarDiagonal(A);
        double[] b = Ab.extraerB();
        int n = A.getCantFilas();

        double[] x = x0.clone();
        double[] xAnt = new double[n];
        int iter = 0;
        boolean convergio = false;

        do {
            iter++;
            System.arraycopy(x, 0, xAnt, 0, n);
            for (int i = 0; i < n; i++) {
                double suma = b[i];
                for (int j = 0; j < n; j++) {
                    if (j != i) {
                        suma -= A.getElemento(i, j) * xAnt[j];
                    }
                }
                x[i] = suma / A.getElemento(i, i);
            }
            double[] dif = new double[n];
            for (int i = 0; i < n; i++) {
                dif[i] = x[i] - xAnt[i];
            }
            convergio = Matriz.normaInfinitoVector(dif) < tolerancia;
        } while (!convergio && iter < maxIter);

        if (!convergio) {
            System.out.println("Advertencia: Jacobi no convergió en " + maxIter + " iteraciones.");
        }
        return x;
    }

    // ==== Gauss-Seidel ====
    public static double[] GaussSeidelSolucion(Matriz Ab, double[] x0, double tolerancia, int maxIter) {
        Matriz A = Ab.extraerA();
        verificarDiagonal(A);
        double[] b = Ab.extraerB();
        int n = A.getCantFilas();

        double[] x = x0.clone();
        double[] xAnt = new double[n];
        int iter = 0;
        boolean convergio = false;

        do {
            iter++;
            System.arraycopy(x, 0, xAnt, 0, n);
            for (int i = 0; i < n; i++) {
                double suma = b[i];
                for (int j = 0; j < i; j++) {
                    suma -= A.getElemento(i, j) * x[j];
                }
                for (int j = i + 1; j < n; j++) {
                    suma -= A.getElemento(i, j) * xAnt[j];
                }
                x[i] = suma / A.getElemento(i, i);
            }
            double[] dif = new double[n];
            for (int i = 0; i < n; i++) {
                dif[i] = x[i] - xAnt[i];
            }
            convergio = Matriz.normaInfinitoVector(dif) < tolerancia;
        } while (!convergio && iter < maxIter);

        if (!convergio) {
            System.out.println("Advertencia: Gauss-Seidel no convergió en " + maxIter + " iteraciones.");
        }
        return x;
    }

    // ==== SOR ====
    public static double[] SORSolucion(Matriz Ab, double[] x0, double omega, double tolerancia, int maxIter) {
        if (omega <= 0 || omega >= 2) {
            throw new IllegalArgumentException("Error: omega debe estar estrictamente en (0, 2).");
        }
        Matriz A = Ab.extraerA();
        verificarDiagonal(A);
        double[] b = Ab.extraerB();
        int n = A.getCantFilas();

        double[] x = x0.clone();
        double[] xAnt = new double[n];
        int iter = 0;
        boolean convergio = false;

        do {
            iter++;
            System.arraycopy(x, 0, xAnt, 0, n);
            for (int i = 0; i < n; i++) {
                double suma = b[i];
                for (int j = 0; j < i; j++) {
                    suma -= A.getElemento(i, j) * x[j];
                }
                for (int j = i + 1; j < n; j++) {
                    suma -= A.getElemento(i, j) * xAnt[j];
                }
                double xGS = suma / A.getElemento(i, i);
                x[i] = (1.0 - omega) * xAnt[i] + omega * xGS;
            }
            double[] dif = new double[n];
            for (int i = 0; i < n; i++) {
                dif[i] = x[i] - xAnt[i];
            }
            convergio = Matriz.normaInfinitoVector(dif) < tolerancia;
        } while (!convergio && iter < maxIter);

        if (!convergio) {
            System.out.println("Advertencia: SOR (ω=" + omega + ") no convergió en " + maxIter + " iteraciones.");
        }
        return x;
    }

    // ==== Southwell ====
    public static double[] Relajacion(Matriz Ab, double[] x0, double tolerancia, int maxIter) {
        Matriz A = Ab.extraerA();
        verificarDiagonal(A);
        double[] b = Ab.extraerB();
        int n = A.getCantFilas();

        double[] x = x0.clone();
        double[] r = calcularResiduo(A, x, b);
        int iter = 0;
        boolean convergio = Matriz.normaInfinitoVector(r) <= tolerancia;

        while (!convergio && iter < maxIter) {
            iter++;
            int m = 0;
            double maxAbs = Math.abs(r[0]);
            for (int i = 1; i < n; i++) {
                double abs = Math.abs(r[i]);
                if (abs > maxAbs) {
                    maxAbs = abs;
                    m = i;
                }
            }
            x[m] = x[m] + r[m] / A.getElemento(m, m);
            r = calcularResiduo(A, x, b);
            convergio = Matriz.normaInfinitoVector(r) <= tolerancia;
        }

        if (!convergio) {
            System.out.println("Advertencia: Relajación no convergió en " + maxIter + " iteraciones.");
        }
        return x;
    }

    // ==== Chequeo mínimo: a_ii ≠ 0 ====
    private static void verificarDiagonal(Matriz A) {
        for (int i = 0; i < A.getCantFilas(); i++) {
            if (Math.abs(A.getElemento(i, i)) < EPSILON) {
                throw new ArithmeticException("Error: a_ii = 0 en la fila " + i + ". División por cero.");
            }
        }
    }

    // ==== Residuo ====
    private static double[] calcularResiduo(Matriz A, double[] x, double[] b) {
        int n = A.getCantFilas();
        double[] r = new double[n];
        for (int i = 0; i < n; i++) {
            double suma = 0.0;
            for (int j = 0; j < n; j++) {
                suma += A.getElemento(i, j) * x[j];
            }
            r[i] = b[i] - suma;
        }
        return r;
    }
}
