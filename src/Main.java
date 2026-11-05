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
            } catch (UnsupportedOperationException e) {
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