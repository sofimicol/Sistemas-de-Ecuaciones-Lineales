package PrograN;

public class Matriz {
    private int cantFilas;
    private int cantColumnas;
    private double[][] datos;

    public int getCantColumnas() {
        return cantColumnas;
    }
    public void setCantColumnas(int cantColumnas) {
        this.cantColumnas = cantColumnas;
    }
    public int getCantFilas() {
        return cantFilas;
    }
    public void setCantFilas(int cantFilas) {
        this.cantFilas = cantFilas;
    }

    public void setElemento(int i, int j, double valor) {
        if (i < this.cantFilas && j < this.cantColumnas)
            this.datos[i][j] = valor;
        else
            System.out.println("Error: Indices incorrectos");
    }

    public double getElemento(int i, int j) {
        if (i < this.cantFilas && j < this.cantColumnas) {
            return datos[i][j];
        } else {
            System.out.println("Error: Indices incorrectos");
            return 0.0;
        }
    }

    public void MostrarMatriz() {
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
        
        if(this.esAmpliada())
        	int limiteCol = this.cantColumnas - 1;
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
        }
        else return k;
    }
    
    public double[] sistDiagonal() {
        if (this.esAmpliada()) {
            double[] b = this.extraer();
            double[] x = new double[this.cantFilas];
            
            for (int i = 0; i < this.cantFilas; i++) {
                if (datos[i][i] == 0) {
                    System.out.println("Error: El sistema no tiene solución única (elemento diagonal es 0 en fila " + i + ").");
                    return null;
                }
                x[i] = b[i] / datos[i][i];
            }
            return x;
        } else {
            System.out.println("Error: El método requiere de una Matriz Ampliada para resolverse.");
            return null;
        }
    }
    
    public double[] sustRegresiva() {
        if (this.esAmpliada()) {
            double[] b = this.extraer();
            double[] x = new double[this.cantFilas];
            int limiteCol = this.cantColumnas - 1; 
            
            for (int i = this.cantFilas - 1; i >= 0; i--) {
                if (datos[i][i] == 0) {
                    System.out.println("Error: Sistema singular (cero en la diagonal en fila " + i + ").");
                    return null;
                }
                double sumatoria = 0.0;
                for (int j = i + 1; j < limiteCol; j++) {
                    sumatoria = sumatoria + (datos[i][j] * x[j]);
                }
                x[i] = (b[i] - sumatoria) / datos[i][i];
            }
            return x;
        } else {
            System.out.println("Error: El método requiere de una Matriz Ampliada para resolverse.");
            return null;
        }
    }
    
    public double[] sustProgresiva() {
        if (this.esAmpliada()) {
            double[] b = this.extraer();
            double[] x = new double[this.cantFilas];
            
            for(int i = 0; i < this.cantFilas; i++) {
                if(datos[i][i] == 0.0) {
                    System.out.println("Error: Sistema singular (cero en la diagonal en fila " + i + ").");
                    return null;
                }
                double sumatoria = 0.0;
                for(int j = 0; j < i; j++) {
                    sumatoria = sumatoria + (datos[i][j] * x[j]);	
                }
                x[i]= (b[i]-sumatoria) / datos[i][i];
            }
            return x;
        } else {
            System.out.println("Error: El método requiere de una Matriz Ampliada para resolverse.");
            return null;
        }
    }
}
