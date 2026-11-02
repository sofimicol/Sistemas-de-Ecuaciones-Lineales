
package PrograN;

public class MetodosDirectosMatriz {

  public static double[] JordanSolucion(Matriz Ab, int n) {
    double[] x = new double[n];

    for(int k = 0; k < n; k++) {
      // Pivoteos si es necesario si Ab[1][1] es nulo, por ejemplo
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
    double[] x = new double[n];

    for(int k = 0; k < n; k++) {
      // Pivoteos si es necesario si Ab[1][1] es nulo, por ejemplo
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

  //*Parte de Lautaro:
  private int aplicarPivoteo(Matriz matriz, int k, int tipoPiv) {
        if (tipoPiv == 1) {
            matriz.pivoteoParcial(k);
        } else if (tipoPiv == 2) {
            return matriz.pivoteoTotal(k); 
        } else if (tipoPiv == 3) {
            matriz.pivoteoSimple(k); 
        }
        return k; 
    }

    public double[] gauss(Matriz G, int tipoPiv) {
        if (!G.esAmpliada()) {
            System.out.println("Error: Gauss requiere una matriz ampliada con el vector b.");
            return null;
        }
        int n = G.getCantFilas();
        int colsTotales = G.getCantColumnas(); 
        // Vector para registrar posibles cambios de columnas
        int[] ordenX = new int[n];
        for (int i = 0; i < n; i++) {
            ordenX[i] = i;
        }
        for (int k = 0; k < n - 1; k++) {   
            int colMovida = aplicarPivoteo(G, k, tipoPiv); 
            // Registramos el posible desorden de incógnitas 
            if (colMovida != k) {
                int temp = ordenX[k];
                ordenX[k] = ordenX[colMovida];
                ordenX[colMovida] = temp;
            }            
            if (G.getElemento(k, k) == 0.0) {
                System.out.println("Error: Pivote nulo encontrado.");
                return null;
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
        
        double[] xParcial = G.sustRegresiva();
        if (xParcial == null) return null;

        double[] xFinal = new double[n];
        for (int i = 0; i < n; i++) {
            xFinal[ordenX[i]] = xParcial[i];
        }
        return xFinal;
    }
    
    public Matriz[] factorizar_x_gauss(Matriz A, int tipoPiv) {
        if (A.esAmpliada()) {
            System.out.println("Error: LU requiere una matriz cuadrada.");
            return null;
        }

        int n = A.getCantFilas();
        Matriz L = new Matriz(n, n);
        Matriz U = new Matriz(n, n);
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                U.setElemento(i, j, A.getElemento(i, j));
            }
        }

        for (int k = 0; k < n - 1; k++) {
            L.setElemento(k, k, 1.0);
            
            aplicarPivoteo(U, k, tipoPiv);
            
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
        L.setElemento(n - 1, n - 1, 1.0);
        return new Matriz[]{L, U};
    }
}
