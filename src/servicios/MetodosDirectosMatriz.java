package servicios;
import modelo.Matriz; 
public class MetodosDirectosMatriz {
	// ==========================================
    // MÉTODO 1: factorizarLU 
    // ==========================================
    public static Matriz[] factorizarLU(Matriz A_original) {
        Matriz A = A_original.copiar();
        int n = A.getCantFilas();
        Matriz L = new Matriz(n, n);
        Matriz U = new Matriz(n, n);

        for (int i = 0; i < n; i++) {
            L.setElemento(i, i, 1.0);
        }

        for (int k = 0; k < n; k++) {
            // El cálculo de U se hace primero
            for (int j = k; j < n; j++) {
                double suma = 0.0;
                for (int s = 0; s < k; s++) {
                    suma += L.getElemento(k, s) * U.getElemento(s, j);
                }
                U.setElemento(k, j, A.getElemento(k, j) - suma);
            }
            
            // FIX: La validación del pivote nulo DEBE hacerse sobre U, no sobre A
            if (Math.abs(U.getElemento(k, k)) < 1e-12) {
              throw new ArithmeticException("Pivote cero en fila " + k + ". La matriz es singular.");
            }

            for (int i = k + 1; i < n; i++) {
                double suma = 0.0;
                for (int s = 0; s < k; s++) {
                    suma += L.getElemento(i, s) * U.getElemento(s, k);
                }
                L.setElemento(i, k, (A.getElemento(i, k) - suma) / U.getElemento(k, k));
            }
        }
        return new Matriz[]{L, U};
    }
	    
 // ==========================================
    // MÉTODOS GAUSS-JORDAN (Inmutabilidad añadida)
    // ==========================================
    public static double[] JordanSolucion(Matriz Ab_original, int n) {
        validarSolucionUnica(Ab_original);
        Matriz Ab = Ab_original.copiar(); // FIX: Evita destruir la matriz de la Interfaz Gráfica
        double[] x = new double[n];

        for(int k = 0; k < n; k++) {
            if (Math.abs(Ab.getElemento(k, k)) < 1e-12) {
                Ab.pivoteoParcial(k); 
                if (Math.abs(Ab.getElemento(k, k)) < 1e-12) {
                    throw new ArithmeticException("Sistema singular: No tiene solución única (pivote nulo en fila " + k + ").");
                }
            }
            for(int i = 0; i < n; i++) {
                if(i != k) {
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

    public static double[] JordanSolucionDiagonalUnitaria(Matriz Ab_original, int n) {
        validarSolucionUnica(Ab_original);
        Matriz Ab = Ab_original.copiar(); // FIX: Evita destruir la matriz de la Interfaz Gráfica
        double[] x = new double[n];

        for(int k = 0; k < n; k++) {
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
                if(i != k) {
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
	  // se guarda en el vector y
	  public static double[] CholeskySolucionL(Matriz Ab) {
	    int n = Ab.getCantFilas();
	    Matriz A = Ab.extraerA();
	    double[] b = Ab.extraerB();
	    validarSimetrica(A);
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
	    Matriz A = Ab.extraerA();
	    double[] b = Ab.extraerB();
	    validarSimetrica(A);
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

	  private static void validarSimetrica(Matriz A) {
	    if (!A.esSimetrica()) {
	      throw new UnsupportedOperationException("Error: Cholesky requiere que A sea simétrica.");
	    }
	  }

	  // Cláusula de escape: si el radicando no es positivo, A no es definida positiva
	  private static double raizCholesky(double radicando, int k) {
	    if (radicando < 1e-12) {
	      throw new ArithmeticException(
	          "Error: La matriz no es definida positiva (radicando no positivo en la fila " + k + ").");
	    }
	    return Math.sqrt(radicando);
	  }
	    // ============================================================
	    // ESTRUCTURA DE DATOS PARA LA INTERFAZ GRÁFICA
	    // ============================================================
	    public static class ResultadoLU {
	        public Matriz L;
	        public Matriz U;
	        public boolean huboPermutacion;

	        public ResultadoLU(Matriz L, Matriz U, boolean huboPermutacion) {
	            this.L = L;
	            this.U = U;
	            this.huboPermutacion = huboPermutacion;
	        }
	    }
	    
	    public static double[] GaussPivoteoSimple(Matriz G) {
	        return GaussSolucion(G, 3);
	    }
	    
	    public static double[] GaussPivoteoParcial(Matriz G) {
	        return GaussSolucion(G, 1);
	    }
	    
	    public static double[] GaussPivoteoTotal(Matriz G) {
	        return GaussSolucion(G, 2);
	    }

	    // ============================================================
	    // MOTOR CENTRAL GAUSSIANO (Privado, no accesible por la UI)
	    // ============================================================

	    private static double[] GaussSolucion (Matriz G_original, int tipoPiv) {
	        Matriz G = G_original.copiar();
	        Matriz coeficientes = G.extraerA();
	        if (!coeficientes.esCuadrada()) {
	            throw new IllegalArgumentException("Error: La matriz de coeficientes no es cuadrada. Ingreso incorrecto."); 
	        }
	        int n = G.getCantFilas();
	        int colsTotales = G.getCantColumnas(); 
	        
	        // Vector para registrar posibles cambios de columnas
	        int[] ordenX = new int[n];
	        for (int i = 0; i < n; i++) {
	            ordenX[i] = i;
	        }
	        
	        for (int k = 0; k < n - 1; k++) {   
	            int colMovida = k;
	            if (tipoPiv == 1) {
	                G.pivoteoParcial(k);
	            } else if (tipoPiv == 2) {
	                colMovida = G.pivoteoTotal(k); 
	            } else if (tipoPiv == 3) {
	                G.pivoteoSimple(k);
	            }
	            
	            // Registramos el posible desorden de incógnitas 
	            if (colMovida != k) {
	                int temp = ordenX[k];
	                ordenX[k] = ordenX[colMovida];
	                ordenX[colMovida] = temp;
	            }            
	            
	            if (Math.abs(G.getElemento(k, k)) < 1e-12) {
	                throw new ArithmeticException("Error: Pivote nulo encontrado en la fila " + k + ".");
	            }
	            
	            for (int i = k + 1; i < n; i++) {
	                double m = G.getElemento(i, k) / G.getElemento(k, k);
	                G.setElemento(i, k, 0.0);
	                
	                for (int j = k + 1; j < colsTotales; j++) {
	                    double nuevoValor = G.getElemento(i, j) - (m * G.getElemento(k, j));
	                    G.setElemento(i, j, nuevoValor);
	                }
	            }
	        }
	        
	        // Reutilizamos el método nativo
	        double[] xParcial = G.sustRegresiva();

	        // Reordenamos el vector final basándonos en el registro
	        double[] xFinal = new double[n];
	        for (int i = 0; i < n; i++) {
	            xFinal[ordenX[i]] = xParcial[i];
	        }
	        return xFinal;
	    }
	    
	    public static ResultadoLU factorizar_x_gauss(Matriz A_original, int tipoPiv) {
	        if (!A_original.esCuadrada()) {
	            throw new IllegalArgumentException("Error: La factorización LU requiere una matriz de coeficientes cuadrada.");
	        }
	        if (tipoPiv == 2) {
	            throw new UnsupportedOperationException("Error: Factorización LU clásica no soporta pivoteo total.");
	        }
	        int n = A_original.getCantFilas();
	        Matriz L = Matriz.identidad(n);
	        Matriz U = A_original.copiar(); 
	        boolean permutacionDetectada = false;
	        
	        for (int k = 0; k < n - 1; k++) {
	            
	            if (tipoPiv != 0) {
	                int filaMayor = k;
	                double maxValor = Math.abs(U.getElemento(k, k));
	                
	                for (int i = k + 1; i < n; i++) {
	                    double valorActual = Math.abs(U.getElemento(i, k));
	                    if ((tipoPiv == 1 && valorActual > maxValor) || 
	                        (tipoPiv == 3 && maxValor < 1e-12 && valorActual > 0)) {
	                        maxValor = valorActual;
	                        filaMayor = i;
	                    }
	                }

	                if (filaMayor != k) {
	                    U.interFilas(k, filaMayor); 
	                    permutacionDetectada = true; 

	                    for (int j = 0; j < k; j++) {
	                        double temp = L.getElemento(k, j);
	                        L.setElemento(k, j, L.getElemento(filaMayor, j));
	                        L.setElemento(filaMayor, j, temp);
	                    }
	                }
	            }

	            if (Math.abs(U.getElemento(k, k)) < 1e-12) {
	                 throw new ArithmeticException("Error: Pivote nulo en fila " + k + ". La matriz es singular.");
	            }
	            
	            for (int i = k + 1; i < n; i++) {
	                double m = U.getElemento(i, k) / U.getElemento(k, k);
	                L.setElemento(i, k, m);
	                U.setElemento(i, k, 0.0);
	                
	                for (int j = k + 1; j < n; j++) {
	                    double nuevoValor = U.getElemento(i, j) - (m * U.getElemento(k, j));
	                    U.setElemento(i, j, nuevoValor);
	                }
	            }
	        }
	        return new ResultadoLU(L, U, permutacionDetectada);
	    }    
	}
