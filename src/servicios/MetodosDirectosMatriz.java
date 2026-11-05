package servicios;

import modelo.Matriz;

public class MetodosDirectosMatriz {
    //SUPONIENDO QUE SE VALIDA EN MAIN QUE EL USUARIO INGRESA UNA MATRIZ AMPLIADA, NO SE VALIDA EN LOS METODOS
    public static Matriz[] factorizarLU(Matriz A) {
        int n = A.getCantFilas();
        Matriz L = new Matriz(n, n);
        Matriz U = new Matriz(n, n);

        for (int i = 0; i < n; i++) {
            L.setElemento(i, i, 1.0);
        }

        for (int k = 0; k < n; k++) {
          //Reunion 4: Validación de Pivote Cero antes de dividir ---
            if (Math.abs(A.getElemento(k, k)) < 1e-12) {
            throw new ArithmeticException("Pivote cero en la fila " + k + ". LU sin pivoteo no es aplicable.");
            }
        // ---------------------------------------------------------
            for (int j = k; j < n; j++) {
                double suma = 0.0;
                for (int s = 0; s < k; s++) {
                    suma += L.getElemento(k, s) * U.getElemento(s, j);
                }
                U.setElemento(k, j, A.getElemento(k, j) - suma);
            }
            if (Math.abs(U.getElemento(k, k)) < 1e-12) {
              throw new ArithmeticException("Pivote cero en fila " + k + ". La matriz es singular.");
            }

            for (int i = k + 1; i < n; i++) {
                double suma = 0.0;
                for (int s = 0; s < k; s++) {
                    suma += L.getElemento(i, s) * U.getElemento(s, k);
                }
            // ------------------------------------------------------------------------------
                L.setElemento(i, k, (A.getElemento(i, k) - suma) / U.getElemento(k, k));
            }
        }

        return new Matriz[]{L, U};
    }
    
    public static double[] JordanSolucion(Matriz Ab, int n) {
    validarSolucionUnica(Ab);
    double[] x = new double[n];

    for(int k = 0; k < n; k++) {
      // Reunion 4- Falta de pivoteos
      // 1. Verificamos si el pivote actual es cero
      if (Math.abs(Ab.getElemento(k, k)) < 1e-12) {
          Ab.pivoteoParcial(k); 
            // 3. Verificamos si DESPUÉS del pivoteo sigue siendo cero
          if (Math.abs(Ab.getElemento(k, k)) < 1e-12) {
              throw new ArithmeticException("Sistema singular: No tiene solución única (pivote nulo en fila " + k + ").");
          }
      }
      for(int i = 0; i < n; i++) {
        if(i!=k) {
          double m  = Ab.getElemento(i, k) / Ab.getElemento(k, k);
          for(int j = k; j < n+1; j++) {
            Ab.setElemento(i, j, Ab.getElemento(i,j) - m * Ab.getElemento(k, j));
          }
        }
      }
    }

    x = Ab.sistDiagonal();

    return x;
  }

  public static double[] JordanSolucionDiagonalUnitaria(Matriz Ab, int n) {
    validarSolucionUnica(Ab);
    double[] x = new double[n];

    for(int k = 0; k < n; k++) {
      // Reunion 4- Falta de pivoteos
      // 1. Verificamos si el pivote actual es cero
      if (Math.abs(Ab.getElemento(k, k)) < 1e-12) {
          Ab.pivoteoParcial(k);
          if (Math.abs(Ab.getElemento(k, k)) < 1e-12) {
              throw new ArithmeticException("Sistema singular: No tiene solución única (pivote nulo en fila " + k + ").");
          }
      }
      double pivot = Ab.getElemento(k, k);
      for(int j = 0; j < n+1; j++) {
        Ab.setElemento(k, j, Ab.getElemento(k, j) / pivot);
      }

      for(int i = 0; i < n; i++) {
        if(i!=k) {
          double m = Ab.getElemento(i, k);
          for(int j = k; j < n+1; j++) {
            Ab.setElemento(i, j, Ab.getElemento(i, j) - m * Ab.getElemento(k, j));
          }
        }
      }
    }

    x = Ab.sistDiagonal();

    return x;
  }

  // Crout: A = L * U1, con diagonal de U1 unitaria
  public static Matriz[] factorizarCroutLU1(Matriz A) {
    int n = A.getCantFilas();
    Matriz L = new Matriz(n, n);
    Matriz U1 = new Matriz(n, n);

    for (int i = 0; i < n; i++) {
      U1.setElemento(i, i, 1.0);
    }

    for (int k = 0; k < n; k++) {
      // Columna k de L
      for (int i = k; i < n; i++) {
        double suma = 0.0;
        for (int p = 0; p < k; p++) {
          suma += L.getElemento(i, p) * U1.getElemento(p, k);
        }
        L.setElemento(i, k, A.getElemento(i, k) - suma);
      }
      if (Math.abs(L.getElemento(k, k)) < 1e-12) {
        throw new ArithmeticException("Error: Pivote nulo en la fila " + k + ". Crout sin pivoteo no es aplicable.");
      }

      // Fila k de U1
      for (int j = k + 1; j < n; j++) {
        double suma = 0.0;
        for (int p = 0; p < k; p++) {
          suma += L.getElemento(k, p) * U1.getElemento(p, j);
        }
        U1.setElemento(k, j, (A.getElemento(k, j) - suma) / L.getElemento(k, k));
      }
    }

    // Retorna [L, U1] tal que A = L * U1:
    // [0] L: triangular inferior; su diagonal contiene los pivotes
    // [1] U1: triangular superior con diagonal unitaria
    return new Matriz[]{L, U1};
  }

  // Crout: A = L1 * U, con diagonal de L1 unitaria
  public static Matriz[] factorizarCroutL1U(Matriz A) {
    int n = A.getCantFilas();
    Matriz L1 = new Matriz(n, n);
    Matriz U = new Matriz(n, n);

    for (int i = 0; i < n; i++) {
      L1.setElemento(i, i, 1.0);
    }

    for (int k = 0; k < n; k++) {
      // Fila k de U
      for (int j = k; j < n; j++) {
        double suma = 0.0;
        for (int p = 0; p < k; p++) {
          suma += L1.getElemento(k, p) * U.getElemento(p, j);
        }
        U.setElemento(k, j, A.getElemento(k, j) - suma);
      }
      if (Math.abs(U.getElemento(k, k)) < 1e-12) {
        throw new ArithmeticException("Error: Pivote nulo en la fila " + k + ". Crout sin pivoteo no es aplicable.");
      }

      // Columna k de L1
      for (int i = k + 1; i < n; i++) {
        double suma = 0.0;
        for (int p = 0; p < k; p++) {
          suma += L1.getElemento(i, p) * U.getElemento(p, k);
        }
        L1.setElemento(i, k, (A.getElemento(i, k) - suma) / U.getElemento(k, k));
      }
    }

    // Retorna [L1, U] tal que A = L1 * U:
    // [0] L1: triangular inferior con diagonal unitaria
    // [1] U: triangular superior; su diagonal contiene los pivotes
    return new Matriz[]{L1, U};
  }

  // Crout L1U sobre [A | b]: la columna b se procesa con la fila de U y queda como y (L1 * y = b)
  public static double[] CroutSolucionL1U(Matriz Ab) {
    validarSolucionUnica(Ab);
    int n = Ab.getCantFilas();
    Matriz L1 = new Matriz(n, n);
    Matriz Uy = new Matriz(n, n + 1); // [U | y]

    for (int i = 0; i < n; i++) {
      L1.setElemento(i, i, 1.0);
    }

    for (int k = 0; k < n; k++) {
      // Fila k de U, incluyendo la columna de b
      for (int j = k; j < n + 1; j++) {
        double suma = 0.0;
        for (int p = 0; p < k; p++) {
          suma += L1.getElemento(k, p) * Uy.getElemento(p, j);
        }
        Uy.setElemento(k, j, Ab.getElemento(k, j) - suma);
      }
      if (Math.abs(Uy.getElemento(k, k)) < 1e-12) {
        throw new ArithmeticException("Error: Pivote nulo en la fila " + k + ". Crout sin pivoteo no es aplicable.");
      }

      // Columna k de L1
      for (int i = k + 1; i < n; i++) {
        double suma = 0.0;
        for (int p = 0; p < k; p++) {
          suma += L1.getElemento(i, p) * Uy.getElemento(p, k);
        }
        L1.setElemento(i, k, (Ab.getElemento(i, k) - suma) / Uy.getElemento(k, k));
      }
    }

    // Uy = [U | y] ya contiene y (solución de L1 * y = b), calculada en el bucle de U.
    // Retorna x, la solución de A * x = b, resolviendo U * x = y por sustitución regresiva
    return Uy.sustRegresiva();
  }

  // Crout LU1 sobre [A | b]: la columna b se procesa con la fila de U1 y queda como y (L * y = b)
  public static double[] CroutSolucionLU1(Matriz Ab) {
    validarSolucionUnica(Ab);
    int n = Ab.getCantFilas();
    Matriz L = new Matriz(n, n);
    Matriz U1y = new Matriz(n, n + 1); // [U1 | y]

    for (int i = 0; i < n; i++) {
      U1y.setElemento(i, i, 1.0);
    }

    for (int k = 0; k < n; k++) {
      // Columna k de L
      for (int i = k; i < n; i++) {
        double suma = 0.0;
        for (int p = 0; p < k; p++) {
          suma += L.getElemento(i, p) * U1y.getElemento(p, k);
        }
        L.setElemento(i, k, Ab.getElemento(i, k) - suma);
      }
      if (Math.abs(L.getElemento(k, k)) < 1e-12) {
        throw new ArithmeticException("Error: Pivote nulo en la fila " + k + ". Crout sin pivoteo no es aplicable.");
      }

      // Fila k de U1, incluyendo la columna de b
      for (int j = k + 1; j < n + 1; j++) {
        double suma = 0.0;
        for (int p = 0; p < k; p++) {
          suma += L.getElemento(k, p) * U1y.getElemento(p, j);
        }
        U1y.setElemento(k, j, (Ab.getElemento(k, j) - suma) / L.getElemento(k, k));
      }
    }

    // U1y = [U1 | y] ya contiene y (solución de L * y = b), calculada en el bucle de U1.
    // Retorna x, la solución de A * x = b, resolviendo U1 * x = y por sustitución regresiva
    return U1y.sustRegresiva();
  }

  // Cholesky A = L * L^T para resolver A * x = b. La "fila adicional" de la cátedra (la que contiene a b)
  // se guarda en el vector y, porque una matriz de (n+1) x n superaría MAX_DIMENSION con n = 100
  public static double[] CholeskySolucionL(Matriz Ab) {
    int n = Ab.getCantFilas();
    Matriz A = separarA(Ab);
    double[] b = separarB(Ab);
    validarSolucionUnica(Ab);
    Matriz L = new Matriz(n, n);
    double[] y = new double[n];

    for (int k = 0; k < n; k++) {
      // Diagonal: L[k,k] = raiz(a[k,k] - suma de L[k,j]^2)
      double suma = 0.0;
      for (int j = 0; j < k; j++) {
        suma += L.getElemento(k, j) * L.getElemento(k, j);
      }
      L.setElemento(k, k, raizCholesky(A.getElemento(k, k) - suma, k));

      // Columna k de L, por debajo de la diagonal
      for (int i = k + 1; i < n; i++) {
        suma = 0.0;
        for (int j = 0; j < k; j++) {
          suma += L.getElemento(i, j) * L.getElemento(k, j);
        }
        L.setElemento(i, k, (A.getElemento(i, k) - suma) / L.getElemento(k, k));
      }

      // Fila adicional (i = n+1 de la cátedra): misma fórmula con b, deja y_k (L * y = b)
      suma = 0.0;
      for (int j = 0; j < k; j++) {
        suma += y[j] * L.getElemento(k, j);
      }
      y[k] = (b[k] - suma) / L.getElemento(k, k);
    }

    // Armamos la ampliada [L^T | y]: L^T es triangular superior
    Matriz LTy = L.transponer();
    LTy.setCantColumnas(n + 1);
    for (int k = 0; k < n; k++) {
      LTy.setElemento(k, n, y[k]);
    }

    // y ya resuelve L * y = b. Retorna x, la solución de A * x = b,
    // resolviendo L^T * x = y por sustitución regresiva
    return LTy.sustRegresiva();
  }

  // Cholesky A = U^T * U para resolver A * x = b: la columna b se procesa con la fila de U y queda como y
  public static double[] CholeskySolucionU(Matriz Ab) {
    int n = Ab.getCantFilas();
    Matriz A = separarA(Ab);
    double[] b = separarB(Ab);
    validarSolucionUnica(Ab);
    Matriz Uy = new Matriz(n, n + 1); // [U | y]

    for (int k = 0; k < n; k++) {
      // Diagonal: U[k,k] = raiz(a[k,k] - suma de U[i,k]^2), columna k por encima de la diagonal
      double suma = 0.0;
      for (int i = 0; i < k; i++) {
        suma += Uy.getElemento(i, k) * Uy.getElemento(i, k);
      }
      Uy.setElemento(k, k, raizCholesky(A.getElemento(k, k) - suma, k));

      // Fila k de U, incluyendo la columna de b
      for (int j = k + 1; j < n + 1; j++) {
        suma = 0.0;
        for (int i = 0; i < k; i++) {
          suma += Uy.getElemento(i, j) * Uy.getElemento(i, k);
        }
        // La columna n es la de b
        double a = (j < n) ? A.getElemento(k, j) : b[k];
        Uy.setElemento(k, j, (a - suma) / Uy.getElemento(k, k));
      }
    }

    // Uy = [U | y] ya contiene y (solución de U^T * y = b), calculada en el bucle de U.
    // Retorna x, la solución de A * x = b, resolviendo U * x = y por sustitución regresiva
    return Uy.sustRegresiva();
  }

  // Regla de entrada de los métodos que resuelven A * x = b: si det(A) = 0 el sistema
  // no tiene solución única (infinitas soluciones o ninguna)
  private static void validarSolucionUnica(Matriz Ab) {
    if (Math.abs(Ab.extraerA().determinante()) < 1e-12) {
      throw new ArithmeticException("Error: det(A) = 0, el sistema no tiene solución única.");
    }
  }

  // Separa la parte A (n x n) de la ampliada [A | b], validando la forma y que A sea simétrica
  private static Matriz separarA(Matriz Ab) {
    int n = Ab.getCantFilas();
    if (Ab.getCantColumnas() != n + 1) {
      throw new UnsupportedOperationException("Error: Cholesky requiere la matriz ampliada [A | b] de n x (n+1).");
    }
    Matriz A = new Matriz(n, n);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        A.setElemento(i, j, Ab.getElemento(i, j));
      }
    }
    if (!A.esSimetrica()) {
      throw new UnsupportedOperationException("Error: Cholesky requiere que A sea simétrica.");
    }
    return A;
  }

  // Separa la columna b (la última) de la ampliada [A | b]
  private static double[] separarB(Matriz Ab) {
    int n = Ab.getCantFilas();
    double[] b = new double[n];
    for (int i = 0; i < n; i++) {
      b[i] = Ab.getElemento(i, n);
    }
    return b;
  }

  // Cláusula de escape: si el radicando no es positivo, A no es definida positiva
  private static double raizCholesky(double radicando, int k) {
    if (radicando < 1e-12) {
      throw new ArithmeticException(
          "Error: La matriz no es definida positiva (radicando no positivo en la fila " + k + ").");
    }
    return Math.sqrt(radicando);
  }
}

  
