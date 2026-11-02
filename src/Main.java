import modelo.Matriz;
import servicios.MetodosDirectosMatriz;

public class Main{

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   INICIANDO TESTEO QA - MÉTODOS DIRECTOS");
        System.out.println("==================================================\n");

        // ---------------------------------------------------------
        // TEST 1: Sistema 2x2 Fácil (Única solución)
        // ---------------------------------------------------------
        System.out.println("--- TEST 1: Sistema 2x2 Fácil ---");
        try {
            Matriz amp1 = new Matriz(new double[][]{
                {1, 2, 3},
                {2, 4, 6}
            });
            double[] x1 = MetodosDirectosMatriz.JordanSolucionDiagonalUnitaria(amp1, 2);
            System.out.println("Éxito. Solución: x= " + x1[0] + ", y= " + x1[1]); // Esperado 2.0 y 1.0
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 2: Sistema 3x3 con Pivote Cero (Requiere pivoteo)
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 2: Sistema 3x3 con Pivote Cero (Sin pivoteo manual) ---");
        try {
            Matriz amp2 = new Matriz(new double[][]{
                {0, 2, 1, 4},
                {1, 1, 1, 6},
                {2, 1, 3, 10}
            });
            // Si no llamamos a amp2.pivoteoParcial(0), Jordan va a fallar (división por cero)
            double[] x2 = MetodosDirectosMatriz.JordanSolucion(amp2, 3);
            System.out.println("Éxito. Solución: " + java.util.Arrays.toString(x2));
        } catch (Exception e) {
            System.out.println("FALLO ESPERADO (Pivote 0): " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("\n--- TEST 2b: Mismo sistema, pero con pivoteo manual previo ---");
        try {
            Matriz amp2b = new Matriz(new double[][]{
                {0, 2, 1, 4},
                {1, 1, 1, 6},
                {2, 1, 3, 10}
            });
            amp2b.pivoteoParcial(0); // Intercambia fila 0 con la 2 (el 2 es mayor)
            double[] x2b = MetodosDirectosMatriz.JordanSolucion(amp2b, 3);
            System.out.println("Éxito. Solución: " + java.util.Arrays.toString(x2b)); // Esperado 1, 1, 2
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 3: Factorización LU 3x3
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 3: Factorización LU 3x3 ---");
        try {
            Matriz A3 = new Matriz(new double[][]{
                {2, -1, -2},
                {-4, 6, 3},
                {-4, -2, 8}
            });
            Matriz[] lu = MetodosDirectosMatriz.factorizarLU(A3);
            System.out.println("L:");
            lu[0].mostrarMatriz();
            System.out.println("U:");
            lu[1].mostrarMatriz();
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getMessage());
        }
        // ---------------------------------------------------------
        // TEST 3: Sistema Singular (Sin solución única)
// ---------------------------------------------------------
System.out.println("\n--- TEST 3.b: Sistema 3x3 Singular (Determinante 0) ---");
try {
    Matriz singular = new Matriz(new double[][]{
        {1, 2, 3, 4},
        {2, 4, 6, 8},  // Fila 1 es el doble de la Fila 0
        {1, 1, 1, 2}
    });
    
    // Intentamos resolver
    double[] xs = MetodosDirectosMatriz.JordanSolucionDiagonalUnitaria(singular, 3);
    System.out.println("Resultado (no debería llegar acá): " + java.util.Arrays.toString(xs));
} catch (Exception e) {
    System.out.println("FALLO ESPERADO (Singular): " + e.getClass().getSimpleName() + " - " + e.getMessage());
}
// ---------------------------------------------------------
// TEST 3c: LU con Pivote Cero (Falla de factorizarLU)
// ---------------------------------------------------------
System.out.println("\n--- TEST 3c: LU con Pivote Cero ---");
try {
    Matriz A_pivoteCero = new Matriz(new double[][]{
        {0, 2, 1},
        {1, 3, 4},
        {2, 1, 5}
    });
    Matriz[] lu2 = MetodosDirectosMatriz.factorizarLU(A_pivoteCero);
    System.out.println("L:");
    lu2[0].mostrarMatriz();
    System.out.println("U:");
    lu2[1].mostrarMatriz();
} catch (Exception e) {
    System.out.println("FALLO ESPERADO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
}
// ---------------------------------------------------------
// TEST 3d: Factorización LU con Singularidad Oculta
// ---------------------------------------------------------
System.out.println("\n--- TEST 3d: LU con Singularidad Oculta (Fila Dependiente) ---");
try {
    // Matriz donde la Fila 3 = Fila 1 + Fila 2
    // Ningún elemento de la diagonal es cero inicialmente.
    Matriz A_oculta = new Matriz(new double[][]{
        {1, 2, 3},
        {2, 5, 7},
        {3, 7, 10}
    });
    
    Matriz[] lu = MetodosDirectosMatriz.factorizarLU(A_oculta);
    
    System.out.println("L:");
    lu[0].mostrarMatriz();
    System.out.println("U:");
    lu[1].mostrarMatriz();
    
    // Si llega acá sin excepción, el programador de LU no implementó el fix
    System.out.println("ADVERTENCIA: El sistema se factorizó pero es singular.");
    
} catch (Exception e) {
    System.out.println("FALLO ESPERADO (Singularidad Oculta): " + e.getClass().getSimpleName() + " - " + e.getMessage());
}
        // ---------------------------------------------------------
        // TEST 4: Sistema 5x5 Fracciones
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 4: Sistema 5x5 Fracciones ---");
        try {
            Matriz amp5 = new Matriz(new double[][]{
                {1, 2, 0, 0, 0, 3},
                {0, 1, 2, 0, 0, 5},
                {0, 0, 1, 2, 0, 7},
                {0, 0, 0, 1, 2, 9},
                {2, 0, 0, 0, 1, 4}
            });
            double[] x5 = MetodosDirectosMatriz.JordanSolucionDiagonalUnitaria(amp5, 5);
            System.out.println("Éxito. Solución: " + java.util.Arrays.toString(x5));
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 5: Sistema Singular (Sin solución única)
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 5: Sistema 3x3 Singular (Determinante 0) ---");
        try {
            Matriz singular = new Matriz(new double[][]{
                {1, 2, 3, 4},
                {2, 4, 6, 8},
                {1, 1, 1, 2}
            });
            // Debería lanzar ArithmeticException en sistDiagonal o división por cero
            double[] xs = MetodosDirectosMatriz.JordanSolucionDiagonalUnitaria(singular, 3);
            System.out.println("Éxito (¿Cómo?): " + java.util.Arrays.toString(xs));
        } catch (Exception e) {
            System.out.println("FALLO ESPERADO (Singular): " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 6: Validación de Memoria (Índices fuera de rango)
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 6: Acceso a Memoria Inválida ---");
        try {
            Matriz m = new Matriz(2, 2);
            // Intentamos acceder a la fila 5 de una matriz 2x2
            double val = m.getElemento(5, 0); 
            System.out.println("Valor: " + val);
        } catch (Exception e) {
            System.out.println("FALLO ESPERADO (Índice): " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 7: Validación de Dimensiones (Rectangularidad y Límites)
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 7: Matriz Irregular y Límites ---");
        try {
            // Intentamos crear una matriz jagged (irregular)
            Matriz jagged = new Matriz(new double[][]{
                {1, 2, 3},
                {1, 2},     // <-- Fila incompleta
                {1, 2, 3}
            });
        } catch (Exception e) {
            System.out.println("FALLO ESPERADO (Jagged): " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        try {
            // Intentamos crear una matriz que excede MAX_DIMENSION (100)
            Matriz gigante = new Matriz(101, 102); 
        } catch (Exception e) {
            System.out.println("FALLO ESPERADO (Límite): " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 8: Lo que mencionaste - Inyección de Datos Inválidos
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 8: Inyección de String en Lugar de Double ---");
        try {
            // Imaginemos que la UI falla y nos pasa un String (esto ni siquiera compila en Java,
            // pero si la UI parsea mal y pasa un NaN, veamos qué pasa)
            Matriz mNan = new Matriz(new double[][]{
                {1, Double.NaN, 3},
                {4, 5, 6},
                {7, 8, 9}
            });
            System.out.println("Matriz construida con NaN (peligro).");
            // Al intentar resolver, el NaN se propaga
            double[] xNan = MetodosDirectosMatriz.JordanSolucionDiagonalUnitaria(mNan, 3);
            System.out.println("Resultado con NaN: " + java.util.Arrays.toString(xNan)); 
            System.out.println("¡Ojo! El programa no falló, devolvió basura.");
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getMessage());
        }

        System.out.println("\n==================================================");
        System.out.println("   FIN DEL TESTEO");
        System.out.println("==================================================");
    }
}