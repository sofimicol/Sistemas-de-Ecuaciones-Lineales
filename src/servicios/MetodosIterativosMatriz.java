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

 // ==== Southwell (Relajación optimizado en memoria) ====
    public static double[] Relajacion(Matriz Ab, double[] x0, double tolerancia, int maxIter) {
        Matriz A = Ab.extraerA();
        verificarDiagonal(A);
        double[] b = Ab.extraerB();
        int n = A.getCantFilas();

        double[] x = x0.clone();
        double[] r = new double[n]; // Instanciamos en memoria UNA SOLA VEZ
        calcularResiduo(A, x, b, r);
        
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
            
            // Actualizamos solo la incógnita con mayor residuo
            x[m] = x[m] + r[m] / A.getElemento(m, m);
            
            // Reciclamos el arreglo 'r' sobreescribiendo sus valores
            calcularResiduo(A, x, b, r);
            convergio = Matriz.normaInfinitoVector(r) <= tolerancia;
        }

        if (!convergio) {
            System.out.println("Advertencia: Relajación no convergió en " + maxIter + " iteraciones.");
        }
        return x;
    }

 // ==== Residuo (Sin fugas de memoria) ====
    private static void calcularResiduo(Matriz A, double[] x, double[] b, double[] r) {
        int n = A.getCantFilas();
        for (int i = 0; i < n; i++) {
            double suma = 0.0;
            for (int j = 0; j < n; j++) {
                suma += A.getElemento(i, j) * x[j];
            }
            r[i] = b[i] - suma; // Se sobreescribe el espacio de memoria existente
        }
    }
    
    // ==== Chequeo Diagonal Dominante ====
    private static void verificarDiagonal(Matriz A) {
        int n = A.getCantFilas();
        boolean esDominante = true;
        
        for (int i = 0; i < n; i++) {
            double a_ii = Math.abs(A.getElemento(i, i));
            
            // Chequeo informático vital: evitar división por cero
            if (a_ii < EPSILON) {
                throw new ArithmeticException("Error: a_ii = 0 en la fila " + i + ". División por cero.");
            }
            
            // Chequeo matemático: Diagonal Estrictamente Dominante
            double sumaFila = 0.0;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    sumaFila += Math.abs(A.getElemento(i, j));
                }
            }
            if (a_ii <= sumaFila) {
                esDominante = false;
            }
        }
        
        if (!esDominante) {
            System.out.println("ADVERTENCIA TEÓRICA: La matriz no es estrictamente diagonal dominante. La convergencia de Jacobi/Gauss-Seidel no está garantizada.");
        }
    }
    
 // ==== Mejoramiento Iterativo ====
    public static double[] MejoramientoIterativo(Matriz Ab, double[] x0, double tolerancia, int maxIter) {
        Matriz A = Ab.extraerA();
        double[] b = Ab.extraerB();
        int n = A.getCantFilas();
        
        double[] x = x0.clone();
        double[] r = new double[n];
        
        // CORRECCIÓN: Usamos el método optimizado que no devuelve nada, sino que modifica 'r'
        calcularResiduo(A, x, b, r); 
        int iter = 0;
        
        while (Matriz.normaInfinitoVector(r) > tolerancia && iter < maxIter) {
            iter++;
            
            // Armamos la nueva matriz ampliada [A | r]
            Matriz Ar = new Matriz(n, n + 1);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    Ar.setElemento(i, j, A.getElemento(i, j));
                }
                Ar.setElemento(i, n, r[i]);
            }
            
            // Resolvemos Az = r usando Gauss con Pivoteo Parcial (ubicado en MetodosDirectosMatriz)
            double[] z = MetodosDirectosMatriz.GaussPivoteoParcial(Ar);
            
            // Sumamos el factor de corrección: x = x + z
            for (int i = 0; i < n; i++) {
                x[i] = x[i] + z[i];
            }
            
            // Recalculamos el residuo para la siguiente iteración reutilizando el arreglo 'r'
            calcularResiduo(A, x, b, r);
        }
        
        if (iter >= maxIter) {
            System.out.println("Advertencia: Mejoramiento Iterativo no convergió en " + maxIter + " iteraciones.");
        }
        return x;
    }
}
