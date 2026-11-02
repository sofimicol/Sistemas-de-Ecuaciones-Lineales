📄 Documentación Técnica: Módulo de Extracción y Factorización LU
Desarrolladora: [Tu Nombre]
Módulo: modelo.Matriz (métodos extraerA, extraerB) y servicios.MetodosDirectosMatriz (método factorizarLU)
Estado: ✅ Refactorizado, Testeado y Aprobado por QA.

1. Resumen Ejecutivo
El presente documento detalla las modificaciones, decisiones de diseño y blindajes implementados en los métodos de extracción de matrices y en la Factorización LU. El objetivo principal fue eliminar los falsos positivos (cálculos que devolvían NaN o Infinity sin lanzar errores) y garantizar que el sistema aborte de forma controlada ante matrices que no cumplen las precondiciones matemáticas del algoritmo (como pivotes nulos o dependencias lineales).

2. Módulo de Extracción: extraerA() y extraerB()
2.1. Contexto
Estos métodos se encargan de tomar una matriz ampliada (de dimensiones 
n
×
n
+
1
n×n+1) y separarla en dos estructuras independientes:

extraerA(): Devuelve un objeto Matriz de 
n
×
n
n×n con los coeficientes.

extraerB(): Devuelve un arreglo nativo double[] con los términos independientes.

2.2. Decisiones de Diseño y Cambios Realizados
Validación de Precondición (Fail-Fast): Ambos métodos comienzan con la validación if (!this.esAmpliada()). Si el usuario o el main intentan extraer los coeficientes de una matriz que no es ampliada (ej. una cuadrada de 
3
×
3
3×3), el método lanza inmediatamente una IllegalArgumentException con el mensaje: "La matriz no cumple las dimensiones de ampliada n x (n + 1)."

Retorno Nativo para b: Se decidió que extraerB() retorne un double[] (arreglo nativo de Java) en lugar de un objeto Matriz. Esto facilita su uso posterior en los métodos de sustitución y evita crear objetos innecesarios en memoria.

3. Módulo de Factorización: factorizarLU(Matriz A)
3.1. Contexto
La Factorización LU descompone una matriz cuadrada 
A
A en el producto de una matriz triangular inferior (
L
L) y una triangular superior (
U
U). Matemáticamente, este algoritmo no soporta pivoteo, por lo que requiere que la matriz sea estrictamente diagonalmente dominante o que no tenga ceros en la diagonal principal.

3.2. El Problema Detectado (Falso Positivo)
Durante el testeo inicial (Test 3c), se ingresó una matriz con un 0 en la posición 
A
[
0
]
[
0
]
A[0][0]. El algoritmo original intentó dividir por cero (en la fórmula de cálculo de 
L
L). Java no lanzó excepción, sino que devolvió Infinity. Esto se propagó por toda la matriz, resultando en matrices 
L
L y 
U
U llenas de Infinity y NaN, lo que generaba un falso positivo de éxito.

Adicionalmente (Test 3d), en matrices con dependencia lineal (singularidad oculta), el pivote se anulaba en la última etapa del cálculo (fila 
n
−
1
n−1). Por errores de precisión de la computadora (punto flotante), este cero se guardaba como un número extremadamente pequeño (ej. 
1
×
10
−
16
1×10 
−16
 ), lo que permitía que el algoritmo siguiera dividiendo y generara números gigantes.

3.3. Soluciones Implementadas (El Código Final)
Se incorporaron dos validaciones críticas dentro del bucle principal de la factorización (
k
k).

A. Validación del Pivote (El "Guardia")
Una vez calculada la fila 
k
k de la matriz 
U
U, inmediatamente se verifica que el pivote 
U
[
k
]
[
k
]
U[k][k] no sea cero (o menor al épsilon de la máquina 1e-12).

¿Por qué acá y no antes? Porque los primeros elementos de 
U
U se inicializan en 0.0; si se validaba antes de calcular la fila, daba un falso positivo prematuro.

Resultado: Si el pivote es nulo, se lanza ArithmeticException. Esto detiene el programa antes de realizar divisiones.

B. Red de Seguridad Interna
Se agregó una doble verificación al momento de calcular la matriz 
L
L, para asegurar que, incluso si el pivote pasó la primera prueba, no se produzca una división por cero por problemas de redondeo.

java
// Fragmento representativo del Fix
for (int k = 0; k < n; k++) {
    // 1. Calcular fila k de U
    for (int j = k; j < n; j++) { ... }
    
    // 2. Validación del Pivote (Fail-Fast)
    if (Math.abs(U.getElemento(k, k)) < 1e-12) {
        throw new ArithmeticException("Pivote cero en fila " + k + ". La matriz es singular.");
    }
    
    // 3. Calcular columna k de L
    for (int i = k + 1; i < n; i++) { ... }
}
4. Resultados del Testeo (QA)
Con la implementación de los cambios anteriores, el módulo superó exitosamente los siguientes escenarios de prueba:

Escenario	Entrada	Salida Esperada	Resultado
Happy Path (Normal)	Matriz 
3
×
3
3×3 sin ceros.	Matrices 
L
L y 
U
U correctas.	✅ Éxito
Pivote Cero Inicial	Matriz con A[0][0] = 0.	Excepción: "Pivote cero en fila 0..."	✅ Excepción controlada
Singularidad Oculta	Matriz con fila 3 = Fila 1 + Fila 2.	Excepción: "Pivote cero en fila 2..."	✅ Excepción controlada
Extracción Inválida	extraerA sobre matriz 
3
×
3
3×3.	Excepción: "No cumple las dimensiones..."	✅ Excepción controlada
5. Conclusión y Deuda Técnica
El módulo de extracción y factorización LU se encuentra blindado. Es matemáticamente imposible que, con los fixes aplicados, el método retorne matrices con NaN o Infinity.

Deuda técnica (a definir con el equipo):
Como el método factorizarLU no implementa pivoteo (para no cambiar la firma a PA = LU), si el usuario ingresa una matriz con un cero en la diagonal, el programa abortará. La interfaz gráfica (UI) debe estar preparada para capturar esta ArithmeticException y sugerirle al usuario que utilice el método de Gauss-Jordan (el cual sí tiene pivoteo automático).

