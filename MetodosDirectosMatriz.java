
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
}
