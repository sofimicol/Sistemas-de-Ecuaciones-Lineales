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
}

  
