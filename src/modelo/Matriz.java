package modelo;

public class Matriz {
    // Épsilon de la máquina para double: 2^-52 ≈ 2.2e-16
    public static final double EPSILON_MAQUINA = Math.ulp(1.0);

    // Número de condición máximo para considerar una matriz bien condicionada
    public static double MAX_CONDICION = 50;

    private int cantFilas;
    private int cantColumnas;
    private double[][] datos;

	    // ============================================================
	    // CONSTRUCTORES
	    // ============================================================

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
	        if (cantFilas <= 0) {
	            throw new IllegalArgumentException("Error: La cantidad de filas debe ser mayor a 0.");
	        }
	        if (cantColumnas <= 0) {
	            throw new IllegalArgumentException("Error: La cantidad de columnas debe ser mayor a 0.");
	        }
	        return true;
	    }
	    // no vamos a dejar que el usuario modifique la cantidad de filas y columnas directamente, sino que lo haga a través de los métodos setCantFilas y setCantColumnas, que validan las dimensiones antes de hacer el cambio.
	    public void setCantFilas(int cantFilas) {
	        if (cantFilas <= 0) {
	            throw new IllegalArgumentException("Error: La cantidad de filas debe ser mayor a 0.");
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
	        if (cantColumnas <= 0) {
	            throw new IllegalArgumentException("Error: La cantidad de columnas debe ser mayor a 0.");
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

	    public static Matriz identidad(int n) {
	        Matriz I = new Matriz(n, n);
	        for (int i = 0; i < n; i++) {
	            I.datos[i][i] = 1.0;
	        }
	        return I;
	    }

	    public Matriz copiar() {
	        return new Matriz(this.datos); // el constructor ya hace copiaProfunda
	    }

	    // Retorna una copia nueva A^T (columnas x filas), con T[j][i] = A[i][j]; esta matriz no se modifica
	    public Matriz transponer() {
	        Matriz T = new Matriz(this.cantColumnas, this.cantFilas);
	        for (int i = 0; i < this.cantFilas; i++) {
	            for (int j = 0; j < this.cantColumnas; j++) {
	                T.datos[j][i] = this.datos[i][j];
	            }
	        }
	        return T;
	    }

	    // Una matriz es simétrica si es cuadrada y A = A^T
	    public boolean esSimetrica() {
	        if (this.cantFilas != this.cantColumnas) {
	            return false;
	        }
	        if (!this.contieneValoresValidos()) {
	            return false;
	        }
	        double escala = this.normaCoeficientes();
	        Matriz T = this.transponer();
	        for (int i = 0; i < this.cantFilas; i++) {
	            for (int j = 0; j < this.cantColumnas; j++) {
	                if (!esCero(this.datos[i][j] - T.datos[i][j], escala, this.cantFilas)) {
	                    return false;
	                }
	            }
	        }
	        return true;
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
	            intercambiarFilas(fila1, fila2);
	            System.out.println("-> Se intercambió la Fila " + fila1 + " con la Fila " + fila2);
	        }
	    }

	    // Igual que interFilas pero sin imprimir (para determinante e inversa)
	    private void intercambiarFilas(int fila1, int fila2) {
	        for (int j = 0; j < this.cantColumnas; j++) {
	            double aux = datos[fila1][j];
	            datos[fila1][j] = datos[fila2][j];
	            datos[fila2][j] = aux;
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

	    // Devuelve la fila con el mayor valor absoluto en la columna k (desde la fila k hacia abajo)
	    private int buscarFilaPivote(int k) {
	        int mayFila = k;
	        double maxValor = Math.abs(datos[k][k]);

	        // Buscamos el mayor numero por debajo del actual pivot
	        for (int i = k + 1; i < this.cantFilas; i++) {
	            if (Math.abs(datos[i][k]) > maxValor) {
	                maxValor = Math.abs(datos[i][k]);
	                mayFila = i;
	            }
	        }
	        return mayFila;
	    }

	    // Devuelve true si hubo intercambio de filas
	    public boolean pivoteoParcial(int k) {
	        int mayFila = buscarFilaPivote(k);
	        if (mayFila != k) {
	            interFilas(k, mayFila);
	            return true;
	        }
	        return false;
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

	    // Sin argumento, decide los ceros de la diagonal con la escala de esta misma matriz.
	    // Si el sistema triangular viene de una eliminación, conviene pasar la escala de la A original:
	    // el crecimiento de los elementos durante la eliminación no debe contar como escala
	    public double[] sistDiagonal() {
	        return this.sistDiagonal(this.normaCoeficientes());
	    }

	    public double[] sistDiagonal(double escala) {
	        if (this.esAmpliada()) {
	            double[] b = this.extraerB();
	            double[] x = new double[this.cantFilas];
	            for (int i = 0; i < this.cantFilas; i++) {
	                if (esCero(datos[i][i], escala, this.cantFilas)) {
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
	        return this.sustRegresiva(this.normaCoeficientes());
	    }

	    public double[] sustRegresiva(double escala) {
	        if (this.esAmpliada()) {
	            double[] b = this.extraerB();
	            double[] x = new double[this.cantFilas];
	            int limiteCol = this.cantColumnas - 1;
	            for (int i = this.cantFilas - 1; i >= 0; i--) {
	                if (esCero(datos[i][i], escala, this.cantFilas)) {
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
	        return this.sustProgresiva(this.normaCoeficientes());
	    }

	    public double[] sustProgresiva(double escala) {
	        if (this.esAmpliada()) {
	            double[] b = this.extraerB();
	            double[] x = new double[this.cantFilas];
	            for (int i = 0; i < this.cantFilas; i++) {
	                if (esCero(datos[i][i], escala, this.cantFilas)) {
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

	    // det(A) = (-1)^intercambios * producto de los pivotes de la eliminación con pivoteo parcial.
	    // Para decidir si A es singular usar esSingular(): det(αA) = α^n * det(A) depende de la escala
	    // y puede dar 0 o Infinity por underflow/overflow aunque A esté bien condicionada
	    public double determinante() {
	        if (this.cantFilas != this.cantColumnas) {
	            throw new UnsupportedOperationException(
	                "Error: El determinante requiere una matriz cuadrada (n x n). " +
	                "Dimensiones recibidas: " + this.cantFilas + "x" + this.cantColumnas + ".");
	        }
	        this.validarValoresFinitos();

	        int[] intercambios = new int[1];
	        double det = 1.0;
	        for (double pivote : this.pivotesConPivoteoParcial(intercambios)) {
	            det *= pivote;
	        }
	        // Cada intercambio de filas cambia el signo del determinante
	        return (intercambios[0] % 2 == 0) ? det : -det;
	    }

	    // A es numéricamente singular si algún pivote de la eliminación con pivoteo parcial es cero
	    // frente a la escala de A (ver esCero). A diferencia de det(A), el resultado no cambia al escalar A
	    public boolean esSingular() {
	        if (this.cantFilas != this.cantColumnas) {
	            throw new UnsupportedOperationException(
	                "Error: La singularidad se define para una matriz cuadrada (n x n). " +
	                "Dimensiones recibidas: " + this.cantFilas + "x" + this.cantColumnas + ".");
	        }
	        this.validarValoresFinitos();

	        double escala = this.normaCoeficientes();
	        for (double pivote : this.pivotesConPivoteoParcial(null)) {
	            if (esCero(pivote, escala, this.cantFilas)) {
	                return true;
	            }
	        }
	        return false;
	    }

	    // Eliminación gaussiana con pivoteo parcial sobre una copia de esta matriz cuadrada.
	    // Retorna los pivotes u_kk; si intercambios no es null, cuenta en intercambios[0] los intercambios de filas
	    private double[] pivotesConPivoteoParcial(int[] intercambios) {
	        Matriz M = this.copiar();
	        int n = M.cantFilas;
	        double[] pivotes = new double[n];

	        for (int k = 0; k < n; k++) {
	            int f = M.buscarFilaPivote(k);
	            if (f != k) {
	                M.intercambiarFilas(k, f);
	                if (intercambios != null) {
	                    intercambios[0]++;
	                }
	            }
	            double pivote = M.datos[k][k];
	            pivotes[k] = pivote;
	            if (pivote == 0.0) {
	                continue; // el mayor de la columna es 0: no hay nada que eliminar debajo
	            }
	            for (int i = k + 1; i < n; i++) {
	                double m = M.datos[i][k] / pivote;
	                for (int j = k; j < n; j++) {
	                    M.datos[i][j] = M.datos[i][j] - m * M.datos[k][j];
	                }
	            }
	        }
	        return pivotes;
	    }

	    // Gauss-Jordan con diagonal unitaria sobre una identidad adjunta: al terminar, I = A^-1
	    public Matriz inversa() {
	        if (this.cantFilas != this.cantColumnas) {
	            throw new UnsupportedOperationException(
	                "Error: La inversa requiere una matriz cuadrada (n x n). " +
	                "Dimensiones recibidas: " + this.cantFilas + "x" + this.cantColumnas + ".");
	        }

	        this.validarValoresFinitos();
	        int n = this.cantFilas;
	        double escala = this.normaCoeficientes();
	        Matriz M = this.copiar();       // no modificamos la matriz original
	        Matriz I = Matriz.identidad(n); // matriz adjunta

	        for (int k = 0; k < n; k++) {
	            // Pivoteo solo si el pivote es nulo (mismo criterio que JordanSolucionDiagonalUnitaria)
	            if (esCero(M.datos[k][k], escala, n)) {
	                int f = M.buscarFilaPivote(k);
	                // El intercambio se aplica a las DOS matrices
	                M.intercambiarFilas(k, f);
	                I.intercambiarFilas(k, f);
	                if (esCero(M.datos[k][k], escala, n)) {
	                    throw new ArithmeticException(
	                        "Error: La matriz es singular (pivote nulo en fila " + k + "). No tiene inversa.");
	                }
	            }

	            // Normalizamos la fila k para que el pivote valga 1
	            double pivote = M.datos[k][k];
	            for (int j = 0; j < n; j++) {
	                M.datos[k][j] = M.datos[k][j] / pivote;
	                I.datos[k][j] = I.datos[k][j] / pivote;
	            }

	            // Eliminamos la columna k en el resto de las filas.
	            // En I recorremos todas las columnas: a la izquierda de k no hay ceros garantizados
	            for (int i = 0; i < n; i++) {
	                if (i != k) {
	                    double m = M.datos[i][k];
	                    for (int j = 0; j < n; j++) {
	                        M.datos[i][j] = M.datos[i][j] - m * M.datos[k][j];
	                        I.datos[i][j] = I.datos[i][j] - m * I.datos[k][j];
	                    }
	                }
	            }
	        }
	        return I;
	    }

	    // Norma Infinito: máximo de las sumas de los valores absolutos de cada fila
	    public double norma() {
	        if (this.cantFilas != this.cantColumnas) {
	            throw new UnsupportedOperationException(
	                "Error: La norma requiere una matriz cuadrada (n x n). " +
	                "Dimensiones recibidas: " + this.cantFilas + "x" + this.cantColumnas + ".");
	        }

	        this.validarValoresFinitos();
	        return this.normaCoeficientes();
	    }

	    // ||A||inf de los coeficientes: en una ampliada [A | b] no cuenta la columna b.
	    // Es la escala con la que se decide si un pivote es cero (ver esCero)
	    public double normaCoeficientes() {
	        int columnas = this.esAmpliada() ? this.cantColumnas - 1 : this.cantColumnas;
	        double maxSuma = 0.0;
	        for (int i = 0; i < this.cantFilas; i++) {
	            double sumaFila = 0.0;
	            for (int j = 0; j < columnas; j++) {
	                sumaFila = sumaFila + Math.abs(datos[i][j]);
	            }
	            if (sumaFila > maxSuma) {
	                maxSuma = sumaFila;
	            }
	        }
	        return maxSuma;
	    }

	    // Número de condición: cond(A) = ||A|| * ||A^-1|| (con norma infinito)
	    public double condicion() {
	        if (this.cantFilas != this.cantColumnas) {
	            throw new UnsupportedOperationException(
	                "Error: El número de condición requiere una matriz cuadrada (n x n). " +
	                "Dimensiones recibidas: " + this.cantFilas + "x" + this.cantColumnas + ".");
	        }

	        // Si A es singular, inversa() lanza ArithmeticException
	        return this.norma() * this.inversa().norma();
	    }

	    public boolean bienCondicionado() {
	        try {
	            return this.condicion() <= MAX_CONDICION;
	        } catch (ArithmeticException e) {
	            // Matriz singular: cond(A) es infinito, nunca está bien condicionada
	            return false;
	        }
	    }

	    public void pivoteoSimple(int k) {
	        double escala = this.normaCoeficientes();
	        if (esCero(datos[k][k], escala, cantFilas)) {
	            for (int i = k + 1; i < cantFilas; i++) {
	                if (!esCero(datos[i][k], escala, cantFilas)) {
	                    interFilas(k, i);
	                    break;
	                }
	            }
	        }
	    }
	        // Norma 1: ||x||_1 = suma de los valores absolutos
	public static double norma1Vector(double[] x) {
	    validarVector(x);
	    double suma = 0.0;
	    for (int i = 0; i < x.length; i++) {
	        suma += Math.abs(x[i]);
	    }
	    return suma;
	}

	// Norma 2 (euclídea): ||x||_2 = sqrt( suma de los cuadrados )
	public static double norma2Vector(double[] x) {
	    validarVector(x);
	    double sumaCuadrados = 0.0;
	    for (int i = 0; i < x.length; i++) {
	        sumaCuadrados += x[i] * x[i];
	    }
	    return Math.sqrt(sumaCuadrados);
	}

	// Norma infinita: ||x||_inf = máximo de los valores absolutos
	public static double normaInfinitoVector(double[] x) {
	    validarVector(x);
	    double maximo = 0.0;
	    for (int i = 0; i < x.length; i++) {
	        double abs = Math.abs(x[i]);
	        if (abs > maximo) {
	            maximo = abs;
	        }
	    }
	    return maximo;
	}

	// Validador compartido por las tres normas vectoriales
	private static void validarVector(double[] x) {
	    if (x == null || x.length == 0) {
	        throw new IllegalArgumentException("Error: El vector no puede ser nulo ni vacío.");
	    }
	}
	
	public boolean esCuadrada() {
	    return this.cantFilas == this.cantColumnas;
	}

	// Cero dinámico: |valor| <= n * ε * escala, con escala = ||A||inf de la matriz con la que se opera.
	// Escalar A por α escala también los pivotes y ||A||, así que el criterio no depende de las unidades;
	// n * ε acompaña el redondeo de la eliminación, que crece con n (mismo criterio que LAPACK y MATLAB).
	// Se escribe como !(|valor| > tolerancia) para que un NaN también cuente como cero
	public static boolean esCero(double valor, double escala, int n) {
	    return !(Math.abs(valor) > n * EPSILON_MAQUINA * escala);
	}

	// false si algún elemento es NaN o Infinity
	public boolean contieneValoresValidos() {
	    for (int i = 0; i < this.cantFilas; i++) {
	        for (int j = 0; j < this.cantColumnas; j++) {
	            if (!Double.isFinite(this.datos[i][j])) {
	                return false;
	            }
	        }
	    }
	    return true;
	}

	public void validarValoresFinitos() {
	    if (!this.contieneValoresValidos()) {
	        throw new IllegalArgumentException("Error: La matriz contiene valores no finitos (NaN o Infinity).");
	    }
	}
}
