package modelo;

public class Matriz {
    // Dimension máxima de la matriz (Consultar al profe y contarlo en reunion 4)

    public static final int MAX_DIMENSION = 100;

    private int cantFilas;
    private int cantColumnas;
    private double[][] datos;

    // ============================================================
    // CONSTRUCTORES
    // ============================================================

    //Constructores faltantes (Reunion 4)
    public Matriz(int cantFilas, int cantColumnas) {
        validarDimensiones(cantFilas, cantColumnas);
        this.cantFilas = cantFilas;
        this.cantColumnas = cantColumnas;
        this.datos = new double[cantFilas][cantColumnas];
    }

    public Matriz(double[][] datos) {
        if (datos == null || datos.length == 0 || datos[0].length == 0) {
            throw new IllegalArgumentException("Error: La matriz no puede ser nula o vacía.");
        }
        if (datos.length > MAX_DIMENSION || datos[0].length > MAX_DIMENSION + 1) {
            throw new IllegalArgumentException("Error: Las dimensiones exceden la tolerancia máxima de " + MAX_DIMENSION + ".");
        }
        int columnas = datos[0].length;
        for (int i = 1; i < datos.length; i++) {
            if (datos[i].length != columnas) {
                throw new IllegalArgumentException(
                    "Error: La matriz debe ser rectangular (todas las filas con la misma longitud).");
            }
        }
        validarDimensiones(datos.length, columnas);
        this.cantFilas = datos.length;
        this.cantColumnas = columnas;
        this.datos = copiaProfunda(datos);
    }
    // ============================================================
    // GETTERS Y SETTERS
    // ============================================================

    public int getCantFilas() {
        return cantFilas;
    }

    public boolean validarDimensiones(int cantFilas, int cantColumnas) {
        if (cantFilas <= 0 || cantFilas > MAX_DIMENSION) {
            throw new IllegalArgumentException("Error: La cantidad de filas debe ser mayor a 0 y no exceder " + MAX_DIMENSION + ".");
        }
        if (cantColumnas <= 0 || cantColumnas > MAX_DIMENSION + 1) {
            throw new IllegalArgumentException("Error: La cantidad de columnas debe ser mayor a 0 y no exceder " + (MAX_DIMENSION+1) + ".");
        }
        return true;
    }
    // no vamos a dejar que el usuario modifique la cantidad de filas y columnas directamente, sino que lo haga a través de los métodos setCantFilas y setCantColumnas, que validan las dimensiones antes de hacer el cambio.
    public void setCantFilas(int cantFilas) {
        if (cantFilas <= 0 || cantFilas > MAX_DIMENSION) {
            throw new IllegalArgumentException(
                "Error: La cantidad de filas debe ser mayor a 0 y no exceder " + MAX_DIMENSION + ".");
        }
        if (cantFilas == this.cantFilas) return;

        double[][] nuevo = new double[cantFilas][this.cantColumnas];
        int filasCopiar = Math.min(cantFilas, this.cantFilas);
        int colsCopiar  = this.cantColumnas;
        for (int i = 0; i < filasCopiar; i++) {
            for (int j = 0; j < colsCopiar; j++) {
                nuevo[i][j] = this.datos[i][j];
            }
        }
        this.datos = nuevo;
        this.cantFilas = cantFilas;
    }

    public int getCantColumnas() {
        return cantColumnas;
    }
//no vamos a dejar que el usuario modifique la cantidad de filas y columnas directamente, sino que lo haga a través de los métodos setCantFilas y setCantColumnas, que validan las dimensiones antes de hacer el cambio.
    public void setCantColumnas(int cantColumnas) {
        if (cantColumnas <= 0 || cantColumnas > MAX_DIMENSION + 1) {
            throw new IllegalArgumentException(
                "Error: La cantidad de columnas debe ser mayor a 0 y no exceder " + (MAX_DIMENSION + 1) + ".");
        }
        if (cantColumnas == this.cantColumnas) return;

        double[][] nuevo = new double[this.cantFilas][cantColumnas];
        int filasCopiar = this.cantFilas;
        int colsCopiar  = Math.min(cantColumnas, this.cantColumnas);
        for (int i = 0; i < filasCopiar; i++) {
            for (int j = 0; j < colsCopiar; j++) {
                nuevo[i][j] = this.datos[i][j];
            }
        }
        this.datos = nuevo;
        this.cantColumnas = cantColumnas;
    }

    public double getElemento(int fila, int columna) {
        if (fila < 0 || fila >= cantFilas || columna < 0 || columna >= cantColumnas) {
            throw new IndexOutOfBoundsException(
                "Error: Índices fuera de rango (" + fila + "," + columna + "). " +
                "Dimensiones válidas: [0.." + (cantFilas - 1) + "] x [0.." + (cantColumnas - 1) + "].");
        }
        return this.datos[fila][columna];
    }

    public void setElemento(int fila, int columna, double valor) {
        if (fila < 0 || fila >= cantFilas || columna < 0 || columna >= cantColumnas) {
            throw new IndexOutOfBoundsException(
                "Error: Índices fuera de rango al asignar (" + fila + "," + columna + ").");
        }
        this.datos[fila][columna] = valor;
    }

    public boolean esAmpliada() {
        return this.cantColumnas == this.cantFilas + 1;
    }

    private double[][] copiaProfunda(double[][] origen) {
        double[][] copia = new double[origen.length][origen[0].length];
        for (int i = 0; i < origen.length; i++) {
            for (int j = 0; j < origen[i].length; j++) {
                copia[i][j] = origen[i][j];
            }
        }
        return copia;
    }

    // ============================================================
    // EXTRACCIONES DE AMPLIADA
    // ============================================================

    public Matriz extraerA() {
        if (!this.esAmpliada()) {
            throw new IllegalArgumentException("La matriz no cumple las dimensiones de ampliada n x (n + 1).");
        }
        int n = this.cantFilas;
        Matriz A = new Matriz(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A.setElemento(i, j, this.getElemento(i, j));
            }
        }
        return A;
    }

    public double[] extraerB() {
        if (!this.esAmpliada()) {
            throw new IllegalArgumentException("La matriz no cumple las dimensiones de ampliada n x (n + 1).");
        }
        int n = this.cantFilas;
        double[] b = new double[n];
        for (int i = 0; i < n; i++) {
            b[i] = this.getElemento(i, n); // Retorna vector nativo double[]
        }
        return b;
    }

    
    public void mostrarMatriz() {
        for(int i = 0; i < this.cantFilas; i++) {
            System.out.print("[ ");
            for(int j = 0; j < this.cantColumnas; j++) {
                System.out.printf("%8.4f ", datos[i][j]);
            }
            System.out.println("]");
        }
    }
    
    public void interFilas(int fila1, int fila2) {
        if(fila1 != fila2) {
            for (int j = 0; j < this.cantColumnas; j++) {
                double aux = datos[fila1][j];
                datos[fila1][j] = datos[fila2][j];
                datos[fila2][j] = aux;
            }
            System.out.println("-> Se intercambió la Fila " + fila1 + " con la Fila " + fila2);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Matriz ").append(cantFilas).append("x").append(cantColumnas);
        if (esAmpliada()) sb.append(" (ampliada)");
        sb.append(System.lineSeparator());
        for (int i = 0; i < cantFilas; i++) {
            sb.append("[ ");
            for (int j = 0; j < cantColumnas; j++) {
                sb.append(String.format("%8.4f ", datos[i][j]));
            }
            sb.append("]").append(System.lineSeparator());
        }
        return sb.toString();
    }
    
    public void interColumnas(int c1, int c2) {
        if (c1 != c2) {
            for (int i = 0; i < this.cantFilas; i++) {
                double aux = datos[i][c1];
                datos[i][c1] = datos[i][c2];
                datos[i][c2] = aux;
            }
            System.out.println("-> Se intercambió la Columna " + c1 + " con la Columna " + c2);
        }
    }
    
    public void pivoteoParcial(int k) {	
        int mayFila = k;
        double maxValor = Math.abs(datos[k][k]);

        // Buscamos el mayor numero por debajo del actual pivot
        for (int i = k + 1; i < this.cantFilas; i++) {
            if (Math.abs(datos[i][k]) > maxValor) {
                maxValor = Math.abs(datos[i][k]);
                mayFila = i;
            }
        }
        if (mayFila != k) {
            interFilas(k, mayFila);
        }
    }
    
    public int pivoteoTotal(int k) {
        int mayFila = k;
        int mayCol = k;
        double maxValor = Math.abs(datos[k][k]);
        int limiteCol = this.cantColumnas;
        if (this.esAmpliada()) {
            limiteCol = this.cantColumnas - 1;
        }
        for (int i = k; i < this.cantFilas; i++) {
            for (int j = k; j < limiteCol; j++) {
                if (Math.abs(datos[i][j]) > maxValor) {
                    maxValor = Math.abs(datos[i][j]);
                    mayFila = i;
                    mayCol = j;
                }
            }
        }
        if (mayFila != k) {
            interFilas(k, mayFila);
        }
        if (mayCol != k) {
            interColumnas(k, mayCol);
            return mayCol;
        } else {
            return k;
        }
    }
    
    public double[] sistDiagonal() {
        if (this.esAmpliada()) {
            double[] b = this.extraerB();
            double[] x = new double[this.cantFilas];
            for (int i = 0; i < this.cantFilas; i++) {
                if (datos[i][i] == 0) {
                    throw new ArithmeticException("Error: Sistema singular (cero en la diagonal en fila " + i + ").");
                }
                x[i] = b[i] / datos[i][i];
            }
            return x;
        } else {
            throw new IllegalStateException("Error: El método requiere de una Matriz Ampliada para resolverse.");
        }
    }
    
    public double[] sustRegresiva() {
        if (this.esAmpliada()) {
            double[] b = this.extraerB();
            double[] x = new double[this.cantFilas];
            int limiteCol = this.cantColumnas - 1;
            for (int i = this.cantFilas - 1; i >= 0; i--) {
                if (datos[i][i] == 0) {
                    throw new ArithmeticException("Error: Sistema singular (cero en la diagonal en fila " + i + ").");
                }
                double sumatoria = 0.0;
                for (int j = i + 1; j < limiteCol; j++) {
                    sumatoria = sumatoria + (datos[i][j] * x[j]);
                }
                x[i] = (b[i] - sumatoria) / datos[i][i];
            }
            return x;
        } else {
            throw new IllegalStateException("Error: El método requiere de una Matriz Ampliada para resolverse.");
        }
    }

    public double[] sustProgresiva() {
        if (this.esAmpliada()) {
            double[] b = this.extraerB();
            double[] x = new double[this.cantFilas];
            for (int i = 0; i < this.cantFilas; i++) {
                if (datos[i][i] == 0.0) {
                    throw new ArithmeticException("Error: Sistema singular (cero en la diagonal en fila " + i + ").");
                }
                double sumatoria = 0.0;
                for (int j = 0; j < i; j++) {
                    sumatoria = sumatoria + (datos[i][j] * x[j]);
                }
                x[i] = (b[i] - sumatoria) / datos[i][i];
            }
        return x;
        } else {
            throw new IllegalStateException("Error: El método requiere de una Matriz Ampliada para resolverse.");
        }
    }

    public void pivoteoSimple(int k) {
        if (datos[k][k] == 0.0) {
            for (int i = k + 1; i < cantFilas; i++) {
                if (datos[i][k] != 0.0) {
                    interFilas(k, i);
                    break; 
                }
            }
        }
    }

}