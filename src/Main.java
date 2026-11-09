import modelo.Matriz;
import servicios.MetodosDirectosMatriz;
import servicios.MetodosIterativosMatriz;
import servicios.LectorMatrizTXT;

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
        // TEST 7: Validación de Dimensiones (Rectangularidad)
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 7: Matriz Irregular ---");
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

        // ---------------------------------------------------------
        // TEST 9: Determinante
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 9: Determinante ---");
        verificarDeterminante("9a 2x2", new double[][]{
            {1, 2},
            {3, 4}
        }, -2.0);
        verificarDeterminante("9b Triangular superior 3x3", new double[][]{
            {2, 5, 1},
            {0, 3, 7},
            {0, 0, 4}
        }, 24.0);
        verificarDeterminante("9c Requiere un intercambio", new double[][]{
            {0, 1},
            {1, 0}
        }, -1.0);
        verificarDeterminante("9d Singular 3x3", new double[][]{
            {1, 2, 3},
            {2, 4, 6},
            {1, 1, 1}
        }, 0.0);
        System.out.println("-- 9e Ampliada 3x4 (se rechaza) --");
        try {
            Matriz ampliada = new Matriz(new double[][]{
                {2, -1, -2, 1},
                {-4, 6, 3, 2},
                {-4, -2, 8, 3}
            });
            ampliada.determinante();
            System.out.println("FALLO: No lanzó excepción.");
        } catch (UnsupportedOperationException e) {
            if (e.getMessage().startsWith("Error:")) {
                System.out.println("FALLO ESPERADO (Ampliada): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            } else {
                System.out.println("FALLO: Mensaje sin formato 'Error:' - " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("-- 9f No cuadrada 2x4 --");
        try {
            Matriz noCuadrada = new Matriz(new double[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8}
            });
            noCuadrada.determinante();
            System.out.println("FALLO: No lanzó excepción.");
        } catch (UnsupportedOperationException e) {
            if (e.getMessage().startsWith("Error:")) {
                System.out.println("FALLO ESPERADO (No cuadrada): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            } else {
                System.out.println("FALLO: Mensaje sin formato 'Error:' - " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("-- 9g La matriz original no se modifica --");
        try {
            double[][] datosOriginales = {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 9}
            };
            Matriz original = new Matriz(datosOriginales);
            original.determinante();
            boolean intacta = true;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (original.getElemento(i, j) != datosOriginales[i][j]) {
                        intacta = false;
                    }
                }
            }
            System.out.println(intacta ? "Éxito. La matriz original quedó intacta." : "FALLO: La matriz original fue modificada.");
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 10: Matriz Inversa
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 10: Matriz Inversa ---");
        verificarInversa("10a 2x2", new double[][]{
            {4, 7},
            {2, 6}
        }, new double[][]{
            {0.6, -0.7},
            {-0.2, 0.4}
        });
        verificarInversa("10b Identidad 3x3", new double[][]{
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
        }, new double[][]{
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
        });
        verificarInversa("10c Requiere pivoteo", new double[][]{
            {0, 1},
            {1, 0}
        }, new double[][]{
            {0, 1},
            {1, 0}
        });

        System.out.println("-- 10d A * A^-1 = I (3x3 con pivote cero) --");
        try {
            double[][] datosA = {
                {0, 2, 1},
                {1, 1, 1},
                {2, 1, 3}
            };
            Matriz inv = new Matriz(datosA).inversa();
            boolean esIdentidad = true;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    double suma = 0.0;
                    for (int s = 0; s < 3; s++) {
                        suma += datosA[i][s] * inv.getElemento(s, j);
                    }
                    double esperado = (i == j) ? 1.0 : 0.0;
                    if (Math.abs(suma - esperado) > 1e-9) {
                        esIdentidad = false;
                    }
                }
            }
            System.out.println(esIdentidad ? "Éxito. A * A^-1 = I" : "FALLO: A * A^-1 no es la identidad.");
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("-- 10e Singular 2x2 --");
        try {
            new Matriz(new double[][]{
                {1, 2},
                {2, 4}
            }).inversa();
            System.out.println("FALLO: No lanzó excepción.");
        } catch (ArithmeticException e) {
            System.out.println("FALLO ESPERADO (Singular): " + e.getClass().getSimpleName() + " - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        verificarInversaNoCuadrada("10f Ampliada 2x3", new double[][]{
            {1, 2, 3},
            {4, 5, 6}
        });
        verificarInversaNoCuadrada("10g No cuadrada 2x4", new double[][]{
            {1, 2, 3, 4},
            {5, 6, 7, 8}
        });

        System.out.println("-- 10h La matriz original no se modifica --");
        try {
            double[][] datosOriginales = {
                {0, 2, 1},
                {1, 1, 1},
                {2, 1, 3}
            };
            Matriz original = new Matriz(datosOriginales);
            original.inversa();
            boolean intacta = true;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (original.getElemento(i, j) != datosOriginales[i][j]) {
                        intacta = false;
                    }
                }
            }
            System.out.println(intacta ? "Éxito. La matriz original quedó intacta." : "FALLO: La matriz original fue modificada.");
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 11: Norma Infinito
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 11: Norma Infinito ---");
        verificarNorma("11a 2x2", new double[][]{
            {1, 2},
            {3, 4}
        }, 7.0);
        // Filas: 6, 15, 8 -> 15 (por columnas daría 12)
        verificarNorma("11b Con negativos 3x3", new double[][]{
            {1, 2, 3},
            {-4, 5, -6},
            {7, 0, 1}
        }, 15.0);
        verificarNorma("11c Identidad 3x3", new double[][]{
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
        }, 1.0);
        verificarNorma("11d Matriz nula 2x2", new double[][]{
            {0, 0},
            {0, 0}
        }, 0.0);

        System.out.println("-- 11e Ampliada 2x3 --");
        try {
            new Matriz(new double[][]{
                {1, 2, 3},
                {4, 5, 6}
            }).norma();
            System.out.println("FALLO: No lanzó excepción.");
        } catch (UnsupportedOperationException e) {
            if (e.getMessage().startsWith("Error:")) {
                System.out.println("FALLO ESPERADO (No cuadrada): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            } else {
                System.out.println("FALLO: Mensaje sin formato 'Error:' - " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 12: Número de Condición
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 12: Número de Condición ---");
        verificarCondicion("12a Identidad 3x3", new double[][]{
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
        }, 1.0);
        // ||A|| = 11, ||A^-1|| = 1.3
        verificarCondicion("12b 2x2", new double[][]{
            {4, 7},
            {2, 6}
        }, 14.3);
        // ||A|| = 2.0001, ||A^-1|| = 20001
        verificarCondicion("12c Mal condicionada", new double[][]{
            {1, 1},
            {1, 1.0001}
        }, 40004.0001);

        System.out.println("-- 12d Singular 2x2 --");
        try {
            new Matriz(new double[][]{
                {1, 2},
                {2, 4}
            }).condicion();
            System.out.println("FALLO: No lanzó excepción.");
        } catch (ArithmeticException e) {
            System.out.println("FALLO ESPERADO (Singular): " + e.getClass().getSimpleName() + " - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("-- 12e Ampliada 2x3 --");
        try {
            new Matriz(new double[][]{
                {1, 2, 3},
                {4, 5, 6}
            }).condicion();
            System.out.println("FALLO: No lanzó excepción.");
        } catch (UnsupportedOperationException e) {
            if (e.getMessage().startsWith("Error: El número de condición")) {
                System.out.println("FALLO ESPERADO (No cuadrada): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            } else {
                System.out.println("FALLO: Mensaje inesperado - " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 13: Bien Condicionado (cond(A) <= MAX_CONDICION)
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 13: Bien Condicionado (MAX_CONDICION = " + Matriz.MAX_CONDICION + ") ---");
        verificarBienCondicionado("13a Identidad 3x3 (cond = 1)", new double[][]{
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
        }, true);
        verificarBienCondicionado("13b 2x2 (cond = 14.3)", new double[][]{
            {4, 7},
            {2, 6}
        }, true);
        verificarBienCondicionado("13c Límite (cond = 50)", new double[][]{
            {50, 0},
            {0, 1}
        }, true);
        verificarBienCondicionado("13d Mal condicionada (cond = 40004)", new double[][]{
            {1, 1},
            {1, 1.0001}
        }, false);
        verificarBienCondicionado("13e Singular", new double[][]{
            {1, 2},
            {2, 4}
        }, false);

        System.out.println("-- 13f Ampliada 2x3 --");
        try {
            new Matriz(new double[][]{
                {1, 2, 3},
                {4, 5, 6}
            }).bienCondicionado();
            System.out.println("FALLO: No lanzó excepción.");
        } catch (UnsupportedOperationException e) {
            System.out.println("FALLO ESPERADO (No cuadrada): " + e.getClass().getSimpleName() + " - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 14: Crout LU1 (diagonal de U unitaria)
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 14: Crout LU1 ---");
        // Mismo A que el TEST 3
        double[][] datosCrout = {
            {2, -1, -2},
            {-4, 6, 3},
            {-4, -2, 8}
        };

        System.out.println("-- 14a Factorización LU1 --");
        try {
            Matriz[] LU1 = MetodosDirectosMatriz.factorizarCroutLU1(new Matriz(datosCrout));
            double[][] esperadoL = {
                {2, 0, 0},
                {-4, 4, 0},
                {-4, -4, 3}
            };
            double[][] esperadoU1 = {
                {1, -0.5, -1},
                {0, 1, -0.25},
                {0, 0, 1}
            };
            boolean coincide = true;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (Math.abs(LU1[0].getElemento(i, j) - esperadoL[i][j]) > 1e-9
                            || Math.abs(LU1[1].getElemento(i, j) - esperadoU1[i][j]) > 1e-9) {
                        coincide = false;
                    }
                }
            }
            System.out.println(coincide ? "Éxito." : "FALLO: L y U1 obtenidas:");
            System.out.println("L:");
            LU1[0].mostrarMatriz();
            System.out.println("U1:");
            LU1[1].mostrarMatriz();
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("-- 14b Pivote cero --");
        try {
            MetodosDirectosMatriz.factorizarCroutLU1(new Matriz(new double[][]{
                {0, 2, 1},
                {1, 1, 1},
                {2, 1, 3}
            }));
            System.out.println("FALLO: No lanzó excepción.");
        } catch (ArithmeticException e) {
            System.out.println("FALLO ESPERADO (Pivote 0): " + e.getClass().getSimpleName() + " - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("-- 14c La matriz original no se modifica --");
        try {
            Matriz original = new Matriz(datosCrout);
            MetodosDirectosMatriz.factorizarCroutLU1(original);
            boolean intacta = true;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (original.getElemento(i, j) != datosCrout[i][j]) {
                        intacta = false;
                    }
                }
            }
            System.out.println(intacta ? "Éxito. La matriz original quedó intacta." : "FALLO: La matriz original fue modificada.");
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 15: Crout L1U (diagonal de L unitaria)
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 15: Crout L1U ---");

        System.out.println("-- 15a Factorización L1U --");
        try {
            Matriz[] L1U = MetodosDirectosMatriz.factorizarCroutL1U(new Matriz(datosCrout));
            double[][] esperadoL1 = {
                {1, 0, 0},
                {-2, 1, 0},
                {-2, -1, 1}
            };
            double[][] esperadoU = {
                {2, -1, -2},
                {0, 4, -1},
                {0, 0, 3}
            };
            boolean coincide = true;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (Math.abs(L1U[0].getElemento(i, j) - esperadoL1[i][j]) > 1e-9
                            || Math.abs(L1U[1].getElemento(i, j) - esperadoU[i][j]) > 1e-9) {
                        coincide = false;
                    }
                }
            }
            System.out.println(coincide ? "Éxito." : "FALLO: L1 y U obtenidas:");
            System.out.println("L1:");
            L1U[0].mostrarMatriz();
            System.out.println("U:");
            L1U[1].mostrarMatriz();
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("-- 15b Pivote cero --");
        try {
            MetodosDirectosMatriz.factorizarCroutL1U(new Matriz(new double[][]{
                {0, 2, 1},
                {1, 1, 1},
                {2, 1, 3}
            }));
            System.out.println("FALLO: No lanzó excepción.");
        } catch (ArithmeticException e) {
            System.out.println("FALLO ESPERADO (Pivote 0): " + e.getClass().getSimpleName() + " - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("-- 15c La matriz original no se modifica --");
        try {
            Matriz original = new Matriz(datosCrout);
            MetodosDirectosMatriz.factorizarCroutL1U(original);
            boolean intacta = true;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (original.getElemento(i, j) != datosCrout[i][j]) {
                        intacta = false;
                    }
                }
            }
            System.out.println(intacta ? "Éxito. La matriz original quedó intacta." : "FALLO: La matriz original fue modificada.");
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 16: Crout - Solución del sistema (L1U y LU1)
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 16: Crout - Solución del sistema ---");
        // Mismo A que el TEST 14, con b elegido para que x = [1, 2, 3]
        double[][] datosCroutAmpliada = {
            {2, -1, -2, -6},
            {-4, 6, 3, 17},
            {-4, -2, 8, 16}
        };
        double[] xEsperado = {1, 2, 3};
        double[][] datosPivoteCero = {
            {0, 2, 1, 4},
            {1, 1, 1, 6},
            {2, 1, 3, 10}
        };

        for (String variante : new String[]{"L1U", "LU1"}) {
            System.out.println("-- 16 Solución " + variante + " --");
            try {
                Matriz original = new Matriz(datosCroutAmpliada);
                double[] x = variante.equals("L1U")
                    ? MetodosDirectosMatriz.CroutSolucionL1U(original)
                    : MetodosDirectosMatriz.CroutSolucionLU1(original);
                boolean coincide = true;
                for (int i = 0; i < 3; i++) {
                    if (Math.abs(x[i] - xEsperado[i]) > 1e-9) {
                        coincide = false;
                    }
                }
                System.out.println(coincide
                    ? "Éxito. Solución: " + java.util.Arrays.toString(x)
                    : "FALLO: Solución = " + java.util.Arrays.toString(x) + " (esperado " + java.util.Arrays.toString(xEsperado) + ")");

                boolean intacta = true;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (original.getElemento(i, j) != datosCroutAmpliada[i][j]) {
                            intacta = false;
                        }
                    }
                }
                System.out.println(intacta ? "Éxito. La matriz original quedó intacta." : "FALLO: La matriz original fue modificada.");
            } catch (Exception e) {
                System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }

            System.out.println("-- 16 Pivote cero " + variante + " --");
            try {
                Matriz pivoteCero = new Matriz(datosPivoteCero);
                if (variante.equals("L1U")) {
                    MetodosDirectosMatriz.CroutSolucionL1U(pivoteCero);
                } else {
                    MetodosDirectosMatriz.CroutSolucionLU1(pivoteCero);
                }
                System.out.println("FALLO: No lanzó excepción.");
            } catch (ArithmeticException e) {
                System.out.println("FALLO ESPERADO (Pivote 0): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            } catch (Exception e) {
                System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }

        // ---------------------------------------------------------
        // TEST 17: Transpuesta
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 17: Transpuesta ---");
        verificarTranspuesta("17a Rectangular 2x3 -> 3x2", new double[][]{
            {1, 2, 3},
            {4, 5, 6}
        }, new double[][]{
            {1, 4},
            {2, 5},
            {3, 6}
        });
        verificarTranspuesta("17b Cuadrada 3x3", new double[][]{
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        }, new double[][]{
            {1, 4, 7},
            {2, 5, 8},
            {3, 6, 9}
        });
        verificarTranspuesta("17c Simétrica (queda igual)", new double[][]{
            {2, -1},
            {-1, 3}
        }, new double[][]{
            {2, -1},
            {-1, 3}
        });
        verificarTranspuesta("17d Vector fila 1x3 -> columna 3x1", new double[][]{
            {1, 2, 3}
        }, new double[][]{
            {1},
            {2},
            {3}
        });

        System.out.println("-- 17e (A^T)^T = A y la original no se modifica --");
        try {
            double[][] datosOriginales = {
                {1, 2, 3},
                {4, 5, 6}
            };
            Matriz original = new Matriz(datosOriginales);
            Matriz dobleT = original.transponer().transponer();
            boolean igual = dobleT.getCantFilas() == 2 && dobleT.getCantColumnas() == 3;
            boolean intacta = original.getCantFilas() == 2 && original.getCantColumnas() == 3;
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 3; j++) {
                    if (igual && dobleT.getElemento(i, j) != datosOriginales[i][j]) {
                        igual = false;
                    }
                    if (intacta && original.getElemento(i, j) != datosOriginales[i][j]) {
                        intacta = false;
                    }
                }
            }
            System.out.println(igual ? "Éxito. (A^T)^T = A" : "FALLO: (A^T)^T distinta de A.");
            System.out.println(intacta ? "Éxito. La matriz original quedó intacta." : "FALLO: La matriz original fue modificada.");
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 18: Cholesky (L * L^T y U^T * U)
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 18: Cholesky ---");

        System.out.println("-- 18a esSimetrica --");
        boolean simetricaOk = new Matriz(new double[][]{{4, 12, -16}, {12, 37, -43}, {-16, -43, 98}}).esSimetrica()
            && !new Matriz(new double[][]{{1, 2}, {3, 4}}).esSimetrica()
            && !new Matriz(new double[][]{{1, 2, 3}, {2, 1, 3}}).esSimetrica();
        System.out.println(simetricaOk ? "Éxito. Detecta simétrica, no simétrica y no cuadrada." : "FALLO: esSimetrica devuelve un resultado incorrecto.");

        // Simétrica definida positiva (L = [[2,0,0],[6,1,0],[-8,5,3]]), b elegido para que x = [1, 2, 3]
        double[][] datosCholesky = {
            {4, 12, -16},
            {12, 37, -43},
            {-16, -43, 98}
        };
        double[] bCholesky = {-20, -43, 192};
        double[] xCholesky = {1, 2, 3};
        double[][] datosNoSimetrica = {
            {4, 1},
            {2, 3}
        };
        // Simétrica pero no definida positiva (autovalores -1 y 3)
        double[][] datosNoDefinida = {
            {1, 2},
            {2, 1}
        };
        double[] b2 = {1, 2};
        double[][] datosAbCholesky = ampliar(datosCholesky, bCholesky);

        for (String variante : new String[]{"L", "U"}) {
            System.out.println("-- 18 Solución Cholesky-" + variante + " --");
            try {
                Matriz Ab = new Matriz(datosAbCholesky);
                double[] x = resolverCholesky(variante, Ab);
                boolean coincide = true;
                for (int i = 0; i < 3; i++) {
                    if (Math.abs(x[i] - xCholesky[i]) > 1e-9) {
                        coincide = false;
                    }
                }
                System.out.println(coincide
                    ? "Éxito. Solución: " + java.util.Arrays.toString(x)
                    : "FALLO: Solución = " + java.util.Arrays.toString(x) + " (esperado " + java.util.Arrays.toString(xCholesky) + ")");

                boolean intacta = true;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (Ab.getElemento(i, j) != datosAbCholesky[i][j]) {
                            intacta = false;
                        }
                    }
                }
                System.out.println(intacta ? "Éxito. Ab quedó intacta." : "FALLO: Ab fue modificada.");
            } catch (Exception e) {
                System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }

            System.out.println("-- 18 No simétrica Cholesky-" + variante + " --");
            try {
                resolverCholesky(variante, new Matriz(ampliar(datosNoSimetrica, b2)));
                System.out.println("FALLO: No lanzó excepción.");
            } catch (UnsupportedOperationException e) {
                System.out.println("FALLO ESPERADO (No simétrica): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            } catch (Exception e) {
                System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }

            System.out.println("-- 18 No definida positiva Cholesky-" + variante + " --");
            try {
                resolverCholesky(variante, new Matriz(ampliar(datosNoDefinida, b2)));
                System.out.println("FALLO: No lanzó excepción.");
            } catch (ArithmeticException e) {
                System.out.println("FALLO ESPERADO (No definida positiva): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            } catch (Exception e) {
                System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }

            System.out.println("-- 18 No ampliada Cholesky-" + variante + " --");
            try {
                resolverCholesky(variante, new Matriz(datosCholesky));
                System.out.println("FALLO: No lanzó excepción.");
            } catch (IllegalArgumentException e) {
                System.out.println("FALLO ESPERADO (No ampliada): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            } catch (Exception e) {
                System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }

        // ---------------------------------------------------------
        // TEST 19: Regla det(A) != 0 en los métodos que resuelven A * x = b
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 19: Regla det(A) != 0 ---");

        // Simétrica y singular (fila 2 = 2 * fila 1), b compatible: infinitas soluciones
        double[][] datosInfinitas = {
            {1, 2, 3},
            {2, 4, 6}
        };
        String[] metodos = {"Jordan", "JordanDiagonalUnitaria", "CroutL1U", "CroutLU1", "CholeskyL", "CholeskyU"};
        for (String metodo : metodos) {
            System.out.println("-- 19 Infinitas soluciones " + metodo + " --");
            try {
                resolverSistema(metodo, new Matriz(datosInfinitas));
                System.out.println("FALLO: No lanzó excepción.");
            } catch (ArithmeticException e) {
                System.out.println(e.getMessage().contains("det(A)")
                    ? "FALLO ESPERADO (det(A) = 0): " + e.getClass().getSimpleName() + " - " + e.getMessage()
                    : "FALLO: No cortó por la regla de det(A) - " + e.getMessage());
            } catch (Exception e) {
                System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }

        // ==========================================================
        // TEST 20: Módulo de Eliminación Gaussiana (Resolución)
        // ==========================================================
        System.out.println("\n==================================================");
        System.out.println("   INICIANDO TESTEO QA - ELIMINACIÓN GAUSSIANA");
        System.out.println("==================================================\n");

        // ---------------------------------------------------------
        // TEST 20a: Gauss - Pivoteo Simple con Cero en Diagonal
        // ---------------------------------------------------------
        System.out.println("--- TEST 20a: Gauss Pivoteo Simple (Cero en Diagonal) ---");
        try {
            // El pivote en [0][0] es 0. El Pivoteo Simple debe intercambiar la fila 0 por la 1.
            Matriz amp20a = new Matriz(new double[][]{
                {0, 2, 1, 4},
                {1, 1, 1, 6},
                {2, 1, 3, 10}
            });
            
            double[] x20a = MetodosDirectosMatriz.GaussPivoteoSimple(amp20a);
            System.out.println("Éxito. Solución: " + java.util.Arrays.toString(x20a)); // Esperado: [1.0, 1.0, 2.0]
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 20b: Gauss - Pivoteo Parcial (Propagación de Errores)
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 20b: Gauss Pivoteo Parcial (Estabilidad Numérica) ---");
        try {
            // Un pivote muy pequeño (0.0001) causa explosión de error si no se pivotea.
            // El pivoteo parcial debe mandar la Fila 2 a la Fila 1.
            Matriz amp20b = new Matriz(new double[][]{
                {0.0001, 1, 1},
                {1, 1, 2}
            });
            
            double[] x20b = MetodosDirectosMatriz.GaussPivoteoParcial(amp20b);
            System.out.println("Éxito. Solución: " + java.util.Arrays.toString(x20b)); // Esperado cercano a [1.0, 1.0]
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 20c: Gauss - Pivoteo Total (Rastreador de Incógnitas)
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 20c: Gauss Pivoteo Total (Rastreo de Columnas) ---");
        try {
            // En este sistema, el valor absoluto más grande está en la columna 2.
            // El algoritmo DEBE intercambiar columnas y reordenar las variables internamente.
            Matriz amp20c = new Matriz(new double[][]{
                {1, 2, 10, 13},
                {1, 5, 2, 8},
                {2, 1, 1, 4}
            });
            
            double[] x20c = MetodosDirectosMatriz.GaussPivoteoTotal(amp20c);
            System.out.println("Éxito. Solución: " + java.util.Arrays.toString(x20c)); // Esperado: [1.0, 1.0, 1.0]
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 20d: Excepción Defensiva - Matriz NO Ampliada
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 20d: Fallo Esperado (Falta vector B) ---");
        try {
            // El usuario olvida cargar la columna de términos independientes.
            Matriz noAmp = new Matriz(new double[][]{
                {1, 2},
                {3, 4}
            });
            
            double[] x20d = MetodosDirectosMatriz.GaussPivoteoParcial(noAmp);
            System.out.println("FALLO: El algoritmo resolvió una matriz sin b: " + java.util.Arrays.toString(x20d));
        } catch (IllegalArgumentException e) {
            System.out.println("ÉXITO DEFENSIVO (Falta Vector b): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("FALLO DE TIPO DE EXCEPCIÓN: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 20e: Singularidad y Sistema sin Solución
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 20e: Fallo Esperado (Sistema Singular Algorítmico) ---");
        try {
            // Sistema singular oculto (Fila 2 = Fila 1 * 2)
            Matriz ampSingular = new Matriz(new double[][]{
                {1, 1, 1, 3},
                {2, 2, 2, 6},
                {0, 2, 1, 3}
            });
            
            double[] x20e = MetodosDirectosMatriz.GaussPivoteoTotal(ampSingular);
            System.out.println("FALLO: Resolvió un sistema sin solución única. Resultado: " + java.util.Arrays.toString(x20e));
        } catch (ArithmeticException e) {
            System.out.println("ÉXITO DEFENSIVO (Singularidad): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("FALLO DE TIPO DE EXCEPCIÓN: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ---------------------------------------------------------
        // TEST 21: Métodos iterativos (ejemplos de Burden & Faires, cap. 7)
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 21: Métodos Iterativos ---");

        // Burden 7.3, Ejemplo 1: diagonal estrictamente dominante, x = (1, 2, -1, 1)
        double[][] datosBurden73 = {
            {10, -1, 2, 0, 6},
            {-1, 11, -1, 3, 25},
            {2, -1, 10, -1, -11},
            {0, 3, -1, 8, 15}
        };
        double[] xBurden73 = {1, 2, -1, 1};
        double[] x0Burden73 = new double[4];
        verificarIterativo("21a Jacobi (Burden 7.3)", xBurden73,
            () -> MetodosIterativosMatriz.JacobiSolucion(new Matriz(datosBurden73), x0Burden73, 1e-10, 1000));
        verificarIterativo("21b Gauss-Seidel (Burden 7.3)", xBurden73,
            () -> MetodosIterativosMatriz.GaussSeidelSolucion(new Matriz(datosBurden73), x0Burden73, 1e-10, 1000));
        verificarIterativo("21c Relajación (Burden 7.3)", xBurden73,
            () -> MetodosIterativosMatriz.Relajacion(new Matriz(datosBurden73), x0Burden73, 1e-10, 10000));

        System.out.println("-- 21d x0 no se modifica --");
        System.out.println(java.util.Arrays.equals(x0Burden73, new double[4])
            ? "Éxito. x0 quedó intacto."
            : "FALLO: x0 fue modificado: " + java.util.Arrays.toString(x0Burden73));

        // Burden 7.4, Ejemplo 3: SOR con ω = 1.25 desde x0 = (1, 1, 1), x = (3, 4, -5)
        double[][] datosBurden74 = {
            {4, 3, 0, 24},
            {3, 4, -1, 30},
            {0, -1, 4, -24}
        };
        double[] xBurden74 = {3, 4, -5};
        verificarIterativo("21e SOR ω=1.25 (Burden 7.4)", xBurden74,
            () -> MetodosIterativosMatriz.SORSolucion(new Matriz(datosBurden74), new double[]{1, 1, 1}, 1.25, 1e-10, 1000));

        // Con las mismas 7 iteraciones, SOR (ω=1.25) debe quedar más cerca que Gauss-Seidel
        System.out.println("-- 21f SOR acelera a Gauss-Seidel (7 iteraciones) --");
        try {
            double errorSOR = errorInfinito(MetodosIterativosMatriz.SORSolucion(new Matriz(datosBurden74), new double[]{1, 1, 1}, 1.25, 0, 7), xBurden74);
            double errorGS = errorInfinito(MetodosIterativosMatriz.GaussSeidelSolucion(new Matriz(datosBurden74), new double[]{1, 1, 1}, 0, 7), xBurden74);
            System.out.println(errorSOR < errorGS
                ? "Éxito. Error SOR = " + errorSOR + " < Error Gauss-Seidel = " + errorGS
                : "FALLO: Error SOR = " + errorSOR + " >= Error Gauss-Seidel = " + errorGS);
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // Burden 7.3, ejercicios: Jacobi converge y Gauss-Seidel diverge, x = (1, 1, 1)
        double[][] datosSoloJacobi = {
            {1, 2, -2, 1},
            {1, 1, 1, 3},
            {2, 2, 1, 5}
        };
        // ... y al revés: Gauss-Seidel converge y Jacobi diverge, x = (1, 1, 1)
        double[][] datosSoloSeidel = {
            {2, -1, 1, 2},
            {2, 2, 2, 6},
            {-1, -1, 2, 0}
        };
        double[] unos = {1, 1, 1};
        verificarIterativo("21g Jacobi converge (contraejemplo)", unos,
            () -> MetodosIterativosMatriz.JacobiSolucion(new Matriz(datosSoloJacobi), new double[3], 1e-10, 100));
        verificarDivergencia("21h Gauss-Seidel diverge (contraejemplo)", unos,
            () -> MetodosIterativosMatriz.GaussSeidelSolucion(new Matriz(datosSoloJacobi), new double[3], 1e-10, 100));
        verificarIterativo("21i Gauss-Seidel converge (contraejemplo)", unos,
            () -> MetodosIterativosMatriz.GaussSeidelSolucion(new Matriz(datosSoloSeidel), new double[3], 1e-10, 100));
        verificarDivergencia("21j Jacobi diverge (contraejemplo)", unos,
            () -> MetodosIterativosMatriz.JacobiSolucion(new Matriz(datosSoloSeidel), new double[3], 1e-10, 100));

        // Burden 7.5, Ejemplo 4: matriz mal condicionada, x = (1, 1, 1)
        double[][] datosBurden75 = {
            {3.3330, 15920, -10.333, 15913},
            {2.2220, 16.710, 9.6120, 28.544},
            {1.5611, 5.1791, 1.6852, 8.4254}
        };
        verificarIterativo("21k Mejoramiento iterativo (Burden 7.5)", unos,
            () -> MetodosIterativosMatriz.MejoramientoIterativo(new Matriz(datosBurden75), new double[3], 1e-10, 20));

        System.out.println("-- 21l SOR con ω fuera de (0, 2) --");
        for (double omega : new double[]{0, 2, -0.5, 2.5}) {
            try {
                MetodosIterativosMatriz.SORSolucion(new Matriz(datosBurden74), new double[3], omega, 1e-10, 100);
                System.out.println("FALLO: No lanzó excepción con ω = " + omega);
            } catch (IllegalArgumentException e) {
                System.out.println("FALLO ESPERADO (ω = " + omega + "): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            } catch (Exception e) {
                System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }

        System.out.println("-- 21m Cero en la diagonal --");
        double[][] datosDiagonalNula = {
            {0, 1, 1},
            {1, 1, 2}
        };
        for (String metodo : new String[]{"Jacobi", "GaussSeidel", "SOR", "Relajacion"}) {
            try {
                resolverIterativo(metodo, new Matriz(datosDiagonalNula));
                System.out.println("FALLO: " + metodo + " no lanzó excepción.");
            } catch (ArithmeticException e) {
                System.out.println("FALLO ESPERADO (" + metodo + "): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            } catch (Exception e) {
                System.out.println("FALLO: Excepción incorrecta en " + metodo + " " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }

        System.out.println("-- 21n Matriz no ampliada --");
        for (String metodo : new String[]{"Jacobi", "GaussSeidel", "SOR", "Relajacion", "Mejoramiento"}) {
            try {
                resolverIterativo(metodo, new Matriz(new double[][]{{4, 1}, {1, 3}}));
                System.out.println("FALLO: " + metodo + " no lanzó excepción.");
            } catch (IllegalArgumentException e) {
                System.out.println("FALLO ESPERADO (" + metodo + "): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            } catch (Exception e) {
                System.out.println("FALLO: Excepción incorrecta en " + metodo + " " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }

        // ---------------------------------------------------------
        // TEST 22: Casos del apunte de la cátedra (Barberis, Sistemas de Ecuaciones Lineales)
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 22: Casos de la Cátedra ---");

        // Sistema sensible del apunte (pág. 17). El apunte dice x = (1, 5, -1): es una errata, A * (1, 5, -1) != b.
        // La solución correcta es x = (1, 2, -1)
        double[][] datosSensible = {
            {3.02, -1.05, 2.53, -1.61},
            {4.33, 0.56, -1.78, 7.23},
            {-0.83, -0.54, 1.47, -3.38}
        };
        double[] xSensible = {1, 2, -1};
        verificarSolucion("22a Solución del sistema sensible", xSensible, 1e-9,
            () -> MetodosDirectosMatriz.GaussPivoteoParcial(new Matriz(datosSensible)));

        // Pág. 18: cond(A) = ||A|| * ||A^-1|| = 6.67 * 1138.6758 = 7594.967585 (el apunte multiplica valores redondeados)
        System.out.println("-- 22b cond(A) del sistema sensible --");
        try {
            Matriz ASensible = new Matriz(datosSensible).extraerA();
            double norma = ASensible.norma();
            double normaInversa = ASensible.inversa().norma();
            double cond = ASensible.condicion();
            boolean ok = Math.abs(norma - 6.67) < 1e-12
                && Math.abs(normaInversa - 1138.6758) < 1e-4
                && Math.abs(cond - 7594.9676) < 1e-3;
            System.out.println(ok
                ? "Éxito. ||A|| = " + norma + ", ||A^-1|| = " + normaInversa + ", cond(A) = " + cond
                : "FALLO: ||A|| = " + norma + ", ||A^-1|| = " + normaInversa + ", cond(A) = " + cond);
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // Pág. 17: perturbaciones. El apunte dice x2 = 6.522890 para a11 = 3: errata de dígitos, el valor es 6.522089
        double[][] datosA11 = {
            {3.0, -1.05, 2.53, -1.61},
            {4.33, 0.56, -1.78, 7.23},
            {-0.83, -0.54, 1.47, -3.38}
        };
        double[] xA11 = {1.127677, 6.522089, 0.733265};
        double[][] datosB3 = {
            {3.02, -1.05, 2.53, -1.61},
            {4.33, 0.56, -1.78, 7.23},
            {-0.83, -0.54, 1.47, -3.3}
        };
        double[] xB3 = {-0.4840218, -51.593141, -21.470771};
        verificarSolucion("22c Perturbación a11 = 3", xA11, 1e-6,
            () -> MetodosDirectosMatriz.GaussPivoteoParcial(new Matriz(datosA11)));
        verificarSolucion("22d Perturbación b3 = -3.3", xB3, 1e-6,
            () -> MetodosDirectosMatriz.GaussPivoteoParcial(new Matriz(datosB3)));

        // Pág. 19: cotas de error relativo ||x* - x|| / ||x|| <= cond(A) * ||δA|| / ||A||  y  <= cond(A) * ||δb|| / ||b||
        System.out.println("-- 22e Cotas de error relativo --");
        try {
            double cond = new Matriz(datosSensible).extraerA().condicion();
            double errorA11 = errorInfinito(xA11, xSensible) / Matriz.normaInfinitoVector(xSensible);
            double cotaA11 = cond * 0.02 / 6.67;
            double errorB3 = errorInfinito(xB3, xSensible) / Matriz.normaInfinitoVector(xSensible);
            double cotaB3 = cond * 0.08 / 7.23;
            System.out.println(errorA11 <= cotaA11 && errorB3 <= cotaB3
                ? "Éxito. δA: " + errorA11 + " <= " + cotaA11 + "; δb: " + errorB3 + " <= " + cotaB3
                : "FALLO: δA: " + errorA11 + " vs " + cotaA11 + "; δb: " + errorB3 + " vs " + cotaB3);
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // Pág. 20: el mejoramiento parte de una aproximación x0 obtenida por un método directo
        verificarIterativo("22f Mejoramiento desde la solución perturbada", xSensible,
            () -> MetodosIterativosMatriz.MejoramientoIterativo(new Matriz(datosSensible), xA11.clone(), 1e-12, 20));
        verificarIterativo("22g Mejoramiento desde x0 = 0", xSensible,
            () -> MetodosIterativosMatriz.MejoramientoIterativo(new Matriz(datosSensible), new double[3], 1e-12, 20));

        // Pág. 24: Gauss-Seidel converge si A es estrictamente diagonal dominante O simétrica definida positiva.
        // La matriz de Cholesky (TEST 18) es SDP y no es diagonal dominante; para Jacobi no hay garantía (acá diverge)
        double[][] datosSDP = ampliar(datosCholesky, bCholesky);
        verificarIterativo("22h Gauss-Seidel converge con A simétrica definida positiva", xCholesky,
            () -> MetodosIterativosMatriz.GaussSeidelSolucion(new Matriz(datosSDP), new double[3], 1e-12, 10000));
        verificarDivergencia("22i Jacobi sin garantía con A simétrica definida positiva", xCholesky,
            () -> MetodosIterativosMatriz.JacobiSolucion(new Matriz(datosSDP), new double[3], 1e-12, 100));

        // Pág. 26: con ω = 1, SOR es Gauss-Seidel (mismas iteraciones, resultado idéntico)
        System.out.println("-- 22j SOR con ω = 1 es Gauss-Seidel --");
        try {
            boolean iguales = true;
            for (int iteraciones : new int[]{1, 3, 7}) {
                double[] xSOR = MetodosIterativosMatriz.SORSolucion(new Matriz(datosBurden74), new double[]{1, 1, 1}, 1.0, 0, iteraciones);
                double[] xGS = MetodosIterativosMatriz.GaussSeidelSolucion(new Matriz(datosBurden74), new double[]{1, 1, 1}, 0, iteraciones);
                if (!java.util.Arrays.equals(xSOR, xGS)) {
                    iguales = false;
                }
            }
            System.out.println(iguales
                ? "Éxito. SOR(ω=1) y Gauss-Seidel coinciden tras 1, 3 y 7 iteraciones."
                : "FALLO: SOR(ω=1) difiere de Gauss-Seidel.");
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // Pág. 25: con 0 < ω < 1 el método es de "relajación" (subrelajación)
        verificarIterativo("22k SOR con ω = 0.5 (subrelajación)", xBurden73,
            () -> MetodosIterativosMatriz.SORSolucion(new Matriz(datosBurden73), new double[4], 0.5, 1e-10, 1000));

        // ---------------------------------------------------------
        // TEST 23: Carga de la matriz ampliada desde TXT (fail-fast)
        // ---------------------------------------------------------
        System.out.println("\n--- TEST 23: Carga desde TXT ---");

        double[][] ampliada2x3 = {
            {4, 1, 5},
            {1, 3, 6}
        };
        verificarLecturaTXT("23a Archivo correcto", "4 1 5\n1 3 6\n", 2, ampliada2x3);
        verificarLecturaTXT("23b Valores en una sola línea, tabs y CRLF", "4\t1  5 1\r\n3 6", 2, ampliada2x3);
        verificarLecturaTXT("23c Coma decimal, punto, científica y signos", "1,5 -3.25 1e-3\n+2 .5 2E2", 2,
            new double[][]{{1.5, -3.25, 0.001}, {2, 0.5, 200}});
        verificarLecturaTXT("23d Archivo con BOM de UTF-8", "\uFEFF4 1 5\n1 3 6", 2, ampliada2x3);

        System.out.println("-- 23e No depende del locale (es-AR) --");
        java.util.Locale localeOriginal = java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(java.util.Locale.forLanguageTag("es-AR"));
            double[][] leida = LectorMatrizTXT.leerAmpliada("1.5 1.000 2\n3 4 5", 2);
            System.out.println(leida[0][0] == 1.5 && leida[0][1] == 1.0
                ? "Éxito. Con locale es-AR, 1.5 -> 1.5 y 1.000 -> 1.0"
                : "FALLO: Con locale es-AR leyó " + java.util.Arrays.deepToString(leida));
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        } finally {
            java.util.Locale.setDefault(localeOriginal);
        }

        verificarErrorTXT("23f Valor no numérico", "4 1 x=5\n1 3 6", 2, "'x=5'", "línea 1");
        verificarErrorTXT("23g Faltan valores", "4 1 5\n1 3", 2, "6", "5");
        verificarErrorTXT("23h Sobran valores", "4 1 5\n1 3 6\n7 8 9", 2, "6", "9");
        verificarErrorTXT("23i Archivo vacío", "", 2, "6", "0");
        verificarErrorTXT("23j NaN", "4 1 5\n1 NaN 6", 2, "'NaN'", "línea 2");
        verificarErrorTXT("23k Infinity", "4 1 5\n1 3 Infinity", 2, "'Infinity'", "línea 2");
        verificarErrorTXT("23l Valor fuera de rango de double", "4 1 5\n1 3 1e400", 2, "'1e400'", "línea 2");
        verificarErrorTXT("23m Coma como separador", "4,1,5\n1,3,6", 2, "'4,1,5'", "línea 1");
        verificarErrorTXT("23n Sufijo de Java (1d)", "4 1 5\n1 3 6d", 2, "'6d'", "línea 2");
        verificarErrorTXT("23o n inválido", "4 1 5\n1 3 6", 0, "n", "");
        verificarErrorTXT("23p Contenido null", null, 2, "", "");

        System.out.println("-- 23q Lectura desde archivo --");
        try {
            java.nio.file.Path archivo = java.nio.file.Files.createTempFile("matriz", ".txt");
            try {
                java.nio.file.Files.writeString(archivo, "4 1 5\n1 3 6\n");
                double[][] leida = LectorMatrizTXT.leerAmpliada(archivo, 2);
                System.out.println(java.util.Arrays.deepEquals(leida, ampliada2x3)
                    ? "Éxito. Leída: " + java.util.Arrays.deepToString(leida)
                    : "FALLO: Leída " + java.util.Arrays.deepToString(leida));
            } finally {
                java.nio.file.Files.deleteIfExists(archivo);
            }
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // Matriz grande: 100 filas de 101 valores
        System.out.println("-- 23s Matriz de 100 x 101 --");
        try {
            int nMax = 100;
            double[][] esperadaMax = new double[nMax][nMax + 1];
            StringBuilder contenidoMax = new StringBuilder();
            for (int i = 0; i < nMax; i++) {
                for (int j = 0; j < nMax + 1; j++) {
                    esperadaMax[i][j] = (i == j) ? 1000 + i : (i - j) / 4.0; // incluye negativos y decimales
                    contenidoMax.append(esperadaMax[i][j]).append(j < nMax ? " " : "\n");
                }
            }
            double[][] leida = LectorMatrizTXT.leerAmpliada(contenidoMax.toString(), nMax);
            Matriz ampliadaMax = new Matriz(leida);
            System.out.println(java.util.Arrays.deepEquals(leida, esperadaMax) && ampliadaMax.esAmpliada()
                ? "Éxito. Cargó " + ampliadaMax.getCantFilas() + " x " + ampliadaMax.getCantColumnas() + " valores sin diferencias."
                : "FALLO: La matriz leída no coincide con la escrita.");
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("-- 23r Archivo inexistente --");
        try {
            LectorMatrizTXT.leerAmpliada(java.nio.file.Path.of("no-existe-" + System.nanoTime() + ".txt"), 2);
            System.out.println("FALLO: No lanzó excepción.");
        } catch (java.io.IOException e) {
            System.out.println("FALLO ESPERADO (Archivo inexistente): " + e.getClass().getSimpleName());
        } catch (Exception e) {
            System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("\n==================================================");
        System.out.println("   FIN DEL TESTEO");
        System.out.println("==================================================");
    }

    private static double[] resolverCholesky(String variante, Matriz Ab) {
        return variante.equals("L")
            ? MetodosDirectosMatriz.CholeskySolucionL(Ab)
            : MetodosDirectosMatriz.CholeskySolucionU(Ab);
    }

    private static double[] resolverSistema(String metodo, Matriz Ab) {
        int n = Ab.getCantFilas();
        switch (metodo) {
            case "Jordan": return MetodosDirectosMatriz.JordanSolucion(Ab, n);
            case "JordanDiagonalUnitaria": return MetodosDirectosMatriz.JordanSolucionDiagonalUnitaria(Ab, n);
            case "CroutL1U": return MetodosDirectosMatriz.CroutSolucionL1U(Ab);
            case "CroutLU1": return MetodosDirectosMatriz.CroutSolucionLU1(Ab);
            case "CholeskyL": return MetodosDirectosMatriz.CholeskySolucionL(Ab);
            default: return MetodosDirectosMatriz.CholeskySolucionU(Ab);
        }
    }

    private static double[] resolverIterativo(String metodo, Matriz Ab) {
        double[] x0 = new double[Ab.getCantFilas()];
        switch (metodo) {
            case "Jacobi": return MetodosIterativosMatriz.JacobiSolucion(Ab, x0, 1e-10, 100);
            case "GaussSeidel": return MetodosIterativosMatriz.GaussSeidelSolucion(Ab, x0, 1e-10, 100);
            case "SOR": return MetodosIterativosMatriz.SORSolucion(Ab, x0, 1.25, 1e-10, 100);
            case "Relajacion": return MetodosIterativosMatriz.Relajacion(Ab, x0, 1e-10, 100);
            default: return MetodosIterativosMatriz.MejoramientoIterativo(Ab, x0, 1e-10, 100);
        }
    }

    private static double errorInfinito(double[] x, double[] esperado) {
        double max = 0.0;
        for (int i = 0; i < x.length; i++) {
            max = Math.max(max, Math.abs(x[i] - esperado[i]));
        }
        return max;
    }

    private static void verificarIterativo(String nombre, double[] esperado, java.util.function.Supplier<double[]> metodo) {
        verificarSolucion(nombre, esperado, 1e-8, metodo);
    }

    private static void verificarSolucion(String nombre, double[] esperado, double tolerancia, java.util.function.Supplier<double[]> metodo) {
        System.out.println("-- " + nombre + " --");
        try {
            double[] x = metodo.get();
            System.out.println(errorInfinito(x, esperado) < tolerancia
                ? "Éxito. Solución: " + java.util.Arrays.toString(x)
                : "FALLO: Solución = " + java.util.Arrays.toString(x) + " (esperado " + java.util.Arrays.toString(esperado) + ")");
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    // Sin convergencia los métodos no lanzan excepción: devuelven la última iteración, lejos de la solución
    private static void verificarDivergencia(String nombre, double[] solucion, java.util.function.Supplier<double[]> metodo) {
        System.out.println("-- " + nombre + " --");
        try {
            double[] x = metodo.get();
            System.out.println(errorInfinito(x, solucion) > 1
                ? "Éxito. Diverge como predice la teoría: " + java.util.Arrays.toString(x)
                : "FALLO: Convergió a " + java.util.Arrays.toString(x) + " y se esperaba divergencia.");
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private static void verificarLecturaTXT(String nombre, String contenido, int n, double[][] esperado) {
        System.out.println("-- " + nombre + " --");
        try {
            double[][] leida = LectorMatrizTXT.leerAmpliada(contenido, n);
            System.out.println(java.util.Arrays.deepEquals(leida, esperado)
                ? "Éxito. Leída: " + java.util.Arrays.deepToString(leida)
                : "FALLO: Leída " + java.util.Arrays.deepToString(leida) + " (esperado " + java.util.Arrays.deepToString(esperado) + ")");
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    // El mensaje debe contener los dos fragmentos, para que el usuario sepa qué y dónde corregir
    private static void verificarErrorTXT(String nombre, String contenido, int n, String fragmento1, String fragmento2) {
        System.out.println("-- " + nombre + " --");
        try {
            LectorMatrizTXT.leerAmpliada(contenido, n);
            System.out.println("FALLO: No lanzó excepción.");
        } catch (IllegalArgumentException e) {
            boolean informativo = e.getMessage() != null && e.getMessage().startsWith("Error:")
                && e.getMessage().contains(fragmento1) && e.getMessage().contains(fragmento2);
            System.out.println(informativo
                ? "FALLO ESPERADO: " + e.getClass().getSimpleName() + " - " + e.getMessage()
                : "FALLO: Mensaje poco informativo - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private static double[][] ampliar(double[][] A, double[] b) {
        double[][] Ab = new double[A.length][A.length + 1];
        for (int i = 0; i < A.length; i++) {
            System.arraycopy(A[i], 0, Ab[i], 0, A.length);
            Ab[i][A.length] = b[i];
        }
        return Ab;
    }

    private static void verificarDeterminante(String nombre, double[][] datos, double esperado) {
        System.out.println("-- " + nombre + " --");
        try {
            double det = new Matriz(datos).determinante();
            if (Math.abs(det - esperado) < 1e-9) {
                System.out.println("Éxito. det = " + det);
            } else {
                System.out.println("FALLO: det = " + det + " (esperado " + esperado + ")");
            }
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private static void verificarNorma(String nombre, double[][] datos, double esperado) {
        System.out.println("-- " + nombre + " --");
        try {
            double norma = new Matriz(datos).norma();
            if (Math.abs(norma - esperado) < 1e-9) {
                System.out.println("Éxito. ||A||inf = " + norma);
            } else {
                System.out.println("FALLO: ||A||inf = " + norma + " (esperado " + esperado + ")");
            }
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private static void verificarCondicion(String nombre, double[][] datos, double esperado) {
        System.out.println("-- " + nombre + " --");
        try {
            double cond = new Matriz(datos).condicion();
            // Error relativo: las matrices mal condicionadas dan valores grandes
            if (Math.abs(cond - esperado) / esperado < 1e-6) {
                System.out.println("Éxito. cond(A) = " + cond);
            } else {
                System.out.println("FALLO: cond(A) = " + cond + " (esperado " + esperado + ")");
            }
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private static void verificarBienCondicionado(String nombre, double[][] datos, boolean esperado) {
        System.out.println("-- " + nombre + " --");
        try {
            boolean resultado = new Matriz(datos).bienCondicionado();
            if (resultado == esperado) {
                System.out.println("Éxito. bienCondicionado = " + resultado);
            } else {
                System.out.println("FALLO: bienCondicionado = " + resultado + " (esperado " + esperado + ")");
            }
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private static void verificarTranspuesta(String nombre, double[][] datos, double[][] esperado) {
        System.out.println("-- " + nombre + " --");
        try {
            Matriz t = new Matriz(datos).transponer();
            boolean coincide = t.getCantFilas() == esperado.length && t.getCantColumnas() == esperado[0].length;
            for (int i = 0; coincide && i < esperado.length; i++) {
                for (int j = 0; j < esperado[i].length; j++) {
                    if (t.getElemento(i, j) != esperado[i][j]) {
                        coincide = false;
                    }
                }
            }
            System.out.println(coincide ? "Éxito. A^T:" : "FALLO: A^T obtenida:");
            t.mostrarMatriz();
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private static void verificarInversa(String nombre, double[][] datos, double[][] esperado) {
        System.out.println("-- " + nombre + " --");
        try {
            Matriz inv = new Matriz(datos).inversa();
            boolean coincide = true;
            for (int i = 0; i < esperado.length; i++) {
                for (int j = 0; j < esperado[i].length; j++) {
                    if (Math.abs(inv.getElemento(i, j) - esperado[i][j]) > 1e-9) {
                        coincide = false;
                    }
                }
            }
            if (coincide) {
                System.out.println("Éxito. A^-1:");
            } else {
                System.out.println("FALLO: A^-1 obtenida:");
            }
            inv.mostrarMatriz();
        } catch (Exception e) {
            System.out.println("FALLO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private static void verificarInversaNoCuadrada(String nombre, double[][] datos) {
        System.out.println("-- " + nombre + " --");
        try {
            new Matriz(datos).inversa();
            System.out.println("FALLO: No lanzó excepción.");
        } catch (UnsupportedOperationException e) {
            if (e.getMessage().startsWith("Error:")) {
                System.out.println("FALLO ESPERADO (No cuadrada): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            } else {
                System.out.println("FALLO: Mensaje sin formato 'Error:' - " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("FALLO: Excepción incorrecta " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}
